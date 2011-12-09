/**
 * 
 */
package cn.bc.business.runcase.dao.hibernate.jpa;

import cn.bc.business.runcase.dao.CaseBaseDao;
import cn.bc.business.runcase.domain.CaseBase;
import cn.bc.orm.hibernate.jpa.HibernateCrudJpaDao;


/**
 * 营运事件Dao的hibernate jpa实现
 * 
 * @author dragon
 */
public class CaseBaseDaoImpl extends HibernateCrudJpaDao<CaseBase> implements CaseBaseDao{
	
	public CaseBase findCaseBaseBysyncId(Long syncId) {
		String hql = "from CaseBase c where c.syncId=? ";
		return (CaseBase) this.getJpaTemplate().find(hql, new Object[] { syncId}).get(0);
	}

}