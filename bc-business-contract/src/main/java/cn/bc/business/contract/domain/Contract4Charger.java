/**
 * 
 */
package cn.bc.business.contract.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

/**
 * 经济的合同
 * 
 * @author dragon
 */
@Entity
@Table(name = "BS_CONTRACT_CHARGER")
@Inheritance(strategy = InheritanceType.JOINED)
public class Contract4Charger extends Contract {
	private static final long serialVersionUID = 1L;
	public static final String ATTACH_TYPE = Contract4Charger.class.getSimpleName();
	public static final String KEY_UID = Contract4Charger.class.getSimpleName();
	public static final String KEY_CODE = "contract.code";
	private String signType;// 签约类型:如新户
	private String oldContent;// 旧合同内容
	private boolean takebackOrigin;// 已经收回原件:0-未1-已
	private boolean includeCost;// 包含检审费用:0-不包含,1-包含
	private String businessType;// 合同性质
	private String contractVersionNo;// 合同版本号
	private String paymentDate;// 缴费日
	private String scrapTo;// 残值归属

	public Contract4Charger() {
		this.setCode("[经济合同]");
	}

	@Column(name = "SIGN_TYPE")
	public String getSignType() {
		return signType;
	}

	public void setSignType(String signType) {
		this.signType = signType;
	}

	@Column(name = "OLD_CONTENT")
	public String getOldContent() {
		return oldContent;
	}

	public void setOldContent(String preIndustryName) {
		this.oldContent = preIndustryName;
	}

	@Column(name = "TAKEBACK_ORIGIN")
	public boolean isTakebackOrigin() {
		return takebackOrigin;
	}

	public void setTakebackOrigin(boolean preIndustryType) {
		this.takebackOrigin = preIndustryType;
	}

	@Column(name = "INCLUDE_COST")
	public boolean isIncludeCost() {
		return includeCost;
	}

	public void setIncludeCost(boolean hiringProcedure) {
		this.includeCost = hiringProcedure;
	}

	@Column(name = "BS_TYPE")
	public String getBusinessType() {
		return businessType;
	}

	public void setBusinessType(String businessType) {
		this.businessType = businessType;
	}

	@Column(name = "CONTRACT_VERSION_NO")
	public String getContractVersionNo() {
		return contractVersionNo;
	}

	public void setContractVersionNo(String contractVersionNo) {
		this.contractVersionNo = contractVersionNo;
	}

	@Column(name = "PAYMENT_DATE")
	public String getPaymentDate() {
		return paymentDate;
	}

	public void setPaymentDate(String paymentDate) {
		this.paymentDate = paymentDate;
	}

	public String getScrapTo() {
		return scrapTo;
	}

	public void setScrapTo(String scrapTo) {
		this.scrapTo = scrapTo;
	}

}