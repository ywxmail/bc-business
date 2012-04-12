/**
 * 
 */
package cn.bc.business.runcase.dao.hibernate.jpa;

import java.io.Serializable;

import cn.bc.business.runcase.dao.CaseLostDao;
import cn.bc.business.runcase.domain.Case4Lost;
import cn.bc.orm.hibernate.jpa.HibernateCrudJpaDao;


/**
 * 营运报失Dao的hibernate jpa实现
 * 
 * @author wis
 */
public class CaseLostDaoImpl extends HibernateCrudJpaDao<Case4Lost> implements CaseLostDao{

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

		Case4Lost e = this.getJpaTemplate().find(Case4Lost.class, id);
		if (e != null)
			this.getJpaTemplate().remove(e);
	}

	//批量物理删除
	@Override
	public void delete(Serializable[] ids) {
		if (ids == null || ids.length == 0)
			return;

		for (Serializable pk : ids) {
			Case4Lost e = this.getJpaTemplate().find(Case4Lost.class, pk);
			if (e != null)
				this.getJpaTemplate().remove(e);
		}
	}
}