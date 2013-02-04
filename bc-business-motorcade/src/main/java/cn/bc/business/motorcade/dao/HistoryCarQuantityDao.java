package cn.bc.business.motorcade.dao;

import java.io.Serializable;

import cn.bc.business.motorcade.domain.HistoryCarQuantity;
import cn.bc.core.dao.CrudDao;

public interface HistoryCarQuantityDao extends CrudDao<HistoryCarQuantity> {
	/**
	 * 
	 * 删除历史车辆数
	 * 
	 * @param id
	 *            车队Id
	 * @return
	 */
	void deleteByMotorcade(Serializable id);

	/**
	 * 车队每日历史车辆数备份
	 * 
	 * @return 插入的数据条目数
	 */
	int doRecordHistoryCarQuantity4Day();
}
