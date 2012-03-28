/**
 * 
 */
package cn.bc.business.contract.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import cn.bc.business.contract.dao.Contract4ChargerDao;
import cn.bc.business.contract.dao.ContractDao;
import cn.bc.business.contract.domain.Contract;
import cn.bc.business.contract.domain.Contract4Charger;
import cn.bc.business.contract.domain.ContractCarManRelation;
import cn.bc.business.contract.domain.ContractCarRelation;
import cn.bc.core.Page;
import cn.bc.core.exception.CoreException;
import cn.bc.core.query.condition.Condition;
import cn.bc.core.service.DefaultCrudService;
import cn.bc.core.util.StringUtils;
import cn.bc.docs.service.AttachService;
import cn.bc.identity.service.IdGeneratorService;
import cn.bc.identity.web.SystemContext;
import cn.bc.identity.web.SystemContextHolder;

/**
 * 责任人合同Service的实现
 * 
 * @author dragon
 */
public class Contract4ChargerServiceImpl extends
		DefaultCrudService<Contract4Charger> implements Contract4ChargerService {
	private Contract4ChargerDao contract4ChargerDao;
	private ContractDao contractDao;
	private IdGeneratorService idGeneratorService;// 用于生成uid的服务
	private AttachService attachService;// 附件服务

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

		SystemContext context = SystemContextHolder.get();
		
		// 更新旧合同的相关信息
		oldContract.setStatus(Contract.STATUS_LOGOUT);// 失效
		oldContract.setMain(Contract.MAIN_HISTORY);// 历史
		oldContract.setLogoutId(context.getUserHistory());//注销人
		oldContract.setLogoutDate(Calendar.getInstance());//注销时间
		this.contract4ChargerDao.save(oldContract);

		// 复制出新的合同
		Contract4Charger newContract = new Contract4Charger();
		try {
			BeanUtils.copyProperties(oldContract, newContract);
		} catch (Exception e) {
			throw new CoreException("复制合同信息错误！", e);
		}
		newContract.setId(null);

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

		SystemContext context = SystemContextHolder.get();
		// 更新旧合同的相关信息
		oldContract.setStatus(Contract.STATUS_LOGOUT);// 失效
		oldContract.setMain(Contract.MAIN_HISTORY);// 历史
		oldContract.setLogoutId(context.getUserHistory());//注销人
		oldContract.setLogoutDate(Calendar.getInstance());//注销时间
		this.contract4ChargerDao.save(oldContract);

		// 复制出新的合同
		Contract4Charger newContract = new Contract4Charger();
		try {
			BeanUtils.copyProperties(oldContract, newContract);
		} catch (Exception e) {
			throw new CoreException("复制合同信息错误！", e);
		}
		newContract.setId(null);

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

		SystemContext context = SystemContextHolder.get();
		// 更新旧合同的相关信息
		oldContract.setStatus(Contract.STATUS_LOGOUT);// 失效
		oldContract.setMain(Contract.MAIN_HISTORY);// 历史
		oldContract.setLogoutId(context.getUserHistory());//注销人
		oldContract.setLogoutDate(Calendar.getInstance());//注销时间
		this.contract4ChargerDao.save(oldContract);

		// 复制出新的合同
		Contract4Charger newContract = new Contract4Charger();
		try {
			BeanUtils.copyProperties(oldContract, newContract);
		} catch (Exception e) {
			throw new CoreException("复制合同信息错误！", e);
		}
		newContract.setId(null);

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
	public void doLogout(Long contractId, Calendar logoutDate) {
		// 获取原来的合同信息
		Contract4Charger contract = this.contract4ChargerDao.load(contractId);
		if (contract == null)
			throw new CoreException("要处理的合同已不存在！contractId=" + contractId);

		// 更新旧合同的相关信息
		contract.setStatus(Contract.STATUS_LOGOUT);// 注销
		// 设置注销人,注销日期
		SystemContext context = SystemContextHolder.get();
		contract.setLogoutId(context.getUserHistory());
		contract.setLogoutDate(logoutDate != null ? logoutDate : Calendar
				.getInstance());
		this.contract4ChargerDao.save(contract);
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

}