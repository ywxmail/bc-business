package cn.bc.business.sync.service;

import java.util.Calendar;

import cn.bc.business.sync.domain.Sync4InfractTraffic;
import cn.bc.core.service.CrudService;
import cn.bc.identity.domain.ActorHistory;

/**
 * 交委接口的交通违法信息同步服务接口
 * 
 * @author rongjih
 * 
 */
public interface Sync4InfractTrafficService extends
		CrudService<Sync4InfractTraffic> {
	/**
	 * 执行同步处理
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
	int doSync(ActorHistory syncer, Calendar fromDate, Calendar toDate,
			StringBuffer strMsg);
}
