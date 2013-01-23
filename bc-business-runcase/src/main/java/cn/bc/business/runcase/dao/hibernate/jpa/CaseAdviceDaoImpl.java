/**
 * 
 */
package cn.bc.business.runcase.dao.hibernate.jpa;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cn.bc.business.runcase.dao.CaseAdviceDao;
import cn.bc.business.runcase.domain.Case4Advice;
import cn.bc.business.runcase.domain.CaseBase;
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

		hql.append("select c.id from CaseBase c")
				.append(" where ((c.happenDate>? or c.happenDate=?) and (c.happenDate<? or c.happenDate=?))")
				.append(" and c.driverId=? and c.type=?");

		logger.debug("carManId: "
				+ carManId
				+ " startDate:"
				+ new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(startDate
						.getTime())

				+ " endDate:"
				+ new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(endDate
						.getTime()));
		// 客管投诉
		List list4keguantousu = this.getJpaTemplate().find(
				hql.toString(),
				new Object[] { startDate, startDate, endDate, endDate,
						carManId, CaseBase.TYPE_COMPLAIN });

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

		Json json = new Json();
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
}