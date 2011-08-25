/**
 * 
 */
package cn.bc.business.contract.dao.hibernate.jpa;


import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import cn.bc.business.contract.dao.ContractLabourDao;
import cn.bc.business.contract.domain.Contract4Labour;
import cn.bc.core.RichEntity;
import cn.bc.orm.hibernate.jpa.HibernateCrudJpaDao;


/**
 * 司机劳动合同Dao的hibernate jpa实现
 * 
 * @author dragon
 */
public class ContractLabourDaoImpl extends HibernateCrudJpaDao<Contract4Labour> implements ContractLabourDao{

	@Override
	public void delete(Serializable id) {
		// 仅将状态标记为已删除
		Map<String, Object> attrs = new HashMap<String, Object>();
		attrs.put("status", new Integer(RichEntity.STATUS_DELETED));
		this.update(id, attrs);
	}

	@Override
	public void delete(Serializable[] ids) {
		// 仅将状态标记为已删除
		Map<String, Object> attrs = new HashMap<String, Object>();
		attrs.put("status", new Integer(RichEntity.STATUS_DELETED));
		this.update(ids, attrs);
	}
}