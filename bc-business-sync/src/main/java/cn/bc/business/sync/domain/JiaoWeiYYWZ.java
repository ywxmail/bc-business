/**
 * 
 */
package cn.bc.business.sync.domain;

import java.util.Calendar;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import cn.bc.sync.domain.SyncBase;

/**
 * 从交委WebService接口同步的营运违章信息
 * 
 * @author wis
 */
@Entity
@Table(name = "BS_SYNC_JIAOWEI_YYWZ")
public class JiaoWeiYYWZ extends SyncBase {
	private static final long serialVersionUID = 1L;
	/** UUID的前缀，实际的uid使用KEY_UID + "-" + id */
	public static final String KEY_UID = SyncBase.class.getSimpleName();
	
	/** 区分接口同步的类型*/
	public static final String KEY_TYPE = JiaoWeiYYWZ.class.getSimpleName();

	// 违章顺序号使用基类的SyncId字段记录，作为同类信息的唯一标识

	private String cId;
	private String wzStatus;// 违章状态
	private String confiscateCertNo;//扣件证号
	private String operator;// 执法人
	private String operateUnit;//执法分队
	private String videoOP;// 摄录员
	private String driverName;//当事人
	private String idcardType;//身份证明类型
	private String idcardCode;//身份证明编号
	private String carPlate;//违章主体
	private String company;//违章企业
	private String address;//违章地段
	private String owner;//业户名称
	private String ownerId;//业户ID
	private String bsCertNo;//经营许可证号
	private String content;//违章内容
	private Calendar happenDate;//违章日期
	private String evidenceUnit;//保存单位
	private String wzType;//违章类型
	private String buslines;//公交线路
	private String carplateColor;//车牌颜色
	private String driverCert;//资格证号
	private String commitStatus;//提交状态
	private String receipt;//罚没收据编号
	private String notice;//放行通知书编号
	private String businessCertNo;//营运证号
	private Float seating;//座位数
	private String oweRecord;//欠笔录
	private String oweSignature;//欠签名
	private String leaveTroops;//留队部
	private String area;//所属区县
	private Calendar closeDate;//结案日期
	private String desc;//备注
	private String clone;//是否克隆车
	private String subject;//违章项目
	private String detain;//扣留物品
	private String pullUnit;//拖车单位
	private Float penalty;//罚款
	private String unitName;// 分公司
	private String motorcadeName;// 所属车队
	
	@Column(name = "C_ID")
	public String getcId() {
		return cId;
	}
	public void setcId(String cId) {
		this.cId = cId;
	}
	@Column(name = "STATUS")
	public String getWzStatus() {
		return wzStatus;
	}
	public void setWzStatus(String wzStatus) {
		this.wzStatus = wzStatus;
	}
	@Column(name = "CERT_KJ")
	public String getConfiscateCertNo() {
		return confiscateCertNo;
	}
	public void setConfiscateCertNo(String confiscateCertNo) {
		this.confiscateCertNo = confiscateCertNo;
	}
	public String getOperator() {
		return operator;
	}
	public void setOperator(String operator) {
		this.operator = operator;
	}
	@Column(name = "OPERATE_UNIT")
	public String getOperateUnit() {
		return operateUnit;
	}
	public void setOperateUnit(String operateUnit) {
		this.operateUnit = operateUnit;
	}
	@Column(name = "VIDEO_OPERATER")
	public String getVideoOP() {
		return videoOP;
	}
	public void setVideoOP(String videoOP) {
		this.videoOP = videoOP;
	}
	@Column(name = "DRIVER_NAME")
	public String getDriverName() {
		return driverName;
	}
	public void setDriverName(String driverName) {
		this.driverName = driverName;
	}
	@Column(name = "IDCARD_TYPE")
	public String getIdcardType() {
		return idcardType;
	}
	public void setIdcardType(String idcardType) {
		this.idcardType = idcardType;
	}
	@Column(name = "IDCARD_CODE")
	public String getIdcardCode() {
		return idcardCode;
	}
	public void setIdcardCode(String idcardCode) {
		this.idcardCode = idcardCode;
	}
	@Column(name = "CAR_PLATE")
	public String getCarPlate() {
		return carPlate;
	}
	public void setCarPlate(String carPlate) {
		this.carPlate = carPlate;
	}
	public String getCompany() {
		return company;
	}
	public void setCompany(String company) {
		this.company = company;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getOwner() {
		return owner;
	}
	public void setOwner(String owner) {
		this.owner = owner;
	}
	@Column(name = "OWNER_ID")
	public String getOwnerId() {
		return ownerId;
	}
	public void setOwnerId(String ownerId) {
		this.ownerId = ownerId;
	}
	@Column(name = "BS_CERT_NO")
	public String getBsCertNo() {
		return bsCertNo;
	}
	public void setBsCertNo(String bsCertNo) {
		this.bsCertNo = bsCertNo;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	@Column(name = "HAPPEN_DATE")
	public Calendar getHappenDate() {
		return happenDate;
	}
	public void setHappenDate(Calendar happenDate) {
		this.happenDate = happenDate;
	}
	@Column(name = "EVIDENCE_UNIT")
	public String getEvidenceUnit() {
		return evidenceUnit;
	}
	public void setEvidenceUnit(String evidenceUnit) {
		this.evidenceUnit = evidenceUnit;
	}
	@Column(name = "WZ_TYPE")
	public String getWzType() {
		return wzType;
	}
	public void setWzType(String wzType) {
		this.wzType = wzType;
	}
	@Column(name = "BUSLINES")
	public String getBuslines() {
		return buslines;
	}
	public void setBuslines(String buslines) {
		this.buslines = buslines;
	}
	@Column(name = "CARPLATE_COLOR")
	public String getCarplateColor() {
		return carplateColor;
	}
	public void setCarplateColor(String carplateColor) {
		this.carplateColor = carplateColor;
	}
	@Column(name = "DRIVER_CERT")
	public String getDriverCert() {
		return driverCert;
	}
	public void setDriverCert(String driverCert) {
		this.driverCert = driverCert;
	}
	@Column(name = "COMMIT_STATUS")
	public String getCommitStatus() {
		return commitStatus;
	}
	public void setCommitStatus(String commitStatus) {
		this.commitStatus = commitStatus;
	}
	public String getReceipt() {
		return receipt;
	}
	public void setReceipt(String receipt) {
		this.receipt = receipt;
	}
	public String getNotice() {
		return notice;
	}
	public void setNotice(String notice) {
		this.notice = notice;
	}
	@Column(name = "CERT_YY")
	public String getBusinessCertNo() {
		return businessCertNo;
	}
	public void setBusinessCertNo(String businessCertNo) {
		this.businessCertNo = businessCertNo;
	}
	public Float getSeating() {
		return seating;
	}
	public void setSeating(Float seating) {
		this.seating = seating;
	}
	@Column(name = "OWE_RECORD")
	public String getOweRecord() {
		return oweRecord;
	}
	public void setOweRecord(String oweRecord) {
		this.oweRecord = oweRecord;
	}
	@Column(name = "OWE_SIGNATURE")
	public String getOweSignature() {
		return oweSignature;
	}
	public void setOweSignature(String oweSignature) {
		this.oweSignature = oweSignature;
	}
	@Column(name = "LEAVE_TROOPS")
	public String getLeaveTroops() {
		return leaveTroops;
	}
	public void setLeaveTroops(String leaveTroops) {
		this.leaveTroops = leaveTroops;
	}
	public String getArea() {
		return area;
	}
	public void setArea(String area) {
		this.area = area;
	}
	@Column(name = "CLOSE_DATE")
	public Calendar getCloseDate() {
		return closeDate;
	}
	public void setCloseDate(Calendar closeDate) {
		this.closeDate = closeDate;
	}
	@Column(name = "DESC_")
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public String getClone() {
		return clone;
	}
	public void setClone(String clone) {
		this.clone = clone;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getDetain() {
		return detain;
	}
	public void setDetain(String detain) {
		this.detain = detain;
	}
	@Column(name = "PULL_UNIT")
	public String getPullUnit() {
		return pullUnit;
	}
	public void setPullUnit(String pullUnit) {
		this.pullUnit = pullUnit;
	}
	public Float getPenalty() {
		return penalty;
	}
	public void setPenalty(Float penalty) {
		this.penalty = penalty;
	}
	
	@Column(name = "UNIT_NAME")
	public String getUnitName() {
		return unitName;
	}

	public void setUnitName(String unitName) {
		this.unitName = unitName;
	}

	@Column(name = "MOTORCADE_NAME")
	public String getMotorcadeName() {
		return motorcadeName;
	}

	public void setMotorcadeName(String motorcadeName) {
		this.motorcadeName = motorcadeName;
	}
}