/**
 * 
 */
package cn.bc.business.cert.dao.hibernate.jpa;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.jpa.JpaCallback;
import org.springframework.util.StringUtils;

import cn.bc.business.cert.dao.CertDao;
import cn.bc.business.cert.domain.Cert;
import cn.bc.core.Page;
import cn.bc.core.query.condition.Condition;
import cn.bc.orm.hibernate.jpa.HibernateCrudJpaDao;


/**
 * 证件Dao的hibernate jpa实现
 * 
 * @author dragon
 */
public class CertDaoImpl extends HibernateCrudJpaDao<Cert> implements CertDao{
	protected final Log logger =  LogFactory.getLog(getClass());
	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	/**
	 * 通过carManId查找证件
	 * @parma carManId 
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public Cert findCertByCarManId(Long carManId){
		
		Cert cert = null;
		String hql = "select c from CarMan cman join cman.certs c where cman.id = ?";
		List list = this.getJpaTemplate().find(hql, new Object [] {carManId});
		if(list.size() == 1){
			cert = (Cert)list.get(0);
			return cert;
		}else if(list.size() < 1){
			return null;
		}else{
			cert = (Cert)list.get(0);
			if(logger.isDebugEnabled()){
				logger.debug("有两个或两个以上证件的，已选择其第一个");
			}
			return cert;
		}
	}

	/**
	 * 查找证件列表
	 * @parma condition 
	 * @return
	 */
	public List<Map<String, Object>> list4man(Condition condition,Long carManId) {
		ArrayList<Object> args 	= new ArrayList<Object>();
		StringBuffer hql = new StringBuffer();
		
		hql.append("SELECT cert.id,cert.status")
		   .append(",cman.name")
		   .append(",cert.certCode,cert.type,cert.issueDate,cert.startDate,cert.endDate,cert.licencer")
		   .append(" FROM CarMan cman JOIN cman.certs cert WHERE cman.id=?");
		
		if(carManId != null && carManId > 0){
			args.add(carManId);
		}
		if(condition != null && condition.getValues().size() > 0){
			hql.append(" AND");
			hql.append(condition.getExpression());
			for (int i = 0;i < condition.getValues().size(); i++){
				args.add(condition.getValues().get(i));
			}
		}

//		hql.append("select cert.id,cert.status,cman.name,cert.certCode,cert.type,cert.issueDate,cert.startDate,cert.licencer " +
//	  		"from Cert cert left join CarMan cman join cman.certs c where cert.id=c.id");

		
		// 排序
		hql.append(" order by cert.id");
		if (logger.isDebugEnabled()) {
			logger.debug("hql=" + hql);
			logger.debug("args="
					+ (condition != null ? StringUtils
							.collectionToCommaDelimitedString(condition
									.getValues()) : "null"));
		}
		
		/*	jdbc查询	
		List list = this.jdbcTemplate.queryForList(hql.toString(),args.toArray());
		*/
		@SuppressWarnings("rawtypes")
		List list = this.getJpaTemplate().find(hql.toString(),args.toArray());
		
		//组装map数据,模拟domain列显示
		List<Map<String,Object>> result = new ArrayList<Map<String,Object>>();
		Map<String,Object> map;
		
		for(Object obj : list){
			Object [] ary = (Object[]) obj;
			map = new HashMap<String,Object>();
			map.put("id",			ary[0]);
			map.put("status", 		ary[1]);
			map.put("name", 		ary[2]);
			map.put("certCode", 	ary[3]);
			map.put("type", 		ary[4]);
			map.put("issueDate", 	ary[5]);
			map.put("startDate", 	ary[6]);
			map.put("endDate", 		ary[7]);
			map.put("licencer", 	ary[8]);
			
			result.add(map);
		}
		return result;
	}

	/**
	 * 查找汽车分页
	 * @parma condition 
	 * @parma Page 
	 * @return
	 */
	public Page<Map<String,Object>> page4man(final Condition condition,final int pageNo,
			final int pageSize) {
		//设置最大页数,如果小于1都按1计算
		final int _pageSize = pageSize < 1 ? 1 : pageSize;	
		final StringBuffer hql = new StringBuffer();
		
		hql.append("SELECT cert.id,cert.status")
		   .append(",cman.name")
//		   .append("(SELECT cman.name from CarMan cman join cman.certs c WHERE c.id = cert.id),")
		   .append(",cert.certCode,cert.type,cert.issueDate,cert.startDate,cert.endDate,cert.licencer");
		
		//方便统计记录数
		String sqlStr =	" FROM CarMan cman JOIN cman.certs cert";
		
		hql.append(sqlStr);
		
		//组合查询条件
		if(condition != null && condition.getValues().size() > 0){
			setWhere(condition,hql);
		}
		//排序
		hql.append(" order by cert.id");
		if (logger.isDebugEnabled()) {
			logger.debug("pageNo=" + pageNo);
			logger.debug("pageSize=" + _pageSize);
			logger.debug("hql=" + hql);
			logger.debug("args="
					+ (condition != null ? StringUtils
							.collectionToCommaDelimitedString(condition
									.getValues()) : "null"));
		}
	
		
		@SuppressWarnings("rawtypes")
		//实现getJapTemLate的execute方法返回一个list
		List list = this.getJpaTemplate().execute(new JpaCallback<List<Object[]>>() {
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
				//计算某页第一条数据的全局索引号，从0开始并设置到queryObject
				queryObject.setFirstResult(Page.getFirstResult(pageNo,
						_pageSize));
				//设置最大页数
				queryObject.setMaxResults(_pageSize);
				//queryObject.getResultList()结果可返回任意类型需强制转换
				return (List<Object[]>) queryObject.getResultList();
			}
		});
		
		//组装map数据,模拟domain列显示
		List<Map<String,Object>> result = new ArrayList<Map<String,Object>>();
		Map<String,Object> map;
		
		for(Object obj : list){
			
			Object [] ary = (Object[]) obj;
			map = new HashMap<String,Object>();
			map.put("id",			ary[0]);
			map.put("status", 		ary[1]);
			map.put("name", 		ary[2]);
			map.put("certCode", 	ary[3]);
			map.put("type", 		ary[4]);
			map.put("issueDate", 	ary[5]);
			map.put("startDate", 	ary[6]);
			map.put("endDate", 		ary[7]);
			map.put("licencer", 	ary[8]);
			
			result.add(map);
		}
		
		//创建一个Page对象
		Page<Map<String,Object>> page = new Page<Map<String,Object>>(pageNo, pageSize, count(condition,sqlStr), result);

		return page;
	}
	
	
	
	/**
	 * 根据查询条件统计总记录数
	 * @param condition
	 * @param sqlStr
	 * @return count
	 */
	public int count(final Condition condition,String sqlStr){
		
		final StringBuffer hql4Count 	= new StringBuffer();
		
		sqlStr = "select count(*) " + sqlStr;
		
		hql4Count.append(sqlStr) ;
		
		//组合查询条件
		if(condition != null && condition.getValues().size() > 0){
			setWhere(condition,hql4Count);
		}
		
		final String queryString = hql4Count.toString();
		
		if (logger.isDebugEnabled()) {
			logger.debug("hql4Count=" + queryString);
			logger.debug("args4Count="
					+ (condition != null ? StringUtils
							.collectionToCommaDelimitedString(condition
									.getValues()) : "null"));
		}
		
		//实现getJapTemLate的execute方法返回一个Long值
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
	 * @param condition
	 * @param hql
	 */
	public void setWhere(Condition condition,StringBuffer hql){
		hql.append(" WHERE ");
		hql.append(condition.getExpression());
	}

	
	/**
	 * 删除CarManNCert
	 * @parma certIds 
	 * @return
	 */
	public void deleteCarManNCert(Long certId) {
		if(certId != null){
			String sql = "delete BS_CARMAN_CERT cmcert where cmcert.cert_id ="+certId;
			this.jdbcTemplate.execute(sql);
		}
	}
	
	/**
	 * 删除批量CarManNCert
	 * @parma certIds 
	 * @return
	 */
	public void deleteCarManNCert(Long[] certIds) {
		ArrayList<Object> args = new ArrayList<Object>();
		StringBuffer sql = new StringBuffer();
		sql.append("delete BS_CARMAN_CERT cmcert where cmcert.cert_id in");
		if(certIds != null && certIds.length > 0){
			StringBuffer sf = new StringBuffer();
			sf.append("(?");
			for(int i=1;i < certIds.length;i++){
				sf.append(",?");
			}
			sf.append(")");
			sql.append(sf.toString());
			for(int i=0;i < certIds.length;i++){
				args.add(certIds[i]);
			}
		}
		
		if (logger.isDebugEnabled()) {
			logger.debug("sql=" + sql.toString());
			logger.debug("args="
					+ StringUtils.collectionToCommaDelimitedString(args));
		}
		//this.jdbcTemplate.execute(sql.toString(),args);
		this.jdbcTemplate.update(sql.toString(),args.toArray());
	}
	
	/**
	 * 保存证件与司机的关联表信息
	 * @parma carManId 
	 * @parma certId 
	 * @return
	 */
	public void carManNCert4Save(Long carManId, Long certId) {
		String sql = "select * from BS_CARMAN_CERT cmcert where cmcert.man_id = ? and cmcert.cert_id = ?";

		//jdbc查询BS_CARMAN_CERT表是否存在对应carManId和certId噶记录
		@SuppressWarnings("rawtypes")
		List list = this.jdbcTemplate.queryForList(sql,new Object[]{carManId,certId});
		
		if(list == null || list.size() < 1){
			String insertSql = "insert into BS_CARMAN_CERT(man_id,cert_id)values("+carManId+","+certId+")";
			//插入BS_CARMAN_CERT中间表
			this.jdbcTemplate.execute(insertSql);
		}
	}
	
	/**
	 * 根据certId查找carMan信息
	 * @parma certId 
	 * @return
	 */
	public Map<String,Object> findCarManMessByCertId(Long certId) {
		Map<String,Object> queryMap = null;
		String sql = "select m.id,m.name from BS_CARMAN_CERT mc left join BS_CARMAN m on m.id = mc.man_id" +
				" where mc.cert_id ="+certId;
		
		//jdbc查询BS_CARMAN记录
		queryMap = this.jdbcTemplate.queryForMap(sql);
		
		return queryMap;
	}
	
	/**
	 * 查找汽车证件列表
	 * @parma condition 
	 * @return
	 */
	public List<? extends Object> list4car(Condition condition, Long carId) {
		ArrayList<Object> args 	= new ArrayList<Object>();
		StringBuffer hql = new StringBuffer();
		
		hql.append("SELECT cert.id,cert.status")
		   .append(",car.plateType,car.plateNo")
		   .append(",cert.certCode,cert.type,cert.issueDate,cert.startDate,cert.endDate,cert.licencer")
		   .append(" FROM Car car JOIN car.certs cert WHERE car.id=?");
		
		if(carId != null && carId > 0){
			args.add(carId);
		}
		if(condition != null && condition.getValues().size() > 0){
			hql.append(" AND");
			hql.append(condition.getExpression());
			for (int i = 0;i < condition.getValues().size(); i++){
				args.add(condition.getValues().get(i));
			}
		}

		
		// 排序
		hql.append(" order by cert.id");
		if (logger.isDebugEnabled()) {
			logger.debug("hql=" + hql);
			logger.debug("args="
					+ (condition != null ? StringUtils
							.collectionToCommaDelimitedString(condition
									.getValues()) : "null"));
		}
		
		/*	jdbc查询	
		List list = this.jdbcTemplate.queryForList(hql.toString(),args.toArray());
		*/
		@SuppressWarnings("rawtypes")
		List list = this.getJpaTemplate().find(hql.toString(),args.toArray());
		
		//组装map数据,模拟domain列显示
		List<Map<String,Object>> result = new ArrayList<Map<String,Object>>();
		Map<String,Object> map;
		
		for(Object obj : list){
			
			Object [] ary = (Object[]) obj;
			map = new HashMap<String,Object>();
			map.put("id",			ary[0]);
			map.put("status", 		ary[1]);
			map.put("plateType", 	ary[2]);
			map.put("plateNo", 		ary[3]);
			map.put("certCode", 	ary[4]);
			map.put("type", 		ary[5]);
			map.put("issueDate", 	ary[6]);
			map.put("startDate", 	ary[7]);
			map.put("endDate", 		ary[8]);
			map.put("licencer", 	ary[9]);
			
			result.add(map);
		}
		return result;
	}
	
	/**
	 * 查找汽车证件分页
	 * @parma condition 
	 * @parma Page 
	 * @return
	 */
	public Page<? extends Object> page4car(final Condition condition, final int pageNo,
			int pageSize) {
		//设置最大页数,如果小于1都按1计算
		final int _pageSize = pageSize < 1 ? 1 : pageSize;	
		final StringBuffer hql = new StringBuffer();
		
		hql.append("SELECT cert.id,cert.status")
		   .append(",car.plateType,car.plateNo")
		   .append(",cert.certCode,cert.type,cert.issueDate,cert.startDate,cert.endDate,cert.licencer");
		
		//方便统计记录数
		String sqlStr =	" FROM Car car JOIN car.certs cert";
		
		hql.append(sqlStr);
		
		//组合查询条件
		if(condition != null && condition.getValues().size() > 0){
			setWhere(condition,hql);
		}
		//排序
		hql.append(" order by cert.id");
		if (logger.isDebugEnabled()) {
			logger.debug("pageNo=" + pageNo);
			logger.debug("pageSize=" + _pageSize);
			logger.debug("hql=" + hql);
			logger.debug("args="
					+ (condition != null ? StringUtils
							.collectionToCommaDelimitedString(condition
									.getValues()) : "null"));
		}
	
		
		@SuppressWarnings("rawtypes")
		//实现getJapTemLate的execute方法返回一个list
		List list = this.getJpaTemplate().execute(new JpaCallback<List<Object[]>>() {
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
				//计算某页第一条数据的全局索引号，从0开始并设置到queryObject
				queryObject.setFirstResult(Page.getFirstResult(pageNo,
						_pageSize));
				//设置最大页数
				queryObject.setMaxResults(_pageSize);
				//queryObject.getResultList()结果可返回任意类型需强制转换
				return (List<Object[]>) queryObject.getResultList();
			}
		});
		
		//组装map数据,模拟domain列显示
		List<Map<String,Object>> result = new ArrayList<Map<String,Object>>();
		Map<String,Object> map;
		
		for(Object obj : list){
			
			Object [] ary = (Object[]) obj;
			map = new HashMap<String,Object>();
			map.put("id",			ary[0]);
			map.put("status", 		ary[1]);
			map.put("plateType", 	ary[2]);
			map.put("plateNo", 		ary[3]);
			map.put("certCode", 	ary[4]);
			map.put("type", 		ary[5]);
			map.put("issueDate", 	ary[6]);
			map.put("startDate", 	ary[7]);
			map.put("endDate", 		ary[8]);
			map.put("licencer", 	ary[9]);
			
			result.add(map);
		}
		
		//创建一个Page对象
		Page<Map<String,Object>> page = new Page<Map<String,Object>>(pageNo, pageSize, count(condition,sqlStr), result);

		return page;
	}
	
	/**
	 * 删除单个CarNCert
	 * @parma certId 
	 * @return
	 */
	public void deleteCarNCert(Long certId) {
		if(certId != null){
			String sql = "delete BS_CAR_CERT carcert where carcert.cert_id ="+certId;
			this.jdbcTemplate.execute(sql);
		}
	}
	
	/**
	 * 删除批量CarNCert
	 * @parma certIds 
	 * @return
	 */
	public void deleteCarNCert(Long[] certIds) {
		ArrayList<Object> args = new ArrayList<Object>();
		StringBuffer sql = new StringBuffer();
		sql.append("delete BS_CAR_CERT carcert where carcert.cert_id in");
		if(certIds != null && certIds.length > 0){
			StringBuffer sf = new StringBuffer();
			sf.append("(?");
			for(int i=1;i < certIds.length;i++){
				sf.append(",?");
			}
			sf.append(")");
			sql.append(sf.toString());
			for(int i=0;i < certIds.length;i++){
				args.add(certIds[i]);
			}
		}
		
		if (logger.isDebugEnabled()) {
			logger.debug("sql=" + sql.toString());
			logger.debug("args="
					+ StringUtils.collectionToCommaDelimitedString(args));
		}
		//this.jdbcTemplate.execute(sql.toString(),args);
		this.jdbcTemplate.update(sql.toString(),args.toArray());
	}
	
	/**
	 * 保存证件与车辆的关联表信息
	 * @parma carId 
	 * @parma certId 
	 * @return
	 */
	public void carNCert4Save(Long carId, Long certId) {
		String sql = "select * from BS_CAR_CERT carcert where carcert.car_id = ? and carcert.cert_id = ?";

		//jdbc查询BS_CAR_CERT表是否存在对应carId和certId噶记录
		@SuppressWarnings("rawtypes")
		List list = this.jdbcTemplate.queryForList(sql,new Object[]{carId,certId});
		
		if(list == null || list.size() < 1){
			String insertSql = "insert into BS_CAR_CERT(car_id,cert_id)values("+carId+","+certId+")";
			//插入BS_CAR_CERT中间表
			this.jdbcTemplate.execute(insertSql);
		}
	}
	
	/**
	 * 根据certId查找car信息
	 * @parma certId 
	 * @return
	 */
	public Map<String, Object> findCarMessByCertId(Long certId) {
		Map<String,Object> queryMap = null;
		String sql = "SELECT c.id,c.plate_type,c.plate_no FROM BS_CAR_CERT cc LEFT JOIN BS_CAR c ON c.id = cc.car_id" +
				" WHERE cc.cert_id ="+certId;
		
		//jdbc查询BS_CAR记录
		queryMap = this.jdbcTemplate.queryForMap(sql);
		
		return queryMap;
	}
	
	/**
	 * 根据carId查找carMan详细信息
	 * @parma certId 
	 * @return
	 */
	public Map<String, Object> findCarByCarId(Long carId) {
		Map<String,Object> queryMap = null;
		String sql = "SELECT car.id,car.factory_type,car.factory_model,car.register_date,car.scrap_date,car.level_,car.vin,car.engine_no" +
				",car.total_weight,car.dim_len,car.dim_width,car.dim_height,car.access_weight,car.access_count FROM BS_CAR car " +
				" WHERE car.id ="+carId;
		
		//jdbc查询BS_CAR记录
		queryMap = this.jdbcTemplate.queryForMap(sql);
		
		return queryMap;
	}


	
	
	
	/*	
	 * 测试字符串拼装
 	public static void main(String[] args) {
		StringBuffer hql = new StringBuffer();	
		hql.append("SELECT cert.id,cert.status,")
		   .append("(SELECT cman.name FROM CarMan cman join cman.certs c WHERE c.id = cert.id),")
		   .append("cert.certCode,cert.type,cert.issueDate,cert.startDate,cert.licencer");
		
		String sqlStr =	" FROM Cert cert ";
		
		hql.append(sqlStr);	
		
		String[] strAry;
		String asName;
		strAry = hql.toString().toLowerCase().split("from");
		strAry = strAry[strAry.length-1].split(" ");
		asName = strAry[strAry.length-1];
		System.out.println("asName.length  is==========================" + strAry[strAry.length-1].length());
		System.out.println("asName is==========================" + strAry[strAry.length-1]);
		System.out.println("asName is==========================" + asName);
	
	}*/
	
}

	