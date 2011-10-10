/**
 * 
 */
package cn.bc.business.runcase.dao.hibernate.jpa;

import java.io.Serializable;

import cn.bc.business.runcase.dao.CaseAdviceDao;
import cn.bc.business.runcase.domain.Case4Advice;
import cn.bc.orm.hibernate.jpa.HibernateCrudJpaDao;


/**
 * 营运投诉与建议Dao的hibernate jpa实现
 * 
 * @author dragon
 */
public class CaseAdviceDaoImpl extends HibernateCrudJpaDao<Case4Advice> implements CaseAdviceDao{

	/**
	 *  author	   : wis.ho
	 *  update date: 2011-9-26
	 *  description: 因为Jap方法不能实现级联删除,所以重写delete方法.
	 */
	
	//单个物理删除
	@Override
	public void delete(Serializable id) {
		if (id == null)
			return;

		Case4Advice e = this.getJpaTemplate().find(Case4Advice.class, id);
		if (e != null)
			this.getJpaTemplate().remove(e);
	}

	//批量物理删除
	@Override
	public void delete(Serializable[] ids) {
		if (ids == null || ids.length == 0)
			return;

		for (Serializable pk : ids) {
			Case4Advice e = this.getJpaTemplate().find(Case4Advice.class, pk);
			if (e != null)
				this.getJpaTemplate().remove(e);
		}
	}
}