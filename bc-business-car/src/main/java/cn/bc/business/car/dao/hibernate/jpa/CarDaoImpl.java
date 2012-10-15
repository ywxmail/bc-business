/**
 * 
 */
package cn.bc.business.car.dao.hibernate.jpa;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.jpa.JpaCallback;
import org.springframework.orm.jpa.JpaTemplate;
import org.springframework.util.StringUtils;

import cn.bc.BCConstants;
import cn.bc.business.car.dao.CarDao;
import cn.bc.business.car.domain.Car;
import cn.bc.business.cert.domain.Cert;
import cn.bc.business.contract.domain.Contract;
import cn.bc.core.Page;
import cn.bc.core.query.condition.Condition;
import cn.bc.core.util.DateUtils;
import cn.bc.db.jdbc.RowMapper;
import cn.bc.orm.hibernate.jpa.HibernateCrudJpaDao;
import cn.bc.orm.hibernate.jpa.HibernateJpaNativeQuery;

/**
 * 车辆Dao的hibernate jpa实现
 * 
 * @author dragon
 */
public class CarDaoImpl extends HibernateCrudJpaDao<Car> implements CarDao {
	private static Log logger = LogFactory.getLog(CarDaoImpl.class);
	private JdbcTemplate jdbcTemplate;
	private JpaTemplate jpaTemplate;

	@Autowired
	public void setJpaTemplate(JpaTemplate jpaTemplate) {
		this.jpaTemplate = jpaTemplate;
	}

	@Autowired
	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	// @Override
	// public void delete(Serializable id) {
	// // 仅将状态标记为注销
	// Map<String, Object> attrs = new HashMap<String, Object>();
	// attrs.put("status", new Integer(BCConstants.STATUS_DISABLED));
	// this.update(id, attrs);
	// }

	// @Override
	// public void delete(Serializable[] ids) {
	// // 仅将状态标记为为注销
	// Map<String, Object> attrs = new HashMap<String, Object>();
	// attrs.put("status", new Integer(BCConstants.STATUS_DISABLED));
	// this.update(ids, attrs);
	// }

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

	public Car findcarOriginNoByOwnership(String ownership, Calendar fileDate) {
		Car car = null;
		String hql = "select c from Car c where c.certNo2 = ? and c.fileDate < ? and c.status = 1";
		hql += " order by c.registerDate Desc";
		List<?> list = this.getJpaTemplate().find(hql, ownership, fileDate);
		if (list.size() == 1) {
			car = (Car) list.get(0);
			return car;
		} else if (list.size() < 1) {
			return null;
		} else {
			car = (Car) list.get(0);
			if (logger.isDebugEnabled()) {
				logger.debug("有两辆或两辆以上的车辆，根据登记日期选择最新的一车辆");
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
		String sql = "select c.id as id from BS_CAR c where c.status_ in (-1,0) and c.code=?";
		Object[] args;
		if (excludeId != null) {
			sql += " and c.id!=?";
			args = new Object[] { code, excludeId };
		} else {
			args = new Object[] { code };
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

	public String getDriverInfoByCarId(final Long carId) {

		final StringBuffer sql = new StringBuffer();
		sql.append("select getDriverInfoByCarId(id) from bs_car where id = ?");

		if (logger.isDebugEnabled()) {
			logger.debug("driverId=" + carId + ";sql=" + sql);
		}
		// JpaCallback 返回函数的类型
		return this.jpaTemplate.execute(new JpaCallback<String>() {
			// JpaCallback接口的方法
			public String doInJpa(EntityManager em) throws PersistenceException {
				Query queryObject = em.createNativeQuery(sql.toString());
				// 设置问号所对应的参数
				queryObject.setParameter(1, carId);
				// 设置从第几个开始查
				queryObject.setFirstResult(0);
				// 设置查几多个[如果想查所有,则不设]
				queryObject.setMaxResults(1);
				String driverInfo;
				try {
					driverInfo = (String) queryObject.getSingleResult();
				} catch (NoResultException e) {
					if (logger.isDebugEnabled())
						logger.debug("driverInfo = null,id=" + carId);
					return null;
				}
				if (driverInfo != null) {
					return driverInfo;
				}
				return null;
			}
		});

	}

	public String getChargerInfoByCarId(final Long carId) {

		final StringBuffer sql = new StringBuffer();
		sql.append("select getChargerInfoByCarId(id) from bs_car where id = ?");

		if (logger.isDebugEnabled()) {
			logger.debug("driverId=" + carId + ";sql=" + sql);
		}
		// JpaCallback 返回函数的类型
		return this.jpaTemplate.execute(new JpaCallback<String>() {
			// JpaCallback接口的方法
			public String doInJpa(EntityManager em) throws PersistenceException {
				Query queryObject = em.createNativeQuery(sql.toString());
				// 设置问号所对应的参数
				queryObject.setParameter(1, carId);
				// 设置从第几个开始查
				queryObject.setFirstResult(0);
				// 设置查几多个[如果想查所有,则不设]
				queryObject.setMaxResults(1);
				String chargerInfo;
				try {
					chargerInfo = (String) queryObject.getSingleResult();
				} catch (NoResultException e) {
					if (logger.isDebugEnabled())
						logger.debug("driverInfo = null,id=" + carId);
					return null;
				}
				if (chargerInfo != null) {
					return chargerInfo;
				}
				return null;
			}
		});

	}

	@SuppressWarnings("unchecked")
	public List<Car> selectCarByStatus(Integer status) {
		String hql = "from Car car";
		if (status != null) {
			hql += " where car.status=?";
		}
		return this.getJpaTemplate().find(hql, status);
	}

	public List<Map<String, Object>> findRetiredCarsOfMonth(Calendar month,
			Long unitId) {
		String sql = "select a.id,a.status_,a.code,a.plate_type as plateType,a.plate_no as plateNo,to_char(a.register_date,'YYYY-MM-DD') as registerDate";
		sql += ",to_char(a.scrap_date,'YYYY-MM-DD') as scrapDate,a.motorcade_id as motorcadeId,e.name,a.company,a.bs_type as bsType,a.charger";
		sql += ",f.name as unitCompany,f.id as unitCompanyId,to_char(c.end_date,'YYYY-MM-DD') as ccEndDate";
		sql += ",to_char(d.commerial_end_date,'YYYY-MM-DD') as commerialEndDate,to_char(d.greenslip_end_date,'YYYY-MM-DD') as greenslipEndDate";
		sql += ",a.factory_type,to_char(d.greenslip_Start_Date,'YYYY-MM-DD') as greenslipStartDate,a.engine_no,a.vin,a.access_count,a.access_weight,a.displacement";
		sql += ",(select string_agg(oman.name,',') from (select man.name from bs_carman man inner join bs_car_driver cd on man.id = cd.driver_id";
		sql += " where cd.car_id = a.id and cd.status_ = 0 ORDER BY man.id) oman) as driver";
		sql += ",(select string_agg(oman.cert_fwzg,',') from (select man.cert_fwzg from bs_carman man inner join bs_car_driver cd on man.id = cd.driver_id";
		sql += " where cd.car_id = a.id and cd.status_ = 0 ORDER BY man.id) oman) as driverFWZG";
		sql += " from bs_car a";
		sql += " inner join bs_motorcade e on e.id=a.motorcade_id";
		sql += " inner join bc_identity_actor f on f.id = e.unit_id";
		sql += " inner join bs_car_contract b on b.car_id=a.id";
		sql += " inner join bs_contract c on c.id=b.contract_id";
		sql += " left join bs_car_policy d on d.car_id=a.id and d.status_=?";
		sql += " where a.status_=? and c.status_=? and d.status_=? and c.type_=2";
		if (unitId != null) {
			sql += " and e.unit_id=" + unitId;
		}
		// 设置指定月第一日
		DateUtils.setToFirstDayOfMonth(month);
		Date fistDayOfMonth = month.getTime();
		// 设置指定月最后日
		DateUtils.setToLastDayOfMonth(month);
		Date lastDayOfMonth = month.getTime();

		// 5年前
		month.add(Calendar.YEAR, -5);
		Date fiveYearsAgoLastDayOfMonth = month.getTime();
		DateUtils.setToFirstDayOfMonth(month);
		Date fiveYearsAgoFistDayOfMonth = month.getTime();

		// 8年前
		month.add(Calendar.YEAR, -3);
		Date eightYearsAgoFistDayOfMonth = month.getTime();
		DateUtils.setToLastDayOfMonth(month);
		Date eightYearsAgoLastDayOfMonth = month.getTime();

		// 现在
		month.add(Calendar.YEAR, 8);
		
		// 半年后
		month.add(Calendar.MONTH, 6);
		Date halfAYearLaterLastDayOfMonth = month.getTime();

		sql += " and (";
		// 1指定月经济合同到期车辆
		sql += " (c.end_date>?  and c.end_date<?)";
		sql += " or";
		// 2指定月5年前登记日期车辆
		sql += " (a.register_date>? and a.register_date<?)";
		sql += " or";
		// 3指定月8年前登记日期车辆
		sql += " (a.register_date>? and a.register_date<?)";
		sql += " or";
		// 4指定月两险日期到期且经济合同在半年内到期的车辆
		// 强制险
		sql += " ((d.greenslip_end_date>? and d.greenslip_end_date<? and c.end_date>? and c.end_date<? ) or d.status_ is null)";
		sql += " or";
		// 商业险
		sql += " ((d.commerial_end_date>? and d.commerial_end_date<? and c.end_date>? and c.end_date<? ) or d.status_ is null)";
		sql += " )";
		
		Object[] args = new Object[] { 
				BCConstants.STATUS_ENABLED,
				BCConstants.STATUS_ENABLED, 
				BCConstants.STATUS_ENABLED,
				BCConstants.STATUS_ENABLED, 
				fistDayOfMonth,
				lastDayOfMonth, 				
				fiveYearsAgoFistDayOfMonth,
				fiveYearsAgoLastDayOfMonth,	
				eightYearsAgoFistDayOfMonth,
				eightYearsAgoLastDayOfMonth, 
				fistDayOfMonth,
				lastDayOfMonth, 
				fistDayOfMonth,
				halfAYearLaterLastDayOfMonth, 
				fistDayOfMonth,
				lastDayOfMonth, 
				fistDayOfMonth,
				halfAYearLaterLastDayOfMonth };
		if(logger.isDebugEnabled()){
			logger.debug("sql=" + sql);
			logger.debug("args=" + args);
		}
		return HibernateJpaNativeQuery.executeNativeSql(getJpaTemplate(), sql,
				args,
				new RowMapper<Map<String, Object>>() {
					public Map<String, Object> mapRow(Object[] rs, int rowNum) {
						Map<String, Object> oi = new HashMap<String, Object>();
						int i = 0;
						oi.put("id", rs[i++]);
						oi.put("status_", rs[i++]);
						oi.put("code", rs[i++]);
						oi.put("plateType", rs[i++]);
						oi.put("plateNo", rs[i++]);
						oi.put("registerDate", rs[i++]);
						oi.put("scrapDate", rs[i++]);
						oi.put("motorcadeId", rs[i++]);
						oi.put("motorcadeName", rs[i++]);
						oi.put("company", rs[i++]);
						oi.put("bsType", rs[i++]);
						oi.put("charger", rs[i++]);
						oi.put("unitCompany", rs[i++]);
						oi.put("unitCompanyId", rs[i++]);
						oi.put("ccEndDate", rs[i++]);
						oi.put("commerialEndDate", rs[i++]);
						oi.put("greenslipEndDate", rs[i++]);
						oi.put("factoryType", rs[i++]);
						oi.put("greenslipStartDate", rs[i++]);
						oi.put("engineNo", rs[i++]);
						oi.put("vin", rs[i++]);
						oi.put("accessCount", rs[i++]);
						oi.put("accessWeight", rs[i++]);
						oi.put("displacement", rs[i++]);
						oi.put("driverName", rs[i++]);
						oi.put("driverFWZG", rs[i++]);
						String companyFullName = oi.get("company").toString();
						if (companyFullName.length() > 0
								&& companyFullName.equalsIgnoreCase("宝城")) {
							oi.put("companyFullName", "广州市宝城汽车出租有限公司");
						} else if (companyFullName.length() > 0
								&& companyFullName.equalsIgnoreCase("广发")) {
							oi.put("companyFullName", "广州市广发出租汽车有限公司");
						}
						oi.put("plate", oi.get("plateType").toString() + "."
								+ oi.get("plateNo").toString());
						// 计算预计交车日期
						List<Date> tempList = new ArrayList<Date>();
						if (oi.get("ccEndDate") != null)
							tempList.add(DateUtils.getDate(oi.get("ccEndDate")
									.toString()));
						if (oi.get("commerialEndDate") != null)
							tempList.add(DateUtils.getDate(oi.get(
									"commerialEndDate").toString()));
						if (oi.get("greenslipEndDate") != null)
							tempList.add(DateUtils.getDate(oi.get(
									"greenslipEndDate").toString()));
						if (tempList.size() == 0) {
							oi.put("predictReturnDate", null);
						} else if (tempList.size() == 1) {
							oi.put("predictReturnDate",
									DateUtils.formatDate(tempList.get(0)));
						} else if (tempList.size() == 2) {
							if (tempList.get(0).after(tempList.get(1))) {
								oi.put("predictReturnDate",
										DateUtils.formatDate(tempList.get(1)));
							} else
								oi.put("predictReturnDate",
										DateUtils.formatDate(tempList.get(0)));
						} else {
							// 排序
							for (int j = 0; j < tempList.size(); j++) {
								for (int k = 0; k + 1 < tempList.size(); k++) {
									if (tempList.get(k).after(
											tempList.get(k + 1))) {
										tempList.add(k, tempList.get(k + 1));
										tempList.remove(k + 2);
									}
								}
							}
							oi.put("predictReturnDate",
									DateUtils.formatDate(tempList.get(0)));
						}
						return oi;
					}
				});
	}

	public Long checkManageNoIsExists(Long carId, Long manageNo) {
		String sql = "select c.id as id from BS_CAR c where c.status_ in (-1,0) and c.manage_no=?";
		Object[] args;
		if (carId != null) {
			sql += " and c.id!=?";
			args = new Object[] { manageNo, carId };
		} else {
			args = new Object[] { manageNo };
		}
		List<Map<String, Object>> list = this.jdbcTemplate.queryForList(sql,
				args);
		if (list != null && !list.isEmpty())
			return new Long(list.get(0).get("id").toString());
		else
			return null;
	}
}