/**
 * 
 */
package cn.bc.business.contract.dao.hibernate.jpa;


import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
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
import org.springframework.jdbc.core.RowMapper;
import org.springframework.orm.jpa.JpaCallback;
import org.springframework.util.StringUtils;

import cn.bc.business.contract.dao.Contract4ChargerDao;
import cn.bc.business.contract.domain.Contract;
import cn.bc.business.contract.domain.Contract4Charger;
import cn.bc.core.Page;
import cn.bc.core.query.condition.Condition;
import cn.bc.orm.hibernate.jpa.HibernateCrudJpaDao;


/**
 * 责任人合同Dao的hibernate jpa实现
 * 
 * @author dragon
 */
public class Contract4ChargerDaoImpl extends HibernateCrudJpaDao<Contract4Charger> implements Contract4ChargerDao{

	protected final Log logger =  LogFactory.getLog(getClass());
	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	/**
	 * 删除单个CarNContract
	 * @parma contractId 
	 * @return
	 */
	public void deleteCarNContract(Long contractId) {
		if(contractId != null){
			String sql = "delete from BS_CAR_CONTRACT where BS_CAR_CONTRACT.contract_id ="+contractId;
			this.jdbcTemplate.execute(sql);
		}
	}

	/**
	 * 删除批量CarNContract
	 * @parma contractId 
	 * @return
	 */
	public void deleteCarNContract(Long[] contractIds) {
		ArrayList<Object> args = new ArrayList<Object>();
		StringBuffer sql = new StringBuffer();
		sql.append("delete from BS_CAR_CONTRACT where BS_CAR_CONTRACT.contract_id in");
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
	 * 保存合同与车辆的关联表信息
	 * @parma carId 
	 * @parma contractId 
	 * @return
	 */
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
	 * 查找车辆合同列表
	 * @parma condition 
	 * @parma carId 
	 * @return
	 */
	public List<Map<String, Object>> list4car(Condition condition, Long carId) {
		ArrayList<Object> args 	= new ArrayList<Object>();
		StringBuffer hql = new StringBuffer();
		
		hql.append("select contract.id, contract.code, contract.type, contract.signDate,contract.startDate,contract.endDate,contract.transactorName,contract.ext_str1,contract.ext_str2")
		   .append(" from Car car join car.contracts contract")
		   .append(" where car.id=?");
		
		if(carId != null && carId > 0){
			args.add(carId);
		}
		if(condition != null && condition.getValues().size() > 0){
			hql.append(" AND ");
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
	 * 查找车辆合同分页
	 * @parma condition 
	 * @parma carId 
	 * @return
	 */
	public Page<Map<String,Object>> page4car(final Condition condition,final int pageNo,
			final int pageSize) {
		//设置最大页数,如果小于1都按1计算
		final int _pageSize = pageSize < 1 ? 1 : pageSize;	
		final StringBuffer hql = new StringBuffer();
		
		hql.append("select contract.id, contract.code, contract.type, contract.signDate,contract.startDate,contract.endDate,contract.transactorName,contract.ext_str1,contract.ext_str2");
		
		//方便统计记录数
		String sqlStr =	" from Contract contract WHERE contract.type="+Contract.TYPE_CHARGER;
		//String sqlStr =	" from Car car join car.contracts contract";
		
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
	 * 根据contractId查找car信息
	 * @parma contractId 
	 * @return
	 */
	public Map<String, Object> findCarInfoByContractId(Long contractId) {
		return null;
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
	 * 根据contractId查找car信息
	 * @parma contractId 
	 * @return
	 */
	public List<String> findChargerIdByContractId(Long contractId) {
		String sql = "select cc.man_id from BS_CARMAN_CONTRACT cc where cc.contract_id="+contractId;
		
		List<String> list = jdbcTemplate.query(sql, new RowMapper<String>(){
			public String mapRow(ResultSet rs, int rowNum) throws SQLException {
				return rs.getString("man_id");
			}
		});
		return list;
	}

	/**
	 * 根据责任人ID和合同ID.保存到人员与合同中间表,不存在插入新纪录,存在删除.重新插入
	 * @param assignChargerIds
	 * @param contractId
	 */
	public void carMansNContract4Save(String assignChargerIds, Long contractId) {
		String sql = "select * from BS_CARMAN_CONTRACT carmancontract where carmancontract.contract_id = ?";
		String insertSql = "";
		//jdbc查询BS_CAR_CONTRACT表是否存在对应carId和contractId噶记录
		@SuppressWarnings("rawtypes")
		List list = this.jdbcTemplate.queryForList(sql,new Object[]{contractId});
		
		if(list == null || list.size() < 1){//不存在插入新纪录
			String [] carManAry = assignChargerIds.split(",");
			for(String carManId : carManAry){
				insertSql = "insert into BS_CARMAN_CONTRACT(man_id,contract_id)values("+carManId+","+contractId+")";
				this.jdbcTemplate.execute(insertSql);
			}
		}else{ //存在删除.重新插入
			String	delSql = "delete from BS_CARMAN_CONTRACT where BS_CARMAN_CONTRACT.contract_id="+contractId;
			this.jdbcTemplate.execute(delSql);
			
			String [] carManAry = assignChargerIds.split(",");
			for(String carManId : carManAry){
				insertSql = "insert into BS_CARMAN_CONTRACT(man_id,contract_id)values("+carManId+","+contractId+")";
				this.jdbcTemplate.execute(insertSql);
			}
		}
	}
	
	/**
	 * 根据合同ID查找关联责任人
	 * @param contractId
	 * @return
	 */
	public Long findCarIdByContractId(Long contractId) {
		String sql = "select cc.car_id from BS_CAR_CONTRACT cc where cc.contract_id="+contractId;
		Long carId = this.jdbcTemplate.queryForLong(sql);
		return carId;
	}

	/**
	 * JDBC
	 * 更新车辆表的负责人信息
	 * @param assignChargerNames
	 * @param carId
	 */
	public void updateCar4dirverName(String assignChargerNames, Long carId) {
		String sql = "UPDATE BS_CAR car SET charger=? WHERE car.id =? AND car.status_ =0";
		this.jdbcTemplate.update(sql, new Object[]{assignChargerNames,carId});
	}

	/**
	 * JDBC
	 * 更新司机表的负责人信息
	 * @param assignChargerNames
	 * @param carId
	 */
	public void updateCarMan4dirverName(String assignChargerNames, Long carId) {
		String sql = "UPDATE BS_CARMAN man SET charger=? WHERE man.id IN("+
					 "SELECT cd.driver_id FROM BS_CAR_DRIVER cd WHERE cd.car_id=? AND man.status_ =0)";
		this.jdbcTemplate.update(sql, new Object[]{assignChargerNames,carId});
	}

	/**
	 * 根据车辆ID查找车辆信息
	 * @param carId
	 * @return
	 */
	public Map<String, Object> findCarByCarId(Long carId) {
		Map<String,Object> queryMap = null;
		String sql = "SELECT car.id,car.plate_type,car.plate_no,car.factory_type,car.factory_model,car.register_date,car.scrap_date,car.level_,car.vin,car.engine_no" +
				",car.total_weight,car.dim_len,car.dim_width,car.dim_height,car.access_weight,car.access_count FROM BS_CAR car " +
				" WHERE car.id ="+carId;
		
		//jdbc查询BS_CAR记录
		try {
			queryMap = this.jdbcTemplate.queryForMap(sql);
		} catch (EmptyResultDataAccessException e) {
			e.getStackTrace();
			//logger.error(e.getMessage(), e);
		}
		
		return queryMap;
	}

	/**
	 * 根据司机ID查找车辆信息
	 * @param carManId
	 * @return
	 */
	public Map<String, Object> findCarByCarManId(Long carManId) {
		Map<String,Object> queryMap = null;
		String sql = "SELECT car.id,car.plate_type,car.plate_no,car.factory_type,car.factory_model,car.register_date,car.scrap_date,car.level_,car.vin,car.engine_no" +
				",car.total_weight,car.dim_len,car.dim_width,car.dim_height,car.access_weight,car.access_count FROM BS_CAR_DRIVER cd left join BS_CAR car on cd.car_id = car.id" +
				" where cd.driver_id="+carManId;
		
		//jdbc查询BS_CAR记录
		try {
			queryMap = this.jdbcTemplate.queryForMap(sql);
		} catch (EmptyResultDataAccessException e) {
			e.getStackTrace();
			//logger.error(e.getMessage(), e);
		}
		
		return queryMap;
	}

	/**
	 * 更新车辆表的负责人信息
	 * @param assignChargerNames
	 * @param carId
	 */
	public void updateCar4ChargerName(String assignChargerNames, Long carId) {
		ArrayList<Object> args = new ArrayList<Object>();
		StringBuffer hql = new StringBuffer();
		hql.append("UPDATE Car c SET c.charger=? WHERE c.id =?");
		args.add(assignChargerNames);
		args.add(carId);
		this.executeUpdate(hql.toString(), args);
	}

	/**
	 * 更新司机表的负责人信息
	 * @param assignChargerNames
	 * @param carId
	 */
	public void updateCarMan4ChargerName(String assignChargerNames, Long carId) {
		ArrayList<Object> args = new ArrayList<Object>();
		StringBuffer hql = new StringBuffer();
		hql.append("UPDATE CarMan man SET man.charger=? WHERE man.id IN(")
		   .append("SELECT cd.driver.id FROM CarByDriver cd WHERE cd.car.id=?)");
		args.add(assignChargerNames);
		args.add(carId);
		this.executeUpdate(hql.toString(), args);
	}
	
	/**
	 * 更新车辆表的负责人信息(调用存储过程)
	 * @param carId
	 */
	public void updateCar4ChargerName(Long carId) {
		String hql = "UPDATE Car c SET c.charger=getChargerInfoByCarId(id) WHERE c.id =?";
		List<Object> args = new ArrayList<Object>();
		args.add(carId);
		if (logger.isDebugEnabled()) {
			logger.debug("hql=" + hql);
			logger.debug("carId=" + carId);
		}
		this.executeUpdate(hql, args);
	}
	
	/**
	 * 更新司机表的负责人信息(调用存储过程)
	 * @param carId
	 */
	public void updateCarMan4ChargerName(Long carId) {
		String hql = "UPDATE CarMan man SET man.charger=getChargerInfoByDriverId(man.id) WHERE man.id IN(" +
				"SELECT cd.driver.id FROM CarByDriver cd WHERE cd.car.id=?)";
		List<Object> args = new ArrayList<Object>();
		args.add(carId);
		if (logger.isDebugEnabled()) {
			logger.debug("hql=" + hql);
			logger.debug("carId=" + carId);
		}
		this.executeUpdate(hql, args);
	}

	/** 判断指定的车辆是否已经存在经济合同 */
	public boolean isExistContract(Long carId) {
		String sql = "select c.* from BS_CONTRACT c"
				+ " inner join BS_CAR_CONTRACT cc ON c.id = cc.contract_id where c.type_="+Contract.TYPE_CHARGER+" and cc.car_id="
				+ carId;

		List<Map<String, Object>> list = this.jdbcTemplate.queryForList(sql);
		return list != null && list.size() > 0;
	}
	
	@Override
	public void delete(Serializable[] ids) {
		if (ids == null || ids.length == 0)
			return;
		for (Serializable id : ids) {
			this.delete(id);
		}
	}

	@Override
	public void delete(Serializable pk) {
		Long contractId = (Long) pk;

		// 删除合同与司机的关联信息
		this.deleteDriverRelation(contractId);

		// 删除合同与车辆的关联信息
		this.deleteCarRelation(contractId);

		// 删除合同自身
		Contract4Charger c = this.load(contractId);
//		Long pid = c.getPid();
		this.getJpaTemplate().remove(c);
//		this.executeUpdate("delete Contract4Labour where id=?",
//				new Object[] { contractId });

//		// 如有父级ID,递归删除父级记录
//		if (pid != null) {
//			delete(pid);
//		}
	}

	/**
	 * 删除合同与司机的关联信息
	 * 
	 * @parma contractId
	 * @return
	 */
	public void deleteDriverRelation(Long contractId) {
		if (contractId != null) {
			this.executeUpdate(
					"delete ContractCarManRelation where contractId=?",
					new Object[] { contractId });
		}
	}

	/**
	 * 删除合同与车辆的关联信息
	 * 
	 * @parma contractId
	 * @return
	 */
	public void deleteCarRelation(Long contractId) {
		if (contractId != null) {
			this.executeUpdate("delete ContractCarRelation where contractId=?",
					new Object[] { contractId });
		}
	}

	/**
	 * 根据合同ID查找关联责任人
	 * @param contractId
	 * @return
	 */
	public List<String> findChargerNameByContractId(Long contractId) {
		String sql = "select c.name from BS_CARMAN_CONTRACT cc" +
				" inner join BS_CARMAN c on cc.man_id = c.id" +
				" where cc.contract_id="+contractId;
		
		List<String> list = jdbcTemplate.query(sql, new RowMapper<String>(){
			public String mapRow(ResultSet rs, int rowNum) throws SQLException {
				return rs.getString("name");
			}
		});
		return list;
	}

	/**
	 * 判断经济合同自编号唯一
	 * @param excludeId
	 * @param code
	 * @return
	 */
	public Long checkCodeIsExist(Long excludeId,String code){
		String sql = "select c.id as id from BS_CONTRACT c"+ 
				" inner join BS_CONTRACT_CHARGER cc ON c.id = cc.id"+
				" where c.status_=? and c.code=? ";
		Object[] args;
		if (excludeId != null) {
			sql += " and c.id!=?";
			args = new Object[] { new Integer(Contract.STATUS_NORMAL), code,
					excludeId };
		} else {
			args = new Object[] { new Integer(Contract.STATUS_NORMAL), code };
		}
		List<Map<String, Object>> list = this.jdbcTemplate.queryForList(sql,
				args);
		if (list != null && !list.isEmpty())
			return new Long(list.get(0).get("id").toString());
		else
			return null;
	}

}