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
import cn.bc.identity.domain.RichFileEntity;

/**
 * 司机营运车辆
 * <p>
 * 关联司机营运哪些车辆、哪些班次
 * </p>
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
	
	
	private int status = RichFileEntity.STATUS_ENABLED;// 状态

	private CarMan driver;// 营运的司机
	private Car oldCar;// 原车辆
	private Motorcade oldMotorcadeId;//原车队
	private int oldDriverState;// 原营运班次:如正班、副班、顶班
	private Car newCar;//现车辆
	private Motorcade newMotorcadeId;//现车队
	private int newDriverState;// 现营运班次:如正班、副班、顶班

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

	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	@JoinColumn(name = "OLD_CAR_ID", referencedColumnName = "ID")
	public Car getOldCar() {
		return oldCar;
	}

	public void setOldCar(Car oldCar) {
		this.oldCar = oldCar;
	}

	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	@JoinColumn(name = "OLD_MOTORCADE_ID", referencedColumnName = "ID")
	public Motorcade getOldMotorcadeId() {
		return oldMotorcadeId;
	}

	public void setOldMotorcadeId(Motorcade oldMotorcadeId) {
		this.oldMotorcadeId = oldMotorcadeId;
	}

	@Column(name = "OLD_DRIVER_STATE")
	public int getOldDriverState() {
		return oldDriverState;
	}

	public void setOldDriverState(int oldDriverState) {
		this.oldDriverState = oldDriverState;
	}

	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	@JoinColumn(name = "NEW_CAR_ID", referencedColumnName = "ID")
	public Car getNewCar() {
		return newCar;
	}

	public void setNewCar(Car newCar) {
		this.newCar = newCar;
	}

	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	@JoinColumn(name = "NEW_MOTORCADE_ID", referencedColumnName = "ID")
	public Motorcade getNewMotorcadeId() {
		return newMotorcadeId;
	}

	public void setNewMotorcadeId(Motorcade newMotorcadeId) {
		this.newMotorcadeId = newMotorcadeId;
	}

	@Column(name = "NEW_DRIVER_STATE")
	public int getNewDriverState() {
		return newDriverState;
	}

	public void setNewDriverState(int newDriverState) {
		this.newDriverState = newDriverState;
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