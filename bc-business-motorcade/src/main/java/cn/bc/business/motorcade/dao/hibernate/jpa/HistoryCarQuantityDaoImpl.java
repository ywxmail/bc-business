package cn.bc.business.motorcade.dao.hibernate.jpa;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import cn.bc.business.motorcade.dao.HistoryCarQuantityDao;
import cn.bc.business.motorcade.domain.HistoryCarQuantity;
import cn.bc.orm.hibernate.jpa.HibernateCrudJpaDao;

/**
 * 删除历史车辆数
 * @author zxr
 *
 */
public class HistoryCarQuantityDaoImpl extends
		HibernateCrudJpaDao<HistoryCarQuantity> implements
		HistoryCarQuantityDao {

	public void deleteByMotorcade(Serializable motorcadeId) {

		String hql = "delete HistoryCarQuantity a where a.motorcade.id=?";
		List<Object> args = new ArrayList<Object>();
		args.add(motorcadeId);
		this.executeUpdate(hql, args);
	}

	
}
