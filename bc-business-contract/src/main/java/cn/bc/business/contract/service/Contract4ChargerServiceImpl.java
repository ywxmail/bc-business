/**
 * 
 */
package cn.bc.business.contract.service;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import cn.bc.BCConstants;
import cn.bc.business.contract.dao.Contract4ChargerDao;
import cn.bc.business.contract.dao.ContractDao;
import cn.bc.business.contract.domain.Contract;
import cn.bc.business.contract.domain.Contract4Charger;
import cn.bc.business.contract.domain.ContractCarManRelation;
import cn.bc.business.contract.domain.ContractCarRelation;
import cn.bc.business.contract.domain.ContractFeeDetail;
import cn.bc.core.Page;
import cn.bc.core.exception.CoreException;
import cn.bc.core.query.condition.Condition;
import cn.bc.core.service.DefaultCrudService;
import cn.bc.core.util.DateUtils;
import cn.bc.core.util.StringUtils;
import cn.bc.docs.domain.Attach;
import cn.bc.docs.service.AttachService;
import cn.bc.identity.service.IdGeneratorService;
import cn.bc.identity.web.SystemContext;
import cn.bc.identity.web.SystemContextHolder;
import cn.bc.template.domain.Template;
import cn.bc.template.service.TemplateService;
import cn.bc.web.formater.CalendarFormater;
import cn.bc.web.ui.json.Json;

/**
 * 责任人合同Service的实现
 * 
 * @author dragon
 */
public class Contract4ChargerServiceImpl extends
		DefaultCrudService<Contract4Charger> implements Contract4ChargerService {
	private static Log logger = LogFactory
			.getLog(Contract4ChargerServiceImpl.class);
	private Contract4ChargerDao contract4ChargerDao;
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
	public void setContract4ChargerDao(Contract4ChargerDao contract4ChargerDao) {
		this.contract4ChargerDao = contract4ChargerDao;
		this.setCrudDao(contract4ChargerDao);
	}

	@Autowired
	public void setAttachService(AttachService attachService) {
		this.attachService = attachService;
	}

	@Autowired
	public void setIdGeneratorService(IdGeneratorService idGeneratorService) {
		this.idGeneratorService = idGeneratorService;
	}

	public Contract4Charger save(Contract4Charger contract4Charger, Long carId,
			String assignChargerIds, String assignChargerNames) {
		if (contract4Charger == null)
			return null;
		boolean isNew = contract4Charger.isNew();
		Long[] chargerIdAry = StringUtils
				.stringArray2LongArray(assignChargerIds.split(","));
		if (!isNew) { // 非新建状态，只需简单保存一下即可，因为编辑时不允许修改车辆
			// 保存合同
			contract4Charger = this.contract4ChargerDao.save(contract4Charger);
			// 处理与责任人的关联关系
			this.contractDao.updateContractCarManRelation(
					contract4Charger.getId(), chargerIdAry);
		} else { // 在新建时需保存与车辆、责任人的关联关系
			// 参数有效性验证
			Assert.notNull(carId);

			// 保存合同
			contract4Charger = this.contract4ChargerDao.save(contract4Charger);

			// 处理与车辆的关联关系
			ContractCarRelation carRelation = new ContractCarRelation(
					contract4Charger.getId(), carId);
			this.contractDao.saveContractCarRelation(carRelation);

			// 处理与责任人的关联关系
			List<ContractCarManRelation> chargerRelationList = new ArrayList<ContractCarManRelation>();
			ContractCarManRelation driverRelation = null;
			for (Long chargerId : chargerIdAry) {
				driverRelation = new ContractCarManRelation(
						contract4Charger.getId(), chargerId);
				chargerRelationList.add(driverRelation);
			}
			this.contractDao.saveContractCarManRelation(chargerRelationList);
			// ContractCarManRelation driverRelation = new
			// ContractCarManRelation(
			// contract4Charger.getId(), null);
		}
		// 更新车辆视图的charger列显示责任人姓名
		this.contract4ChargerDao.updateCar4ChargerName(carId);
		// this.contract4ChargerDao.updateCar4ChargerName(assignChargerNames,carId);
		// 更新司机视图的charger列显示责任人姓名
		this.contract4ChargerDao.updateCarMan4ChargerName(carId);
		// this.contract4ChargerDao.updateCarMan4ChargerName(assignChargerNames,carId);
		return contract4Charger;
	}

	/**
	 * 续约
	 */
	public Contract4Charger doRenew(Long contractId, Calendar newStartDate,
			Calendar newEndDate, String code) {
		// 获取原来的合同信息
		Contract4Charger oldContract = this.contract4ChargerDao
				.load(contractId);
		if (oldContract == null)
			throw new CoreException("要处理的合同已不存在！contractId=" + contractId);

		// 复制出新的合同
		Contract4Charger newContract = new Contract4Charger();
		try {
			BeanUtils.copyProperties(oldContract, newContract);
		} catch (Exception e) {
			throw new CoreException("复制合同信息错误！", e);
		}
		newContract.setId(null);

		// 更新旧合同的相关信息
		SystemContext context = SystemContextHolder.get();

		oldContract.setStatus(Contract.STATUS_LOGOUT);// 失效
		oldContract.setMain(Contract.MAIN_HISTORY);// 历史
		oldContract.setLogoutId(context.getUserHistory());// 注销人
		oldContract.setLogoutDate(Calendar.getInstance());// 注销时间
		this.contract4ChargerDao.save(oldContract);

		/*
		 * //生成合同编号 String oldContractCode = oldContract.getCode();
		 * if(oldContractCode.lastIndexOf("-") > 0){ //判断旧合同编号是否存在字符"-"
		 * //将字符"-"后的数字+1 int num =
		 * Integer.parseInt(oldContractCode.split("-")[1]); oldContractCode =
		 * oldContractCode.split("-")[0]+"-"+ ++num; }else{ oldContractCode =
		 * oldContractCode+"-1"; }
		 */
		// newContract.setCode(this.idGeneratorService
		// .nextSN4Month("CLHT"+Contract4Charger.KEY_CODE));
		newContract.setCode(code);

		// 设置新的合同期限
		newContract.setStartDate(newStartDate);
		newContract.setEndDate(newEndDate);

		// 设置创建人信息和最后修改人信息
		newContract.setAuthor(context.getUserHistory());
		newContract.setFileDate(Calendar.getInstance());
		newContract.setModifier(context.getUserHistory());
		newContract.setModifiedDate(Calendar.getInstance());

		// 主版本号 加1
		newContract.setVerMajor(oldContract.getVerMajor() + 1);

		// 关联 续签合同与原合同
		newContract.setPid(contractId);

		// 设置签约类型、操作类型、状态、main
		newContract.setSignType("续约");
		newContract.setOpType(Contract.OPTYPE_RENEW);// 续签
		newContract.setMain(Contract.MAIN_NOW);// 当前
		newContract.setStatus(Contract.STATUS_NORMAL);// 正常

		// 保存新的合同信息以获取id
		newContract.setUid(this.idGeneratorService
				.next(Contract4Charger.KEY_UID));
		newContract = this.contract4ChargerDao.save(newContract);
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
		attachService.doCopy(Contract4Charger.KEY_UID, oldUid,
				Contract4Charger.KEY_UID, newContract.getUid(), true);

		// 返回续签的合同
		return newContract;
	}

	/**
	 * 过户
	 */
	public Contract4Charger doChaneCharger(Long carId, Boolean takebackOrigin,
			String assignChargerIds, String assignChargerNames,
			Long contractId, Calendar newStartDate, Calendar newEndDate,
			String code) {
		// 获取原来的合同信息
		Contract4Charger oldContract = this.contract4ChargerDao
				.load(contractId);
		if (oldContract == null)
			throw new CoreException("要处理的合同已不存在！contractId=" + contractId);

		// 复制出新的合同
		Contract4Charger newContract = new Contract4Charger();
		try {
			BeanUtils.copyProperties(oldContract, newContract);
		} catch (Exception e) {
			throw new CoreException("复制合同信息错误！", e);
		}
		newContract.setId(null);

		// 更新旧合同的相关信息
		SystemContext context = SystemContextHolder.get();

		oldContract.setStatus(Contract.STATUS_LOGOUT);// 失效
		oldContract.setMain(Contract.MAIN_HISTORY);// 历史
		oldContract.setLogoutId(context.getUserHistory());// 注销人
		oldContract.setLogoutDate(Calendar.getInstance());// 注销时间
		this.contract4ChargerDao.save(oldContract);

		/*
		 * 
		 * // 生成新的合同编号 String oldContractCode = oldContract.getCode();
		 * if(oldContractCode.lastIndexOf("-") > 0){ //判断旧合同编号是否存在字符"-"
		 * //将字符"-"后的数字+1 int num =
		 * Integer.parseInt(oldContractCode.split("-")[1]); oldContractCode =
		 * oldContractCode.split("-")[0]+"-"+ ++num; }else{ oldContractCode =
		 * oldContractCode+"-1"; } newContract.setCode(oldContractCode);
		 */
		// newContract.setCode(this.idGeneratorService
		// .nextSN4Month(Contract4Labour.KEY_CODE));
		newContract.setCode(code);

		// 设置新的合同期限
		newContract.setStartDate(newStartDate);
		newContract.setEndDate(newEndDate);

		// 设置原件是否已收回
		newContract.setTakebackOrigin(takebackOrigin);

		// 设置冗余显示责任人姓名
		newContract.setExt_str2(assignChargerNames);

		// 设置创建人信息和最后修改人信息
		newContract.setAuthor(context.getUserHistory());
		newContract.setFileDate(Calendar.getInstance());
		newContract.setModifier(context.getUserHistory());
		newContract.setModifiedDate(Calendar.getInstance());

		// 主版本号 加1
		newContract.setVerMajor(oldContract.getVerMajor() + 1);

		// 关联 过户合同与原合同
		newContract.setPid(contractId);

		// 设置签约类型、操作类型、状态、main
		newContract.setSignType("过户");
		newContract.setOpType(Contract.OPTYPE_CHANGECHARGER);// 过户
		newContract.setMain(Contract.MAIN_NOW);// 当前
		newContract.setStatus(Contract.STATUS_NORMAL);// 正常

		// 保存新的合同信息以获取id
		newContract.setUid(this.idGeneratorService
				.next(Contract4Charger.KEY_UID));
		newContract = this.contract4ChargerDao.save(newContract);
		Long newId = newContract.getId();

		// 复制原合同的附件给新的合同
		String oldUid = oldContract.getUid();
		attachService.doCopy(Contract4Charger.KEY_UID, oldUid,
				Contract4Charger.KEY_UID, newContract.getUid(), true);

		// 参数有效性验证
		Assert.notNull(carId);

		// 处理责任人id列表
		Long[] chargerIdAry = StringUtils
				.stringArray2LongArray(assignChargerIds.split(","));

		// 处理与车辆的关联关系
		ContractCarRelation carRelation = new ContractCarRelation(newId, carId);
		this.contractDao.saveContractCarRelation(carRelation);

		// 处理与责任人的关联关系
		List<ContractCarManRelation> chargerRelationList = new ArrayList<ContractCarManRelation>();
		ContractCarManRelation driverRelation = null;
		for (Long chargerId : chargerIdAry) {
			driverRelation = new ContractCarManRelation(newId, chargerId);
			chargerRelationList.add(driverRelation);
		}
		this.contractDao.saveContractCarManRelation(chargerRelationList);

		// 更新车辆视图的charger列显示责任人姓名
		this.contract4ChargerDao.updateCar4ChargerName(carId);
		// 更新司机视图的charger列显示责任人姓名
		this.contract4ChargerDao.updateCarMan4ChargerName(carId);

		// 返回续签的合同
		return newContract;
	}

	/**
	 * 重发包
	 */
	public Contract4Charger doChaneCharger2(Long carId, Boolean takebackOrigin,
			String assignChargerIds, String assignChargerNames,
			Long contractId, Calendar newStartDate, Calendar newEndDate,
			String code) {
		// 获取原来的合同信息
		Contract4Charger oldContract = this.contract4ChargerDao
				.load(contractId);
		if (oldContract == null)
			throw new CoreException("要处理的合同已不存在！contractId=" + contractId);

		// 复制出新的合同
		Contract4Charger newContract = new Contract4Charger();
		try {
			BeanUtils.copyProperties(oldContract, newContract);
		} catch (Exception e) {
			throw new CoreException("复制合同信息错误！", e);
		}
		newContract.setId(null);

		// 更新旧合同的相关信息
		SystemContext context = SystemContextHolder.get();

		oldContract.setStatus(Contract.STATUS_LOGOUT);// 失效
		oldContract.setMain(Contract.MAIN_HISTORY);// 历史
		oldContract.setLogoutId(context.getUserHistory());// 注销人
		oldContract.setLogoutDate(Calendar.getInstance());// 注销时间
		this.contract4ChargerDao.save(oldContract);

		/*
		 * // 生成新的合同编号
		 * 
		 * String oldContractCode = oldContract.getCode();
		 * if(oldContractCode.lastIndexOf("-") > 0){ //判断旧合同编号是否存在字符"-"
		 * //将字符"-"后的数字+1 int num =
		 * Integer.parseInt(oldContractCode.split("-")[1]); oldContractCode =
		 * oldContractCode.split("-")[0]+"-"+ ++num; }else{ oldContractCode =
		 * oldContractCode+"-1"; } newContract.setCode(oldContractCode);
		 */
		// newContract.setCode(this.idGeneratorService
		// .nextSN4Month(Contract4Labour.KEY_CODE));
		newContract.setCode(code);

		// 设置新的合同期限
		newContract.setStartDate(newStartDate);
		newContract.setEndDate(newEndDate);

		// 设置原件是否已收回
		newContract.setTakebackOrigin(takebackOrigin);

		// 设置冗余显示责任人姓名
		newContract.setExt_str2(assignChargerNames);

		// 设置创建人信息和最后修改人信息
		newContract.setAuthor(context.getUserHistory());
		newContract.setFileDate(Calendar.getInstance());
		newContract.setModifier(context.getUserHistory());
		newContract.setModifiedDate(Calendar.getInstance());

		// 主版本号 加1
		newContract.setVerMajor(oldContract.getVerMajor() + 1);

		// 关联 过户合同与原合同
		newContract.setPid(contractId);

		// 设置签约类型、操作类型、状态、main
		newContract.setSignType("重发包");
		newContract.setOpType(Contract.OPTYPE_CHANGECHARGER2);// 重发包
		newContract.setMain(Contract.MAIN_NOW);// 当前
		newContract.setStatus(Contract.STATUS_NORMAL);// 正常

		// 保存新的合同信息以获取id
		newContract.setUid(this.idGeneratorService
				.next(Contract4Charger.KEY_UID));
		newContract = this.contract4ChargerDao.save(newContract);
		Long newId = newContract.getId();

		// 复制原合同的附件给新的合同
		String oldUid = oldContract.getUid();
		attachService.doCopy(Contract4Charger.KEY_UID, oldUid,
				Contract4Charger.KEY_UID, newContract.getUid(), true);

		// 参数有效性验证
		Assert.notNull(carId);

		// 处理责任人id列表
		Long[] chargerIdAry = StringUtils
				.stringArray2LongArray(assignChargerIds.split(","));

		// 处理与车辆的关联关系
		ContractCarRelation carRelation = new ContractCarRelation(newId, carId);
		this.contractDao.saveContractCarRelation(carRelation);

		// 处理与责任人的关联关系
		List<ContractCarManRelation> chargerRelationList = new ArrayList<ContractCarManRelation>();
		ContractCarManRelation driverRelation = null;
		for (Long chargerId : chargerIdAry) {
			driverRelation = new ContractCarManRelation(newId, chargerId);
			chargerRelationList.add(driverRelation);
		}
		this.contractDao.saveContractCarManRelation(chargerRelationList);

		// 更新车辆视图的charger列显示责任人姓名
		this.contract4ChargerDao.updateCar4ChargerName(carId);
		// 更新司机视图的charger列显示责任人姓名
		this.contract4ChargerDao.updateCarMan4ChargerName(carId);

		// 返回续签的合同
		return newContract;
	}

	/**
	 * 注销
	 */
	public void doLogout(Long contractId, Calendar stopDate) {
		// 获取原来的合同信息
		Contract4Charger contract = this.contract4ChargerDao.load(contractId);
		if (contract == null)
			throw new CoreException("要处理的合同已不存在！contractId=" + contractId);

		// 更新旧合同的相关信息
		contract.setStatus(Contract.STATUS_LOGOUT);// 注销
		// 设置注销人,注销日期
		SystemContext context = SystemContextHolder.get();
		contract.setLogoutId(context.getUserHistory());
		if (stopDate != null) {
			contract.setStopDate(stopDate);
		}
		contract.setLogoutDate(Calendar.getInstance());
		this.contract4ChargerDao.save(contract);
	}

	/**
	 * 复制新合同
	 */
	public Contract4Charger doCopyContract(Long id, int opType, String signType) {
		// 获取原来的合同信息
		Contract4Charger oldContract = this.contract4ChargerDao.load(id);
		if (oldContract == null)
			throw new CoreException("要处理的合同已不存在！contractId=" + id);

		// 复制出新的合同
		Contract4Charger newContract = new Contract4Charger();
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
		attachService.doCopy(Contract4Charger.KEY_UID, oldUid,
				Contract4Charger.KEY_UID, newContract.getUid(), true);

		// 设置创建人,最后更新人的信息
		SystemContext context = SystemContextHolder.get();

		newContract.setAuthor(context.getUserHistory());
		newContract.setModifier(context.getUserHistory());
		newContract.setFileDate(Calendar.getInstance());
		newContract.setModifiedDate(Calendar.getInstance());

		// 设置操作的信息
		newContract.setId(null);
		newContract.setCode("CLHT"
				+ DateUtils.formatDateTime(new Date(), "yyyyMM")); // 自动生成经济合同编号的前缀
		newContract.setSignType(signType);
		newContract.setOpType(opType);
		newContract.setSignDate(null);
		newContract.setStartDate(null);
		newContract.setContractFeeDetail(null);
		newContract.setStatus(BCConstants.STATUS_DRAFT);
		newContract.setVerMajor(newContract.getVerMajor() + 1);// 版本号+1
		if (newContract.getOpType() == Contract.OPTYPE_CHANGECHARGER
				|| newContract.getOpType() == Contract.OPTYPE_CHANGECHARGER2) {
			newContract.setTakebackOrigin(true);
		}
		// 记录旧的保存合同id
		newContract.setPid(id);

		return newContract;
	}

	/**
	 * 操作
	 */
	public Contract4Charger doOperate(Long carId, Contract4Charger e,
			String assignChargerIds, Long contractId, String stopDate) {
		boolean isNew=e.isNew();
		// 获取原来的合同信息
		Contract4Charger oldContract = this.contract4ChargerDao
				.load(contractId);
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
		}
		this.contract4ChargerDao.save(oldContract);

		// 保存新合同信息
		Contract4Charger newContract = e;
		// 如果是保存为草稿的，先将实际结束日期保存在新合同的冗余字段中
		if (e.getStatus() == BCConstants.STATUS_DRAFT) {
			e.setExt_str3(stopDate);
		} else {
			e.setExt_str3(null);
		}
		newContract = this.contract4ChargerDao.save(newContract);
		Long newId = newContract.getId();

		// 参数有效性验证
		Assert.notNull(carId);
		// 如果是新建
		if (isNew) {
			// 处理责任人id列表
			Long[] chargerIdAry = StringUtils
					.stringArray2LongArray(assignChargerIds.split(","));

			// 处理与车辆的关联关系
			ContractCarRelation carRelation = new ContractCarRelation(newId,
					carId);
			this.contractDao.saveContractCarRelation(carRelation);

			// 处理与责任人的关联关系
			List<ContractCarManRelation> chargerRelationList = new ArrayList<ContractCarManRelation>();
			ContractCarManRelation driverRelation = null;
			for (Long chargerId : chargerIdAry) {
				driverRelation = new ContractCarManRelation(newId, chargerId);
				chargerRelationList.add(driverRelation);
			}
			this.contractDao.saveContractCarManRelation(chargerRelationList);
		}
		// 更新车辆视图的charger列显示责任人姓名
		this.contract4ChargerDao.updateCar4ChargerName(carId);
		// 更新司机视图的charger列显示责任人姓名
		this.contract4ChargerDao.updateCarMan4ChargerName(carId);

		// 返回续签的合同
		return newContract;

	}

	@Override
	public void delete(Serializable id) {
		// 删除合同
		this.contract4ChargerDao.delete(id);
	}

	@Override
	public void delete(Serializable[] ids) {
		// 批量合同
		this.contract4ChargerDao.delete(ids);
	}

	/**
	 * 删除单个CarNContract
	 * 
	 * @parma contractId
	 * @return
	 */
	public void deleteCarNContract(Long contractId) {
		if (contractId != null) {
			this.contract4ChargerDao.deleteCarNContract(contractId);
		}
	}

	/**
	 * 删除批量CarNContract
	 * 
	 * @parma contractId
	 * @return
	 */
	public void deleteCarNContract(Long[] contractIds) {
		if (contractIds != null && contractIds.length > 0) {
			this.contract4ChargerDao.deleteCarNContract(contractIds);
		}
	}

	/**
	 * 保存合同与车辆的关联表信息
	 * 
	 * @parma carId
	 * @parma contractId
	 * @return
	 */
	public void carNContract4Save(Long carId, Long contractId) {
		this.contract4ChargerDao.carNContract4Save(carId, contractId);
	}

	/**
	 * 查找车辆合同列表
	 * 
	 * @parma condition
	 * @parma carId
	 * @return
	 */
	public List<Map<String, Object>> list4car(Condition condition, Long carId) {
		return this.contract4ChargerDao.list4car(condition, carId);
	}

	/**
	 * 查找车辆合同分页
	 * 
	 * @parma condition
	 * @parma carId
	 * @return
	 */
	public Page<Map<String, Object>> page4car(Condition condition, int pageNo,
			int pageSize) {
		return this.contract4ChargerDao.page4car(condition, pageNo, pageSize);
	}

	/**
	 * 根据contractId查找car信息
	 * 
	 * @parma contractId
	 * @return
	 */
	public Map<String, Object> findCarInfoByContractId(Long contractId) {
		Map<String, Object> queryMap = null;
		queryMap = this.contract4ChargerDao.findCarInfoByContractId(contractId);
		return queryMap;
	}

	/**
	 * 根据contractId查找责任人信息
	 * 
	 * @parma contractId
	 * @return
	 */
	public List<String> findChargerIdByContractId(Long contractId) {
		List<String> list = new ArrayList<String>();
		list = this.contract4ChargerDao.findChargerIdByContractId(contractId);
		return list;
	}

	/**
	 * 根据责任人ID和合同ID.保存到人员与合同中间表,不存在插入新纪录,存在删除.重新插入
	 * 
	 * @param assignChargerIds
	 * @param contractId
	 */
	public void carMansNContract4Save(String assignChargerIds, Long contractId) {
		this.contract4ChargerDao.carMansNContract4Save(assignChargerIds,
				contractId);
	}

	/**
	 * 根据合同ID查找关联责任人
	 * 
	 * @param contractId
	 * @return
	 */
	public Long findCarIdByContractId(Long contractId) {
		Long carId = null;
		carId = contract4ChargerDao.findCarIdByContractId(contractId);
		return carId;
	}

	/**
	 * 更新车辆表的负责人信息
	 * 
	 * @param assignChargerNames
	 * @param carId
	 */
	public void updateCar4dirverName(String assignChargerNames, Long carId) {
		this.contract4ChargerDao
				.updateCar4dirverName(assignChargerNames, carId);
	}

	/**
	 * 更新司机表的负责人信息
	 * 
	 * @param assignChargerNames
	 * @param carId
	 */
	public void updateCarMan4dirverName(String assignChargerNames, Long carId) {
		this.contract4ChargerDao.updateCarMan4dirverName(assignChargerNames,
				carId);
	}

	/**
	 * 根据车辆ID查找车辆信息
	 * 
	 * @param carId
	 * @return
	 */
	public Map<String, Object> findCarByCarId(Long carId) {
		Map<String, Object> queryMap = null;
		queryMap = this.contract4ChargerDao.findCarByCarId(carId);
		return queryMap;
	}

	/**
	 * 根据司机ID查找车辆信息
	 * 
	 * @param carManId
	 * @return
	 */
	public Map<String, Object> findCarByCarManId(Long carManId) {
		Map<String, Object> queryMap = null;
		queryMap = this.contract4ChargerDao.findCarByCarManId(carManId);
		return queryMap;
	}

	/**
	 * 判断指定的车辆是否已经存在经济合同
	 * 
	 * @param carId
	 * @return
	 */
	public boolean isExistContract(Long carId) {
		return this.contract4ChargerDao.isExistContract(carId);
	}

	/**
	 * 根据合同ID查找关联责任人
	 * 
	 * @param contractId
	 * @return
	 */
	public List<String> findChargerNameByContractId(Long contractId) {
		List<String> list = new ArrayList<String>();
		list = this.contract4ChargerDao.findChargerNameByContractId(contractId);
		return list;
	}

	/**
	 * 判断经济合同自编号唯一
	 * 
	 * @param excludeId
	 * @param code
	 * @return
	 */
	public Long checkCodeIsExist(Long excludeId, String code) {
		return this.contract4ChargerDao.checkCodeIsExist(excludeId, code);
	}

	public Attach doAddAttachFromTemplate(Long id, String templateCode)
			throws IOException {
		// 加载合同
		Contract4Charger c = this.load(id);		

		// 生成格式化参数
		Map<String, Object> params = new HashMap<String, Object>();

		//合同信息
		params.put("code", c.getCode());
		params.put("signDate",new CalendarFormater("dd/MM/yyyy").format(c.getSignDate()));
	
		//责任人信息
		List<Map<String,String>> chargerList=this.contract4ChargerDao.findChargerByContractId(c.getId());
		params.put("carMan",chargerList.get(0).get("name"));
		params.put("FWZGZ",chargerList.get(0).get("certFWZG"));
		params.put("cert4Indentity", chargerList.get(0).get("certIdentity"));
		params.put("phone",chargerList.get(0).get("phone")!=null||chargerList.get(0).get("phone").length()>0?
							chargerList.get(0).get("phone"):chargerList.get(0).get("phone1"));
		params.put("address",chargerList.get(0).get("address"));
		
		if(chargerList.size()>1&&chargerList.get(1)!=null){
			params.put("carMan2", chargerList.get(1).get("name"));
			params.put("FWZGZ2",chargerList.get(1).get("certFWZG"));
			params.put("cert4Indentity2", chargerList.get(1).get("certIdentity"));
			params.put("phone2",chargerList.get(1).get("phone")!=null||chargerList.get(1).get("phone").length()>0?
					chargerList.get(1).get("phone"):chargerList.get(1).get("phone1"));
			params.put("address2",chargerList.get(1).get("address"));
		}else{
			params.put("carMan2", null);
			params.put("FWZGZ2", null);
			params.put("cert4Indentity2", null);
			params.put("phone2", null);
			params.put("address2", null);
		}
		
		//车辆信息
		List<Map<String,String>> carList=this.contract4ChargerDao.findCarByContractId(c.getId());
		params.put("carCode", carList.get(0).get("code"));
		params.put("plate", carList.get(0).get("plateType")+"."+carList.get(0).get("plateNo"));
		params.put("factoryTypeModel", carList.get(0).get("factoryType")+carList.get(0).get("factoryMode"));
		params.put("vin", carList.get(0).get("vin"));
		params.put("engineNo", carList.get(0).get("engineNo"));
		params.put("color", carList.get(0).get("color"));
		//格式化处理行驶证登记日期
		String rDate=carList.get(0).get("registerDate").substring(0,carList.get(0).get("registerDate").indexOf(" "));
		String[] tempArrDate=rDate.split("-");
		params.put("registerDate",
				tempArrDate.length<3?null:tempArrDate[2]+"/"+tempArrDate[1]+"/"+tempArrDate[0]);
		
		//合同开始日期
		Calendar startDate=c.getStartDate();
		Calendar endDate=c.getEndDate();
		int sumMonth=(endDate.get(Calendar.YEAR)-startDate.get(Calendar.YEAR))*12+(endDate.get(Calendar.MONTH)-startDate.get(Calendar.MONTH));
		params.put("sumMonth", sumMonth);
		params.put("sumStartYear",new CalendarFormater("yyyy").format(startDate));
		params.put("sumStartMonth",new CalendarFormater("MM").format(startDate));
		params.put("sumStartDay", new CalendarFormater("dd").format(startDate));
		params.put("sumEndYear", new CalendarFormater("yyyy").format(endDate));
		params.put("sumEndMonth", new CalendarFormater("MM").format(endDate));
		params.put("sumEndDay", new CalendarFormater("dd").format(endDate));
		
		//合同费用明细
		//声明保存每月承包款的集合
		List<ContractFeeDetail> cfdList=new ArrayList<ContractFeeDetail>();
		if(c.getContractFeeDetail()!=null&&c.getContractFeeDetail().size()>0){
			//遍历合同费用明细
			for(ContractFeeDetail cfd:c.getContractFeeDetail()){
				//合同保证金
				if(cfd.getName()!=null&&cfd.getName().equals("合同保证金")){
					params.put("htbzj",cfd.getPrice()!=0?multiDigit2Chinese(String.valueOf(cfd.getPrice())):siginDigit2Chinese(0));
				}else if(cfd.getName()!=null&&cfd.getName().equals("安全互助金")){
					//安全互助金
					params.put("aqhzj",cfd.getPrice()!=0?
							multiDigit2Chinese(String.valueOf(cfd.getPrice()/cfd.getCount())):siginDigit2Chinese(0));
				}else if(cfd.getName()!=null&&cfd.getName().equals("每月承包款")){
					cfdList.add(cfd);
				}	
			}
		}
		
		if(params.get("htbzj")==null){
			//合同保证金
			params.put("htbzj", "　　");
		}
		
		if(params.get("aqhzj")==null){
			//安全互助金
			params.put("aqhzj", "　　");
		}
		
		//处理每月承包费的生成
		if(cfdList.size()>0){
			for(int i=1;i<7&&i<cfdList.size()+1;i++){
				params.put("cfdsy"+i,new CalendarFormater("yyyy").format(cfdList.get(i-1).getStartDate()));
				params.put("cfdsm"+i,new CalendarFormater("MM").format(cfdList.get(i-1).getStartDate()));
				params.put("cfdsd"+i,new CalendarFormater("dd").format(cfdList.get(i-1).getStartDate()));
				params.put("cfdey"+i,new CalendarFormater("yyyy").format(cfdList.get(i-1).getEndDate()));
				params.put("cfdem"+i,new CalendarFormater("MM").format(cfdList.get(i-1).getEndDate()));
				params.put("cfded"+i,new CalendarFormater("dd").format(cfdList.get(i-1).getEndDate()));
				//金额
				String price=String.valueOf(cfdList.get(i-1).getPrice());
				int index=price.indexOf(".");
				if(index>4||index==4){
					params.put("cfdmqian"+i,siginDigit2Chinese(Integer.valueOf(price.substring(index-4, index-3))));
					params.put("cfdmbai"+i, siginDigit2Chinese(Integer.valueOf(price.substring(index-3, index-2))));
					params.put("cfdmshi"+i, siginDigit2Chinese(Integer.valueOf(price.substring(index-2, index-1))));
					params.put("cfdmyuan"+i, siginDigit2Chinese(Integer.valueOf(price.substring(index-1, index-0))));
				}else if(index==3){
					params.put("cfdmqian"+i, "／");
					params.put("cfdmbai"+i, siginDigit2Chinese(Integer.valueOf(price.substring(index-3, index-2))));
					params.put("cfdmshi"+i, siginDigit2Chinese(Integer.valueOf(price.substring(index-2, index-1))));
					params.put("cfdmyuan"+i, siginDigit2Chinese(Integer.valueOf(price.substring(index-1, index-0))));
				}else if(index==2){
					params.put("cfdmqian"+i, "／");
					params.put("cfdmbai"+i, "／");
					params.put("cfdmshi"+i, siginDigit2Chinese(Integer.valueOf(price.substring(index-2, index-1))));
					params.put("cfdmyuan"+i, siginDigit2Chinese(Integer.valueOf(price.substring(index-1, index-0))));
				}else if(index==1){
					params.put("cfdmqian"+i, "／");
					params.put("cfdmbai"+i, "／");
					params.put("cfdmshi"+i, "／");
					params.put("cfdmyuan"+i, siginDigit2Chinese(Integer.valueOf(price.substring(index-1, index-0))));
				}else{
					params.put("cfdmqian"+i, "　");
					params.put("cfdmbai"+i, "　");
					params.put("cfdmshi"+i, "　");
					params.put("cfdmyuan"+i, "　");
				}
			}
			
			
			//每月承包费不足6年时
			if(cfdList.size()<6){
				for(int i=1;i<6-cfdList.size()+1;i++){
					params.put("cfdsy"+(cfdList.size()+i),"　／");
					params.put("cfdsm"+(cfdList.size()+i),"／");
					params.put("cfdsd"+(cfdList.size()+i),"／");
					params.put("cfdey"+(cfdList.size()+i), "　／");
					params.put("cfdem"+(cfdList.size()+i), "／");
					params.put("cfded"+(cfdList.size()+i), "／");
					params.put("cfdmqian"+(cfdList.size()+i), "／");
					params.put("cfdmbai"+(cfdList.size()+i), "／");
					params.put("cfdmshi"+(cfdList.size()+i), "／");
					params.put("cfdmyuan"+(cfdList.size()+i), "／");
				}
			}
			
		}else{
			//承包费
			for(int i=1;i<7;i++){
				params.put("cfdsy"+i,"　　");
				params.put("cfdsm"+i,"　");
				params.put("cfdsd"+i,"　");
				params.put("cfdey"+i, "　");
				params.put("cfdem"+i, "　");
				params.put("cfded"+i, "　");
				params.put("cfdmqian"+i, "　");
				params.put("cfdmbai"+i, "　");
				params.put("cfdmshi"+i, "　");
				params.put("cfdmyuan"+i, "　");
			}
			
		}
		
		// 生成附件
		String ptype = Contract4Charger.ATTACH_TYPE;
		String puid = c.getUid();
		Template template = this.templateService.loadByCode(templateCode);
		if (template == null) {
			logger.warn("模板不存在,返回null:code=" + templateCode);
			return null;
		} else {
			Attach attach = template.format2Attach(params, ptype, puid);
			this.attachService.save(attach);
			return attach;
		}
	}
	
	//多位数字转换为中文繁体
	private String multiDigit2Chinese(String n) {
	        String num1[] = {"零", "壹", "贰", "叁", "肆", "伍", "陆","柒","捌","玖",};
	        String num2[] = {"", "拾", "佰", "仟", "万", "亿", "兆", "吉", "太", "拍", "艾"};
	        n=n.indexOf(".")>0?n.substring(0,n.indexOf(".")):n;
	        
	        int len = n.length();
	        
	        if (len <= 5) {
	            String ret = "";
	            for (int i = 0; i < len; ++i) {
	                if (n.charAt(i) == '0') {
	                    int j = i + 1;
	                    while (j < len && n.charAt(j) == '0') ++j;
	                    if (j < len)
	                        ret += "零";
	                    i = j - 1;
	                } else
	                    ret = ret + num1[n.substring(i, i + 1).charAt(0) - '0'] + num2[len - i - 1];
	            }
	            return ret;
	        } else if (len <= 8) {
	            String ret = multiDigit2Chinese(n.substring(0, len - 4));
	            if (ret.length() != 0)
	                ret += num2[4];
	            return ret + multiDigit2Chinese(n.substring(len - 4));
	        } else {
	            String ret = multiDigit2Chinese(n.substring(0, len - 8));
	            if (ret.length() != 0)
	                ret += num2[5];
	            return ret + multiDigit2Chinese(n.substring(len - 8));
	        }
	 }
	
	 //个位数字转换为中文繁体
	 private String siginDigit2Chinese(Object n) {
		 	String num[] = {"零", "壹", "贰", "叁", "肆", "伍", "陆","柒","捌","玖",};
	        if(n==null)return null;
	        if(n instanceof Integer&&Integer.parseInt(n.toString())<10){
	        	return num[(Integer) n];
	        }else if(n instanceof Long&&Long.parseLong(n.toString())<10){
	        	return num[Integer.parseInt(n.toString())];
	        }else{
	        	return n.toString();
	        }
	 }

	public String json;

	public String doWarehousing(Long contractCarId, String carMansId,
			Contract4Charger contract4Charger) {
		Map<String, Object> carInfoMap; // 车辆Map
		Map<String, Object> carManInfoMap; // 车辆Map
		Json json = new Json();
		boolean success4car = true;
		boolean success4carMan = true;
		String msg = "";
		// 查找车辆的状态和车牌号
		carInfoMap = this.contract4ChargerDao.findCarByCarId(contractCarId);
		if (carInfoMap != null) {
			// 如果车辆状态不为草稿时，不能入库
			if (Long.valueOf(String.valueOf(carInfoMap.get("status_"))) == BCConstants.STATUS_DRAFT) {
				success4car = false;
				msg = carInfoMap.get("plate_type") + "."
						+ carInfoMap.get("plate_no");

			}
		}
		String[] carManIds = carMansId.split(",");
		for (String carManId : carManIds) {
			// 查找责任人的状态和姓名
			carManInfoMap = this.contract4ChargerDao
					.getCarManInfoByCarManId(Long.valueOf(carManId));
			if (carManInfoMap != null) {
				// 如果责任人状态不为草稿时，不能入库
				if (Long.valueOf(String.valueOf(carManInfoMap.get("status_"))) == BCConstants.STATUS_DRAFT) {
					success4carMan = false;
					msg = (String) (msg.length() > 0 || msg != "" ? msg + " 、"
							+ carManInfoMap.get("name") : carManInfoMap
							.get("name"));

				}
			}

		}
		// 如果都为入库状态，则该经济合同可以入库
		if (success4car == true && success4carMan == true) {
			contract4Charger.setStatus(BCConstants.STATUS_ENABLED);
			// 如果pid不为空，则为变更操作新建入库
			if (contract4Charger.getPid() != null) {
				this.doOperate(contractCarId, contract4Charger,
						getChargerIds(contract4Charger.getExt_str2()),
						contract4Charger.getPid(),
						contract4Charger.getExt_str3());
			} else {
				// 无变更操作下新建
				this.save(contract4Charger, contractCarId,
						getChargerIds(contract4Charger.getExt_str2()), null);

			}
			json.put("success", true);
		} else {
			json.put("success", false);
			json.put("msg", "入库失败！请先将  " + msg + " 入库！");
		}
		this.json = json.toString();
		return this.json;
	}

	/**
	 * 获取责任人的id
	 * 
	 * @param chargersStr
	 * @return
	 */
	public String getChargerIds(String chargersStr) {
		String ids = "";
		if (null != chargersStr && chargersStr.trim().length() > 0) {
			String[] chargerAry = chargersStr.split(";");
			for (int i = 0; i < chargerAry.length; i++) {
				if (i > 0) {
					ids = ids + ",";
				}
				ids = ids + chargerAry[i].split(",")[1];

			}

		}

		return ids;
	}

}