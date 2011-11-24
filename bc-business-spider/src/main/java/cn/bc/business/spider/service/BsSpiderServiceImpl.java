package cn.bc.business.spider.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.orm.jpa.JpaTemplate;

import cn.bc.business.car.domain.Car;
import cn.bc.business.spider.JinDunSpider4JiaoTongWeiFa;
import cn.bc.core.cache.Cache;
import cn.bc.core.query.condition.Direction;
import cn.bc.core.query.condition.impl.AndCondition;
import cn.bc.core.query.condition.impl.EqualsCondition;
import cn.bc.core.query.condition.impl.OrderCondition;
import cn.bc.core.util.DateUtils;
import cn.bc.db.jdbc.RowMapper;
import cn.bc.db.jdbc.SqlObject;
import cn.bc.orm.hibernate.jpa.HibernateJpaNativeQuery;

/**
 * 外网爬虫器
 * 
 * @author rongjih
 * 
 */
public class BsSpiderServiceImpl implements BsSpiderService {
	private static Log logger = LogFactory.getLog(BsSpiderServiceImpl.class);
	private final static String CACHE_KEY_JINDUN_JTWZ = "JinDunJiaoTongWeiZhang";
	private Cache cache;
	private JinDunSpider4JiaoTongWeiFa jinDunSpider4JiaoTongWeiFa;
	private JpaTemplate jpaTemplate;

	public JpaTemplate getJpaTemplate() {
		return jpaTemplate;
	}

	@Autowired
	public void setJpaTemplate(JpaTemplate jpaTemplate) {
		this.jpaTemplate = jpaTemplate;
	}

	@Autowired
	public void setCache(@Qualifier("jinDunCache") Cache cache) {
		// ehcache.xml中配置该缓存时间为1天，以避免重复抓取
		this.cache = cache;
	}

	public Map<String, List<Map<String, Object>>> findJinDunJiaoTongWeiZhang(
			String carIds) {
		Map<String, List<Map<String, Object>>> all = findAll();

		if (carIds == null || carIds.length() == 0)
			return all;

		String[] ids = carIds.split(",");
		Map<String, List<Map<String, Object>>> need = new HashMap<String, List<Map<String, Object>>>();
		for (String id : ids) {
			if (all.containsKey(id))
				need.put(id, all.get(id));
		}
		return need;
	}

	public Map<String, List<Map<String, Object>>> findAll() {
		if (cache.get(CACHE_KEY_JINDUN_JTWZ) == null) {
			logger.warn("Find all from jinDun and cache them.");
			Map<String, List<Map<String, Object>>> all = this.doSpider();
			cache.put(CACHE_KEY_JINDUN_JTWZ, all);
			return all;
		} else {
			if (logger.isDebugEnabled())
				logger.debug("load all from cache.");
			return cache.get(CACHE_KEY_JINDUN_JTWZ);
		}
	}

	private Map<String, List<Map<String, Object>>> doSpider() {
		if (jinDunSpider4JiaoTongWeiFa == null) {
			jinDunSpider4JiaoTongWeiFa = new JinDunSpider4JiaoTongWeiFa();
		}

		// 获取所有在案车辆的信息
		List<Map<String, Object>> all = new HibernateJpaNativeQuery<Map<String, Object>>(
				jpaTemplate, getSqlObject()).condition(
				new AndCondition().add(
						new EqualsCondition("c.status_", new Integer(
								Car.STATUS_ENABLED))).add(
						new OrderCondition("c.register_date", Direction.Desc)))
				.list();
		if (logger.isWarnEnabled()) {
			logger.warn("在案车辆数=" + all.size());
		}

		Map<String, List<Map<String, Object>>> all4Map = new LinkedHashMap<String, List<Map<String, Object>>>();

		// 循环每部车从金盾网获取未处理的交通违法信息
		List<Map<String, Object>> details;
		Map<String, Object> detail;
		int i = 0, j = 0;
		Date fromDate = new Date();
		try {
			String plateNo, engineNo;
			for (Map<String, Object> one : all) {
				j++;
				plateNo = one.get("plate_no").toString();
				engineNo = one.get("engine_no") != null ? one.get("engine_no")
						.toString() : null;
				if (engineNo == null || engineNo.length() < 4) {
					logger.warn("不规范的机动车号，忽略不处理：plateNo=" + plateNo);
					continue;
				}
				List<Map<String, Object>> jtwf = jinDunSpider4JiaoTongWeiFa
						.setPlateNo(plateNo).setEngineNo(engineNo).excute();

				if (jtwf != null && !jtwf.isEmpty()) {
					details = new ArrayList<Map<String, Object>>();
					for (Map<String, Object> map : jtwf) {
						detail = new LinkedHashMap<String, Object>();
						details.add(detail);

						// 合并one和map到detail
						detail.putAll(one);
						detail.putAll(map);
						if (logger.isInfoEnabled()) {
							logger.info((i++) + ":" + detail);
						}
					}
					all4Map.put(one.get("id").toString(), details);
				} else {
					if (logger.isDebugEnabled()) {
						logger.debug(j + ":" + one.get("plate_no")
								+ " 没有未处理的交通违法信息");
					}
				}
			}
		} catch (Exception e) {
			// 产生异常就终止抓取
			logger.error("连接异常，终止了连接(toNum=" + j + ")。" + e.getMessage(), e);
		}
		if (logger.isWarnEnabled()) {
			logger.warn("从金盾网获取在案车辆的未处理交通违法信息总耗时："
					+ DateUtils.getWasteTime(fromDate));
		}

		return all4Map;
	}

	private SqlObject<Map<String, Object>> getSqlObject() {
		SqlObject<Map<String, Object>> sqlObject = new SqlObject<Map<String, Object>>();

		// 构建查询语句,where和order by不要包含在sql中(要统一放到condition中)
		StringBuffer sql = new StringBuffer();
		sql.append("select c.id,c.plate_type,c.plate_no,c.engine_no,c.driver,c.charger");
		sql.append(",c.register_date,c.bs_type");
		sql.append(",c.motorcade_id,m.name");
		sql.append(" from bs_car c");
		sql.append(" inner join bs_motorcade m on m.id=c.motorcade_id");
		sqlObject.setSql(sql.toString());

		// 注入参数
		sqlObject.setArgs(null);

		// 数据映射器
		sqlObject.setRowMapper(new RowMapper<Map<String, Object>>() {
			public Map<String, Object> mapRow(Object[] rs, int rowNum) {
				Map<String, Object> map = new HashMap<String, Object>();
				int i = 0;
				map.put("id", rs[i++]);
				map.put("plate_type", rs[i++]);
				map.put("plate_no", rs[i++]);
				map.put("engine_no", rs[i++]);
				map.put("driver", rs[i++]);
				map.put("charger", rs[i++]);
				map.put("register_date", rs[i++]);
				map.put("bs_type", rs[i++]);
				map.put("motorcade_id", rs[i++]);
				map.put("motorcade_name", rs[i++]);
				return map;
			}
		});
		return sqlObject;
	}
}
