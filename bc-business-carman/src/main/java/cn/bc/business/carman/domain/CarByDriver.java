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
import cn.bc.identity.domain.FileEntityImpl;
import cn.bc.identity.domain.RichFileEntity;

/**
 * 司机营运车辆
 * <p>关联司机营运哪些车辆、哪些班次</p>
 * 
 * @author dragon
 */
@Entity
@Table(name = "BS_CAR_DRIVER")
public class CarByDriver extends FileEntityImpl {
	private static final long serialVersionUID = 1L;
	/** 营运类型：示定义 */
	public static final int TYPE_WEIDINGYI = 0;
	/** 营运类型：正班 */
	public static final int TYPE_ZHENGBAN = 1;
	/** 营运类型：副班 */
	public static final int TYPE_FUBAN = 2;
	/** 营运类型：顶班 */
	public static final int TYPE_DINGBAN = 3;
	private int status = RichFileEntity.STATUS_ENABLED;//状态

	private int classes;// 营运班次:如正班、副班、顶班
	private Calendar startDate;// 开始时段
	private Calendar endDate;// 结束时段
	private Car car;// 营运的车辆
	private CarMan driver;// 营运的司机
	private String description;// 备注
	
	
	@Column(name = "DESC_")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
    
	public int getClasses() {
		return classes;
	}

	public void setClasses(int classes) {
		this.classes = classes;
	}

	@Column(name = "START_DATE")
	public Calendar getStartDate() {
		return startDate;
	}

	public void setStartDate(Calendar startDate) {
		this.startDate = startDate;
	}

	@Column(name = "END_DATE")
	public Calendar getEndDate() {
		return endDate;
	}

	public void setEndDate(Calendar enddate) {
		this.endDate = enddate;
	}

	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	@JoinColumn(name = "CAR_ID", referencedColumnName = "ID")
	public Car getCar() {
		return car;
	}

	public void setCar(Car car) {
		this.car = car;
	}

	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	@JoinColumn(name = "DRIVER_ID", referencedColumnName = "ID")
	public CarMan getDriver() {
		return driver;
	}

	public void setDriver(CarMan driver) {
		this.driver = driver;
	}
	@Column(name = "STATUS_")
	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}
}