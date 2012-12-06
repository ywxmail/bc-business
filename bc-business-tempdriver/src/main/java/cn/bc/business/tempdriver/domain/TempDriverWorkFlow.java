package cn.bc.business.tempdriver.domain;

import java.util.Calendar;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import cn.bc.core.EntityImpl;

/**
 * 司机招聘流程记录
 * 
 * @author lbj
 */
@Entity
@Table(name = "BS_TEMP_DRIVER_WORKFLOW")
public class TempDriverWorkFlow extends EntityImpl {
	private static final long serialVersionUID = 1L;
	
	/** 审批结果：审核中*/
	public static final int OFFER_STATUS_CHECK= 1;
	
	/** 审批结果：聘用*/
	public static final int OFFER_STATUS_PASS= 2;
	
	/** 审批结果：弃用*/
	public static final int OFFER_STATUS_NOPASS= 3;
	
	private TempDriver tempDriver;//招聘的司机
	private String procInstId;//流程实例ID
	private int offerStatus;//审批结果
	private Calendar startTime;//流程发起时间
	
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "PID", referencedColumnName = "ID")
	public TempDriver getTempDriver() {
		return tempDriver;
	}
	public void setTempDriver(TempDriver tempDriver) {
		this.tempDriver = tempDriver;
	}
	
	@Column(name = "PROC_INST_ID")
	public String getProcInstId() {
		return procInstId;
	}
	public void setProcInstId(String procInstId) {
		this.procInstId = procInstId;
	}
	
	@Column(name = "OFFER_STATUS")
	public int getOfferStatus() {
		return offerStatus;
	}
	public void setOfferStatus(int offerStatus) {
		this.offerStatus = offerStatus;
	}
	
	@Column(name = "START_TIME")
	public Calendar getStartTime() {
		return startTime;
	}
	public void setStartTime(Calendar startTime) {
		this.startTime = startTime;
	}
	
}