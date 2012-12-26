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

import cn.bc.business.runcase.dao.CaseTrafficDao;
import cn.bc.business.runcase.domain.Case4InfractTraffic;
import cn.bc.orm.hibernate.jpa.HibernateCrudJpaDao;
import cn.bc.web.ui.json.Json;

/**
 * 营运交通违章Dao的hibernate jpa实现
 * 
 * @author dragon
 */
public class CaseTrafficDaoImpl extends
		HibernateCrudJpaDao<Case4InfractTraffic> implements CaseTrafficDao {
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

		Case4InfractTraffic e = this.getJpaTemplate().find(
				Case4InfractTraffic.class, id);
		if (e != null)
			this.getJpaTemplate().remove(e);
	}

	// 批量物理删除
	@Override
	public void delete(Serializable[] ids) {
		if (ids == null || ids.length == 0)
			return;

		for (Serializable pk : ids) {
			Case4InfractTraffic e = this.getJpaTemplate().find(
					Case4InfractTraffic.class, pk);
			if (e != null)
				this.getJpaTemplate().remove(e);
		}
	}

	public String getCaseTrafficInfoByCarManId(Long carManId,
			Calendar startDate, Calendar endDate) {
		StringBuffer hql = new StringBuffer();

		hql.append("select c.jeom from Case4InfractTraffic c")
				.append(" where ((c.happenDate>? or c.happenDate=?) and (c.happenDate<? or c.happenDate=?))")
				.append(" and c.driverId=?");

		logger.debug("carManId: "
				+ carManId
				+ " startDate:"
				+ new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(startDate
						.getTime())

				+ " endDate:"
				+ new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(endDate
						.getTime()));
		@SuppressWarnings("rawtypes")
		List list = this.getJpaTemplate()
				.find(hql.toString(),
						new Object[] { startDate, startDate, endDate, endDate,
								carManId });

		// 违法宗数
		int count = list.size();
		// 累计扣分
		int accumulatedPoints = 0;
		for (Object obj : list) {
			if (obj != null) {
				accumulatedPoints += ((Float) obj).intValue();
			}
		}
		// 剩余分数
		int remainder;
		if ((12 - accumulatedPoints > 0) || (12 - accumulatedPoints == 0)) {
			remainder = 12 - accumulatedPoints;
		} else {
			remainder = 0;
		}
		Json json = new Json();
		json.put("count", count);
		json.put("accumulatedPoints", accumulatedPoints);
		json.put("remainder", remainder);

		return json.toString();
	}
}