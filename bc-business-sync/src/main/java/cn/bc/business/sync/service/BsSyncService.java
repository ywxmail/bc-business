package cn.bc.business.sync.service;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

import cn.bc.identity.domain.ActorHistory;

/**
 * 交委接口的交通违法信息同步服务接口
 * 
 * @author rongjih
 * 
 */
public interface BsSyncService {
	/**
	 * 同步交委接口的交通违法信息
	 * 
	 * @param syncer
	 *            执行同步操作的用户
	 * @param fromDate
	 *            起始日期
	 * @param toDate
	 *            结束日期
	 * @param strMsg
	 *            异常信息
	 * @return 新同步数据的条目数
	 */
	int doSync4JiaoWeiJTWF(ActorHistory syncer, Calendar fromDate,
			Calendar toDate, StringBuffer strMsg);

	/**
	 * 网络抓取金盾网的交通违法信息
	 * 
	 * @param syncer
	 *            执行同步操作的用户
	 * @param cars
	 *            要抓取的车辆的列表，Map中只包含如下属性的值：id、plateNo、plateType、engineNo
	 * @param strMsg
	 *            异常信息
	 * @return 新抓取数据的条目数
	 */
	int doSync4JinDunJTWF(ActorHistory syncer, List<Map<String, Object>> cars,
			StringBuffer strMsg);
}
