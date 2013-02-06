package cn.bc.business.motorcade.service;

import java.util.Calendar;

import cn.bc.business.motorcade.domain.HistoryCarQuantity;
import cn.bc.core.service.CrudService;

public interface HistoryCarQuantityService extends
		CrudService<HistoryCarQuantity> {
	/**
	 * 车队每日历史车辆数备份
	 */
	void doRecordHistoryCarQuantity4Day();

	/**
	 * 获取指定车队指定日的车辆数
	 * 
	 * @param motorcadeId
	 *            车队ID
	 * @param date
	 *            日期
	 * @return
	 */
	HistoryCarQuantity loadByDate(Long motorcadeId, Calendar date);
}
