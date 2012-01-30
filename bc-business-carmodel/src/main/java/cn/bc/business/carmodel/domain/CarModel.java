/**
 * 
 */
package cn.bc.business.carmodel.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import cn.bc.identity.domain.RichFileEntityImpl;

/**
 * 车型配置类
 * 
 * @author wis
 */
@Entity
@Table(name = "BS_CAR_MODEL")
public class CarModel extends RichFileEntityImpl {
	private static final long serialVersionUID = 1L;
	public static final String KEY_UID = CarModel.class.getSimpleName();
	public static final String KEY_CODE = "carmodel.code";
	
	public static final int STATUS_NORMAL  = 0;
	
	private String factoryType;// 厂牌类型，如“桑塔纳”
	private String factoryModel;// 厂牌型号，如“SVW7182QQD”
	private String engineType;// 发动机类型
	private String fuelType;// 燃料类型，如“汽油”
	private int displacement;// 排量，单位ml
	private float power;// 功率，单位kw
	private String turnType;// 转向方式，如“方向盘”
	private int tireCount;// 轮胎数
	private String tireFrontDistance;//前轮距
	private String tireBehindDistance;//后轮距
	private String tireStandard;// 轮胎规格
	private int axisDistance;// 轴距
	private int axisCount;// 轴数
	private int pieceCount;// 后轴钢板弹簧片数
	private int dimLen;// 外廓尺寸：长，单位mm
	private int dimWidth;// 外廓尺寸：宽，单位mm
	private int dimHeight;// 外廓尺寸：高，单位mm
	private int totalWeight;// 总质量，单位kg
	private int accessWeight;// 核定承载量，单位kg
	private int accessCount;// 载客人数

	@Column(name = "FACTORY_TYPE")
	public String getFactoryType() {
		return factoryType;
	}

	public void setFactoryType(String factoryType) {
		this.factoryType = factoryType;
	}

	@Column(name = "FACTORY_MODEL")
	public String getFactoryModel() {
		return factoryModel;
	}

	public void setFactoryModel(String factoryModel) {
		this.factoryModel = factoryModel;
	}

	@Column(name = "ENGINE_TYPE")
	public String getEngineType() {
		return engineType;
	}

	public void setEngineType(String engineType) {
		this.engineType = engineType;
	}

	@Column(name = "FUEL_TYPE")
	public String getFuelType() {
		return fuelType;
	}

	public void setFuelType(String fuelType) {
		this.fuelType = fuelType;
	}

	@Column(name = "DISPLACEMENT")
	public int getDisplacement() {
		return displacement;
	}

	public void setDisplacement(int displacement) {
		this.displacement = displacement;
	}

	@Column(name = "POWER")
	public float getPower() {
		return power;
	}

	public void setPower(float power) {
		this.power = power;
	}

	@Column(name = "TURN_TYPE")
	public String getTurnType() {
		return turnType;
	}

	public void setTurnType(String turnType) {
		this.turnType = turnType;
	}

	@Column(name = "TIRE_COUNT")
	public int getTireCount() {
		return tireCount;
	}

	public void setTireCount(int tireCount) {
		this.tireCount = tireCount;
	}

	@Column(name = "TIRE_FRONT_DISTANCE")
	public String getTireFrontDistance() {
		return tireFrontDistance;
	}

	public void setTireFrontDistance(String tireFrontDistance) {
		this.tireFrontDistance = tireFrontDistance;
	}

	@Column(name = "TIRE_BEHIND_DISTANCE")
	public String getTireBehindDistance() {
		return tireBehindDistance;
	}

	public void setTireBehindDistance(String tireBehindDistance) {
		this.tireBehindDistance = tireBehindDistance;
	}

	@Column(name = "TIRE_STANDARD")
	public String getTireStandard() {
		return tireStandard;
	}

	public void setTireStandard(String tireStandard) {
		this.tireStandard = tireStandard;
	}

	@Column(name = "AXIS_DISTANCE")
	public int getAxisDistance() {
		return axisDistance;
	}

	public void setAxisDistance(int axisDistance) {
		this.axisDistance = axisDistance;
	}

	@Column(name = "AXIS_COUNT")
	public int getAxisCount() {
		return axisCount;
	}

	public void setAxisCount(int axisCount) {
		this.axisCount = axisCount;
	}

	@Column(name = "PIECE_COUNT")
	public int getPieceCount() {
		return pieceCount;
	}

	public void setPieceCount(int pieceCount) {
		this.pieceCount = pieceCount;
	}

	@Column(name = "DIM_LEN")
	public int getDimLen() {
		return dimLen;
	}

	public void setDimLen(int dimLen) {
		this.dimLen = dimLen;
	}

	@Column(name = "DIM_WIDTH")
	public int getDimWidth() {
		return dimWidth;
	}

	public void setDimWidth(int dimWidth) {
		this.dimWidth = dimWidth;
	}

	@Column(name = "DIM_HEIGHT")
	public int getDimHeight() {
		return dimHeight;
	}

	public void setDimHeight(int dimHeight) {
		this.dimHeight = dimHeight;
	}

	@Column(name = "TOTAL_WEIGHT")
	public int getTotalWeight() {
		return totalWeight;
	}

	public void setTotalWeight(int totalWeight) {
		this.totalWeight = totalWeight;
	}

	@Column(name = "ACCESS_WEIGHT")
	public int getAccessWeight() {
		return accessWeight;
	}

	public void setAccessWeight(int accessWeight) {
		this.accessWeight = accessWeight;
	}

	@Column(name = "ACCESS_COUNT")
	public int getAccessCount() {
		return accessCount;
	}

	public void setAccessCount(int accessCount) {
		this.accessCount = accessCount;
	}
}