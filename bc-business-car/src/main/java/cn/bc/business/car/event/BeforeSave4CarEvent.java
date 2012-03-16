package cn.bc.business.car.event;

import org.springframework.context.ApplicationEvent;

import cn.bc.business.motorcade.domain.Motorcade;

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
	private String company;
	private Motorcade motorcade;

	public Long getCarId() {
		return carId;
	}

	public String getCompany() {
		return company;
	}

	public Motorcade getMotorcade() {
		return motorcade;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public void setMotorcade(Motorcade motorcade) {
		this.motorcade = motorcade;
	}

	public BeforeSave4CarEvent(Long carId, String company, Motorcade motorcade) {
		super(carId);
		this.carId = carId;
		this.company = company;
		this.motorcade = motorcade;
	}
}
