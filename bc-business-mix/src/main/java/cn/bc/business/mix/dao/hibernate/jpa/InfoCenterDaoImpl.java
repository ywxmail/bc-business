package cn.bc.business.mix.dao.hibernate.jpa;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.JpaCallback;
import org.springframework.orm.jpa.JpaTemplate;
import org.springframework.util.StringUtils;

import cn.bc.business.mix.dao.InfoCenterDao;

/**
 * 信息中心综合查询
 * 
 * @author dragon
 * 
 */
public class InfoCenterDaoImpl implements InfoCenterDao {
	protected static final Log logger = LogFactory
			.getLog(InfoCenterDaoImpl.class);
	private JpaTemplate jpaTemplate;

	@Autowired
	public void setJpaTemplate(JpaTemplate jpaTemplate) {
		this.jpaTemplate = jpaTemplate;
	}

	public JSONArray findCars(Long unitId, Long motorcadeId) {
		if (unitId == null && motorcadeId == null)
			return new JSONArray();

		final StringBuffer sql = new StringBuffer();
		final List<Object> args = new ArrayList<Object>();
		sql.append("select c.id,c.code,c.plate_type,c.plate_no from bs_car c");
		sql.append(" inner join bs_motorcade m on m.id=c.motorcade_id");
		sql.append(" inner join bc_identity_actor unit on unit.id=m.unit_id");
		if (unitId != null) {
			sql.append(" where unit.id=?");
			args.add(unitId);
			if (motorcadeId != null) {
				sql.append(" and m.id=?");
				args.add(motorcadeId);
			}
		} else {
			sql.append(" where m.id=?");
			args.add(motorcadeId);
		}
		sql.append(" order by c.register_date desc");

		if (logger.isDebugEnabled()) {
			logger.debug("args="
					+ StringUtils.collectionToCommaDelimitedString(args)
					+ ";sql=" + sql);
		}
		return this.jpaTemplate.execute(new JpaCallback<JSONArray>() {
			@SuppressWarnings("unchecked")
			public JSONArray doInJpa(EntityManager em)
					throws PersistenceException {
				Query queryObject = em.createNativeQuery(sql.toString());
				// jpaTemplate.prepareQuery(queryObject);

				// 注入参数
				if (args != null) {
					int i = 0;
					for (Object value : args) {
						queryObject.setParameter(i + 1, value);// jpa的索引号从1开始
						i++;
					}
				}

				List<Object[]> rs = queryObject.getResultList();
				JSONArray jsons = new JSONArray();
				if (rs != null) {
					JSONObject json;
					for (int j = 0; j < rs.size(); j++) {
						json = new JSONObject();
						try {
							json.put("id", rs.get(j)[0]);
							json.put("code", rs.get(j)[1]);
							json.put("plate", rs.get(j)[2] + "." + rs.get(j)[3]);
						} catch (JSONException e) {
							logger.error(e.getMessage(), e);
						}
						jsons.put(json);
					}
				}
				return jsons;
			}
		});
	}

	public JSONArray findCars(String searchType, String searchText) {
		// TODO Auto-generated method stub
		return null;
	}

	public JSONObject findCarDetail(Long carId) throws Exception {
		JSONObject json = new JSONObject();
		JSONObject car = new JSONObject();
		json.put("id", carId);
		json.put("car", car);
		car.put("id", carId);
		return json;
	}
}
