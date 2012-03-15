/**
 * 
 */
package cn.bc.business.carman.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;

import cn.bc.business.car.domain.Car;
import cn.bc.business.car.event.BeforeSave4CarEvent;
import cn.bc.business.car.service.CarService;
import cn.bc.business.carman.domain.CarByDriverHistory;
import cn.bc.business.carman.service.CarByDriverHistoryService;
import cn.bc.business.motorcade.domain.Motorcade;
import cn.bc.business.motorcade.service.MotorcadeService;

/**
 * 保存车辆前处理事件的监听器：获取迁移类型为转车队的迁移记录信息的公司和车队写入车辆信息的冗余字段中
 * 
 * @author dragon
 * 
 */
public class SetCarByDriverHistoryInfo4Car implements
		ApplicationListener<BeforeSave4CarEvent> {
	public CarService carService;
	public CarByDriverHistoryService carByDriverHistoryService;
	private MotorcadeService motorcadeService;

	@Autowired
	public void CarService(CarService carService) {
		this.carService = carService;
	}

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
		// 获取车辆信息
		Car car = this.carService.load(event.getCarId());
		CarByDriverHistory h = this.carByDriverHistoryService
				.getNeWsetCarByDriverHistory4CarAndMoveType(event.getCarId(),
						CarByDriverHistory.MOVETYPE_ZCD);
		if (h != null) {
			// 单位
			car.setCompany(h.getToUnit());
			// 车队
			Motorcade m = this.motorcadeService.load(h.getToMotorcadeId());
			car.setMotorcade(m);
			this.carService.save(car);
		}
	}
}
