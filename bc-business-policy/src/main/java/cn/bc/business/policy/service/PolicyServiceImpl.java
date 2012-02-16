/**
 * 
 */
package cn.bc.business.policy.service;

import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import cn.bc.business.policy.dao.PolicyDao;
import cn.bc.business.policy.domain.BuyPlant;
import cn.bc.business.policy.domain.Policy;
import cn.bc.core.exception.CoreException;
import cn.bc.core.service.DefaultCrudService;
import cn.bc.docs.service.AttachService;
import cn.bc.identity.service.IdGeneratorService;
import cn.bc.identity.web.SystemContext;
import cn.bc.identity.web.SystemContextHolder;

/**
 * 车辆保单Service的实现
 * 
 * @author dragon
 */
public class PolicyServiceImpl extends DefaultCrudService<Policy> implements
		PolicyService {
	private PolicyDao policyDao;
	private IdGeneratorService idGeneratorService;// 用于生成uid的服务
	private AttachService attachService;// 附件服务

	public PolicyDao getPolicyDao() {
		return policyDao;
	}

	public void setPolicyDao(PolicyDao policyDao) {
		this.policyDao = policyDao;
		this.setCrudDao(policyDao);
	}

	@Autowired
	public void setIdGeneratorService(IdGeneratorService idGeneratorService) {
		this.idGeneratorService = idGeneratorService;
	}

	@Autowired
	public void setAttachService(AttachService attachService) {
		this.attachService = attachService;
	}

	// 续保
	public Policy doRenew(Long policyId) {
		// 获取原来的车保信息
		Policy oldPolicy = this.policyDao.load(policyId);
		if (oldPolicy == null)
			throw new CoreException("要处理的车保已不存在！policyId=" + policyId);

		// 更新旧车保的相关信息
		// oldPolicy.setStatus(BCConstants.STATUS_DISABLED);// 失效
		// oldPolicy.setMain(Policy.MAIN_HISTORY);// 历史
		// this.policyDao.save(oldPolicy);

		// 复制出新的车保
		Policy newPolicy = new Policy();
		try {
			BeanUtils.copyProperties(oldPolicy, newPolicy);
		} catch (Exception e) {
			throw new CoreException("复制车保信息错误！", e);
		}
		newPolicy.setId(null);

		// 复制承保险种
		Set<BuyPlant> s = new HashSet<BuyPlant>();
		BuyPlant newBP;
		for (BuyPlant oldBP : oldPolicy.getBuyPlants()) {
			newBP = new BuyPlant();
			BeanUtils.copyProperties(oldBP, newBP);
			newBP.setPolicy(newPolicy);
			newBP.setId(null);
			s.add(newBP);
		}
		newPolicy.setBuyPlants(s);

		// 设置最后修改人信息
		SystemContext context = SystemContextHolder.get();
		newPolicy.setModifier(context.getUserHistory());
		newPolicy.setModifiedDate(Calendar.getInstance());

		// 主版本号 加1
		newPolicy.setVerMajor(oldPolicy.getVerMajor() + 1);

		// 关联续保车保与原车保
		newPolicy.setPid(policyId);

		// 设置操作类型、状态、main
		newPolicy.setOpType(Policy.OPTYPE_RENEWAL);// 续签
		newPolicy.setMain(Policy.MAIN_NOW);// 当前
		newPolicy.setStatus(Policy.STATUS_ENABLED);// 正常

		// 保存新的车保信息以获取id
		newPolicy.setUid(this.idGeneratorService.next(Policy.KEY_UID));

		// 复制原合同的附件给新的合同
		String oldUid = newPolicy.getUid();
		attachService.doCopy(Policy.KEY_UID, oldUid, Policy.KEY_UID,
				newPolicy.getUid(), true);

		// newPolicy = this.policyDao.save(newPolicy);

		// 返回续保的保单
		return newPolicy;

	}

	public void doSurrender(Long policyId, Calendar surrenderDate) {
		// 获取车保信息
		Policy policy = this.policyDao.load(policyId);
		// 设置停保日期
		policy.setStopDate(surrenderDate);
		// 设置操作类型为停保
		policy.setOpType(Policy.OPTYPE_SURRENDERS);
		// 设置状态为停保
		policy.setStatus(Policy.STATUS_SURRENDER);
		this.policyDao.save(policy);

	}
	
	//注销
	public void doLogout(Long policyId){
		// 获取车保信息
		Policy policy = this.policyDao.load(policyId);
		// 设置注销日期
		policy.setLogoutDate(Calendar.getInstance());
		SystemContext context = SystemContextHolder.get();
		//设置注销人
		policy.setLogout(context.getUserHistory());
		// 设置状态为注销
		policy.setStatus(Policy.STATUS_DISABLED);
		this.policyDao.save(policy);

		
	}

	@Override
	public Policy save(Policy entity) {
		// 获取旧车保的ID
		Long oldPolicyId = entity.getPid();
		Calendar surrenderDate = entity.getStopDate();
		if (oldPolicyId != null && surrenderDate == null) {
			// 停保日期为空，Pid不为空时才执行更改原车保的信息
			Policy oldPolicy = this.policyDao.load(oldPolicyId);
			// 将原车保的状态设为失效
			oldPolicy.setStatus(Policy.STATUS_DISABLED);// 注销
			// 将原车保的当前版本设为历史
			oldPolicy.setMain(Policy.MAIN_HISTORY);// 历史
			this.policyDao.save(oldPolicy);
		}
		return super.save(entity);
	}


	public List<Policy> getPolicise(Long carId, Calendar happenTime) {	
		return this.policyDao.getPolicise(carId, happenTime);
	}

}