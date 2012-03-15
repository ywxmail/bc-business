package cn.bc.business.car.event;

import org.springframework.context.ApplicationEvent;

/**
 * 用户登录系统的事件
 * 
 * @author dragon
 * 
 */
public class BeforeSave4CarEvent extends ApplicationEvent {

	private static final long serialVersionUID = 1L;
	// 车辆Id
	private final Long carId;

	public Long getCarId() {
		return carId;
	}

	public BeforeSave4CarEvent(Long carId) {
		super(carId);
		this.carId = carId;
	}
}
