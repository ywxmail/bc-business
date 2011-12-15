package cn.bc.business.arrange.dao.hibernate.jpa;

import cn.bc.business.arrange.dao.SignRecordDao;
import cn.bc.business.arrange.domain.SignRecord;
import cn.bc.orm.hibernate.jpa.HibernateCrudJpaDao;

/**
 * 签到记录Dao的实现
 * 
 * @author dragon
 * 
 */
public class SignRecordDaoImpl extends HibernateCrudJpaDao<SignRecord>
		implements SignRecordDao {
	// private static Log logger = LogFactory.getLog(ArrangeDaoImpl.class);
}
