/**
 * 
 */
package cn.bc.business.runcase.dao.hibernate.jpa;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.StringUtils;

import cn.bc.BCConstants;
import cn.bc.business.runcase.dao.CaseAdviceDao;
import cn.bc.business.runcase.domain.Case4Advice;
import cn.bc.business.runcase.domain.CaseBase;
import cn.bc.core.util.DateUtils;
import cn.bc.identity.web.SystemContext;
import cn.bc.identity.web.SystemContextHolder;
import cn.bc.orm.hibernate.jpa.HibernateCrudJpaDao;
import cn.bc.web.ui.json.Json;

/**
 * 营运投诉与建议Dao的hibernate jpa实现
 * 
 * @author dragon
 */
public class CaseAdviceDaoImpl extends HibernateCrudJpaDao<Case4Advice>
		implements CaseAdviceDao {
	protected final Log logger = LogFactory.getLog(getClass());
	private JdbcTemplate jdbcTemplate;

	@Autowired
	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	/**
	 * author : wis.ho update date: 2011-9-26 description:
	 * 因为Jap方法不能实现级联删除,所以重写delete方法.
	 */

	// 单个物理删除
	@Override
	public void delete(Serializable id) {
		if (id == null)
			return;

		Case4Advice e = this.getJpaTemplate().find(Case4Advice.class, id);
		if (e != null)
			this.getJpaTemplate().remove(e);
	}

	// 批量物理删除
	@Override
	public void delete(Serializable[] ids) {
		if (ids == null || ids.length == 0)
			return;

		for (Serializable pk : ids) {
			Case4Advice e = this.getJpaTemplate().find(Case4Advice.class, pk);
			if (e != null)
				this.getJpaTemplate().remove(e);
		}
	}

	@SuppressWarnings("rawtypes")
	public String getCaseTrafficInfoByCarManId(Long carManId,
			Calendar startDate, Calendar endDate) {
		StringBuffer hql = new StringBuffer();

		String sql = "select subject,count(subject) from bs_case_base where (happen_date>to_date(?,'YYYY-MM-DD HH24:MI:SS') or happen_date=to_date(?,'YYYY-MM-DD HH24:MI:SS')) and (happen_date<to_date(?,'YYYY-MM-DD HH24:MI:SS') or happen_date=to_date(?,'YYYY-MM-DD HH24:MI:SS')) and driver_id=? and type_=? group by subject";

		hql.append("select c.id from CaseBase c")
				.append(" where ((c.happenDate>? or c.happenDate=?) and (c.happenDate<? or c.happenDate=?))")
				.append(" and c.driverId=? and c.type=?");
		Object[] args = new Object[] { startDate, startDate, endDate, endDate,
				carManId, CaseBase.TYPE_COMPLAIN };
		if (logger.isDebugEnabled()) {
			logger.debug("args="
					+ StringUtils.arrayToCommaDelimitedString(args));
			logger.debug("startDate="
					+ DateUtils.formatCalendar2Second(startDate) + ",endDate="
					+ DateUtils.formatCalendar2Second(endDate));
			logger.debug("sql=" + sql);
		}
		// 客管投诉
		List<Map<String, Object>> list4keguantousu = null;
		try {
			list4keguantousu = this.jdbcTemplate.queryForList(sql, args);

		} catch (EmptyResultDataAccessException e) {
			e.getStackTrace();
		}

		// 公司投诉
		List list4gongsitousu = this.getJpaTemplate().find(
				hql.toString(),
				new Object[] { startDate, startDate, endDate, endDate,
						carManId, CaseBase.TYPE_COMPANY_COMPLAIN });

		// 交通违章
		List list4jiaotongweizhang = this.getJpaTemplate().find(
				hql.toString(),
				new Object[] { startDate, startDate, endDate, endDate,
						carManId, CaseBase.TYPE_INFRACT_TRAFFIC });

		// 营运违章
		List list4yingyunweizhang = this.getJpaTemplate().find(
				hql.toString(),
				new Object[] { startDate, startDate, endDate, endDate,
						carManId, CaseBase.TYPE_INFRACT_BUSINESS });
		// 事故理赔
		List list4shigulipei = this.getJpaTemplate().find(
				hql.toString(),
				new Object[] { startDate, startDate, endDate, endDate,
						carManId, CaseBase.TYPE_ACCIDENT });
		// 组装客管投诉详细信息
		String keguantousuInfo = "";

		if (list4keguantousu.size() != 0) {
			for (int i = 0; i < list4keguantousu.size(); i++) {
				Map<String, Object> m = list4keguantousu.get(i);
				if (i == 0) {
					keguantousuInfo += m.get("subject").toString()
							+ m.get("count").toString() + "宗";

				} else {
					keguantousuInfo += "," + m.get("subject").toString()
							+ m.get("count").toString() + "宗";
				}

			}
		}
		Json json = new Json();
		json.put("count4keguantousu", list4keguantousu.size());
		if (keguantousuInfo.trim().length() != 0) {
			json.put("keguantousuInfo", keguantousuInfo);
		}
		json.put("count4keguantousu", list4keguantousu.size());
		json.put("count4gongsitousu", list4gongsitousu.size());
		json.put("count4jiaotongweizhang", list4jiaotongweizhang.size());
		json.put("count4yingyunweizhang", list4yingyunweizhang.size());
		json.put("count4shigulipei", list4shigulipei.size());
		json.put("startDate",
				new SimpleDateFormat("yyyy-MM-dd").format(startDate.getTime()));
		json.put("endDate", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
				.format(endDate.getTime()));

		return json.toString();
	}

	public void updateCaseAdviceInfo4Flow(Long id,
			Map<String, Object> attributes) {
		final List<Object> args = new ArrayList<Object>();
		final StringBuffer sql = new StringBuffer();
		sql.append("update bs_case_base");
		// set
		int i = 0;
		// 状态是否为注销
		boolean isLogout = false;
		Object value;
		for (String key : attributes.keySet()) {
			value = attributes.get(key);
			if (value != null) {
				if (i > 0)
					sql.append("," + key + "=?");
				else
					sql.append(" set " + key + "=?");
				args.add(attributes.get(key));
				logger.debug(attributes.get(key) + "  :  "
						+ attributes.get(key).getClass());
			} else {
				if (i > 0)
					sql.append("," + key + "=null");
				else
					sql.append(" set " + key + "=null");
			}

			// 判断状态是否为注销
			if (key.equals("status") || key.equals("status_")) {
				if (value != null) {
					if (value.equals(BCConstants.STATUS_DISABLED)) {
						isLogout = true;
					}
				}
			}

			i++;
		}

		// 如果状态为注销就添加注销人和注销时间
		if (isLogout) {
			sql.append(",close_date=?,closer_id=?,closer_name=?");
			args.add(Calendar.getInstance());
			SystemContext context = SystemContextHolder.get();
			args.add(context.getUserHistory().getId());
			args.add(context.getUserHistory().getName());
		}

		// pks
		if (id != null) {
			sql.append(" where id=?");
			args.add(id);
		}
		logger.debug("sql: " + sql);
		this.jdbcTemplate.update(sql.toString(), args.toArray());
	}
}