package cn.bc.business.ownership.dao.hibernate.jpa;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import cn.bc.business.ownership.dao.OwnershipDao;
import cn.bc.business.ownership.domain.Ownership;
import cn.bc.orm.hibernate.jpa.HibernateCrudJpaDao;

/**
 * @author zxr 车辆经营权dao实现类
 * 
 */
public class OwnershipDaoImpl extends HibernateCrudJpaDao<Ownership> implements
		OwnershipDao {

	public Ownership getEntityByCarid(Long carId) {
		Ownership os = null;
		String hql = "select o from Ownership o where o.car.id=?";
		List<?> list = this.getJpaTemplate().find(hql, new Object[] { carId });
		if (list.size() == 1) {
			os = (Ownership) list.get(0);
			return os;
		} else if (list.size() == 0) {
			return null;
		} else {
			os = (Ownership) list.get(0);

		}
		return os;
	}

	public void updateOwnershipByCarId(Map<String, Object> map, Long[] carIds) {
		// 更新时的参数
		ArrayList<Object> args = new ArrayList<Object>();
		if (map != null) {
			String updateOwnershipInfo = "";
			Iterator<String> iterator = map.keySet().iterator();
			String key;
			while (iterator.hasNext()) {
				key = iterator.next();
				// 更新时不设作者和创建时间
				if (!(("fileDate").equals(key) || ("author").equals(key))) {
					if (("modifier").equals(key)) {
						updateOwnershipInfo = updateOwnershipInfo + "o." + key
								+ ".id" + "=" + "?" + ",";
						args.add(map.get(key));
					} else {
						updateOwnershipInfo = updateOwnershipInfo + "o." + key
								+ "=" + "?" + ",";
						args.add(map.get(key));
					}
				}

			}
			updateOwnershipInfo = updateOwnershipInfo.substring(0,
					updateOwnershipInfo.length() - 1);
			// 更新经营权的hql
			StringBuffer updateHql = new StringBuffer();
			updateHql.append("update Ownership o set ");
			updateHql.append(updateOwnershipInfo);
			if (carIds != null && carIds.length > 0) {
				if (carIds.length == 1) {
					updateHql.append(" where o.car.id =?");
					args.add(carIds[0]);
				} else {
					updateHql.append(" where o.car.id in (?");
					args.add(carIds[0]);
					for (int n = 1; n < carIds.length; n++) {
						updateHql.append(",?");
						args.add(carIds[n]);
					}
					updateHql.append(")");
				}
			}
			this.executeUpdate(updateHql.toString(), args.toArray());
		}

	}

	@SuppressWarnings("unchecked")
	public ArrayList<Object> getUpdateCarIdsList(Long[] carIds) {
		List<?> updateCarIds;
		if (carIds.length == 1) {
			String hql = "select o.car.id from Ownership o where o.car.id=?";
			updateCarIds = this.getJpaTemplate().find(hql,
					new Object[] { carIds[0] });

		} else {
			List<Long> args = new ArrayList<Long>();
			StringBuffer hql = new StringBuffer();
			hql.append("select o.car.id from Ownership o where o.car.id in (?");
			args.add(carIds[0]);
			for (int i = 1; i < carIds.length; i++) {
				hql.append(",?");
				args.add(carIds[i]);
			}
			hql.append(")");
			updateCarIds = this.getJpaTemplate().find(hql.toString(),
					args.toArray());

		}
		if (updateCarIds.size() == 0) {
			return null;
		} else {
			return ((ArrayList<Object>) updateCarIds);
		}
	}
}
