package cn.bc.business.socialSecurityRule.dao.hibernate.jpa;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.bc.business.socialSecurityRule.dao.SocialSecurityRuleDao;
import cn.bc.business.socialSecurityRule.domain.SocialSecurityRule;
import cn.bc.db.jdbc.RowMapper;
import cn.bc.orm.hibernate.jpa.HibernateCrudJpaDao;
import cn.bc.orm.hibernate.jpa.HibernateJpaNativeQuery;

public class SocialSecurityRuleDaoImpl extends HibernateCrudJpaDao<SocialSecurityRule> implements
		SocialSecurityRuleDao {

	public List<Map<String, String>> findAreaOption() {
		String hql="SELECT s.area_name,1";
		   hql+=" FROM bs_socialsecurityrule s";
		   hql+=" GROUP BY s.area_name"; 
		 return	HibernateJpaNativeQuery.executeNativeSql(getJpaTemplate(), hql,null
		 	,new RowMapper<Map<String, String>>() {
				public Map<String, String> mapRow(Object[] rs, int rowNum) {
					Map<String, String> oi = new HashMap<String, String>();
					int i = 0;
					oi.put("value", rs[i++].toString());
					return oi;
				}
		});
	}

	public List<Map<String, String>> findHouseTypeOption() {
		String hql="SELECT s.house_type,1";
		   hql+=" FROM bs_socialsecurityrule s";
		   hql+=" GROUP BY s.house_type"; 
		 return	HibernateJpaNativeQuery.executeNativeSql(getJpaTemplate(), hql,null
		 	,new RowMapper<Map<String, String>>() {
				public Map<String, String> mapRow(Object[] rs, int rowNum) {
					Map<String, String> oi = new HashMap<String, String>();
					int i = 0;
					oi.put("value", rs[i++].toString());
					return oi;
				}
		});
	}
	
	@SuppressWarnings("unchecked")
	public List<SocialSecurityRule> findSocialSecurityRules(String areaName,String houseType,int year){
		String hql="from SocialSecurityRule s";
		hql+=" where s.areaName=? and s.houseType=? and s.startYear <= ?";
		hql+=" order by s.startYear desc,s.startMonth desc";
		return this.getJpaTemplate().find(hql,areaName,houseType,year);
	}
	
	

	
}
