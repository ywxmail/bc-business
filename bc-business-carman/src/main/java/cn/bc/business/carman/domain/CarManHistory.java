/**
 * 
 */
package cn.bc.business.carman.domain;

import java.util.Calendar;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import cn.bc.business.car.domain.Car;
import cn.bc.business.motorcade.domain.Motorcade;
import cn.bc.identity.domain.FileEntityImpl;

/**
 * 司机迁移历史
 * 
 * @author dragon
 */
@Entity
@Table(name = "BS_CARMAN_HISTORY")
public class CarManHistory extends FileEntityImpl {
	private static final long serialVersionUID = 1L;

	private CarMan driver;// 司机
	private String type;// 迁移属性，如新入职
	private Calendar shiftDate;// 迁移日期
	private Car toCar;// 原车辆
	private Car fromCar;// 迁往车辆
	private Motorcade fromMotorcade;// 原车队
	private Motorcade toMotorcade;// 迁往车队
	private String fromClasses;// 原营运班次:如正班、副班、顶班
	private String toClasses;// 新营运班次

	@Column(name = "TYPE_")
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Column(name = "FROM_CLASSES")
	public String getFromClasses() {
		return fromClasses;
	}

	public void setFromClasses(String classes) {
		this.fromClasses = classes;
	}

	@Column(name = "TO_CLASSES")
	public String getToClasses() {
		return toClasses;
	}

	public void setToClasses(String toClasses) {
		this.toClasses = toClasses;
	}

	@Column(name = "SHIFT_DATE")
	public Calendar getShiftDate() {
		return shiftDate;
	}

	public void setShiftDate(Calendar shiftDate) {
		this.shiftDate = shiftDate;
	}

	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	@JoinColumn(name = "FROM_CARID", referencedColumnName = "ID")
	public Car getFromCar() {
		return fromCar;
	}

	public void setFromCar(Car fromCar) {
		this.fromCar = fromCar;
	}

	public void setToCar(Car car) {
		this.toCar = car;
	}

	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	@JoinColumn(name = "TO_CARID", referencedColumnName = "ID")
	public Car getToCar() {
		return toCar;
	}

	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	@JoinColumn(name = "DRIVER_ID", referencedColumnName = "ID")
	public CarMan getDriver() {
		return driver;
	}

	public void setDriver(CarMan driver) {
		this.driver = driver;
	}

	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	@JoinColumn(name = "FROM_MOTORCADEID", referencedColumnName = "ID")
	public Motorcade getFromMotorcade() {
		return fromMotorcade;
	}

	public void setFromMotorcade(Motorcade fromMotorcade) {
		this.fromMotorcade = fromMotorcade;
	}

	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	@JoinColumn(name = "TO_MOTORCADEID", referencedColumnName = "ID")
	public Motorcade getToMotorcade() {
		return toMotorcade;
	}

	public void setToMotorcade(Motorcade toMotorcade) {
		this.toMotorcade = toMotorcade;
	}
}