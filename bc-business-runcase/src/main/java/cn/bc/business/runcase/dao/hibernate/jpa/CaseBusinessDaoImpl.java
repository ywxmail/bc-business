/**
 * 
 */
package cn.bc.business.runcase.dao.hibernate.jpa;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import cn.bc.BCConstants;
import cn.bc.business.runcase.dao.CaseBusinessDao;
import cn.bc.business.runcase.domain.Case4InfractBusiness;
import cn.bc.identity.web.SystemContext;
import cn.bc.identity.web.SystemContextHolder;
import cn.bc.orm.hibernate.jpa.HibernateCrudJpaDao;


/**
 * 营运交通违章Dao的hibernate jpa实现
 * 
 * @author dragon
 */
public class CaseBusinessDaoImpl extends HibernateCrudJpaDao<Case4InfractBusiness> implements CaseBusinessDao{
	protected final Log logger = LogFactory.getLog(getClass());
	private JdbcTemplate jdbcTemplate;

	@Autowired
	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	
	/**
	 *  author	   : wis.ho
	 *  update date: 2011-9-26
	 *  description: 因为Jap方法不能实现级联删除,所以重写delete方法.
	 */
	
	//单个物理删除
	@Override
	public void delete(Serializable id) {
		if (id == null)
			return;

		Case4InfractBusiness e = this.getJpaTemplate().find(Case4InfractBusiness.class, id);
		if (e != null)
			this.getJpaTemplate().remove(e);
	}

	//批量物理删除
	@Override
	public void delete(Serializable[] ids) {
		if (ids == null || ids.length == 0)
			return;

		for (Serializable pk : ids) {
			Case4InfractBusiness e = this.getJpaTemplate().find(Case4InfractBusiness.class, pk);
			if (e != null)
				this.getJpaTemplate().remove(e);
		}
	}

	public void update4Flow(Long id, Map<String, Object> attributes) {
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