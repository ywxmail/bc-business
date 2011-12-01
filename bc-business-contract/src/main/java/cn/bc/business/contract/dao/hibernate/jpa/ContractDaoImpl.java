/**
 * 
 */
package cn.bc.business.contract.dao.hibernate.jpa;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.StringUtils;

import cn.bc.business.contract.dao.ContractDao;
import cn.bc.business.contract.domain.Contract;
import cn.bc.core.query.condition.Condition;
import cn.bc.orm.hibernate.jpa.HibernateCrudJpaDao;


/**
 * 合同Dao的hibernate jpa实现
 * 
 * @author dragon
 */
public class ContractDaoImpl extends HibernateCrudJpaDao<Contract> implements ContractDao{
	private static Log logger = LogFactory.getLog(ContractDaoImpl.class);

	public List<Map<String, Object>> list4Car(Condition condition,Long carId) {
		ArrayList<Object> args 	= new ArrayList<Object>();
		StringBuffer hql = new StringBuffer();
		
		hql.append("select contract.id,contract.type, contract.ext_str1, contract.ext_str2, contract.signDate,contract.startDate,contract.endDate,contract.transactorName,contract.code")
		   .append(" from Car car join car.contracts contract")
		   .append(" where contract.main=0 and car.id=?");
		
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
			map.put("id",				ary[0]);
			map.put("type", 			ary[1]);
			map.put("ext_str1", 		ary[2]);
			map.put("ext_str2", 		ary[3]);
			map.put("signDate", 		ary[4]);
			map.put("startDate", 		ary[5]);
			map.put("endDate", 			ary[6]);
			map.put("transactorName", 	ary[7]);
			map.put("code", 			ary[8]);
			
			result.add(map);
		}
		return result;
	}

	public List<Map<String, Object>> list4CarMan(Condition condition,
			Long carManId) {
		ArrayList<Object> args 	= new ArrayList<Object>();
		StringBuffer hql = new StringBuffer();
		
		hql.append("select contract.id,contract.type, contract.ext_str1, contract.ext_str2, contract.signDate,contract.startDate,contract.endDate,contract.transactorName,contract.code")
		   .append(" from CarMan carman join carman.contracts contract")
		   .append(" where contract.main=0 and carman.id=?");
		
		if(carManId != null && carManId > 0){
			args.add(carManId);
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
			map.put("id",				ary[0]);
			map.put("type", 			ary[1]);
			map.put("ext_str1", 		ary[2]);
			map.put("ext_str2", 		ary[3]);
			map.put("signDate", 		ary[4]);
			map.put("startDate", 		ary[5]);
			map.put("endDate", 			ary[6]);
			map.put("transactorName", 	ary[7]);
			map.put("code", 			ary[8]);
			
			result.add(map);
		}
		return result;
	}

}