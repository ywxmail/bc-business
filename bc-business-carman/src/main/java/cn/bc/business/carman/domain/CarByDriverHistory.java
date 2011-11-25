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
 * 迁移记录
 * 
 * @author dragon
 */
@Entity
@Table(name = "BS_CAR_DRIVER_HISTORY")
public class CarByDriverHistory extends FileEntityImpl {
	private static final long serialVersionUID = 1L;
	public static final String KEY_UID = "carBydriverHistory.uid";
	/** 营运类型：示定义 */
	public static final int TYPE_WEIDINGYI = 0;
	/** 营运类型：正班 */
	public static final int TYPE_ZHENGBAN = 1;
	/** 营运类型：副班 */
	public static final int TYPE_FUBAN = 2;
	/** 营运类型：顶班 */
	public static final int TYPE_DINGBAN = 3;

	/** 营运类型：车辆到车辆 */
	public static final int MOVETYPE_CLDCL = 0;
	/** 营运类型：公司到公司(已注销) */
	public static final int MOVETYPE_GSDGSYZX = 1;
	/** 营运类型：注销未有去向 */
	public static final int MOVETYPE_ZXWYQX = 2;
	/** 营运类型：由外公司迁回 */
	public static final int MOVETYPE_YWGSQH = 3;
	/** 营运类型：交回未注销 */
	public static final int MOVETYPE_JHWZX = 4;
	/** 营运类型：新入职 */
	public static final int MOVETYPE_XRZ = 5;
	/** 营运类型：转车队 */
	public static final int MOVETYPE_ZCD = 5;

	private Long driverId;// 营运的司机
	private Long fromCarId;// 原车辆
	private Long fromMotorcadeId;// 原车队
	private int fromClasses;// 原营运班次:如正班、副班、顶班
	private Long toCarId;// 现车辆
	private Long toMotorcadeId;// 现车队
	private int toClasses;// 现营运班次:如正班、副班、顶班

	private Calendar moveDate;// 迁移时间
	private int moveType;// 迁移类型
	private String fromUnit;// 原单位
	private String toUnit;// 现单位
	private Calendar handPapersDate;// 交证日期
	private String cancelId;// 注销单号
	private String description;// 备注

	@Column(name = "DESC_")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	// @ManyToOne(fetch = FetchType.EAGER, optional = false)
	// @JoinColumn(name = "FROM_CAR_ID", referencedColumnName = "ID")
	// public Car getFromCar() {
	// return fromCar;
	// }
	//
	// public void setFromCar(Car fromCar) {
	// this.fromCar = fromCar;
	// }
	//
	// public void setFromMotorcade(Motorcade fromMotorcade) {
	// this.fromMotorcade = fromMotorcade;
	// }
	//
	// @ManyToOne(fetch = FetchType.EAGER, optional = false)
	// @JoinColumn(name = "FROM_MOTORCADE_ID", referencedColumnName = "ID")
	// public Motorcade getFromMotorcade() {
	// return fromMotorcade;
	// }
	
	
	@Column(name = "DRIVER_ID")
	public Long getDriverId() {
		return driverId;
	}

	public void setDriverId(Long driverId) {
		this.driverId = driverId;
	}
	@Column(name = "FROM_CAR_ID")
	public Long getFromCarId() {
		return fromCarId;
	}
	public void setFromCarId(Long fromCarId) {
		this.fromCarId = fromCarId;
	}

	@Column(name = "FROM_MOTORCADE_ID")
	public Long getFromMotorcadeId() {
		return fromMotorcadeId;
	}

	public void setFromMotorcadeId(Long fromMotorcadeId) {
		this.fromMotorcadeId = fromMotorcadeId;
	}

	@Column(name = "TO_CAR_ID")
	public Long getToCarId() {
		return toCarId;
	}

	public void setToCarId(Long toCarId) {
		this.toCarId = toCarId;
	}

	@Column(name = "TO_MOTORCADE_ID")
	public Long getToMotorcadeId() {
		return toMotorcadeId;
	}

	public void setToMotorcadeId(Long toMotorcadeId) {
		this.toMotorcadeId = toMotorcadeId;
	}

	@Column(name = "FROM_CLASSES")
	public int getFromClasses() {
		return fromClasses;
	}

	public void setFromClasses(int fromClasses) {
		this.fromClasses = fromClasses;
	}

	// @ManyToOne(fetch = FetchType.EAGER, optional = false)
	// @JoinColumn(name = "TO_CAR_ID", referencedColumnName = "ID")
	// public Car getToCar() {
	// return toCar;
	// }
	//
	// public void setToCar(Car toCar) {
	// this.toCar = toCar;
	// }
	//
	// @ManyToOne(fetch = FetchType.EAGER, optional = false)
	// @JoinColumn(name = "TO_MOTORCADE_ID", referencedColumnName = "ID")
	// public Motorcade getToMotorcade() {
	// return toMotorcade;
	// }
	//
	// public void setToMotorcade(Motorcade toMotorcade) {
	// this.toMotorcade = toMotorcade;
	// }

	@Column(name = "TO_CLASSES")
	public int getToClasses() {
		return toClasses;
	}

	public void setToClasses(int toClasses) {
		this.toClasses = toClasses;
	}

	@Column(name = "MOVE_DATE")
	public Calendar getMoveDate() {
		return moveDate;
	}

	public void setMoveDate(Calendar moveDate) {
		this.moveDate = moveDate;
	}

	@Column(name = "MOVE_TYPE")
	public int getMoveType() {
		return moveType;
	}

	public void setMoveType(int moveType) {
		this.moveType = moveType;
	}

	@Column(name = "FROM_UNIT")
	public String getFromUnit() {
		return fromUnit;
	}

	public void setFromUnit(String fromUnit) {
		this.fromUnit = fromUnit;
	}

	@Column(name = "TO_UNIT")
	public String getToUnit() {
		return toUnit;
	}

	public void setToUnit(String toUnit) {
		this.toUnit = toUnit;
	}

	@Column(name = "HAND_PAPERS_DATE")
	public Calendar getHandPapersDate() {
		return handPapersDate;
	}

	public void setHandPapersDate(Calendar handPapersDate) {
		this.handPapersDate = handPapersDate;
	}

	@Column(name = "CANCEL_ID")
	public String getCancelId() {
		return cancelId;
	}

	public void setCancelId(String cancelId) {
		this.cancelId = cancelId;
	}

	// @ManyToOne(fetch = FetchType.EAGER, optional = false)
	// @JoinColumn(name = "DRIVER_ID", referencedColumnName = "ID")
	// public CarMan getDriver() {
	// return driver;
	// }
	//
	// public void setDriver(CarMan driver) {
	// this.driver = driver;
	// }

}