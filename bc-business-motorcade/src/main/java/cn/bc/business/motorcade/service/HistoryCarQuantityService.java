package cn.bc.business.motorcade.service;

import cn.bc.business.motorcade.domain.HistoryCarQuantity;
import cn.bc.core.service.CrudService;

public interface HistoryCarQuantityService extends
		CrudService<HistoryCarQuantity> {
	/**
	 * 车队每日历史车辆数备份
	 */
	void doRecordHistoryCarQuantity4Day();
}
