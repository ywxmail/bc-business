/**
 * 
 */
package cn.bc.business.car.dao.hibernate.jpa;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.jpa.JpaCallback;
import org.springframework.util.StringUtils;

import cn.bc.BCConstants;
import cn.bc.business.car.dao.CarDao;
import cn.bc.business.car.domain.Car;
import cn.bc.business.cert.domain.Cert;
import cn.bc.business.contract.domain.Contract;
import cn.bc.core.Page;
import cn.bc.core.query.condition.Condition;
import cn.bc.orm.hibernate.jpa.HibernateCrudJpaDao;

/**
 * 车辆Dao的hibernate jpa实现
 * 
 * @author dragon
 */
public class CarDaoImpl extends HibernateCrudJpaDao<Car> implements CarDao {
	private static Log logger = LogFactory.getLog(CarDaoImpl.class);
	private JdbcTemplate jdbcTemplate;

	@Autowired
	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	@Override
	public void delete(Serializable id) {
		// 仅将状态标记为注销
		Map<String, Object> attrs = new HashMap<String, Object>();
		attrs.put("status", new Integer(BCConstants.STATUS_DISABLED));
		this.update(id, attrs);
	}

	@Override
	public void delete(Serializable[] ids) {
		// 仅将状态标记为为注销
		Map<String, Object> attrs = new HashMap<String, Object>();
		attrs.put("status", new Integer(BCConstants.STATUS_DISABLED));
		this.update(ids, attrs);
	}

	/**
	 * 查找汽车列表
	 * 
	 * @parma condition
	 * @return
	 */
	public List<Map<String, Object>> list(Condition condition) {
		ArrayList<Object> args = new ArrayList<Object>();
		StringBuffer hql = new StringBuffer();
		hql.append(
				"SELECT car.id,car.status,car.code,car.plateType,car.plateNo,car.driver")
		// .append("(select m.driver.name from CarByDriver m where m.car.id = car.id and m.classes="+CarByDriver.TYPE_FUBAN+"),")
				.append(",car.factoryType,car.factoryModel,car.businessType,car.motorcade.name,car.oldUnitName,car.registerDate,car.originNo,car.vin,car.certNo2,car.charger FROM Car car ");

		// 组合查询条件
		if (condition != null && condition.getValues().size() > 0) {
			setWhere(condition, hql);
		}

		for (int i = 0; i < condition.getValues().size(); i++) {
			args.add(condition.getValues().get(i));
		}

		// 排序
		hql.append(" order by car.status,car.fileDate desc");
		if (logger.isDebugEnabled()) {
			logger.debug("hql=" + hql);
			logger.debug("args="
					+ (condition != null ? StringUtils
							.collectionToCommaDelimitedString(condition
									.getValues()) : "null"));
		}

		@SuppressWarnings("rawtypes")
		List list = this.getJpaTemplate().find(hql.toString(), args.toArray());

		// 组装map数据,模拟domain列显示
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		Map<String, Object> map;

		for (Object obj : list) {
			Object[] ary = (Object[]) obj;
			map = new HashMap<String, Object>();
			map.put("id", ary[0]);
			map.put("status", ary[1]);
			map.put("code", ary[2]);
			map.put("plateType", ary[3]);
			map.put("plateNo", ary[4]);
			map.put("driver", ary[5]);
			map.put("factoryType", ary[6]);
			map.put("factoryModel", ary[7]);
			map.put("businessType", ary[8]);
			map.put("motorcade", ary[9]);
			map.put("oldUnitName", ary[10]);
			map.put("registerDate", ary[11]);
			map.put("originNo", ary[12]);
			map.put("vin", ary[13]);
			map.put("certNo2", ary[14]);
			map.put("charger", ary[15]);

			result.add(map);
		}
		return result;
	}

	/**
	 * 查找汽车分页
	 * 
	 * @parma condition
	 * @parma Page
	 * @return
	 */
	public Page<Map<String, Object>> page(final Condition condition,
			final int pageNo, int pageSize) {
		// 设置最大页数,如果小于1都按1计算
		final int _pageSize = pageSize < 1 ? 1 : pageSize;
		final StringBuffer hql = new StringBuffer();

		// 因为用正班做查询条件1部车辆返回多个正班司机.待解决 CarByDriver.TYPE_ZHENGBAN
		hql.append(
				"SELECT car.id,car.status,car.code,car.plateType,car.plateNo,car.driver")
		// .append("(select m.driver.name from CarByDriver m where m.car.id = car.id and m.classes="+CarByDriver.TYPE_FUBAN+"),")
				.append(",car.factoryType,car.factoryModel,car.businessType,car.motorcade.name,car.oldUnitName,car.registerDate,car.originNo,car.vin,car.certNo2,car.charger");

		// 方便统计记录数
		String sqlStr = " FROM Car car ";

		hql.append(sqlStr);

		// 组合查询条件
		if (condition != null && condition.getValues().size() > 0) {
			setWhere(condition, hql);
		}
		// 排序
		hql.append(" order by car.status,car.fileDate desc");
		// if (logger.isDebugEnabled()) {
		logger.debug("pageNo=" + pageNo);
		logger.debug("pageSize=" + _pageSize);
		logger.debug("hql=" + hql);
		logger.debug("args="
				+ (condition != null ? StringUtils
						.collectionToCommaDelimitedString(condition.getValues())
						: "null"));
		// }

		@SuppressWarnings("rawtypes")
		// 实现getJapTemLate的execute方法返回一个list
		List list = this.getJpaTemplate().execute(
				new JpaCallback<List<Object[]>>() {
					@SuppressWarnings("unchecked")
					public List<Object[]> doInJpa(EntityManager em)
							throws PersistenceException {
						Query queryObject = em.createQuery(hql.toString());
						getJpaTemplate().prepareQuery(queryObject);
						if (condition != null && condition.getValues() != null) {
							int i = 0;
							for (Object value : condition.getValues()) {
								queryObject.setParameter(i + 1, value);// jpa的索引号从1开始
								i++;
							}
						}
						// 计算某页第一条数据的全局索引号，从0开始并设置到queryObject
						queryObject.setFirstResult(Page.getFirstResult(pageNo,
								_pageSize));
						// 设置最大页数
						queryObject.setMaxResults(_pageSize);
						// queryObject.getResultList()结果可返回任意类型需强制转换
						return (List<Object[]>) queryObject.getResultList();
					}
				});

		// 组装map数据,模拟domain列显示
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		Map<String, Object> map;

		for (Object obj : list) {

			Object[] ary = (Object[]) obj;
			map = new HashMap<String, Object>();
			map.put("id", ary[0]);
			map.put("status", ary[1]);
			map.put("code", ary[2]);
			map.put("plateType", ary[3]);
			map.put("plateNo", ary[4]);
			map.put("driver", ary[5]);
			map.put("factoryType", ary[6]);
			map.put("factoryModel", ary[7]);
			map.put("businessType", ary[8]);
			map.put("motorcade", ary[9]);
			map.put("oldUnitName", ary[10]);
			map.put("registerDate", ary[11]);
			map.put("originNo", ary[12]);
			map.put("vin", ary[13]);
			map.put("certNo2", ary[14]);
			map.put("charger", ary[15]);

			result.add(map);
		}

		// 创建一个Page对象
		Page<Map<String, Object>> page = new Page<Map<String, Object>>(pageNo,
				pageSize, count(condition, sqlStr), result);

		return page;
	}

	/**
	 * 根据查询条件统计总记录数
	 * 
	 * @param condition
	 * @param sqlStr
	 * @return count
	 */
	public int count(final Condition condition, String sqlStr) {

		final StringBuffer hql4Count = new StringBuffer();

		sqlStr = "select count(*) " + sqlStr;

		hql4Count.append(sqlStr);
		// 组合查询条件
		if (condition != null && condition.getValues().size() > 0) {
			setWhere(condition, hql4Count);
		}

		final String queryString = hql4Count.toString();

		if (logger.isDebugEnabled()) {
			logger.debug("hql4Count=" + queryString);
			logger.debug("args4Count="
					+ (condition != null ? StringUtils
							.collectionToCommaDelimitedString(condition
									.getValues()) : "null"));
		}

		// 实现getJapTemLate的execute方法返回一个Long值
		Long count = getJpaTemplate().execute(new JpaCallback<Long>() {
			public Long doInJpa(EntityManager em) throws PersistenceException {
				Query queryObject = em.createQuery(queryString);
				getJpaTemplate().prepareQuery(queryObject);
				if (condition != null && condition.getValues() != null) {
					int i = 0;
					for (Object value : condition.getValues()) {
						queryObject.setParameter(i + 1, value);// jpa的索引号从1开始
						i++;
					}
				}
				return (Long) queryObject.getSingleResult();
			}
		});
		return count.intValue();
	}

	/**
	 * 组合查询条件
	 * 
	 * @param condition
	 * @param args
	 * @param hql
	 */
	public void setWhere(Condition condition, StringBuffer hql) {
		String[] strAry;
		String asName;
		// 处理字符串获取别名
		strAry = hql.toString().toLowerCase().split("from");
		strAry = strAry[strAry.length - 1].split(" ");
		asName = strAry[strAry.length - 1];

		if (condition != null && condition.getValues().size() > 0) {
			hql.append(" WHERE ");
			// 为需要查询的条件加上别名如 xx.列
			hql.append(condition.getExpression(asName));
			// if(condition.getValues().size() == 1){
			// //当模块Action的getSearchFields()方法只有一个参数时
			// 获取搜索框输入的参数并拼装到args
			// args.add(condition.getValues().get(0));
			// }else{
			// hql.append(condition.getExpression(asName));
			// //循环获取搜索框输入的参数并拼装到args
			// for (int i = 0;i < condition.getValues().size(); i++){
			// args.add(condition.getValues().get(i));
			// }
			// }

		}
	}

	/**
	 * 根据司机ID查找返回状态为启用中相关辆信息
	 * 
	 * @parma carManId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Car> findAllcarBycarManId(Long carManId) {
		String hql = "select c.car from CarByDriver c where c.driver.id=? and c.status=?";
		// 0为启用中
		return this.getJpaTemplate().find(hql,
				new Object[] { carManId, new Integer(0) });
	}

	/**
	 * 根据车牌号查找车辆id
	 * 
	 * @parma carPlateNo
	 * @return Long
	 */
	public Long findcarIdByCarPlateNo(String carPlateNo) {
		Long carId = null;
		String sql = "select c.Id from BS_CAR c where c.PLATE_NO='"
				+ carPlateNo + "'";
		try {
			carId = this.jdbcTemplate.queryForLong(sql);
		} catch (EmptyResultDataAccessException e) {
			e.getStackTrace();
		}
		return carId;
	}

	public Car findcarOriginNoByCode(String code) {
		Car car = null;
		String hql = "select c from Car c where c.code = ?";
		hql += " order by c.registerDate Desc";
		List<?> list = this.getJpaTemplate().find(hql, code);
		if (list.size() == 1) {
			car = (Car) list.get(0);
			return car;
		} else if (list.size() < 1) {
			return null;
		} else {
			car = (Car) list.get(0);
			if (logger.isDebugEnabled()) {
				logger.debug("有两辆或两辆以上的车辆，已选择其第一辆");
			}
			return car;
		}
	}

	public Map<String, Object> findcarInfoByCarPlateNo2(String carPlateNo) {
		Map<String, Object> queryMap = null;
		String sql = "SELECT bia.name as unit_name,m.name as motorcade_name"
				+ " FROM BS_CAR car"
				+ " inner join bs_motorcade m on m.id=car.motorcade_id"
				+ " inner join bc_identity_actor bia on bia.id=m.unit_id"
				+ " where car.plate_no='" + carPlateNo + "'";

		// jdbc查询BS_CAR记录
		try {
			queryMap = this.jdbcTemplate.queryForMap(sql);
		} catch (EmptyResultDataAccessException e) {
			e.getStackTrace();
		}

		return queryMap;
	}

	public Long checkCodeIsExists(Long excludeId, String code) {
		// 只查在案的车辆，因为新车可能沿用旧车的自编号
		String sql = "select c.id as id from BS_CAR c where c.status_=? and c.code=?";
		Object[] args;
		if (excludeId != null) {
			sql += " and c.id!=?";
			args = new Object[] { new Integer(Car.CAR_STAUTS_NORMAL), code,
					excludeId };
		} else {
			args = new Object[] { new Integer(Car.CAR_STAUTS_NORMAL), code };
		}
		List<Map<String, Object>> list = this.jdbcTemplate.queryForList(sql,
				args);
		if (list != null && !list.isEmpty())
			return new Long(list.get(0).get("id").toString());
		else
			return null;
	}

	public Long checkPlateIsExists(Long excludeId, String plateType,
			String plateNo) {
		// 只查在案的车辆，因为新车可能沿用旧车的自编号
		String sql = "select c.id as id from BS_CAR c where c.plate_type=? and c.plate_no=?";
		Object[] args;
		if (excludeId != null) {
			sql += " and c.id!=?";
			args = new Object[] { plateType, plateNo, excludeId };
		} else {
			args = new Object[] { plateType, plateNo };
		}
		List<Map<String, Object>> list = this.jdbcTemplate.queryForList(sql,
				args);
		if (list != null && !list.isEmpty())
			return new Long(list.get(0).get("id").toString());
		else
			return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Car save(Car entity) {
		if (!entity.isNew()) {
			// 重新加载证件信息，避免保存时将其删除
			if (entity.getCerts() == null)
				entity.setCerts(new HashSet<Cert>());
			entity.getCerts()
					.addAll(this
							.getJpaTemplate()
							.find("select cert from Car car join car.certs cert where car.id=?",
									entity.getId()));

			// 重新加载合同信息，避免保存时将其删除
			if (entity.getContracts() == null)
				entity.setContracts(new HashSet<Contract>());
			entity.getContracts()
					.addAll(this
							.getJpaTemplate()
							.find("select contract from Car car join car.contracts contract where car.id=?",
									entity.getId()));
		}

		// 调用基类的保存
		return super.save(entity);
	}
}