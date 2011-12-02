/**
 * 
 */
package cn.bc.business.contract.dao.hibernate.jpa;


import java.io.Serializable;
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
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.jpa.JpaCallback;
import org.springframework.util.StringUtils;

import cn.bc.business.contract.dao.ContractLabourDao;
import cn.bc.business.contract.domain.Contract;
import cn.bc.business.contract.domain.Contract4Labour;
import cn.bc.core.Page;
import cn.bc.core.query.condition.Condition;
import cn.bc.orm.hibernate.jpa.HibernateCrudJpaDao;


/**
 * 司机劳动合同Dao的hibernate jpa实现
 * 
 * @author dragon
 */
public class ContractLabourDaoImpl extends HibernateCrudJpaDao<Contract4Labour> implements ContractLabourDao{

	protected final Log logger =  LogFactory.getLog(getClass());
	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	@Override
	public void delete(Serializable[] ids) {
		if (ids == null || ids.length == 0)
			return;

		for (Serializable pk : ids) {
			Contract4Labour e = this.getJpaTemplate().find(Contract4Labour.class, pk);
			if (e != null)
				this.getJpaTemplate().remove(e);
		}
	}

	/**
	 * 删除单个CarManNContract
	 * @parma contractId 
	 * @return
	 */	
	public void deleteCarManNContract(Long contractId) {
		if(contractId != null){
			String sql = "delete from BS_CARMAN_CONTRACT where BS_CARMAN_CONTRACT.contract_id ="+contractId;
			this.jdbcTemplate.execute(sql);
		}
	}

	/**
	 * 删除批量CarManNContract
	 * @parma contractIds[] 
	 * @return
	 */
	public void deleteCarManNContract(Long[] contractIds) {
		ArrayList<Object> args = new ArrayList<Object>();
		StringBuffer sql = new StringBuffer();
		sql.append("delete from BS_CARMAN_CONTRACT where BS_CARMAN_CONTRACT.contract_id in");
		if(contractIds != null && contractIds.length > 0){
			StringBuffer sf = new StringBuffer();
			sf.append("(?");
			for(int i=1;i < contractIds.length;i++){
				sf.append(",?");
			}
			sf.append(")");
			sql.append(sf.toString());
			for(int i=0;i < contractIds.length;i++){
				args.add(contractIds[i]);
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
	 * 保存合同与司机的关联表信息
	 * @parma carManId 
	 * @parma contractId 
	 * @return
	 */
	public void carManNContract4Save(Long carManId, Long contractId) {
		String sql = "select * from BS_CARMAN_CONTRACT carmancontract where carmancontract.contract_id = ?";
		String insertSql = "";
		//jdbc查询BS_CARMAN_CONTRACT表是否存在对应carManId和contractId噶记录
		@SuppressWarnings("rawtypes")
		List list = this.jdbcTemplate.queryForList(sql,new Object[]{contractId});
		
		if(list == null || list.size() < 1){
			//插入BS_CARMAN_CONTRACT中间表
			insertSql = "insert into BS_CARMAN_CONTRACT(man_id,contract_id)values("+carManId+","+contractId+")";
			this.jdbcTemplate.execute(insertSql);
		}else{
			//删除BS_CARMAN_CONTRACT中间表重复数据
			String	delSql = "delete from BS_CARMAN_CONTRACT where BS_CARMAN_CONTRACT.contract_id="+contractId;
			this.jdbcTemplate.execute(delSql);
			
			//插入BS_CARMAN_CONTRACT中间表
			insertSql = "insert into BS_CARMAN_CONTRACT(man_id,contract_id)values("+carManId+","+contractId+")";
			this.jdbcTemplate.execute(insertSql);
		}
	}
	
	public void carNContract4Save(Long carId, Long contractId) {
		String sql = "select * from BS_CAR_CONTRACT carcontract where carcontract.contract_id = ?";
		String insertSql = "";
		//jdbc查询BS_CAR_CONTRACT表是否存在对应carId和contractId噶记录
		@SuppressWarnings("rawtypes")
		List list = this.jdbcTemplate.queryForList(sql,new Object[]{contractId});
		
		if(list == null || list.size() < 1){
			//插入BS_CAR_CONTRACT中间表
			insertSql = "insert into BS_CAR_CONTRACT(car_id,contract_id)values("+carId+","+contractId+")";
			this.jdbcTemplate.execute(insertSql);
		}else{
			//删除BS_CAR_CONTRACT中间表重复数据
			String	delSql = "delete from BS_CAR_CONTRACT where BS_CAR_CONTRACT.contract_id="+contractId;
			this.jdbcTemplate.execute(delSql);
			
			//插入BS_CAR_CONTRACT中间表
			insertSql = "insert into BS_CAR_CONTRACT(car_id,contract_id)values("+carId+","+contractId+")";
			this.jdbcTemplate.execute(insertSql);
		}
	}
	
	/**
	 * 查找劳动合同列表
	 * @parma condition 
	 * @parma carId 
	 * @return
	 */
	public List<Map<String, Object>> list4carMan(Condition condition, Long carManId) {
		ArrayList<Object> args 	= new ArrayList<Object>();
		StringBuffer hql = new StringBuffer();
		
		hql.append("select contract.id, contract.code, contract.type, contract.signDate,contract.startDate,contract.endDate,contract.transactorName,contract.ext_str1,contract.ext_str2,")
		   .append(" from CarMan carman join carman.contracts contract")
		   .append(" where carman.id=?");
		
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

		
		// 排序
		hql.append(" order by contract.signDate DESC");
		if (logger.isDebugEnabled()) {
			logger.debug("hql=" + hql);
			logger.debug("args="
					+ (condition != null ? StringUtils
							.collectionToCommaDelimitedString(condition
									.getValues()) : "null"));
		}
		
		@SuppressWarnings("rawtypes")
		List list = this.getJpaTemplate().find(hql.toString(),args.toArray());
		
		//组装map数据,模拟domain列显示
		List<Map<String,Object>> result = new ArrayList<Map<String,Object>>();
		Map<String,Object> map;
		
		for(Object obj : list){
			
			Object [] ary = (Object[]) obj;
			map = new HashMap<String,Object>();
			map.put("id",				ary[0]);
			map.put("code", 			ary[1]);
			map.put("type", 			ary[2]);
			map.put("signDate", 		ary[3]);
			map.put("startDate", 		ary[4]);
			map.put("endDate", 			ary[5]);
			map.put("transactorName", 	ary[6]);
			map.put("ext_str1", 		ary[7]);
			map.put("ext_str2", 		ary[8]);
			
			result.add(map);
		}
		return result;
	}

	/**
	 * 查找劳动合同分页
	 * @parma condition 
	 * @parma carId 
	 * @return
	 */
	public Page<Map<String,Object>> page4carMan(final Condition condition,final int pageNo,
			final int pageSize) {
		//设置最大页数,如果小于1都按1计算
		final int _pageSize = pageSize < 1 ? 1 : pageSize;	
		final StringBuffer hql = new StringBuffer();
		
		hql.append("select contract.id, contract.code, contract.type, contract.signDate,contract.startDate,contract.endDate,contract.transactorName,contract.ext_str1,contract.ext_str2");
		
		//方便统计记录数
		String sqlStr =	" from Contract contract WHERE contract.type="+Contract.TYPE_LABOUR;
		//String sqlStr =	" from CarMan carman join carman.contracts contract";
		
		hql.append(sqlStr);
		
		//组合查询条件
		if(condition != null && condition.getValues().size() > 0){
			setWhere(condition,hql);
		}
		//排序
		hql.append(" order by contract.signDate DESC");
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
			map.put("id",				ary[0]);
			map.put("code", 			ary[1]);
			map.put("type", 			ary[2]);
			map.put("signDate", 		ary[3]);
			map.put("startDate", 		ary[4]);
			map.put("endDate", 			ary[5]);
			map.put("transactorName", 	ary[6]);
			map.put("ext_str1", 		ary[7]);
			map.put("ext_str2", 		ary[8]);
			
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
		hql.append(" AND ");
		hql.append(condition.getExpression());
	}


	/**
	 * 根据carManId查找cert信息
	 * @parma carManId 
	 * @return
	 */
	public Map<String, Object> findCertByCarManId(Long carManId) {
		Map<String,Object> queryMap = null;
		String sql = "select cert.id,cert.cert_code from BS_CARMAN_CERT cc inner join BS_CERT cert on cc.cert_id = cert.id" +
				" where cert.type_=4 and cc.man_id="+carManId;
		
		//jdbc查询BS_CERT记录
		try {
			queryMap = this.jdbcTemplate.queryForMap(sql);
		} catch (EmptyResultDataAccessException e) {
			e.getStackTrace();
			//logger.error(e.getMessage(), e);
		}
		return queryMap;
	}
	
	public Long findCarIdByContractId(Long contractId) {
		String sql = "select cc.car_id from BS_CAR_CONTRACT cc where cc.contract_id="+contractId;
		Long carId = jdbcTemplate.queryForLong(sql);
		return carId;
	}
	
	public Long findCarManIdByContractId(Long contractId) {
		String sql = "select cc.man_id from BS_CARMAN_CONTRACT cc where cc.contract_id="+contractId;
		Long carManId = jdbcTemplate.queryForLong(sql);
		return carManId;
	}

	public Map<String, Object> findCarManByCarId(Long carId) {
		Map<String,Object> queryMap = null;
		String sql = "SELECT man.id,man.name,man.cert_fwzg FROM BS_CAR_DRIVER cd left join Bs_Carman man on cd.driver_id = man.id"+
					" where cd.car_id="+carId;
		
		//jdbc查询BS_CARMAN记录
		try {
			queryMap = this.jdbcTemplate.queryForMap(sql);
		} catch (EmptyResultDataAccessException e) {
			e.getStackTrace();
			//logger.error(e.getMessage(), e);
		}
		
		return queryMap;
	}

	public List<Map<String, Object>> selectRelateCarByCarManId(Long carManId) {
		List<Map<String,Object>> list = null;
		String sql = "SELECT car.id,car.plate_type,car.plate_no,car.bs_type,car.factory_type,car.factory_model,car.register_date,car.scrap_date,car.level_,car.vin,car.engine_no" +
				",car.total_weight,car.dim_len,car.dim_width,car.dim_height,car.access_weight,car.access_count,cd.car_id FROM BS_CAR_DRIVER cd left join BS_CAR car on cd.car_id = car.id" +
				" where cd.driver_id="+carManId;
		
		list = this.jdbcTemplate.queryForList(sql);
		
		return list;
	}

	public Map<String, Object> findCarManByCarManId(Long carManId) {
		Map<String,Object> queryMap = null;
		String sql = "SELECT man.id,man.name,man.sex,man.cert_fwzg,man.cert_identity,man.origin,man.house_type" +
					 ",man.birthdate FROM BS_CARMAN man" +
					 " where man.id="+carManId;
		
		//jdbc查询BS_CARMAN记录
		try {
			queryMap = this.jdbcTemplate.queryForMap(sql);
		} catch (EmptyResultDataAccessException e) {
			e.getStackTrace();
			//logger.error(e.getMessage(), e);
		}
		
		return queryMap;
	}

	public Map<String, Object> findCarByCarManId(Long carId) {
		Map<String,Object> queryMap = null;
		String sql = "SELECT car.id,car.plate_type,car.plate_no,car.bs_type,car.factory_type,car.factory_model,car.register_date,car.scrap_date,car.level_,car.vin,car.engine_no" +
					 ",car.total_weight,car.dim_len,car.dim_width,car.dim_height,car.access_weight,car.access_count FROM BS_CAR car"+
					 " where car.id="+carId;
		
		//jdbc查询BS_CAR记录
		try {
			queryMap = this.jdbcTemplate.queryForMap(sql);
		} catch (EmptyResultDataAccessException e) {
			e.getStackTrace();
			//logger.error(e.getMessage(), e);
		}
		
		return queryMap;
	}

	public List<Map<String, Object>> selectRelateCarManByCarId(Long carId) {
		List<Map<String,Object>> list = null;
		String sql = "SELECT man.id,man.name,man.sex,man.cert_fwzg,man.cert_identity,man.origin,man.house_type" +
				",man.birthdate,cd.driver_id FROM BS_CARMAN man left join BS_CAR_DRIVER cd on man.id=cd.driver_id" +
				" where cd.car_id="+carId;
		
		list = this.jdbcTemplate.queryForList(sql);
		
		return list;
	}

	/**
	 * 根据司机ID查找关联的司机否存在劳动合同
	 * @parma carManId 
	 * @return
	 */
	public List<Map<String, Object>> findCarManIsExistContract(Long carManId) {
		List<Map<String,Object>> list = null;
		String sql = "select c.* from BS_CONTRACT c"+
					" inner join BS_CARMAN_CONTRACT cc ON c.id = cc.contract_id where cc.man_id="+carManId;
		
		list = this.jdbcTemplate.queryForList(sql);
		return list;
	}


}