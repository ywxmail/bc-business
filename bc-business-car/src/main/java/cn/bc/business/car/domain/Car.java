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

	public static final int CAR_STAUTS_NORMAL = 0;
	public static final int CAR_STAUTS_LOGOUT = 1;

	private Motorcade motorcade;// 所属车队
	private String driver; // 司机信息：[司机1姓名],[司机1班次],[司机1id];[司机2姓名],[司机2班次],[司机2id];...
	private String charger; // 责任人信息：[责任人1姓名,责任人1id;责任人2姓名,责任人2id;...]
	private String company;// 所属公司:宝城、广发
	private String businessType;// 营运性质

	private String code;// 自编号
	private String originNo;// 原车号
	private String plateType;// 车牌归属，如“粤A”
	private String plateNo;// 车牌号码，如“C4X74”
	private String vin;// 车辆识别代号
	private String factoryType;// 车辆品牌，如“桑塔纳”
	private String factoryModel;// 车辆型号，如“SVW7182QQD”

	private Calendar registerDate;// 登记日期
	private Calendar operateDate;// 投产日期
	private Calendar scrapDate;// 报废日期
	private Calendar factoryDate;// 出厂日期

	private String registerNo;// 机动车登记编号
	private String level;// 车辆定级
	private String color;// 车身颜色

	private String engineNo;// 发动机号码
	private String engineType;// 发动机型号
	private String fuelType;// 燃料种类，如“汽油”
	private int displacement;// 排量，单位ml
	private float power;// 功率，单位kw
	private String turnType;// 转向方式，如“方向盘”
	private int tireCount;// 轮胎数
	private String tireFrontDistance;// 前轮距
	private String tireBehindDistance;// 后轮距
	private String tireStandard;// 轮胎规格
	private int axisDistance;// 轴距
	private int axisCount;// 轴数
	private int pieceCount;// 钢板弹簧片数
	private int dimLen;// 外廓尺寸：长，单位mm
	private int dimWidth;// 外廓尺寸：宽，单位mm
	private int dimHeight;// 外廓尺寸：高，单位mm
	private int totalWeight;// 总质量，单位kg
	private int accessWeight;// 核定载质量，单位kg
	private int accessCount;// 核定载客

	private float originalValue;// 固定资产原值
	private String invoiceNo1;// 购车发票号
	private String invoiceNo2;// 购置税发票号
	private String paymentType;// 缴费日

	private String certNo1;// 购置税证号
	private String certNo2;// 经营权使用证号
	private String certNo3;// 强检证号
	private String certNo4;// 道路运输证号
	private String taximeterFactory;// 计价器制造厂
	private String taximeterType;// 计价器型号
	private String taximeterNo;// 计价器出厂编号

	private String desc1;// 备注1
	private String desc2;// 备注2
	private String desc3;// 备注3

	private boolean logout;// 启动注销程序:0-未,1-已
	private Calendar returnDate;// 交车日期
	private int logoutReason;// 注销原因 0-"",1-转蓝归公司,2-转蓝归责任人,3-报废,4-被盗,5-被抢,9-其它
	private String distanceScrapMonth;// 离报废日期
	private boolean verify;// 主管部门核准:0-未,1-已
	private Calendar verifyDate;// 核准日期
	private String logoutRemark;// 注销备注

	private Set<Cert> certs;// 拥有的证件
	private Set<Contract> contracts;// 合同
	
	//LPG属性配置
	private String lpgName;//专用装置供应商
	private String lpgModel;//专用装置品牌型号
	private String lpgGpModel;//钢瓶品牌型号
	private String lpgGpId;//钢瓶编号
	private Calendar lpgGpDate;//钢瓶出厂日期
	private String lpgJcfModel;//集成阀品牌型号
	private String lpgQhqModel;//汽化器品牌型号
	private String lpgPsqModel;//混合/喷射器品牌型号
	private String lpgRefitFactory;//改装厂
	private Calendar lpgRefitDate;//改装出厂日期
	private String lpgInsuranceId;//LPG保单号
	private Calendar lpgInsuranceStartDate;//LPG保单开始日期
	private Calendar lpgInsuranceEndDate;//LPG保单结束日期
	
	private String carTvScreen;//车载电视屏
	private String rentNo;//
	

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

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
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

	@Column(name = "CERT_NO4")
	public String getCertNo4() {
		return certNo4;
	}

	public void setCertNo4(String certNo4) {
		this.certNo4 = certNo4;
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

	@Column(name = "IS_LOGOUT")
	public boolean isLogout() {
		return logout;
	}

	public void setLogout(boolean logout) {
		this.logout = logout;
	}

	@Column(name = "RETURN_DATE")
	public Calendar getReturnDate() {
		return returnDate;
	}

	public void setReturnDate(Calendar returnDate) {
		this.returnDate = returnDate;
	}

	@Column(name = "LOGOUT_REASON")
	public int getLogoutReason() {
		return logoutReason;
	}

	public void setLogoutReason(int logoutReason) {
		this.logoutReason = logoutReason;
	}

	@Column(name = "DISTANCE_SCRAP_MONTH")
	public String getDistanceScrapMonth() {
		return distanceScrapMonth;
	}

	public void setDistanceScrapMonth(String distanceScrapMonth) {
		this.distanceScrapMonth = distanceScrapMonth;
	}

	@Column(name = "IS_VERIFY")
	public boolean isVerify() {
		return verify;
	}

	public void setVerify(boolean verify) {
		this.verify = verify;
	}

	@Column(name = "VERIFY_DATE")
	public Calendar getVerifyDate() {
		return verifyDate;
	}

	public void setVerifyDate(Calendar verifyDate) {
		this.verifyDate = verifyDate;
	}

	@Column(name = "LOGOUT_REMARK")
	public String getLogoutRemark() {
		return logoutRemark;
	}

	public void setLogoutRemark(String logoutRemark) {
		this.logoutRemark = logoutRemark;
	}

	@Column(name = "LPG_NAME")
	public String getLpgName() {
		return lpgName;
	}

	public void setLpgName(String lpgName) {
		this.lpgName = lpgName;
	}

	@Column(name = "LPG_MODEL")
	public String getLpgModel() {
		return lpgModel;
	}

	public void setLpgModel(String lpgModel) {
		this.lpgModel = lpgModel;
	}

	@Column(name = "LPG_GP_MODEL")
	public String getLpgGpModel() {
		return lpgGpModel;
	}

	public void setLpgGpModel(String lpgGpModel) {
		this.lpgGpModel = lpgGpModel;
	}

	@Column(name = "LPG_GP_ID")
	public String getLpgGpId() {
		return lpgGpId;
	}

	public void setLpgGpId(String lpgGpId) {
		this.lpgGpId = lpgGpId;
	}

	@Column(name = "LPG_GP_DATE")
	public Calendar getLpgGpDate() {
		return lpgGpDate;
	}

	public void setLpgGpDate(Calendar lpgGpDate) {
		this.lpgGpDate = lpgGpDate;
	}

	@Column(name = "LPG_JCF_MODEL")
	public String getLpgJcfModel() {
		return lpgJcfModel;
	}

	public void setLpgJcfModel(String lpgJcfModel) {
		this.lpgJcfModel = lpgJcfModel;
	}

	@Column(name = " LPG_QHQ_MODEL")
	public String getLpgQhqModel() {
		return lpgQhqModel;
	}

	public void setLpgQhqModel(String lpgQhqModel) {
		this.lpgQhqModel = lpgQhqModel;
	}

	@Column(name = "LPG_PSQ_MODEL")
	public String getLpgPsqModel() {
		return lpgPsqModel;
	}

	public void setLpgPsqModel(String lpgPsqModel) {
		this.lpgPsqModel = lpgPsqModel;
	}

	@Column(name = "LPG_REFIT_FACTORY")
	public String getLpgRefitFactory() {
		return lpgRefitFactory;
	}

	public void setLpgRefitFactory(String lpgRefitFactory) {
		this.lpgRefitFactory = lpgRefitFactory;
	}

	@Column(name = "LPG_REFIT_DATE")
	public Calendar getLpgRefitDate() {
		return lpgRefitDate;
	}

	public void setLpgRefitDate(Calendar lpgRefitDate) {
		this.lpgRefitDate = lpgRefitDate;
	}
	
	@Column(name = "LPG_INSURANCE_ID")
	public String getLpgInsuranceId() {
		return lpgInsuranceId;
	}

	public void setLpgInsuranceId(String lpgInsuranceId) {
		this.lpgInsuranceId = lpgInsuranceId;
	}

	@Column(name = "LPG_INSURANCE_STARTDATE")
	public Calendar getLpgInsuranceStartDate() {
		return lpgInsuranceStartDate;
	}

	public void setLpgInsuranceStartDate(Calendar lpgInsuranceStartDate) {
		this.lpgInsuranceStartDate = lpgInsuranceStartDate;
	}

	@Column(name = "LPG_INSURANCE_ENDDATE")
	public Calendar getLpgInsuranceEndDate() {
		return lpgInsuranceEndDate;
	}

	public void setLpgInsuranceEndDate(Calendar lpgInsuranceEndDate) {
		this.lpgInsuranceEndDate = lpgInsuranceEndDate;
	}

	@Column(name = "CAR_TV_SCREEN")
	public String getCarTvScreen() {
		return carTvScreen;
	}

	public void setCarTvScreen(String carTvScreen) {
		this.carTvScreen = carTvScreen;
	}

	@Column(name = "rent_no")
	public String getRentNo() {
		return rentNo;
	}

	public void setRentNo(String rentNo) {
		this.rentNo = rentNo;
	}

}