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
import org.json.JSONException;
import org.json.JSONObject;
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
import cn.bc.business.socialSecurityRule.service.SocialSecurityRuleService;
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
import cn.bc.template.util.DocxUtils;
import cn.bc.web.formater.NubmerFormater;
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
		}
		// 如果是在案状态
		if (contract4Charger.getStatus() == BCConstants.STATUS_ENABLED) {
			// 更新车辆视图的charger列显示责任人姓名
			this.contract4ChargerDao.updateCar4ChargerName(carId);
			// this.contract4ChargerDao.updateCar4ChargerName(assignChargerNames,carId);
			// 更新司机视图的charger列显示责任人姓名
			this.contract4ChargerDao.updateCarMan4ChargerName(carId);
			// this.contract4ChargerDao.updateCarMan4ChargerName(assignChargerNames,carId);
			// 如果是在案状态就更新车辆的合同性质
			this.contract4ChargerDao.updateCarWithbusinessType(
					contract4Charger.getBusinessType(), carId);
		}

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
		// 设置提前终止方，协议期限为空
		newContract.setQuitterId(null);
		newContract.setAgreementEndDate(null);
		newContract.setAgreementStartDate(null);

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
		boolean isNew = e.isNew();
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
			this.contract4ChargerDao.save(oldContract);
		}

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
		// 如果是在案状态就
		if (newContract.getStatus() == BCConstants.STATUS_ENABLED) {
			// 更新车辆视图的charger列显示责任人姓名
			this.contract4ChargerDao.updateCar4ChargerName(carId);
			// 更新司机视图的charger列显示责任人姓名
			this.contract4ChargerDao.updateCarMan4ChargerName(carId);
			// 更新车辆的合同性质
			this.contract4ChargerDao.updateCarWithbusinessType(
					e.getBusinessType(), carId);

		}

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
	 * 根据车辆Id获取在案的正副班司机数量
	 * 
	 * @parma carId
	 * @return
	 */
	public int getDriverAmount(Long carId) {
		return this.contract4ChargerDao.getDriverAmount(carId);
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

	// 社保
	private SocialSecurityRuleService SocialSecurityRuleService;

	@Autowired
	public void setSocialSecurityRuleService(
			SocialSecurityRuleService socialSecurityRuleService) {
		SocialSecurityRuleService = socialSecurityRuleService;
	}

	public Attach doAddAttachFromTemplate(Long id, String templateCode)
			throws IOException {
		// 加载合同
		Contract4Charger c = this.load(id);

		// 获取模板
		Template template = this.templateService.loadByCode(templateCode);
		if (template == null) {
			logger.warn("模板不存在,返回null:code=" + templateCode);
			return null;
		}

		// 生成格式化参数
		Map<String, Object> params = new HashMap<String, Object>();
		// 财务部收费通知集合，用于填写收费通知单上的值
		List<Map<String, String>> cDetailList = new ArrayList<Map<String, String>>();
		// 汽车修理厂收费通知集合，用于填写收费通知单上的值
		List<Map<String, String>> cDetailFactoryList = new ArrayList<Map<String, String>>();
		// 保存收费通知单上每一条项目的集合
		Map<String, String> cDetailMap = null;

		// -----合同信息----开始--
		params.put("code", c.getCode());
		params.put("signDate",
				DateUtils.formatCalendar(c.getSignDate(), "yyyy-MM-dd"));
		// 缴费日
		params.put(
				"paymentDate",
				c.getPaymentDate() != null && c.getPaymentDate().length() > 0 ? c
						.getPaymentDate().equals("0") ? "月末" : c
						.getPaymentDate() : " ");
		// 合同开始日期
		Calendar startDate = c.getStartDate();
		// 合同结束日期
		Calendar endDate = c.getEndDate();
		// 合同期限内的总月份的数量
		int countMonth = (endDate.get(Calendar.YEAR) - startDate
				.get(Calendar.YEAR))
				* 12
				+ (endDate.get(Calendar.MONTH) - startDate.get(Calendar.MONTH));
		params.put("countMonth", countMonth);
		int sumYear = countMonth / 12;
		// word-docx文档上的特殊处理 合同期限数量中文表达，例如 伍年零伍个月
		String sumDate = "　　";
		if (sumYear > 0) {
			sumDate = multiDigit2Chinese(sumYear + "") + "年";
		} else {
			sumDate = "／年";
		}
		int sumMonth = countMonth % 12;
		if (sumMonth > 0) {
			sumDate += multiDigit2Chinese(sumMonth + "") + "个月";
			params.put("sumMonth", multiDigit2Chinese(sumMonth + ""));
		} else {
			sumDate += siginDigit2Chinese(sumMonth) + "个月";
			params.put("sumMonth", siginDigit2Chinese(sumMonth));
		}
		params.put("sumDate", sumDate);
		params.put("sumStartYear", DateUtils.formatCalendar(startDate, "yyyy"));
		params.put("sumStartMonth", DateUtils.formatCalendar(startDate, "MM"));
		params.put("sumStartDay", DateUtils.formatCalendar(startDate, "dd"));
		params.put("sumEndYear", DateUtils.formatCalendar(endDate, "yyyy"));
		params.put("sumEndMonth", DateUtils.formatCalendar(endDate, "MM"));
		params.put("sumEndDay", DateUtils.formatCalendar(endDate, "dd"));
		// 挂靠合同加载社保信息
		NubmerFormater nf = new NubmerFormater("0.##");
		// 公司unit
		params.put("unitSSRuleBZ",
				nf.format(SocialSecurityRuleService.countNowUnit4GZ("本地城镇")));
		params.put("unitSSRuleWZ",
				nf.format(SocialSecurityRuleService.countNowUnit4GZ("外地城镇")));
		params.put("unitSSRuleBC",
				nf.format(SocialSecurityRuleService.countNowUnit4GZ("本地农村")));
		params.put("unitSSRuleWC",
				nf.format(SocialSecurityRuleService.countNowUnit4GZ("外地农村")));
		// 个人persional
		params.put("psnlSSRuleBZ", nf.format(SocialSecurityRuleService
				.countNowPersonal4GZ("本地城镇")));
		params.put("psnlSSRuleWZ", nf.format(SocialSecurityRuleService
				.countNowPersonal4GZ("外地城镇")));
		params.put("psnlSSRuleBC", nf.format(SocialSecurityRuleService
				.countNowPersonal4GZ("本地农村")));
		params.put("psnlSSRuleWC", nf.format(SocialSecurityRuleService
				.countNowPersonal4GZ("外地农村")));
		// 签约类型
		params.put("signType", c.getSignType());
		// 收费通知单 签约类型对应的手续
		if (c.getSignType().equals("新户")) {
			params.put("signProcedure", "出车");
		} else if (c.getSignType().equals("过户")) {
			params.put("signProcedure", "过户");
		} else if (c.getSignType().equals("续约")) {
			params.put("signProcedure", "续期");
		} else if (c.getSignType().equals("重发包")) {
			params.put("signProcedure", "重发包");
		}

		// 合同性质
		if (c.getBusinessType().equals("承包合同")) {
			params.put("businessType", "承包");
		} else if (c.getBusinessType().equals("合作合同")
				|| c.getBusinessType().equals("合作合同SS")) {
			params.put("businessType", "买断");
		} else if (c.getBusinessType().equals("挂靠合同")) {
			params.put("businessType", "挂靠");
		} else if (c.getBusinessType().equals("中标车")) {
			params.put("businessType", "中标");
		} else {
			params.put("businessType", c.getBusinessType());
		}

		// 履约方名称
		if (c.getQuitterId() != null) {
			params.put("agreementName", this.contract4ChargerDao
					.getCarManInfoByCarManId(c.getQuitterId()).get("name"));
		}
		// 履约方协议期限
		if (c.getAgreementStartDate() != null) {
			params.put("agreementStartDate", DateUtils.formatCalendar(
					c.getAgreementStartDate(), "yyyy年MM月dd日"));
		}
		if (c.getAgreementEndDate() != null) {
			params.put("agreementEndDate", DateUtils.formatCalendar(
					c.getAgreementEndDate(), "yyyy年MM月dd日"));
		}

		// -----合同信息----结束--

		// ---合同费用明细----开始---
		// 声明保存每月承包款的集合
		List<ContractFeeDetail> cfdList = new ArrayList<ContractFeeDetail>();
		if (c.getContractFeeDetail() != null
				&& c.getContractFeeDetail().size() > 0) {
			try {
				// 遍历合同费用明细
				for (ContractFeeDetail cfd : c.getContractFeeDetail()) {
					// 合同费用明细编码，后半部分,如“CC.ORDER.AQHZJ”，则code为“AQHZJ”
					String code = cfd.getCode().substring(
							cfd.getCode().lastIndexOf(".") + 1);
					// 将编码的后半部分作为占位符key，并加入相应的值
					params.put(code, nf.format(cfd.getPrice()));
					// 将编码的后半部分作为占位符key+4cn，并加入相应的中文值
					params.put(code + "4CN", cfd.getPrice() < 1 ? "零"
							: multiDigit2Chinese(cfd.getPrice() + ""));

					// 每月承包款
					if (code.equals("MYCBK")) {
						cfdList.add(cfd);
					}

					// 收费通知单合同费用项目
					cDetailMap = new HashMap<String, String>();
					cDetailMap.put("pg", cfd.getName());
					cDetailMap.put("desc", cfd.getDescription());
					// 声明金额栏
					String price = "";
					// 拼装日期
					if (cfd.getStartDate() != null && cfd.getEndDate() != null) {
						price += DateUtils.formatCalendar(cfd.getStartDate(),
								"yyyy年MM月dd日");
						price += "至";
						price += DateUtils.formatCalendar(cfd.getEndDate(),
								"yyyy年MM月dd日");
					}
					price += nf.format(cfd.getPrice()) + "元";
					// 付款方式
					String strPayTypeDate = "";
					switch (cfd.getPayType()) {
					case ContractFeeDetail.PAY_TYPE_MONTH:
						strPayTypeDate = "/月";
						break;
					case ContractFeeDetail.PAY_TYPE_SEASON:
						strPayTypeDate = "/季";
						break;
					case ContractFeeDetail.PAY_TYPE_YEAR:
						strPayTypeDate = "/年";
						break;
					}

					// 没特殊配置的默认加入财务部收费通知集合
					if (cfd.getSpec() == null || cfd.getSpec().length() == 0) {
						cDetailMap.put("price", price + strPayTypeDate);
						cDetailList.add(cDetailMap);
						continue;
					}

					// 特殊配置
					JSONObject jo = new JSONObject(cfd.getSpec());
					if (jo.length() == 0) {
						cDetailMap.put("price", price + strPayTypeDate);
						cDetailList.add(cDetailMap);
						continue;
					}

					// 是否加入每人
					String strTemp = "";
					if (!jo.isNull("isByDriver") && jo.getBoolean("isByDriver"))
						strTemp = "/人";

					cDetailMap.put("price", price + strTemp + strPayTypeDate);

					// 财务部收费通知集合
					if (jo.isNull("isByFinancial")
							|| jo.getBoolean("isByFinancial")) {
						cDetailList.add(cDetailMap);
					}

					// 维修厂收费通知集合
					if (!jo.isNull("isByGarage") && jo.getBoolean("isByGarage")) {
						cDetailFactoryList.add(cDetailMap);
					}
				}

			} catch (JSONException e) {
				logger.error(e.getMessage(), e);
				try {
					throw e;
				} catch (JSONException e1) {
					e1.printStackTrace();
				}
				;
			}
		}

		// word-docx文档上的特殊处理，处理每月承包费的生成
		if (cfdList.size() > 0) {
			for (int i = 1; i < cfdList.size() + 1; i++) {
				params.put("cfdsy" + i, DateUtils.formatCalendar(
						cfdList.get(i - 1).getStartDate(), "yyyy"));
				params.put("cfdsm" + i, DateUtils.formatCalendar(
						cfdList.get(i - 1).getStartDate(), "MM"));
				params.put("cfdsd" + i, DateUtils.formatCalendar(
						cfdList.get(i - 1).getStartDate(), "dd"));
				params.put("cfdey" + i, DateUtils.formatCalendar(
						cfdList.get(i - 1).getEndDate(), "yyyy"));
				params.put("cfdem" + i, DateUtils.formatCalendar(
						cfdList.get(i - 1).getEndDate(), "MM"));
				params.put("cfded" + i, DateUtils.formatCalendar(
						cfdList.get(i - 1).getEndDate(), "dd"));
				// word文档 金额
				params.put("mycbf" + i, multiDigit2Chinese(String
						.valueOf(cfdList.get(i - 1).getPrice())));
			}
			// 每月承包费不足6年时
			if (cfdList.size() < 6) {
				for (int i = 1; i < 6 - cfdList.size() + 1; i++) {
					params.put("cfdsy" + (cfdList.size() + i), "　／");
					params.put("cfdsm" + (cfdList.size() + i), "／");
					params.put("cfdsd" + (cfdList.size() + i), "／");
					params.put("cfdey" + (cfdList.size() + i), "　／");
					params.put("cfdem" + (cfdList.size() + i), "／");
					params.put("cfded" + (cfdList.size() + i), "／");
					params.put("mycbf" + (cfdList.size() + i), "／");
				}
			}
		}
		// ---合同费用明细----结束---

		// -----责任人信息---开始--
		List<Map<String, String>> chargerList = this.contract4ChargerDao
				.findChargerByContractId(c.getId());
		// 循环变量
		int chargerI = 1;
		String chargers = "  ";
		for (Map<String, String> chargerMap : chargerList) {
			if (chargerI == 1) {
				chargers = chargerMap.get("name");
				params.put("charger", chargerMap.get("name"));
				params.put("FWZGZ", chargerMap.get("certFWZG"));
				params.put("cert4Indentity", chargerMap.get("certIdentity"));
				params.put(
						"phone",
						chargerMap.get("phone") != null
								|| chargerMap.get("phone").length() > 0 ? chargerMap
								.get("phone") : chargerMap.get("phone1"));
				params.put("address", chargerList.get(0).get("address"));
			} else {
				chargers += "、" + chargerMap.get("name");
				params.put("charger" + chargerI, chargerMap.get("name"));
				params.put("FWZGZ" + chargerI, chargerMap.get("certFWZG"));
				params.put("cert4Indentity" + chargerI,
						chargerMap.get("certIdentity"));
				params.put(
						"phone" + chargerI,
						chargerMap.get("phone") != null
								|| chargerMap.get("phone").length() > 0 ? chargerMap
								.get("phone") : chargerMap.get("phone1"));
				params.put("address" + chargerI, chargerMap.get("address"));
			}
			chargerI++;
		}
		params.put("chargers", chargers);
		// -----责任人信息---结束--

		// ------车辆信息-----开始--
		List<Map<String, String>> carList = this.contract4ChargerDao
				.findCarByContractId(c.getId());
		// 公司
		String company = null;
		if (carList.get(0).get("company").equals("宝城")) {
			company = "广州市宝城汽车出租有限公司";
		} else if (carList.get(0).get("company").equals("广发")) {
			company = "广州市广发出租汽车有限公司";
		}
		params.put("company", company);
		params.put("company4Short", carList.get(0).get("company"));
		params.put("carCode", carList.get(0).get("code"));
		params.put("plateType", carList.get(0).get("plateType"));
		params.put("plateNo", carList.get(0).get("plateNo"));
		params.put("plate", carList.get(0).get("plateType") + "."
				+ carList.get(0).get("plateNo"));
		params.put("factoryTypeModel", carList.get(0).get("factoryType")
				+ carList.get(0).get("factoryMode"));
		params.put("factoryType", carList.get(0).get("factoryType"));
		params.put("factoryModel", carList.get(0).get("factoryMode"));
		params.put("vin", carList.get(0).get("vin"));
		params.put("engineNo", carList.get(0).get("engineNo"));
		params.put("color", carList.get(0).get("color"));
		// 经营权证
		params.put("certNo2", carList.get(0).get("certNo2"));
		// 行驶证登记日期
		params.put("registerDate", carList.get(0).get("registerDate"));
		// 营运证号：道路运输证号
		params.put("certNo4", carList.get(0).get("certNo4"));
		// 车队
		params.put("motorcade", carList.get(0).get("motorcade"));
		// ------车辆信息-----结束--

		// ------正副班司机信息-----开始--
		List<Map<String, String>> driverList = this.contract4ChargerDao
				.findDriverByContractId(c.getId());
		// 循环变量
		int driverI = 1;
		String drivers = "  ";
		for (Map<String, String> driverMap : driverList) {
			if (driverI == 1) {
				drivers = driverMap.get("name");
				params.put("driver", driverMap.get("name"));
				params.put("dFWZGZ", driverMap.get("certFWZG"));
				params.put("dCert4Indentity", driverMap.get("certIdentity"));
				params.put(
						"dPhone",
						driverMap.get("phone") != null
								|| driverMap.get("phone").length() > 0 ? driverMap
								.get("phone") : driverMap.get("phone1"));
				params.put("dAddress", driverMap.get("address"));
			} else {
				drivers += "、" + driverMap.get("name");
				params.put("driver" + driverI, driverMap.get("name"));
				params.put("dFWZGZ" + driverI, driverMap.get("certFWZG"));
				params.put("dCert4Indentity" + driverI,
						driverMap.get("certIdentity"));
				params.put(
						"dPhone" + driverI,
						driverMap.get("phone") != null
								|| driverMap.get("phone").length() > 0 ? driverMap
								.get("phone") : driverMap.get("phone1"));
				params.put("dAddress" + driverI, driverMap.get("address"));
			}
			driverI++;

			if (driverMap.get("houseType") != null
					&& driverMap.get("houseType").length() > 0) {
				// 收费通知单社保项目
				Float unit = this.SocialSecurityRuleService
						.countNowUnit4GZ(driverMap.get("houseType"));
				Float personal = this.SocialSecurityRuleService
						.countNowPersonal4GZ(driverMap.get("houseType"));
				cDetailMap = new HashMap<String, String>();
				cDetailMap.put("pg", "社保：" + driverMap.get("name"));
				if (driverMap.get("joinDate") != null
						&& driverMap.get("joinDate").length() > 0) {
					String[] dateArr = driverMap.get("joinDate").split("-");
					cDetailMap.put("price", "按实际社保数实收：" + unit + "+" + personal
							+ "=" + (unit + personal) + "元," + dateArr[0] + "年"
							+ dateArr[1] + "月起保");
				} else {
					cDetailMap.put("price", "按实际社保数实收：" + unit + "+" + personal
							+ "=" + (unit + personal) + "元");
				}
				cDetailMap.put("desc", "身份证：" + driverMap.get("certIdentity"));
				cDetailList.add(cDetailMap);
			}
		}
		params.put("drivers", drivers);
		// ------正班司机信息-----结束--

		// word 2007 文档处理
		if (template.getTemplateType().getCode().equals("word-docx")) {
			// 获取文件中的${XXXX}占位标记的键名列表
			List<String> markers = DocxUtils.findMarkers(template
					.getInputStream());
			// 占位符列表与参数列表匹配,当占位符列表值没出现在参数列表key值时，增加此key值
			for (String key : markers) {
				if (!params.containsKey(key))
					params.put(key, "　　");
			}
		}

		// Excel 97-2003 工作薄文档处理
		if (template.getTemplateType().getCode().equals("xls")) {
			params.put("FinancialFeeMsg", cDetailList);
			params.put("GarageFeeMsg", cDetailFactoryList);
		}

		// 生成附件
		String ptype = Contract4Charger.ATTACH_TYPE;
		String puid = c.getUid();
		Attach attach = template.format2Attach(params, ptype, puid);
		this.attachService.save(attach);
		return attach;

	}

	// 多位数字转换为中文繁体
	private String multiDigit2Chinese(String n) {
		String num1[] = { "零", "壹", "贰", "叁", "肆", "伍", "陆", "柒", "捌", "玖", };
		String num2[] = { "", "拾", "佰", "仟", "万", "亿", "兆", "吉", "太", "拍", "艾" };
		n = n.indexOf(".") > 0 ? n.substring(0, n.indexOf(".")) : n;

		int len = n.length();

		if (len <= 5) {
			String ret = "";
			for (int i = 0; i < len; ++i) {
				if (n.charAt(i) == '0') {
					int j = i + 1;
					while (j < len && n.charAt(j) == '0')
						++j;
					if (j < len)
						ret += "零";
					i = j - 1;
				} else
					ret = ret + num1[n.substring(i, i + 1).charAt(0) - '0']
							+ num2[len - i - 1];
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

	// 个位数字转换为中文繁体
	private String siginDigit2Chinese(Object n) {

		String num[] = { "零", "壹", "贰", "叁", "肆", "　伍", "陆", "柒", "捌", "玖", };
		if (n == null)
			return null;
		if (n instanceof Integer && Integer.parseInt(n.toString()) < 10) {
			return num[(Integer) n];
		} else if (n instanceof Long && Long.parseLong(n.toString()) < 10) {
			return num[Integer.parseInt(n.toString())];
		} else {
			return n.toString();
		}
	}

	public String json;

	public String doWarehousing(Long contractCarId, String carMansId,
			Contract4Charger contract4Charger, Long draftCarId,
			String draftCarManId) {
		Json json = new Json();
		// 如果存在草稿车辆，将草稿车辆的状态更新为在案
		if (draftCarId != null) {
			this.contract4ChargerDao.doWarehous4Car(draftCarId);
		}
		// 如果存在草稿司机，将草稿司机的状态更新为在案
		if (draftCarManId != null) {
			String[] carManIds = carMansId.split(",");
			for (String carManId : carManIds) {
				this.contract4ChargerDao.doWarehous4CarMan(Long
						.valueOf(carManId));
			}
		}

		// 经济合同入库
		contract4Charger.setStatus(BCConstants.STATUS_ENABLED);
		// 如果pid不为空，则为变更操作新建入库
		if (contract4Charger.getPid() != null) {
			this.doOperate(contractCarId, contract4Charger,
					getChargerIds(contract4Charger.getExt_str2()),
					contract4Charger.getPid(), contract4Charger.getExt_str3());

		} else {
			// 无变更操作下新建
			this.save(contract4Charger, contractCarId,
					getChargerIds(contract4Charger.getExt_str2()), null);

		}

		json.put("success", true);

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

	public String checkDriverOrCarStatus(Long carId, String carMansId) {
		Map<String, Object> carInfoMap; // 车辆Map
		Map<String, Object> carManInfoMap; // 车辆Map
		Json json = new Json();
		boolean success4car = true;
		boolean success4carMan = true;
		String msg = "";
		String draftCarId = "";
		String drafDriverId = "";
		// 查找车辆的状态和车牌号
		carInfoMap = this.contract4ChargerDao.findCarByCarId(carId);
		if (carInfoMap != null) {
			// 如果车辆状态不为草稿时，不能入库
			if (Long.valueOf(String.valueOf(carInfoMap.get("status_"))) == BCConstants.STATUS_DRAFT) {
				success4car = false;
				msg = "车辆 " + carInfoMap.get("plate_type") + "."
						+ carInfoMap.get("plate_no");
				draftCarId = String.valueOf(carId);
			}
		}
		String[] carManIds = carMansId.split(",");
		int i = 0;
		for (String carManId : carManIds) {
			// 查找责任人的状态和姓名
			carManInfoMap = this.contract4ChargerDao
					.getCarManInfoByCarManId(Long.valueOf(carManId));
			if (carManInfoMap != null) {
				// 如果责任人状态不为草稿时，不能入库
				if (Long.valueOf(String.valueOf(carManInfoMap.get("status_"))) == BCConstants.STATUS_DRAFT) {
					success4carMan = false;
					msg = (String) (msg.length() > 0 || msg != "" ? msg + " 、"
							+ "司机 " + carManInfoMap.get("name") : "司机 "
							+ carManInfoMap.get("name"));
					if (i > 0) {
						drafDriverId += "," + carManId;
					} else {
						drafDriverId = carManId;
					}
					i++;
				}
			}

		}
		// 如果都为入库状态，则该经济合同可以入库
		if (success4car == true && success4carMan == true) {
			json.put("success", true);
		} else {
			json.put("success", false);
			json.put("msg", msg + " 尚未入库！是否将 " + msg + " 同时入库？");
			// 如果车辆或司机Id不为空就写入Id
			if (draftCarId.length() > 0) {
				json.put("carId", draftCarId);
			}
			if (drafDriverId.length() > 0) {
				json.put("carManId", drafDriverId);
			}
		}
		this.json = json.toString();
		return this.json;
	}

}