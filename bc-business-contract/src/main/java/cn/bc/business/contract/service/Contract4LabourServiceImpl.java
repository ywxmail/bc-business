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

import cn.bc.business.contract.dao.Contract4LabourDao;
import cn.bc.business.contract.dao.ContractDao;
import cn.bc.business.contract.domain.Contract;
import cn.bc.business.contract.domain.Contract4Labour;
import cn.bc.business.contract.domain.ContractCarManRelation;
import cn.bc.business.contract.domain.ContractCarRelation;
import cn.bc.core.Page;
import cn.bc.core.exception.CoreException;
import cn.bc.core.query.condition.Condition;
import cn.bc.core.service.DefaultCrudService;
import cn.bc.docs.service.AttachService;
import cn.bc.identity.service.IdGeneratorService;

/**
 * 司机劳动合同Service的实现
 * 
 * @author dragon
 */
public class Contract4LabourServiceImpl extends
		DefaultCrudService<Contract4Labour> implements Contract4LabourService {
	private Contract4LabourDao contract4LabourDao;
	private ContractDao contractDao;
	private IdGeneratorService idGeneratorService;// 用于生成uid的服务
	private AttachService attachService;// 附件服务

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

	public Contract4Labour doRenew(Long contractId, Calendar newStartDate,
			Calendar newEndDate) {
		// 获取原来的合同信息
		Contract4Labour oldContract = this.contract4LabourDao.load(contractId);
		if (oldContract == null)
			throw new CoreException("要处理的合同已不存在！contractId=" + contractId);

		// 更新旧合同的相关信息
		oldContract.setStatus(Contract.STATUS_FAILURE);// 失效
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
		newContract.setCode(this.idGeneratorService
				.nextSN4Month(Contract4Labour.KEY_CODE));

		// 设置新的合同期限
		newContract.setStartDate(newStartDate);
		newContract.setEndDate(newEndDate);

		// TODO 设置最后修改人信息：从当前线程变量中获取

		// 主版本号 加1
		newContract.setVerMajor(oldContract.getVerMajor() + 1);

		// 关联 续签合同与原合同
		newContract.setPid(contractId);

		// 设置操作类型、状态、main
		newContract.setOpType(Contract.OPTYPE_RENEW);// 续签
		newContract.setMain(Contract.MAIN_NOW);// 当前
		newContract.setStatus(Contract.STATUS_NORMAL);// 正常

		// 保存新的合同信息以获取id
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
		String oldUid = newContract.getUid();
		newContract.setUid(this.idGeneratorService
				.next(Contract4Labour.KEY_UID));
		attachService.doCopy(Contract4Labour.KEY_UID, oldUid,
				Contract4Labour.KEY_UID, newContract.getUid(), true);

		// 返回续签的合同
		return newContract;
	}

	public void doResign(Long contractId, Calendar resignDate) {
		throw new CoreException("need implement");
	}

	public Contract4Labour doChangeCar(Long contractId, Long newCarId) {
		throw new CoreException("need implement");
	}

	@Override
	public void delete(Serializable id) {
		// 删除合同
		this.contract4LabourDao.delete(id);
	}

	@Override
	public void delete(Serializable[] ids) {
		// 批量合同
		this.contract4LabourDao.delete(ids);
	}

	/**
	 * 删除单个CarManNContract
	 * 
	 * @parma contractId
	 * @return
	 */
	public void deleteCarManNContract(Long contractId) {
		if (contractId != null) {
			contract4LabourDao.deleteCarManNContract(contractId);
		}
	}

	/**
	 * 删除批量CarManNContract
	 * 
	 * @parma contractIds[]
	 * @return
	 */
	public void deleteCarManNContract(Long[] contractIds) {
		if (contractIds != null && contractIds.length > 0) {
			this.contract4LabourDao.deleteCarManNContract(contractIds);
		}
	}

	/**
	 * 保存合同与司机的关联表信息
	 * 
	 * @parma carManId
	 * @parma contractId
	 * @return
	 */
	public void carManNContract4Save(Long carManId, Long contractId) {
		this.contract4LabourDao.carManNContract4Save(carManId, contractId);
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
	 * 保存车辆与合同的关联信息 jdbc查询BS_CAR_CONTRACT表是否存在相应carId和contractId的记录
	 * 
	 * @parma carId
	 * @parma contractId
	 * @return
	 */
	public void carNContract4Save(Long carId, Long contractId) {
		this.contract4LabourDao.carNContract4Save(carId, contractId);
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

	/**
	 * 根据司机ID查找关联的司机否存在劳动合同
	 * 
	 * @parma carManId
	 * @return
	 */
	public List<Map<String, Object>> findCarManIsExistContract(Long carManId) {
		List<Map<String, Object>> list = null;
		list = this.contract4LabourDao.findCarManIsExistContract(carManId);
		return list;
	}

	/**
	 * 删除单个Injury
	 * 
	 * @parma contractId
	 * @return
	 */
	public void deleteInjury(Long contractId) {
		if (contractId != null) {
			contract4LabourDao.deleteInjury(contractId);
		}
	}

	/**
	 * 删除批量Injury
	 * 
	 * @parma contractIds[]
	 * @return
	 */
	public void deleteInjury(Long[] contractIds) {
		if (contractIds != null && contractIds.length > 0) {
			this.contract4LabourDao.deleteInjury(contractIds);
		}
	}

}