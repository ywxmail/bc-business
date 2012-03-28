package cn.bc.business.ownership.dao.hibernate.jpa;

import cn.bc.business.ownership.dao.OwnershipDao;
import cn.bc.business.ownership.domain.Ownership;
import cn.bc.orm.hibernate.jpa.HibernateCrudJpaDao;

/**
 * @author zxr 车辆经营权dao实现类
 * 
 */
public class OwnershipDaoImpl extends HibernateCrudJpaDao<Ownership> implements
		OwnershipDao {
}
