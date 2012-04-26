package cn.bc.business.fee.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import cn.bc.core.EntityImpl;

/**
 * 承包费明细
 * 
 * @author wis
 */
@Entity
@Table(name = "BS_FEE_DETAIL")
public class FeeDetail extends EntityImpl {
	private static final long serialVersionUID = 1L;
	
	/**明细类型：实收明细*/
	public static final int TYPE_REAL 	= 1;
	/**明细类型：欠费明细*/
	public static final int TYPE_OWE 	= 2;
	
	private Fee fee;// 承包费 管理
	private int feeType;// 明细类型
	private String feeName;// 费用名称
	private float charge; //金额
	private String feeDescription;// 备注
	
	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	@JoinColumn(name = "FID", referencedColumnName = "ID")
	public Fee getFee() {
		return fee;
	}
	public void setFee(Fee fee) {
		this.fee = fee;
	}
	
	@Column(name = "FEE_TYPE")
	public int getFeeType() {
		return feeType;
	}
	public void setFeeType(int feeType) {
		this.feeType = feeType;
	}
	@Column(name = "FEE_NAME")
	public String getFeeName() {
		return feeName;
	}
	public void setFeeName(String feeName) {
		this.feeName = feeName;
	}
	public float getCharge() {
		return charge;
	}
	public void setCharge(float charge) {
		this.charge = charge;
	}
	@Column(name = "FEE_DESC")
	public String getFeeDescription() {
		return feeDescription;
	}
	public void setFeeDescription(String feeDescription) {
		this.feeDescription = feeDescription;
	}


}