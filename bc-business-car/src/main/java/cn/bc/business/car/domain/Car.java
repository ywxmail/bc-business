/**
 * 
 */
package cn.bc.business.car.domain;

import java.util.Calendar;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;

import cn.bc.business.cert.domain.Cert;
import cn.bc.business.contract.domain.Contract;
import cn.bc.business.motorcade.domain.Motorcade;
import cn.bc.identity.domain.RichFileEntityImpl;

/**
 * 车辆
 * 
 * @author dragon
 */
@Entity
@Table(name = "BS_CAR")
public class Car extends RichFileEntityImpl {
	private static final long serialVersionUID = 1L;
	public static final String KEY_UID = "car.uid";
	public static final String KEY_CODE = "car.code";

	private Motorcade motorcade;// 所属车队
	private String driver; // 司机姓名
	private String charger; // 责任人姓名
	private String oldUnitName;// 所属单位
	private String businessType;// 营运性质

	private String code;// 自编号
	private String originNo;// 原车号
	private String plateType;// 车牌归属，如“粤A”
	private String plateNo;// 车牌号码，如“C4X74”
	private String vin;// 车辆识别代号
	private String factoryType;// 厂牌类型，如“桑塔纳”
	private String factoryModel;// 厂牌型号，如“SVW7182QQD”

	private Calendar registerDate;// 登记日期
	private Calendar operateDate;// 投产日期
	private Calendar scrapDate;// 报废日期
	private Calendar factoryDate;// 出厂日期

	private String registerNo;// 机动车登记编号
	private String level;// 车辆定级
	private String color;// 颜色

	private String engineNo;// 发动机号码
	private String engineType;// 发动机类型
	private String fuelType;// 燃料类型，如“汽油”
	private int displacement;// 排量，单位ml
	private float power;// 功率，单位kw
	private String turnType;// 转向方式，如“方向盘”
	private int tireCount;// 轮胎数
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

	private float originalValue;// 固定资产原值
	private String invoiceNo1;// 购车发票号
	private String invoiceNo2;// 购置税发票号
	private String paymentType;// 缴费日

	private String certNo1;// 购置税证号
	private String certNo2;// 经营权使用证号
	private String certNo3;// 强检证号
	private String taximeterFactory;// 计价器制造厂
	private String taximeterType;// 计价器型号
	private String taximeterNo;// 计价器出厂编号

	private String desc1;// 备注1
	private String desc2;// 备注2
	private String desc3;// 备注3

	private Set<Cert> certs;// 拥有的证件
	private Set<Contract> contracts;// 合同

	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	@JoinColumn(name = "MOTORCADE_ID", referencedColumnName = "ID")
	public Motorcade getMotorcade() {
		return motorcade;
	}

	public void setMotorcade(Motorcade motorcade) {
		this.motorcade = motorcade;
	}

	@Column(name = "DRIVER")
	public String getDriver() {
		return driver;
	}

	public void setDriver(String driver) {
		this.driver = driver;
	}

	@Column(name = "CHARGER")
	public String getCharger() {
		return charger;
	}

	public void setCharger(String charger) {
		this.charger = charger;
	}

	@OneToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "BS_CAR_CERT", joinColumns = @JoinColumn(name = "CAR_ID", referencedColumnName = "ID"), inverseJoinColumns = @JoinColumn(name = "CERT_ID", referencedColumnName = "ID"))
	@OrderBy("certCode asc")
	public Set<Cert> getCerts() {
		return certs;
	}

	public void setCerts(Set<Cert> certs) {
		this.certs = certs;
	}

	@OneToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "BS_CAR_CONTRACT", joinColumns = @JoinColumn(name = "CAR_ID", referencedColumnName = "ID"), inverseJoinColumns = @JoinColumn(name = "CONTRACT_ID", referencedColumnName = "ID"))
	@OrderBy("fileDate desc")
	public Set<Contract> getContracts() {
		return contracts;
	}

	public void setContracts(Set<Contract> contracts) {
		this.contracts = contracts;
	}

	@Column(name = "OLD_UNIT_NAME")
	public String getOldUnitName() {
		return oldUnitName;
	}

	public void setOldUnitName(String oldUnitName) {
		this.oldUnitName = oldUnitName;
	}

	@Column(name = "BS_TYPE")
	public String getBusinessType() {
		return businessType;
	}

	public void setBusinessType(String businessType) {
		this.businessType = businessType;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	@Column(name = "ORIGIN_NO")
	public String getOriginNo() {
		return originNo;
	}

	public void setOriginNo(String originNo) {
		this.originNo = originNo;
	}

	@Column(name = "PLATE_TYPE")
	public String getPlateType() {
		return plateType;
	}

	public void setPlateType(String plateType) {
		this.plateType = plateType;
	}

	@Column(name = "PLATE_NO")
	public String getPlateNo() {
		return plateNo;
	}

	@Transient
	public String getPlate() {
		return this.plateType + "." + this.plateNo;
	}

	public void setPlateNo(String plateNo) {
		this.plateNo = plateNo;
	}

	public String getVin() {
		return vin;
	}

	public void setVin(String vin) {
		this.vin = vin;
	}

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

	@Column(name = "REGISTER_DATE")
	public Calendar getRegisterDate() {
		return registerDate;
	}

	public void setRegisterDate(Calendar registerDate) {
		this.registerDate = registerDate;
	}

	@Column(name = "OPERATE_DATE")
	public Calendar getOperateDate() {
		return operateDate;
	}

	public void setOperateDate(Calendar operateDate) {
		this.operateDate = operateDate;
	}

	@Column(name = "SCRAP_DATE")
	public Calendar getScrapDate() {
		return scrapDate;
	}

	public void setScrapDate(Calendar scrapDate) {
		this.scrapDate = scrapDate;
	}

	@Column(name = "FACTORY_DATE")
	public Calendar getFactoryDate() {
		return factoryDate;
	}

	public void setFactoryDate(Calendar factoryDate) {
		this.factoryDate = factoryDate;
	}

	@Column(name = "REGISTER_NO")
	public String getRegisterNo() {
		return registerNo;
	}

	public void setRegisterNo(String registerNo) {
		this.registerNo = registerNo;
	}

	@Column(name = "LEVEL_")
	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	@Column(name = "ENGINE_NO")
	public String getEngineNo() {
		return engineNo;
	}

	public void setEngineNo(String engineNo) {
		this.engineNo = engineNo;
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

	public int getDisplacement() {
		return displacement;
	}

	public void setDisplacement(int displacement) {
		this.displacement = displacement;
	}

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

	@Column(name = "ORIGINAL_VALUE")
	public float getOriginalValue() {
		return originalValue;
	}

	public void setOriginalValue(float originalValue) {
		this.originalValue = originalValue;
	}

	@Column(name = "INVOICE_NO1")
	public String getInvoiceNo1() {
		return invoiceNo1;
	}

	public void setInvoiceNo1(String invoiceNo1) {
		this.invoiceNo1 = invoiceNo1;
	}

	@Column(name = "INVOICE_NO2")
	public String getInvoiceNo2() {
		return invoiceNo2;
	}

	public void setInvoiceNo2(String invoiceNo2) {
		this.invoiceNo2 = invoiceNo2;
	}

	@Column(name = "PAYMENT_TYPE")
	public String getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}

	@Column(name = "CERT_NO1")
	public String getCertNo1() {
		return certNo1;
	}

	public void setCertNo1(String certNo1) {
		this.certNo1 = certNo1;
	}

	@Column(name = "CERT_NO2")
	public String getCertNo2() {
		return certNo2;
	}

	public void setCertNo2(String certNo2) {
		this.certNo2 = certNo2;
	}

	@Column(name = "CERT_NO3")
	public String getCertNo3() {
		return certNo3;
	}

	public void setCertNo3(String certNo3) {
		this.certNo3 = certNo3;
	}

	@Column(name = "TAXIMETER_FACTORY")
	public String getTaximeterFactory() {
		return taximeterFactory;
	}

	public void setTaximeterFactory(String taximeterFactory) {
		this.taximeterFactory = taximeterFactory;
	}

	@Column(name = "TAXIMETER_TYPE")
	public String getTaximeterType() {
		return taximeterType;
	}

	public void setTaximeterType(String taximeterType) {
		this.taximeterType = taximeterType;
	}

	@Column(name = "TAXIMETER_NO")
	public String getTaximeterNo() {
		return taximeterNo;
	}

	public void setTaximeterNo(String taximeterNo) {
		this.taximeterNo = taximeterNo;
	}

	@Column(name = "DESC1")
	public String getDesc1() {
		return desc1;
	}

	public void setDesc1(String desc1) {
		this.desc1 = desc1;
	}

	@Column(name = "DESC2")
	public String getDesc2() {
		return desc2;
	}

	public void setDesc2(String desc2) {
		this.desc2 = desc2;
	}

	@Column(name = "DESC3")
	public String getDesc3() {
		return desc3;
	}

	public void setDesc3(String desc3) {
		this.desc3 = desc3;
	}
}