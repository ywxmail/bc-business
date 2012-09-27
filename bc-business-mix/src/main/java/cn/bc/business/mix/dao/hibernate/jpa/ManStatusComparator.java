package cn.bc.business.mix.dao.hibernate.jpa;

import java.util.Comparator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.json.JSONObject;

public class ManStatusComparator implements Comparator<JSONObject> {
	protected static final Log logger = LogFactory
			.getLog(ManStatusComparator.class);

	public int compare(JSONObject m1, JSONObject m2) {
		try {
			int status1 = m1.getInt("judgeStatus");
			int status2 = m2.getInt("judgeStatus");

			int r;
			if (status1 < status2) {
				r = -1;
			} else if (status1 > status2) {
				r = 1;
			} else {
				r = 0;
				// 状态相等就使用类型进行排序比较
				String type1 = m1.getString("judgeType");
				String type2 = m2.getString("judgeType");

				if ("责任人".equals(type1)) {
					if ("责任人".equals(type2)) {
						r = 0;
					} else if ("司机和责任人".equals(type2)) {
						r = -1;
					} else if ("司机".equals(type2)) {
						r = -1;
					}
				} else if ("司机和责任人".equals(type1)) {
					if ("责任人".equals(type2)) {
						r = 1;
					} else if ("司机和责任人".equals(type2)) {
						r = 0;
					} else if ("司机".equals(type2)) {
						r = -1;
					}
				} else if ("司机".equals(type1)) {
					if ("责任人".equals(type2)) {
						r = 1;
					} else if ("司机和责任人".equals(type2)) {
						r = 1;
					} else if ("司机".equals(type2)) {
						r = 0;
					}
				}
			}
			return r;
		} catch (JSONException e) {
			logger.error(e.getMessage(), e);
		}
		return 0;
	}
}
