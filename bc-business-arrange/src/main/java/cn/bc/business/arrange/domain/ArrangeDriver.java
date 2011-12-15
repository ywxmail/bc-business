package cn.bc.business.arrange.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 安排的安全学习司机
 * 
 * @author dragon
 */
@Entity
@Table(name = "BS_ARRANGE_DRIVER")
public class ArrangeDriver extends ArrangeMember {
	private static final long serialVersionUID = 1L;

	// --基类的memberId、memberName对应司机的id和姓名

	private Long unitId;// 单位ID
	private String unitName;// 单位名称
	private Long motorcadeId;// 车队ID
	private String motorcadeName;// 车队名称
	private Long carId;// 车辆ID
	private String carPlateType;// 车牌类型：如粤A
	private String carPlateNo;// 车牌号码：如368P1
	private String driverCert;// 司机资格证号

	@Column(name = "UNIT_ID")
	public Long getUnitId() {
		return unitId;
	}

	public void setUnitId(Long unitId) {
		this.unitId = unitId;
	}

	@Column(name = "UNIT_NAME")
	public String getUnitName() {
		return unitName;
	}

	public void setUnitName(String unitName) {
		this.unitName = unitName;
	}

	@Column(name = "MOTORCADE_ID")
	public Long getMotorcadeId() {
		return motorcadeId;
	}

	public void setMotorcadeId(Long motorcadeId) {
		this.motorcadeId = motorcadeId;
	}

	@Column(name = "MOTORCADE_NAME")
	public String getMotorcadeName() {
		return motorcadeName;
	}

	public void setMotorcadeName(String motorcadeName) {
		this.motorcadeName = motorcadeName;
	}

	@Column(name = "CAR_ID")
	public Long getCarId() {
		return carId;
	}

	public void setCarId(Long carId) {
		this.carId = carId;
	}

	@Column(name = "CAR_PLATE_TYPE")
	public String getCarPlateType() {
		return carPlateType;
	}

	public void setCarPlateType(String carPlateType) {
		this.carPlateType = carPlateType;
	}

	@Column(name = "CAR_PLATE_NO")
	public String getCarPlateNo() {
		return carPlateNo;
	}

	public void setCarPlateNo(String carPlateNo) {
		this.carPlateNo = carPlateNo;
	}

	@Column(name = "DRIVER_CERT")
	public String getDriverCert() {
		return driverCert;
	}

	public void setDriverCert(String driverCert) {
		this.driverCert = driverCert;
	}
}