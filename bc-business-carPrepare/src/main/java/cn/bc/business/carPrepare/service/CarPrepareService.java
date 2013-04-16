/**
 * 
 */
package cn.bc.business.carPrepare.service;

import java.util.Calendar;
import java.util.Set;

import cn.bc.business.carPrepare.domain.CarPrepare;
import cn.bc.business.carPrepare.domain.CarPrepareItem;
import cn.bc.core.service.CrudService;

/**
 * 出车准备Service
 * 
 * @author zxr
 */
public interface CarPrepareService extends CrudService<CarPrepare> {

	/**
	 * 根据年份或月份生成车辆更新计划
	 * 
	 * @param plan4Year年份
	 * @param plan4Month月份
	 * @return
	 */
	String doAnnualPlan(String plan4Year, String plan4Month);

	/**
	 * 初始进度项目
	 * 
	 * @param entity
	 *            车辆更新项目domain
	 * @param carPrepareItems
	 *            车辆更新项目Set集合
	 * @param name
	 *            项目名称
	 * @param date更新日期
	 * @param staus状态
	 * @param order
	 *            排序号
	 */
	void initializeCarPrepareItemInfo(CarPrepare entity,
			Set<CarPrepareItem> carPrepareItems, String name, Calendar date,
			int staus, int order);

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

	/**
	 * 发起流程
	 * 
	 * @param key
	 *            流程key值
	 * @param id
	 *            车辆更新信息的ID
	 * @param e
	 *            车辆更新对象
	 * @return
	 */
	String doStartFlow(String key, Long carPrepartId, CarPrepare e);
}