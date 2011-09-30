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
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.jpa.JpaCallback;
import org.springframework.util.StringUtils;

import cn.bc.business.contract.dao.ContractChargerDao;
import cn.bc.business.contract.domain.Contract4Charger;
import cn.bc.core.Page;
import cn.bc.core.query.condition.Condition;
import cn.bc.orm.hibernate.jpa.HibernateCrudJpaDao;


/**
 * 责任人合同Dao的hibernate jpa实现
 * 
 * @author dragon
 */
public class ContractChargerDaoImpl extends HibernateCrudJpaDao<Contract4Charger> implements ContractChargerDao{

	protected final Log logger =  LogFactory.getLog(getClass());
	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	//批量物理删除
	@Override
	public void delete(Serializable[] ids) {
		if (ids == null || ids.length == 0)
			return;

		for (Serializable pk : ids) {
			Contract4Charger e = this.getJpaTemplate().find(Contract4Charger.class, pk);
			if (e != null)
				this.getJpaTemplate().remove(e);
		}
	}
	
	/**
	 * 删除单个CarNContract
	 * @parma contractId 
	 * @return
	 */
	public void deleteCarNContract(Long contractId) {
		if(contractId != null){
			String sql = "delete BS_CAR_CONTRACT carcontract where carcontract.contract_id ="+contractId;
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
		sql.append("delete BS_CAR_CONTRACT carcontract where carcontract.contract_id in");
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
		String sql = "select * from BS_CAR_CONTRACT carcontract where carcontract.car_id = ? and carcontract.contract_id = ?";

		//jdbc查询BS_CAR_CONTRACT表是否存在对应carId和contractId噶记录
		@SuppressWarnings("rawtypes")
		List list = this.jdbcTemplate.queryForList(sql,new Object[]{carId,contractId});
		
		if(list == null || list.size() < 1){
			String insertSql = "insert into BS_CAR_CONTRACT(car_id,contract_id)values("+carId+","+contractId+")";
			//插入BS_CAR_CONTRACT中间表
			this.jdbcTemplate.execute(insertSql);
		}
		
	}

	/**
	 * 查找车辆合同列表
	 * @parma condition 
	 * @parma carId 
	 * @return
	 */
	public List<? extends Object> list4car(Condition condition, Long carId) {
		ArrayList<Object> args 	= new ArrayList<Object>();
		StringBuffer hql = new StringBuffer();
		
		hql.append("select contract.id, contract.code, contract.type, contract.signDate,contract.startDate,contract.endDate,contract.transactorName")
		   .append(" from Car car join car.contracts contract")
		   .append(" where car.id=?");
		
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
		hql.append(" order by contract.id");
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
		
		hql.append("select contract.id, contract.code, contract.type, contract.signDate,contract.startDate,contract.endDate,contract.transactorName");
		
		//方便统计记录数
		String sqlStr =	" from Car car join car.contracts contract";
		
		hql.append(sqlStr);
		
		//组合查询条件
		if(condition != null && condition.getValues().size() > 0){
			setWhere(condition,hql);
		}
		//排序
		hql.append(" order by contract.id");
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
		// TODO Auto-generated method stub
		return null;
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


}