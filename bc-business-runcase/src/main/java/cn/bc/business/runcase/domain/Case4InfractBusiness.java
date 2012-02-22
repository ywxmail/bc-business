/**
 * 
 */
package cn.bc.business.runcase.domain;

import java.util.Calendar;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 营运违章
 * 
 * @author dragon
 */
@Entity
@Table(name = "BS_CASE_INFRACT_BUSINESS")
public class Case4InfractBusiness extends CaseBase {
	private static final long serialVersionUID = 1L;
	public static final String KEY_CODE = "runcase.code";
	public static final String ATTACH_TYPE = Case4InfractBusiness.class
			.getSimpleName();

	/** 营运违章 */
	public static final int CATEGORY_BUSINESS = 0;
	/** 站场违章 */
	public static final int CATEGORY_STATION = 1;
	
	private Calendar receiveDate;// 接案日期
	private Long receiverId;// 接案人ID(对应ActorHistory的ID)
	private String receiverName;// 接案人姓名

	private String charger;// 责任人

	private String duty;// 责任
	private String sort;// 性质
	private String extent;// 程度
	private String confiscateCertType;// 没收证件类型
	private String confiscateCertNo;// 扣件证号
	private String businessCertNo;// 营运证号

	private String detain;// 扣留物品
	private float jeom;// 扣分
	private float penalty;// 罚款金额
	private float penalty2;// 违约金
	private String area;// 所属区县
	private String pullUnit;// 拖车单位
	private String operator;// 执法人
	private String operateUnit;// 执法机关
	private String receipt;// 罚没收据编号
	private String comment;// 处理意见

	private boolean invalid;// 是否无效
	private boolean seal;// 是否盖章
	private boolean deliver;// 是否交结案单
	private boolean close;// 是否结案
	private boolean overdue;// 是否过期
	private boolean stop;// 是否停场
	
	private Long transactorId;// 经办人ID(对应ActorHistory的ID)
	private String transactorName;// 经办人姓名
	
	private Long branchChargerId; // 分公司负责人id(对应ActorHistory的ID)
	private String branchChargerName;// 分公司负责人姓名
	private Long companyApprovalId; // 公司审批人id(对应ActorHistory的ID)
	private String companyApprovalName;// 公司审批人姓名
	
	private String content;// 内容
	private String stopProduction;// 停产
	private String study;// 学习
	private float dedit;// 违约金
	private String driverRating;// 驾驶员评级
	
	private int category;//类别:0-营运违章,1-站场违章

	@Column(name = "RECEIVE_DATE")
	public Calendar getReceiveDate() {
		return receiveDate;
	}

	public void setReceiveDate(Calendar receiveDate) {
		this.receiveDate = receiveDate;
	}

	@Column(name = "RECEIVER_ID")
	public Long getReceiverId() {
		return receiverId;
	}

	public void setReceiverId(Long receiverId) {
		this.receiverId = receiverId;
	}

	@Column(name = "RECEIVER_NAME")
	public String getReceiverName() {
		return receiverName;
	}

	public void setReceiverName(String receiverName) {
		this.receiverName = receiverName;
	}

	public String getCharger() {
		return charger;
	}

	public void setCharger(String charger) {
		this.charger = charger;
	}

	public String getDuty() {
		return duty;
	}

	public void setDuty(String duty) {
		this.duty = duty;
	}

	public String getSort() {
		return sort;
	}

	public void setSort(String sort) {
		this.sort = sort;
	}

	public String getExtent() {
		return extent;
	}

	public void setExtent(String extent) {
		this.extent = extent;
	}

	@Column(name = "CERT_KJ_TYPE")
	public String getConfiscateCertType() {
		return confiscateCertType;
	}

	public void setConfiscateCertType(String confiscateCertType) {
		this.confiscateCertType = confiscateCertType;
	}

	@Column(name = "CERT_KJ")
	public String getConfiscateCertNo() {
		return confiscateCertNo;
	}

	public void setConfiscateCertNo(String confiscateCertNo) {
		this.confiscateCertNo = confiscateCertNo;
	}

	@Column(name = "CERT_YY")
	public String getBusinessCertNo() {
		return businessCertNo;
	}

	public void setBusinessCertNo(String businessCertNo) {
		this.businessCertNo = businessCertNo;
	}

	public String getDetain() {
		return detain;
	}

	public void setDetain(String detain) {
		this.detain = detain;
	}

	public float getJeom() {
		return jeom;
	}

	public void setJeom(float jeom) {
		this.jeom = jeom;
	}

	public float getPenalty() {
		return penalty;
	}

	public void setPenalty(float penalty) {
		this.penalty = penalty;
	}

	public float getPenalty2() {
		return penalty2;
	}

	public void setPenalty2(float penalty2) {
		this.penalty2 = penalty2;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	@Column(name = "PULL_UNIT")
	public String getPullUnit() {
		return pullUnit;
	}

	public void setPullUnit(String pullUnit) {
		this.pullUnit = pullUnit;
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

	public String getReceipt() {
		return receipt;
	}

	public void setReceipt(String receipt) {
		this.receipt = receipt;
	}

	@Column(name = "COMMENT_")
	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	@Column(name = "IS_INVALID")
	public boolean isInvalid() {
		return invalid;
	}

	public void setInvalid(boolean invalid) {
		this.invalid = invalid;
	}

	@Column(name = "IS_SEAL")
	public boolean isSeal() {
		return seal;
	}

	public void setSeal(boolean seal) {
		this.seal = seal;
	}

	@Column(name = "IS_DELIVER")
	public boolean isDeliver() {
		return deliver;
	}

	public void setDeliver(boolean deliver) {
		this.deliver = deliver;
	}

	@Column(name = "IS_CLOSE")
	public boolean isClose() {
		return close;
	}

	public void setClose(boolean close) {
		this.close = close;
	}

	@Column(name = "IS_OVERDUE")
	public boolean isOverdue() {
		return overdue;
	}

	public void setOverdue(boolean overdue) {
		this.overdue = overdue;
	}

	@Column(name = "IS_STOP")
	public boolean isStop() {
		return stop;
	}

	public void setStop(boolean stop) {
		this.stop = stop;
	}
	
	@Column(name = "TRANSACTOR_ID")
	public Long getTransactorId() {
		return transactorId;
	}

	public void setTransactorId(Long transactorId) {
		this.transactorId = transactorId;
	}

	@Column(name = "TRANSACTOR_NAME")
	public String getTransactorName() {
		return transactorName;
	}

	public void setTransactorName(String transactorName) {
		this.transactorName = transactorName;
	}

	@Column(name = "BRANCH_CHARGER_ID")
	public Long getBranchChargerId() {
		return branchChargerId;
	}

	public void setBranchChargerId(Long branchChargerId) {
		this.branchChargerId = branchChargerId;
	}

	@Column(name = "BRANCH_CHARGER_NAME")
	public String getBranchChargerName() {
		return branchChargerName;
	}

	public void setBranchChargerName(String branchChargerName) {
		this.branchChargerName = branchChargerName;
	}

	@Column(name = "COMPANY_APPROVAL_ID")
	public Long getCompanyApprovalId() {
		return companyApprovalId;
	}

	public void setCompanyApprovalId(Long companyApprovalId) {
		this.companyApprovalId = companyApprovalId;
	}

	@Column(name = "COMPANY_APPROVAL_NAME")
	public String getCompanyApprovalName() {
		return companyApprovalName;
	}

	public void setCompanyApprovalName(String companyApprovalName) {
		this.companyApprovalName = companyApprovalName;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	@Column(name = "STOP_PRODUCTION")
	public String getStopProduction() {
		return stopProduction;
	}

	public void setStopProduction(String stopProduction) {
		this.stopProduction = stopProduction;
	}

	public String getStudy() {
		return study;
	}

	public void setStudy(String study) {
		this.study = study;
	}

	public float getDedit() {
		return dedit;
	}

	public void setDedit(float dedit) {
		this.dedit = dedit;
	}

	@Column(name = "DRIVER_RATING")
	public String getDriverRating() {
		return driverRating;
	}

	public void setDriverRating(String driverRating) {
		this.driverRating = driverRating;
	}

	public int getCategory() {
		return category;
	}

	public void setCategory(int category) {
		this.category = category;
	}
	
}