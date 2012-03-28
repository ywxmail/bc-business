package cn.bc.business.ownership.service;

import cn.bc.business.ownership.dao.OwnershipDao;
import cn.bc.business.ownership.domain.Ownership;
import cn.bc.core.service.DefaultCrudService;

/**
 * @author zxr 车辆经营权serviec实现类
 * 
 */
public class OwnershipServiceImpl extends DefaultCrudService<Ownership>
		implements OwnershipService {
	private OwnershipDao ownershipDao;

	public void setOwnershipDao(OwnershipDao ownershipDao) {
		this.ownershipDao = ownershipDao;
		this.setCrudDao(ownershipDao);
	}

	public OwnershipDao getOwnershipDao() {
		return ownershipDao;
	}

}
