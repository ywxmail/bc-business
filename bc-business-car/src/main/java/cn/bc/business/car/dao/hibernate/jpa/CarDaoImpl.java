/**
 * 
 */
package cn.bc.business.car.dao.hibernate.jpa;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.orm.jpa.JpaCallback;
import org.springframework.util.StringUtils;

import cn.bc.business.car.dao.CarDao;
import cn.bc.business.car.domain.Car;
import cn.bc.core.Page;
import cn.bc.core.RichEntity;
import cn.bc.core.query.condition.Condition;
import cn.bc.orm.hibernate.jpa.HibernateCrudJpaDao;

/**
 * 车辆Dao的hibernate jpa实现
 * 
 * @author dragon
 */
public class CarDaoImpl extends HibernateCrudJpaDao<Car> implements CarDao {
	private static Log logger = LogFactory.getLog(CarDaoImpl.class);

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
		//0为启用中
		return this.getJpaTemplate().find(hql, new Object[] { carManId,new Integer( 0 )});
	}
}