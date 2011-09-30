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
 * 责任人的合同
 * 
 * @author dragon
 */
@Entity
@Table(name = "BS_CONTRACT_CHARGER")
@Inheritance(strategy = InheritanceType.JOINED)
public class Contract4Charger extends Contract {
	private static final long serialVersionUID = 1L;
	public static final String ATTACH_TYPE = Contract4Charger.class.getSimpleName();
	private String signType;// 签约类型:如新户
	private boolean logout;// 注销:0-未,1-已
	private String oldContent;// 旧合同内容
	private boolean takebackOrigin;// 已经收回原件:0-未1-已
	private boolean includeCost;// 包含检审费用:0-不包含,1-包含
	private String businessType;// 合同性质
	private Long changerId1;	//责任ID1
	private String changerName1;//责任姓名1	
	private Long changerId2;	//责任ID2
	private String changerName2;//责任姓名2	

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

	public boolean isLogout() {
		return logout;
	}

	public void setLogout(boolean additionProtocol) {
		this.logout = additionProtocol;
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

	@Column(name = "CHARGER1_ID")
	public Long getChangerId1() {
		return changerId1;
	}

	public void setChangerId1(Long changerId1) {
		this.changerId1 = changerId1;
	}

	@Column(name = "CHARGER1_NAME")
	public String getChangerName1() {
		return changerName1;
	}

	public void setChangerName1(String changerName1) {
		this.changerName1 = changerName1;
	}

	@Column(name = "CHARGER2_ID")
	public Long getChangerId2() {
		return changerId2;
	}

	public void setChangerId2(Long changerId2) {
		this.changerId2 = changerId2;
	}

	@Column(name = "CHARGER2_NAME")
	public String getChangerName2() {
		return changerName2;
	}

	public void setChangerName2(String changerName2) {
		this.changerName2 = changerName2;
	}
	
	
}