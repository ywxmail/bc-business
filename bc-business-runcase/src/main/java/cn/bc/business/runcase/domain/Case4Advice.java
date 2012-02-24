/**
 * 
 */
package cn.bc.business.runcase.domain;

import java.util.Calendar;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 投诉与建议
 * 
 * @author dragon
 */
@Entity
@Table(name = "BS_CASE_ADVICE")
public class Case4Advice extends CaseBase4AdviceAndPraise {
	private static final long serialVersionUID = 1L;
	public static final String ATTACH_TYPE = Case4Advice.class.getSimpleName();
	public static final String KEY_CODE = "runcase.code";
			
	/** 投诉还是建议：投诉 */
	public static final int ADVICE_TYPE_COMPLAIN = 0;
	/** 投诉还是建议：建议 */
	public static final int ADVICE_TYPE_SUGGEST = 1;

	/** 核准状态：未核准 */
	public static final int HANDLE_STATUS_NEW = 0;
	/** 核准状态：已核准 */
	public static final int HANDLE_STATUS_DONE = 1;
	
	private int adviceType;// 标识是投诉还是建议:见 ADVICE_TYPE_XXX 常数的定义
	private String receiveCode;// 受理号
	private String duty;// 责任
	private String extent;// 程度
	private Calendar deliverDate;// 结案书交回日期
	private boolean invalid;// 是否无效
	
	private Long transactorId;// 经办人ID(对应ActorHistory的ID)
	private String transactorName;// 经办人姓名
	
	private Long branchChargerId; // 分公司负责人id(对应ActorHistory的ID)
	private String branchChargerName;// 分公司负责人姓名
	private Long companyApprovalId; // 公司审批人id(对应ActorHistory的ID)
	private String companyApprovalName;// 公司审批人姓名
	
	private String stopProduction;// 停产
	private String study;// 学习
	private float dedit;// 违约金
	private String driverRating;// 驾驶员评级
	
	private Long handlerId; // 核准人id(对应ActorHistory的ID)
	private String handlerName; // 核准人姓名
	private Integer handleStatus = Case4Advice.HANDLE_STATUS_NEW; //核准状态:0-未核准;1-已核准 (默认未核准)
	private String handleOpinion; // 核准意见
	private Calendar handleDate; // 核准时间
	
	private String charger;// 责任人
	
	@Column(name = "ADVICE_TYPE")
	public int getAdviceType() {
		return adviceType;
	}

	public void setAdviceType(int adviceType) {
		this.adviceType = adviceType;
	}

	@Column(name = "RECEIVE_CODE")
	public String getReceiveCode() {
		return receiveCode;
	}

	public void setReceiveCode(String receiveCode) {
		this.receiveCode = receiveCode;
	}

	public String getDuty() {
		return duty;
	}

	public void setDuty(String duty) {
		this.duty = duty;
	}

	public String getExtent() {
		return extent;
	}

	public void setExtent(String extent) {
		this.extent = extent;
	}

	@Column(name = "DELIVER_DATE")
	public Calendar getDeliverDate() {
		return deliverDate;
	}

	public void setDeliverDate(Calendar deliverDate) {
		this.deliverDate = deliverDate;
	}

	@Column(name = "IS_INVALID")
	public boolean isInvalid() {
		return invalid;
	}

	public void setInvalid(boolean invalid) {
		this.invalid = invalid;
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

	@Column(name = "HANDLER_ID")
	public Long getHandlerId() {
		return handlerId;
	}

	public void setHandlerId(Long handlerId) {
		this.handlerId = handlerId;
	}

	@Column(name = "HANDLER_NAME")
	public String getHandlerName() {
		return handlerName;
	}

	public void setHandlerName(String handlerName) {
		this.handlerName = handlerName;
	}

	@Column(name = "HANDLE_STATUS")
	public Integer getHandleStatus() {
		return handleStatus;
	}

	public void setHandleStatus(Integer handleStatus) {
		this.handleStatus = handleStatus;
	}

	@Column(name = "HANDLE_OPINION")
	public String getHandleOpinion() {
		return handleOpinion;
	}

	public void setHandleOpinion(String handleOpinion) {
		this.handleOpinion = handleOpinion;
	}

	@Column(name = "HANDLE_DATE")
	public Calendar getHandleDate() {
		return handleDate;
	}

	public void setHandleDate(Calendar handleDate) {
		this.handleDate = handleDate;
	}

	public String getCharger() {
		return charger;
	}

	public void setCharger(String charger) {
		this.charger = charger;
	}

}