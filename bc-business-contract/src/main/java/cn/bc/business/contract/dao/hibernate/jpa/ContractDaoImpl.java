/**
 * 
 */
package cn.bc.business.contract.dao.hibernate.jpa;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.StringUtils;

import cn.bc.business.contract.dao.ContractDao;
import cn.bc.business.contract.domain.Contract;
import cn.bc.business.contract.domain.ContractCarManRelation;
import cn.bc.business.contract.domain.ContractCarRelation;
import cn.bc.core.exception.CoreException;
import cn.bc.core.query.condition.Condition;
import cn.bc.orm.hibernate.jpa.HibernateCrudJpaDao;

/**
 * 合同Dao的hibernate jpa实现
 * 
 * @author dragon
 */
public class ContractDaoImpl extends HibernateCrudJpaDao<Contract> implements
		ContractDao {
	private static Log logger = LogFactory.getLog(ContractDaoImpl.class);

	public void saveContractCarRelation(ContractCarRelation entity) {
		if (null != entity) {
			this.getJpaTemplate().persist(entity);
		}
	}

	public void saveContractCarRelation(Collection<ContractCarRelation> entities) {
		if (null != entities && !entities.isEmpty()) {
			for (ContractCarRelation entity : entities)
				this.saveContractCarRelation(entity);
		}
	}

	public void saveContractCarManRelation(ContractCarManRelation entity) {
		if (null != entity) {
			this.getJpaTemplate().persist(entity);
		}
	}

	public void saveContractCarManRelation(
			Collection<ContractCarManRelation> entities) {
		if (null != entities && !entities.isEmpty()) {
			for (ContractCarManRelation entity : entities)
				this.saveContractCarManRelation(entity);
		}
	}

	public void updateContractCarRelation(Long contractId, Long carId) {
		if (contractId == null)
			return;

		// 查找现存的关系
		@SuppressWarnings("unchecked")
		List<ContractCarRelation> olds = this.getJpaTemplate().find(
				"from ContractCarRelation where contractId=?", contractId);

		if (olds.isEmpty()) {// 保存新的关系
			this.saveContractCarRelation(new ContractCarRelation(contractId,
					carId));
		} else {
			if (olds.size() > 1) {// 不正常的情况
				throw new CoreException("合同存在多个与之关联的车辆！contractId="
						+ contractId);
			}

			ContractCarRelation old = olds.get(0);
			if (carId == null) {// 删除存在的关系
				this.executeUpdate(
						"delete from ContractCarRelation where contractId=? and carId=?",
						new Object[] { old.getContractId(), old.getCarId() });
			}
		}
	}

	public void updateContractCarRelation(Long contractId, Long[] carIds) {
		if (contractId == null)
			return;
		// 查找现存的关系
		@SuppressWarnings("unchecked")
		List<ContractCarRelation> olds = this.getJpaTemplate().find(
				"from ContractCarRelation where contractId=?", contractId);

		if (carIds != null && carIds.length > 0) {
			// 分离出没有改变的关系和需要新增的关系
			List<ContractCarRelation> sames = new ArrayList<ContractCarRelation>();// 没有改变的
			List<ContractCarRelation> news = new ArrayList<ContractCarRelation>();// 新加的
			ContractCarRelation cur = null;
			for (int i = 0; i < carIds.length; i++) {
				for (ContractCarRelation old : olds) {
					if (old.equals(contractId, carIds[i])) {
						cur = old;
						break;
					}
				}
				if (cur != null) {
					sames.add(cur);
				} else {
					news.add(new ContractCarRelation(contractId, carIds[i]));
				}
			}

			// 查找出需要删除的关系并执行删除
			if (!olds.isEmpty() && sames.size() != olds.size()) {
				// 查找出需要删除的关系
				List<ContractCarRelation> toDeleteArs = new ArrayList<ContractCarRelation>();
				boolean isSame;
				for (ContractCarRelation old : olds) {
					isSame = false;
					for (ContractCarRelation one : sames) {
						if (one.equals(old)) {
							isSame = true;
							break;
						}
					}
					if (!isSame) {
						toDeleteArs.add(old);
					}
				}

				// 执行删除操作
				for (ContractCarRelation one : toDeleteArs) {
					this.executeUpdate(
							"delete from ContractCarRelation where contractId=? and carId=?",
							new Object[] { one.getContractId(), one.getCarId() });
				}
			}

			// 保存需要新增的关系
			if (!news.isEmpty()) {
				this.saveContractCarRelation(news);
			}
		} else {
			// 删除所有现存的隶属关系
			for (ContractCarRelation one : olds) {
				this.executeUpdate(
						"delete from ContractCarRelation where contractId=? and carId=?",
						new Object[] { one.getContractId(), one.getCarId() });
			}
		}
	}

	public void updateContractCarManRelation(Long contractId, Long carManId) {
		if (contractId == null)
			return;

		// 查找现存的关系
		@SuppressWarnings("unchecked")
		List<ContractCarRelation> olds = this.getJpaTemplate().find(
				"from ContractCarManRelation where contractId=?", contractId);

		if (olds.isEmpty()) {// 保存新的关系
			this.saveContractCarManRelation(new ContractCarManRelation(
					contractId, carManId));
		} else {
			if (olds.size() > 1) {// 不正常的情况
				throw new CoreException("合同存在多个与之关联的司机！contractId="
						+ contractId);
			}

			ContractCarRelation old = olds.get(0);
			if (carManId == null) {// 删除存在的关系
				this.executeUpdate(
						"delete from ContractCarManRelation where contractId=? and carManId=?",
						new Object[] { old.getContractId(), old.getCarId() });
			}
		}
	}

	public void updateContractCarManRelation(Long contractId, Long[] carManIds) {
		if (contractId == null)
			return;
		// 查找现存的关系
		@SuppressWarnings("unchecked")
		List<ContractCarManRelation> olds = this.getJpaTemplate().find(
				"from ContractCarManRelation where contractId=?", contractId);
		if (carManIds != null && carManIds.length > 0) {
			// 分离出没有改变的关系和需要新增的关系
			List<ContractCarManRelation> sames = new ArrayList<ContractCarManRelation>();// 没有改变的
			List<ContractCarManRelation> news = new ArrayList<ContractCarManRelation>();// 新加的
			ContractCarManRelation cur;
			for (int i = 0; i < carManIds.length; i++) {
				cur = null;
				for (ContractCarManRelation old : olds) {
					if (old.equals(contractId, carManIds[i])) {
						cur = old;
						break;
					}
				}
				if (cur != null) {
					sames.add(cur);
				} else {
					news.add(new ContractCarManRelation(contractId,
							carManIds[i]));
				}
			}

			// 查找出需要删除的关系并执行删除
			if (!olds.isEmpty() && sames.size() != olds.size()) {
				// 查找出需要删除的关系
				List<ContractCarManRelation> toDeleteArs = new ArrayList<ContractCarManRelation>();
				boolean isSame;
				for (ContractCarManRelation old : olds) {
					isSame = false;
					for (ContractCarManRelation one : sames) {
						if (one.equals(old)) {
							isSame = true;
							break;
						}
					}
					if (!isSame) {
						toDeleteArs.add(old);
					}
				}

				// 执行删除操作
				for (ContractCarManRelation one : toDeleteArs) {
					this.executeUpdate(
							"delete from ContractCarManRelation where contractId=? and carManId=?",
							new Object[] { one.getContractId(),
									one.getCarManId() });
				}
			}

			// 保存需要新增的关系
			if (!news.isEmpty()) {
				this.saveContractCarManRelation(news);
			}
		} else {
			// 删除所有现存的隶属关系
			for (ContractCarManRelation one : olds) {
				this.executeUpdate(
						"delete from ContractCarManRelation where contractId=? and carManId=?",
						new Object[] { one.getContractId(), one.getCarManId() });
			}
		}
	}

	@SuppressWarnings("unchecked")
	public List<ContractCarRelation> findContractCarRelation(Long contractId) {
		return this.getJpaTemplate().find(
				"from ContractCarRelation where contractId=?", contractId);
	}

	@SuppressWarnings("unchecked")
	public List<ContractCarManRelation> findContractCarManRelation(
			Long contractId) {
		return this.getJpaTemplate().find(
				"from ContractCarManRelation where contractId=?", contractId);
	}

	public List<Map<String, Object>> list4Car(Condition condition, Long carId) {
		ArrayList<Object> args = new ArrayList<Object>();
		StringBuffer hql = new StringBuffer();

		hql.append(
				"select contract.id,contract.type, contract.ext_str1, contract.ext_str2, contract.signDate,contract.startDate,contract.endDate,contract.transactorName,contract.code")
				.append(" from Car car join car.contracts contract")
				.append(" where contract.main=0 and car.id=?");

		if (carId != null && carId > 0) {
			args.add(carId);
		}
		if (condition != null && condition.getValues().size() > 0) {
			hql.append(" AND ");
			hql.append(condition.getExpression());
			for (int i = 0; i < condition.getValues().size(); i++) {
				args.add(condition.getValues().get(i));
			}
		}

		// 排序
		hql.append(" order by contract.signDate DESC");
		if (logger.isDebugEnabled()) {
			logger.debug("hql=" + hql);
			logger.debug("args="
					+ (condition != null ? StringUtils
							.collectionToCommaDelimitedString(condition
									.getValues()) : "null"));
		}

		/*
		 * jdbc查询 List list =
		 * this.jdbcTemplate.queryForList(hql.toString(),args.toArray());
		 */
		@SuppressWarnings("rawtypes")
		List list = this.getJpaTemplate().find(hql.toString(), args.toArray());

		// 组装map数据,模拟domain列显示
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		Map<String, Object> map;

		for (Object obj : list) {

			Object[] ary = (Object[]) obj;
			map = new HashMap<String, Object>();
			map.put("id", ary[0]);
			map.put("type", ary[1]);
			map.put("ext_str1", ary[2]);
			map.put("ext_str2", ary[3]);
			map.put("signDate", ary[4]);
			map.put("startDate", ary[5]);
			map.put("endDate", ary[6]);
			map.put("transactorName", ary[7]);
			map.put("code", ary[8]);

			result.add(map);
		}
		return result;
	}

	public List<Map<String, Object>> list4CarMan(Condition condition,
			Long carManId) {
		ArrayList<Object> args = new ArrayList<Object>();
		StringBuffer hql = new StringBuffer();

		hql.append(
				"select contract.id,contract.type, contract.ext_str1, contract.ext_str2, contract.signDate,contract.startDate,contract.endDate,contract.transactorName,contract.code")
				.append(" from CarMan carman join carman.contracts contract")
				.append(" where contract.main=0 and carman.id=?");

		if (carManId != null && carManId > 0) {
			args.add(carManId);
		}
		if (condition != null && condition.getValues().size() > 0) {
			hql.append(" AND ");
			hql.append(condition.getExpression());
			for (int i = 0; i < condition.getValues().size(); i++) {
				args.add(condition.getValues().get(i));
			}
		}

		// 排序
		hql.append(" order by contract.signDate DESC");
		if (logger.isDebugEnabled()) {
			logger.debug("hql=" + hql);
			logger.debug("args="
					+ (condition != null ? StringUtils
							.collectionToCommaDelimitedString(condition
									.getValues()) : "null"));
		}

		/*
		 * jdbc查询 List list =
		 * this.jdbcTemplate.queryForList(hql.toString(),args.toArray());
		 */
		@SuppressWarnings("rawtypes")
		List list = this.getJpaTemplate().find(hql.toString(), args.toArray());

		// 组装map数据,模拟domain列显示
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		Map<String, Object> map;

		for (Object obj : list) {

			Object[] ary = (Object[]) obj;
			map = new HashMap<String, Object>();
			map.put("id", ary[0]);
			map.put("type", ary[1]);
			map.put("ext_str1", ary[2]);
			map.put("ext_str2", ary[3]);
			map.put("signDate", ary[4]);
			map.put("startDate", ary[5]);
			map.put("endDate", ary[6]);
			map.put("transactorName", ary[7]);
			map.put("code", ary[8]);

			result.add(map);
		}
		return result;
	}

}