/**
 * 
 */
package cn.bc.business.runcase.dao.hibernate.jpa;

import java.io.Serializable;

import cn.bc.business.runcase.dao.CaseTrafficDao;
import cn.bc.business.runcase.domain.Case4InfractTraffic;
import cn.bc.orm.hibernate.jpa.HibernateCrudJpaDao;


/**
 * 营运交通违章Dao的hibernate jpa实现
 * 
 * @author dragon
 */
public class CaseTrafficDaoImpl extends HibernateCrudJpaDao<Case4InfractTraffic> implements CaseTrafficDao{

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

		Case4InfractTraffic e = this.getJpaTemplate().find(Case4InfractTraffic.class, id);
		if (e != null)
			this.getJpaTemplate().remove(e);
	}

	//批量物理删除
	@Override
	public void delete(Serializable[] ids) {
		if (ids == null || ids.length == 0)
			return;

		for (Serializable pk : ids) {
			Case4InfractTraffic e = this.getJpaTemplate().find(Case4InfractTraffic.class, pk);
			if (e != null)
				this.getJpaTemplate().remove(e);
		}
	}
}