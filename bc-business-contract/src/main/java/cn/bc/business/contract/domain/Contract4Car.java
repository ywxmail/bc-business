/**
 * 
 */
package cn.bc.business.contract.domain;

import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

import cn.bc.business.car.domain.Car;

/**
 * 与车有关的合合同信息
 * 
 * @author dragon
 */
@MappedSuperclass
public class Contract4Car extends Contract {
	private static final long serialVersionUID = 1L;
	public static final String ATTACH_TYPE = Contract4Car.class.getSimpleName();
	private Car car;// 车辆

	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	@JoinColumn(name = "CAR_ID", referencedColumnName = "ID")
	public Car getCar() {
		return car;
	}

	public void setCar(Car car) {
		this.car = car;
	}
}