package cn.bc.business.motorcade.service;

import java.util.Calendar;

import cn.bc.business.motorcade.dao.HistoryCarQuantityDao;
import cn.bc.business.motorcade.domain.HistoryCarQuantity;
import cn.bc.core.service.DefaultCrudService;

public class HistoryCarQuantityServiceImpl extends
		DefaultCrudService<HistoryCarQuantity> implements
		HistoryCarQuantityService {
	private HistoryCarQuantityDao historyCarQuantityDao;

	public void setHistoryCarQuantityDao(
			HistoryCarQuantityDao historyCarQuantityDao) {
		this.historyCarQuantityDao = historyCarQuantityDao;
		this.setCrudDao(historyCarQuantityDao);
	}

	public void doRecordHistoryCarQuantity4Day() {
		this.historyCarQuantityDao.doRecordHistoryCarQuantity4Day();
	}

	public HistoryCarQuantity loadByDate(Long motorcadeId, Calendar date) {
		return this.historyCarQuantityDao.loadByDate(motorcadeId, date);
	}
}
