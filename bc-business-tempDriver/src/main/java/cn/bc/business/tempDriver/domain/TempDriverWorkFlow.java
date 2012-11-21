package cn.bc.business.tempDriver.domain;

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
	
	/** 录用状态：审核中*/
	public static final int OFFER_STATUS_CHECK= 0;
	
	/** 录用状态：录用*/
	public static final int OFFER_STATUS_PASS= 1;
	
	/** 录用状态：不录用*/
	public static final int OFFER_STATUS_NOPASS= 2;
	
	private TempDriver tempDriver;//招聘的司机
	private String procInstId;//流程实例ID
	private int offerStatus;//录用状态
	private Calendar startTime;//流程发起时间
	
	@ManyToOne(fetch = FetchType.EAGER, optional = false)
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