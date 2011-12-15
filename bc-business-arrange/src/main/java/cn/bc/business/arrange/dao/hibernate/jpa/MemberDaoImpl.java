package cn.bc.business.arrange.dao.hibernate.jpa;

import cn.bc.business.arrange.dao.MemberDao;
import cn.bc.business.arrange.domain.Member;
import cn.bc.orm.hibernate.jpa.HibernateCrudJpaDao;

/**
 * 考勤成员Dao的实现
 * 
 * @author dragon
 * 
 */
public class MemberDaoImpl extends HibernateCrudJpaDao<Member> implements
		MemberDao {
	// private static Log logger = LogFactory.getLog(ArrangeDaoImpl.class);
}
