/**
 * 
 */
package cn.bc.business.carPrepare.dao;

import cn.bc.business.carPrepare.domain.CarPrepare;
import cn.bc.core.dao.CrudDao;

/**
 * 出车准备Dao
 * 
 * @author zxr
 */
public interface CarPrepareDao extends CrudDao<CarPrepare> {
	/**
	 * 根据车牌类型和车牌号码查找车辆更新计划信息
	 * 
	 * @param plateType
	 *            车牌类型如：粤A
	 * @param plateNo
	 *            车牌号码
	 * @return
	 */
	CarPrepare getCarPrepareByPlateTypeAndPlateNo(String plateType,
			String plateNo);
}