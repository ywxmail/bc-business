package cn.bc.business.motorcade.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import cn.bc.business.motorcade.dao.HistoryCarQuantityDao;
import cn.bc.business.motorcade.dao.MotorcadeDao;
import cn.bc.business.motorcade.domain.Motorcade;
import cn.bc.core.service.DefaultCrudService;

public class MotorcadeServiceImpl extends DefaultCrudService<Motorcade>
		implements MotorcadeService {
	private MotorcadeDao motorcadeDao;
	private HistoryCarQuantityDao historyCarQuantityDao;

	public void setMotorcadeDao(MotorcadeDao motorcadeDao) {
		this.motorcadeDao = motorcadeDao;
		this.setCrudDao(motorcadeDao);
	}

	public void setHistoryCarQuantityDao(
			HistoryCarQuantityDao historyCarQuantityDao) {
		this.historyCarQuantityDao = historyCarQuantityDao;

	}

	@Override
	public void delete(Serializable id) {
		// 删除历史车辆数
		this.historyCarQuantityDao.deleteByMotorcade(id);

		// 删除车队
		this.motorcadeDao.delete(id);
	}

	@Override
	public void delete(Serializable[] ids) {
		this.motorcadeDao.delete(ids);
	}

	public List<Motorcade> findActive() {
		return this.motorcadeDao.findActive();
	}

	public List<Map<String, String>> findEnabled4Option() {
		return this.motorcadeDao.findEnabled4Option();
	}

	public List<Map<String, String>> find4Option(Integer[] statuses) {
		return this.motorcadeDao.find4Option(statuses);
	}
}
