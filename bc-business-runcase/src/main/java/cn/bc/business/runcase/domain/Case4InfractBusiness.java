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

	private Calendar receiveDate;// 接案日期
	private Long receiverId;// 接案人ID(对应ActorHistory的ID)
	private String receiverName;// 接案人姓名

	private Long chargerId;// 责任人1ID(对应CarMan的ID)
	private String chargerName;// 责任人1姓名
	private Long chargerId2;// 责任人2ID(对应CarMan的ID)
	private String chargerName2;// 责任人2姓名

	private String duty;// 责任
	private String sort;// 性质
	private String extent;// 程度
	private String confiscateCertType;// 没收证件类型
	private String confiscateCertNo;// 扣件证号
	private String businessCertNo;// 营运证号

	private String detain;// 扣留物品
	private Float jeom;// 扣分
	private Float penalty;// 罚款金额
	private Float penalty2;// 违约金
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

	@Column(name = "CHARGER1_ID")
	public Long getChargerId() {
		return chargerId;
	}

	public void setChargerId(Long chargerId) {
		this.chargerId = chargerId;
	}

	@Column(name = "CHARGER1_NAME")
	public String getChargerName() {
		return chargerName;
	}

	public void setChargerName(String chargerName) {
		this.chargerName = chargerName;
	}

	@Column(name = "CHARGER2_ID")
	public Long getChargerId2() {
		return chargerId2;
	}

	public void setChargerId2(Long chargerId2) {
		this.chargerId2 = chargerId2;
	}

	@Column(name = "CHARGER2_NAME")
	public String getChargerName2() {
		return chargerName2;
	}

	public void setChargerName2(String chargerName2) {
		this.chargerName2 = chargerName2;
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

	public Float getJeom() {
		return jeom;
	}

	public void setJeom(Float jeom) {
		this.jeom = jeom;
	}

	public Float getPenalty() {
		return penalty;
	}

	public void setPenalty(Float penalty) {
		this.penalty = penalty;
	}

	public Float getPenalty2() {
		return penalty2;
	}

	public void setPenalty2(Float penalty2) {
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
}