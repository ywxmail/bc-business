package cn.bc.business.fee.template.dao.hibernate.jpa;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.bc.BCConstants;
import cn.bc.business.fee.template.dao.FeeTemplateDao;
import cn.bc.business.fee.template.domain.FeeTemplate;
import cn.bc.core.query.condition.Condition;
import cn.bc.core.query.condition.impl.AndCondition;
import cn.bc.core.query.condition.impl.EqualsCondition;
import cn.bc.db.jdbc.RowMapper;
import cn.bc.orm.hibernate.jpa.HibernateCrudJpaDao;
import cn.bc.orm.hibernate.jpa.HibernateJpaNativeQuery;

public class FeeTemplateDaoImpl extends HibernateCrudJpaDao<FeeTemplate>
		implements FeeTemplateDao {

	// 返回模板
	public List<Map<String, String>> getTemplate() {
		String hql = "SELECT a.id,a.name ";
		hql += " FROM bs_fee_template a";
		hql += " WHERE a.status_=0 and a.type_=";
		hql += FeeTemplate.TYPE_TEMPLATE;
		return HibernateJpaNativeQuery.executeNativeSql(getJpaTemplate(), hql,
				null, new RowMapper<Map<String, String>>() {
					public Map<String, String> mapRow(Object[] rs, int rowNum) {
						Map<String, String> oi = new HashMap<String, String>();
						int i = 0;
						oi.put("key", rs[i++].toString());
						oi.put("value", rs[i++].toString());
						return oi;
					}
				});
	}

	// 检测模板是否存在费用
	public boolean isTemplateExistFee(Long id) {
		Condition c = new AndCondition().add(new EqualsCondition("pid", id))
				.add(new EqualsCondition("status", BCConstants.STATUS_ENABLED));
		return this.createQuery().condition(c).count() > 0;
	}

	// 检测费用是否属于模板
	public boolean isFeeBelong2Template(Long pid) {
		Condition c = new AndCondition().add(new EqualsCondition("id", pid))
				.add(new EqualsCondition("status", BCConstants.STATUS_ENABLED));
		return this.createQuery().condition(c).count() > 0;
	}

	// 返回属于此模板的费用集合
	public List<Map<String, String>> getFeeBelong2Template(Long pid) {
		String hql = "SELECT a.id,a.name,a.price,a.count_,a.pay_type,a.desc_,a.spec";
		hql += " FROM bs_fee_template a";
		hql += " WHERE a.status_=";
		hql += BCConstants.STATUS_ENABLED;
		hql += " and a.pid=? order by a.order_ asc";
		return HibernateJpaNativeQuery.executeNativeSql(getJpaTemplate(), hql,
				new Object[] { pid }, new RowMapper<Map<String, String>>() {
					public Map<String, String> mapRow(Object[] rs, int rowNum) {
						Map<String, String> oi = new HashMap<String, String>();
						int i = 0;
						oi.put("id", rs[i++].toString());
						oi.put("name", rs[i++].toString());
						oi.put("price", rs[i++].toString());
						oi.put("count", rs[i++].toString());
						oi.put("payType", rs[i++].toString());
						oi.put("desc", rs[i++].toString());
						Object spec = rs[i++];
						oi.put("spec", spec != null ? spec.toString() : "");
						return oi;
					}
				});
	}
}
