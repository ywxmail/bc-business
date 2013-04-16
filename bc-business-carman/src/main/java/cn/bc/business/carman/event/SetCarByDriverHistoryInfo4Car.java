/**
 * 
 */
package cn.bc.business.carman.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;

import cn.bc.business.car.event.BeforeSave4CarEvent;
import cn.bc.business.carman.domain.CarByDriverHistory;
import cn.bc.business.carman.service.CarByDriverHistoryService;
import cn.bc.business.motorcade.domain.Motorcade;
import cn.bc.business.motorcade.service.MotorcadeService;

/**
 * 保存车辆前处理事件的监听器：获取迁移类型为转车队的迁移记录信息的公司和车队写入车辆信息的冗余字段中
 * 
 * @author zxr
 * 
 */
public class SetCarByDriverHistoryInfo4Car implements
		ApplicationListener<BeforeSave4CarEvent> {
	public CarByDriverHistoryService carByDriverHistoryService;
	private MotorcadeService motorcadeService;

	@Autowired
	public void setMotorcadeService(MotorcadeService motorcadeService) {
		this.motorcadeService = motorcadeService;
	}

	@Autowired
	public void setCarByDriverHistoryService(
			CarByDriverHistoryService carByDriverHistoryService) {
		this.carByDriverHistoryService = carByDriverHistoryService;
	}

	public void onApplicationEvent(BeforeSave4CarEvent event) {
		CarByDriverHistory h = this.carByDriverHistoryService
				.getNeWsetCarByDriverHistory4CarAndMoveType(event.getCarId(),
						CarByDriverHistory.MOVETYPE_ZCD);
		if (h != null) {
			// 单位
			String company = h.getToUnit();
			event.setCompany(company);
			// 车队
			Motorcade m = this.motorcadeService.load(h.getToMotorcadeId());
			event.setMotorcade(m);
		}
	}
}
