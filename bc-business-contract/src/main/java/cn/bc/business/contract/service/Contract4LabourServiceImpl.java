/**
 * 
 */
package cn.bc.business.contract.service;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import cn.bc.BCConstants;
import cn.bc.business.contract.dao.Contract4LabourDao;
import cn.bc.business.contract.dao.ContractDao;
import cn.bc.business.contract.domain.Contract;
import cn.bc.business.contract.domain.Contract4Charger;
import cn.bc.business.contract.domain.Contract4Labour;
import cn.bc.business.contract.domain.ContractCarManRelation;
import cn.bc.business.contract.domain.ContractCarRelation;
import cn.bc.core.Page;
import cn.bc.core.exception.CoreException;
import cn.bc.core.query.condition.Condition;
import cn.bc.core.service.DefaultCrudService;
import cn.bc.core.util.DateUtils;
import cn.bc.docs.domain.Attach;
import cn.bc.docs.service.AttachService;
import cn.bc.identity.service.IdGeneratorService;
import cn.bc.identity.web.SystemContext;
import cn.bc.identity.web.SystemContextHolder;
import cn.bc.template.domain.Template;
import cn.bc.template.service.TemplateService;
import cn.bc.template.util.DocxUtils;
import cn.bc.web.ui.json.Json;

/**
 * 司机劳动合同Service的实现
 * 
 * @author dragon
 */
public class Contract4LabourServiceImpl extends
		DefaultCrudService<Contract4Labour> implements Contract4LabourService {
	private static Log logger = LogFactory
			.getLog(Contract4LabourServiceImpl.class);
	private Contract4LabourDao contract4LabourDao;
	private ContractDao contractDao;
	private IdGeneratorService idGeneratorService;// 用于生成uid的服务
	private AttachService attachService;// 附件服务
	private TemplateService templateService;// 模板服务

	@Autowired
	public void setTemplateService(TemplateService templateService) {
		this.templateService = templateService;
	}

	@Autowired
	public void setContractDao(ContractDao contractDao) {
		this.contractDao = contractDao;
	}

	@Autowired
	public void setContract4LabourDao(Contract4LabourDao contract4LabourDao) {
		this.contract4LabourDao = contract4LabourDao;
		this.setCrudDao(contract4LabourDao);
	}

	@Autowired
	public void setAttachService(AttachService attachService) {
		this.attachService = attachService;
	}

	@Autowired
	public void setIdGeneratorService(IdGeneratorService idGeneratorService) {
		this.idGeneratorService = idGeneratorService;
	}

	public Contract4Labour save(Contract4Labour contract4Labour, Long carId,
			Long driverId) {
		if (contract4Labour == null)
			return null;
		boolean isNew = contract4Labour.isNew();

		if (!isNew) { // 非新建状态，只需简单保存一下即可，因为编辑时不允许修改车辆、司机信息
			contract4Labour = this.contract4LabourDao.save(contract4Labour);
		} else { // 在新建时需保存与车辆、司机的关联关系
			// 参数有效性验证
			Assert.notNull(carId);
			Assert.notNull(driverId);

			// 保存合同
			contract4Labour = this.contract4LabourDao.save(contract4Labour);

			// 处理与车辆的关联关系
			ContractCarRelation carRelation = new ContractCarRelation(
					contract4Labour.getId(), carId);
			this.contractDao.saveContractCarRelation(carRelation);

			// 处理与司机的关联关系
			ContractCarManRelation driverRelation = new ContractCarManRelation(
					contract4Labour.getId(), driverId);
			this.contractDao.saveContractCarManRelation(driverRelation);

		}
		if (contract4Labour.getStatus() == BCConstants.STATUS_ENABLED) {
			// 在案时才更新
			// 更新司机的户口性质,区域
			this.contract4LabourDao.updateCarMan4CarManInfo(driverId,
					contract4Labour.getHouseType(),
					contract4Labour.getRegion());
		}
		// this.contract4LabourDao.updateCarMan4Description(driverId,description);

		return contract4Labour;
	}

	/**
	 * 续约
	 */
	public Contract4Labour doRenew(Long contractId, Calendar newStartDate,
			Calendar newEndDate) {
		// 获取原来的合同信息
		Contract4Labour oldContract = this.contract4LabourDao.load(contractId);
		if (oldContract == null)
			throw new CoreException("要处理的合同已不存在！contractId=" + contractId);

		// 更新旧合同的相关信息
		oldContract.setStatus(Contract.STATUS_LOGOUT);// 失效
		oldContract.setMain(Contract.MAIN_HISTORY);// 历史
		this.contract4LabourDao.save(oldContract);

		// 复制出新的合同
		Contract4Labour newContract = new Contract4Labour();
		try {
			BeanUtils.copyProperties(oldContract, newContract);
		} catch (Exception e) {
			throw new CoreException("复制合同信息错误！", e);
		}
		newContract.setId(null);

		// 生成新的合同编号
		String oldContractCode = oldContract.getCode();
		if (oldContractCode.lastIndexOf("-") > 0) { // 判断旧合同编号是否存在字符"-"
			// 将字符"-"后的数字+1
			int num = Integer.parseInt(oldContractCode.split("-")[1]);
			oldContractCode = oldContractCode.split("-")[0] + "-" + ++num;
		} else {
			oldContractCode = oldContractCode + "-1";
		}
		newContract.setCode(oldContractCode);
		// newContract.setCode(this.idGeneratorService
		// .nextSN4Month(Contract4Labour.KEY_CODE));

		// 设置新的合同期限
		newContract.setStartDate(newStartDate);
		newContract.setEndDate(newEndDate);

		// 设置创建人信息和最后修改人信息
		SystemContext context = SystemContextHolder.get();
		newContract.setAuthor(context.getUserHistory());
		newContract.setFileDate(Calendar.getInstance());
		newContract.setModifier(context.getUserHistory());
		newContract.setModifiedDate(Calendar.getInstance());

		// 主版本号 加1
		newContract.setVerMajor(oldContract.getVerMajor() + 1);

		// 关联 续签合同与原合同
		newContract.setPid(contractId);

		// 设置操作类型、状态、main
		newContract.setOpType(Contract.OPTYPE_RENEW);// 续签
		newContract.setMain(Contract.MAIN_NOW);// 当前
		newContract.setStatus(Contract.STATUS_NORMAL);// 正常

		// 保存新的合同信息以获取id
		newContract.setUid(this.idGeneratorService
				.next(Contract4Labour.KEY_UID));
		newContract = this.contract4LabourDao.save(newContract);
		Long newId = newContract.getId();

		// 复制合同与车辆的关系
		List<ContractCarRelation> oldCCRelations = this.contractDao
				.findContractCarRelation(contractId);
		if (!oldCCRelations.isEmpty()) {
			List<ContractCarRelation> copyCCRelations = new ArrayList<ContractCarRelation>();
			for (ContractCarRelation old : oldCCRelations) {
				copyCCRelations.add(new ContractCarRelation(newId, old
						.getCarId()));
			}
			this.contractDao.saveContractCarRelation(copyCCRelations);
		}

		// 复制合同与司机的关系：劳动合同
		// 复制合同与责任人的关系：经济合同
		List<ContractCarManRelation> oldCMRelations = this.contractDao
				.findContractCarManRelation(contractId);
		if (!oldCMRelations.isEmpty()) {
			List<ContractCarManRelation> copyCMRelations = new ArrayList<ContractCarManRelation>();
			for (ContractCarManRelation old : oldCMRelations) {
				copyCMRelations.add(new ContractCarManRelation(newId, old
						.getCarManId()));
			}
			this.contractDao.saveContractCarManRelation(copyCMRelations);
		}

		// 复制原合同的附件给新的合同
		String oldUid = oldContract.getUid();
		attachService.doCopy(Contract4Labour.KEY_UID, oldUid,
				Contract4Labour.KEY_UID, newContract.getUid(), true);

		// 返回续签的合同
		return newContract;
	}

	/**
	 * 转车
	 */
	public Contract4Labour doChangeCar(Long contractId, Long newCarId,
			String newCarPlate) {
		// 获取原来的合同信息
		Contract4Labour oldContract = this.contract4LabourDao.load(contractId);
		if (oldContract == null)
			throw new CoreException("要处理的合同已不存在！contractId=" + contractId);

		// 更新旧合同的相关信息
		oldContract.setStatus(Contract.STATUS_LOGOUT);// 失效
		oldContract.setMain(Contract.MAIN_HISTORY);// 历史
		this.contract4LabourDao.save(oldContract);

		// 复制出新的合同
		Contract4Labour newContract = new Contract4Labour();
		try {
			BeanUtils.copyProperties(oldContract, newContract);
		} catch (Exception e) {
			throw new CoreException("复制合同信息错误！", e);
		}
		newContract.setId(null);

		// 生成新的合同编号
		String oldContractCode = oldContract.getCode();
		if (oldContractCode.lastIndexOf("-") > 0) { // 判断旧合同编号是否存在字符"-"
			// 将字符"-"后的数字+1
			int num = Integer.parseInt(oldContractCode.split("-")[1]);
			oldContractCode = oldContractCode.split("-")[0] + "-" + ++num;
		} else {
			oldContractCode = oldContractCode + "-1";
		}
		newContract.setCode(oldContractCode);

		// 设置创建人信息和最后修改人信息
		SystemContext context = SystemContextHolder.get();
		newContract.setAuthor(context.getUserHistory());
		newContract.setFileDate(Calendar.getInstance());
		newContract.setModifier(context.getUserHistory());
		newContract.setModifiedDate(Calendar.getInstance());

		// 主版本号 加1
		newContract.setVerMajor(oldContract.getVerMajor() + 1);

		// 关联 转车合同与原合同
		newContract.setPid(contractId);

		// 新合同的车牌号
		newContract.setExt_str1(newCarPlate);

		// 设置操作类型、状态、main
		newContract.setOpType(Contract.OPTYPE_RENEW);// 续签
		newContract.setMain(Contract.MAIN_NOW);// 当前
		newContract.setStatus(Contract.STATUS_NORMAL);// 正常

		// 保存新的合同信息以获取id
		newContract.setUid(this.idGeneratorService
				.next(Contract4Labour.KEY_UID));
		newContract = this.contract4LabourDao.save(newContract);
		Long newId = newContract.getId();

		// 新建合同与车辆的关系
		ContractCarRelation carRelation = new ContractCarRelation(newId,
				newCarId);
		this.contractDao.saveContractCarRelation(carRelation);

		// 复制合同与司机的关系：劳动合同
		// 复制合同与责任人的关系：经济合同
		List<ContractCarManRelation> oldCMRelations = this.contractDao
				.findContractCarManRelation(contractId);
		if (!oldCMRelations.isEmpty()) {
			List<ContractCarManRelation> copyCMRelations = new ArrayList<ContractCarManRelation>();
			for (ContractCarManRelation old : oldCMRelations) {
				copyCMRelations.add(new ContractCarManRelation(newId, old
						.getCarManId()));
			}
			this.contractDao.saveContractCarManRelation(copyCMRelations);
		}

		// 复制原合同的附件给新的合同
		String oldUid = oldContract.getUid();
		attachService.doCopy(Contract4Labour.KEY_UID, oldUid,
				Contract4Labour.KEY_UID, newContract.getUid(), true);

		// 返回转车的合同
		return newContract;
	}

	/**
	 * 离职
	 */
	public void doResign(Long contractId, Calendar resignDate, Calendar stopDate) {
		// 获取原来的合同信息
		Contract4Labour contract = this.contract4LabourDao.load(contractId);
		if (contract == null)
			throw new CoreException("要处理的合同已不存在！contractId=" + contractId);

		// 更新旧合同的相关信息
		contract.setStatus(Contract.STATUS_RESGIN);// 离职
		// 设置离职日期
		contract.setLeaveDate(resignDate);
		// 设置停保日期
		contract.setStopDate(stopDate);

		this.contract4LabourDao.save(contract);
		// throw new CoreException("need implement");
	}

	/**
	 * 查找劳动合同列表
	 * 
	 * @parma condition
	 * @parma carId
	 * @return
	 */
	public List<Map<String, Object>> list4carMan(Condition condition,
			Long carManId) {
		return this.contract4LabourDao.list4carMan(condition, carManId);
	}

	/**
	 * 查找劳动合同分页
	 * 
	 * @parma condition
	 * @parma carId
	 * @return
	 */
	public Page<Map<String, Object>> page4carMan(Condition condition,
			int pageNo, int pageSize) {
		return this.contract4LabourDao.page4carMan(condition, pageNo, pageSize);
	}

	/**
	 * 根据carManId查找cert信息
	 * 
	 * @parma carManId
	 * @return
	 */
	public Map<String, Object> findCertByCarManId(Long carManId) {
		Map<String, Object> queryMap = null;
		queryMap = this.contract4LabourDao.findCertByCarManId(carManId);
		return queryMap;
	}

	/**
	 * 根据合同ID查找车辆ID
	 * 
	 * @param contractId
	 * @return
	 */
	public Long findCarIdByContractId(Long contractId) {
		Long carId = null;
		carId = contract4LabourDao.findCarIdByContractId(contractId);
		return carId;
	}

	/**
	 * 根据合同ID查找司机ID
	 * 
	 * @param contractId
	 * @return
	 */
	public Long findCarManIdByContractId(Long contractId) {
		Long carManId = null;
		carManId = contract4LabourDao.findCarManIdByContractId(contractId);
		return carManId;
	}

	/**
	 * 根据车辆Id查找车辆
	 * 
	 * @param carId
	 * @return
	 */
	public Map<String, Object> findCarManByCarId(Long carId) {
		Map<String, Object> queryMap = null;
		queryMap = this.contract4LabourDao.findCarManByCarId(carId);
		return queryMap;
	}

	/**
	 * 根据司机ID相应的车
	 * 
	 * @param carManId
	 * @return
	 */
	public List<Map<String, Object>> selectRelateCarByCarManId(Long carManId) {
		List<Map<String, Object>> list = null;
		list = this.contract4LabourDao.selectRelateCarByCarManId(carManId);
		return list;
	}

	/**
	 * 根据司机ID查找司机
	 * 
	 * @param carManId
	 * @return
	 */
	public Map<String, Object> findCarManByCarManId(Long carManId) {
		Map<String, Object> queryMap = null;
		queryMap = this.contract4LabourDao.findCarManByCarManId(carManId);
		return queryMap;
	}

	/**
	 * 根据车辆ID查找车辆
	 * 
	 * @param carId
	 * @return
	 */
	public Map<String, Object> findCarByCarId(Long carId) {
		Map<String, Object> queryMap = null;
		queryMap = this.contract4LabourDao.findCarByCarManId(carId);
		return queryMap;
	}

	/**
	 * 根据车辆ID查找车辆
	 * 
	 * @param carId
	 * @return
	 */
	public List<Map<String, Object>> selectRelateCarManByCarId(Long carId) {
		List<Map<String, Object>> list = null;
		list = this.contract4LabourDao.selectRelateCarManByCarId(carId);
		return list;
	}

	public boolean isExistContractByDriverId(Long driverId) {
		return this.contract4LabourDao.isExistContractByDriverId(driverId);
	}

	/**
	 * 格式化日期
	 * 
	 * @return
	 */
	public String calendarToString(Calendar object) {
		if (null != object && object.toString().length() > 0) {
			Calendar calendar = object;
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			String dateStr = df.format(calendar.getTime());
			return dateStr;
		} else {
			return "";
		}
	}

	/**
	 * 判断经济合同自编号唯一
	 * 
	 * @param excludeId
	 * @param code
	 * @return
	 */
	public Long checkInsurCodeIsExist(Long excludeId, String insurCode) {
		return this.contract4LabourDao.checkInsurCodeIsExist(excludeId,
				insurCode);
	}

	public Contract4Labour doCopyContract(Long id, int opType) {
		// 获取原来的合同信息
		Contract4Labour oldContract = this.contract4LabourDao.load(id);
		if (oldContract == null)
			throw new CoreException("要处理的合同已不存在！contractId=" + id);

		// 复制出新的合同
		Contract4Labour newContract = new Contract4Labour();
		try {
			BeanUtils.copyProperties(oldContract, newContract);
		} catch (Exception e) {
			throw new CoreException("复制合同信息错误！", e);
		}

		// 保存新的合同信息以获取id
		newContract.setUid(this.idGeneratorService
				.next(Contract4Charger.KEY_UID));

		// 复制原合同的附件给新的合同
		String oldUid = oldContract.getUid();
		attachService.doCopy(Contract4Labour.KEY_UID, oldUid,
				Contract4Labour.KEY_UID, newContract.getUid(), true);

		// 设置创建人,最后更新人的信息
		SystemContext context = SystemContextHolder.get();

		newContract.setAuthor(context.getUserHistory());
		newContract.setModifier(context.getUserHistory());
		newContract.setFileDate(Calendar.getInstance());
		newContract.setModifiedDate(Calendar.getInstance());

		// 设置操作的信息
		newContract.setId(null);
		newContract.setOpType(opType);
		newContract.setSignDate(null);
		newContract.setStartDate(null);
		newContract.setVerMajor(oldContract.getVerMajor() + 1);// 版本号+1
		newContract.setVerMinor(0);
		// 记录旧的保存合同id
		newContract.setPid(id);

		return newContract;
	}

	/**
	 * 操作
	 */
	public Contract4Labour doOperate(Long carId, Contract4Labour e,
			Long contractId, String stopDate) {
		// 是否新建
		boolean isNew = e.isNew();
		// 获取原来的合同信息
		Contract4Labour oldContract = this.contract4LabourDao.load(contractId);
		if (oldContract == null)
			throw new CoreException("要处理的合同已不存在！contractId=" + contractId);

		// 保存旧合同信息
		// 更新旧合同的相关信息
		SystemContext context = SystemContextHolder.get();
		// 如果是保存为草稿的，不对原合同作任何操作
		if (e.getStatus() != BCConstants.STATUS_DRAFT) {
			oldContract.setStatus(Contract.STATUS_LOGOUT);// 失效
			oldContract.setMain(Contract.MAIN_HISTORY);// 历史
			oldContract.setLogoutId(context.getUserHistory());// 注销人
			oldContract.setLogoutDate(Calendar.getInstance());// 注销时间
			// 合同实际结束日期
			Calendar stopDate4Charger = DateUtils.getCalendar(stopDate);
			oldContract.setStopDate(stopDate4Charger);// 合同实际结束日期
			this.contract4LabourDao.save(oldContract);
		}

		// 保存新合同信息
		Contract4Labour newContract = e;
		// 如果是保存为草稿的，先将实际结束日期保存在新合同的冗余字段中
		if (e.getStatus() == BCConstants.STATUS_DRAFT) {
			e.setExt_str3(stopDate);
		} else {
			e.setExt_str3(null);
		}

		newContract = this.contract4LabourDao.save(newContract);
		Long newId = newContract.getId();
		// 如果是新建
		if (isNew) {

			// 复制合同与车辆的关系
			List<ContractCarRelation> oldCCRelations = this.contractDao
					.findContractCarRelation(contractId);
			if (!oldCCRelations.isEmpty()) {
				List<ContractCarRelation> copyCCRelations = new ArrayList<ContractCarRelation>();
				for (ContractCarRelation old : oldCCRelations) {
					copyCCRelations.add(new ContractCarRelation(newId, old
							.getCarId()));
				}
				this.contractDao.saveContractCarRelation(copyCCRelations);
			}

			// 复制合同与司机的关系：劳动合同
			// 复制合同与责任人的关系：经济合同
			List<ContractCarManRelation> oldCMRelations = this.contractDao
					.findContractCarManRelation(contractId);
			if (!oldCMRelations.isEmpty()) {
				List<ContractCarManRelation> copyCMRelations = new ArrayList<ContractCarManRelation>();
				for (ContractCarManRelation old : oldCMRelations) {
					copyCMRelations.add(new ContractCarManRelation(newId, old
							.getCarManId()));
				}
				this.contractDao.saveContractCarManRelation(copyCMRelations);
			}
		}

		// 返回续签的合同
		return newContract;

	}

	public Attach doAddAttachFromTemplate(Long id, String templateCode)
			throws IOException {
		// 加载合同
		Contract4Labour c = this.load(id);
		// 生成格式化参数
		Map<String, Object> params = new HashMap<String, Object>();
		// 合同信息
		params.put("code", c.getCode());
		Calendar startDate = c.getStartDate();
		Calendar endDate = c.getEndDate();
		params.put("startYear", DateUtils.formatCalendar(startDate, "yyyy"));
		params.put("startMonth", DateUtils.formatCalendar(startDate, "MM"));
		params.put("startDay", DateUtils.formatCalendar(startDate, "dd"));
		params.put("endYear", DateUtils.formatCalendar(endDate, "yyyy"));
		params.put("endMonth", DateUtils.formatCalendar(endDate, "MM"));
		params.put("endDay", DateUtils.formatCalendar(endDate, "dd"));
		// 车辆信息
		Map<String, Object> carMap = this.contract4LabourDao
				.findCarByCarId(this.contract4LabourDao
						.findCarIdByContractId(id));
		// 公司
		String company = null;
		if (carMap.get("company").equals("宝城")) {
			company = "广州市宝城汽车出租有限公司";
		} else if (carMap.get("company").equals("广发")) {
			company = "广州市广发出租汽车有限公司";
		}
		params.put("company", company);

		// 司机信息
		Map<String, Object> carManMap = this.contract4LabourDao
				.findCarManByCarManId(this.contract4LabourDao
						.findCarManIdByContractId(id));
		params.put("carMan", carManMap.get("name").toString());
		params.put("certIdentity", carManMap.get("cert_Identity").toString());

		// 经济合同信息
		List<Map<String, String>> chargerList = this.contract4LabourDao
				.findContract4ChargerByContarct4LabourId(id);
		if(chargerList!=null&&chargerList.size()>0){
			String signDate = chargerList.get(0).get("signDate");
			if (signDate != null && signDate != "") {
				params.put("chargerSignYear",
						signDate.substring(0, signDate.indexOf("-")));
				params.put(
						"chargerSignMonth",
						signDate.substring(signDate.indexOf("-") + 1,
								signDate.lastIndexOf("-")));
				params.put("chargerSignDay",
						signDate.substring(signDate.lastIndexOf("-") + 1));
			}
		}
			

		// 生成附件
		String ptype = Contract4Labour.ATTACH_TYPE;
		String puid = c.getUid();
		Template template = this.templateService.loadByCode(templateCode);
		if (template == null) {
			logger.warn("模板不存在,返回null:code=" + templateCode);
			return null;
		} else {
			
			// 根据模板参数获取的替换值
			Map<String, Object> mapFormatSql = new HashMap<String, Object>();
			mapFormatSql.put("id", id);
			Map<String, Object> mapParams = templateService.getMapParams(
					template.getId(), mapFormatSql);
			if (mapParams != null)
				params.putAll(mapParams);
			
			if(template.getTemplateType().getCode().equals("word-docx")&&
					template.isFormatted()){
				// 获取文件中的${XXXX}占位标记的键名列表
				List<String> markers = DocxUtils.findMarkers(template
						.getInputStream());
				// 占位符列表与参数列表匹配,当占位符列表值没出现在参数列表key值时，增加此key值
				for (String key : markers) {
					if (!params.containsKey(key)) 
						params.put(key,"　　");
				}
			}
			Attach attach = template.format2Attach(params, ptype, puid);
			this.attachService.save(attach);
			return attach;
		}
	}

	public String json;

	public String doWarehousing(Long carId, Long driverId, Contract4Labour e) {
		Map<String, Object> carInfoMap; // 车辆Map
		Map<String, Object> carManInfoMap; // 车辆Map
		Json json = new Json();
		boolean success4car = true;
		boolean success4carMan = true;
		String msg = "";
		// 查找车辆的状态和车牌号
		carInfoMap = this.contract4LabourDao.findCarByCarId(carId);
		if (carInfoMap != null) {
			// 如果车辆状态不为草稿时，不能入库
			if (Long.valueOf(String.valueOf(carInfoMap.get("status_"))) == BCConstants.STATUS_DRAFT) {
				success4car = false;
				msg = carInfoMap.get("plate_type") + "."
						+ carInfoMap.get("plate_no");

			}
		}
		// 查找责任人的状态和姓名
		carManInfoMap = this.findCarManByCarManId(driverId);
		if (carManInfoMap != null) {
			// 如果责任人状态不为草稿时，不能入库
			if (Long.valueOf(String.valueOf(carManInfoMap.get("status_"))) == BCConstants.STATUS_DRAFT) {
				success4carMan = false;
				msg = (String) (msg.length() > 0 || msg != "" ? msg + " 、"
						+ carManInfoMap.get("name") : carManInfoMap.get("name"));

			}
		}

		// 如果都为入库状态，则该经济合同可以入库
		if (success4car == true && success4carMan == true) {

			e.setStatus(BCConstants.STATUS_ENABLED);
			// 如果pid不为空，则为变更操作新建入库
			if (e.getPid() != null) {
				this.doOperate(carId, e, e.getPid(), e.getExt_str3());
			} else {
				// 无变更操作下新建
				this.save(e, carId, driverId);
			}
			json.put("success", true);

			e.setStatus(BCConstants.STATUS_ENABLED);
			this.save(e);
			json.put("success", true);
		} else {
			json.put("success", false);
			json.put("msg", "入库失败！请先将  " + msg + " 入库！");
		}
		this.json = json.toString();
		return this.json;
	}
}
