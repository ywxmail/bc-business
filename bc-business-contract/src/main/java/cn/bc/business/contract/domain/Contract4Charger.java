/**
 * 
 */
package cn.bc.business.contract.domain;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import cn.bc.business.carman.domain.CarMan;

/**
 * 责任人的合同
 * 
 * @author dragon
 */
@Entity
@Table(name = "BS_CONTRACT_CHARGER")
@Inheritance(strategy = InheritanceType.JOINED)
public class Contract4Charger extends Contract4Car {
	private static final long serialVersionUID = 1L;
	public static final String ATTACH_TYPE = Contract4Charger.class.getSimpleName();
	private Set<CarMan> chargers;// 责任人列表
	private String signType;// 签约类型:如新户
	private boolean logout;// 注销:0-未,1-已
	private String oldContent;// 旧合同内容
	private boolean takebackOrigin;// 已经收回原件:0-未1-已
	private boolean includeCost;// 包含检审费用:0-不包含,1-包含
	private String businessType;// 合同性质

	public Contract4Charger() {
		this.setCode("[责任人合同]");
	}

	@ManyToMany(fetch=FetchType.EAGER)
    @JoinTable(name="BS_CARMAN_CONTRACT",
        joinColumns=
            @JoinColumn(name="CONTRACT_ID", referencedColumnName="ID"),
        inverseJoinColumns=
            @JoinColumn(name="CARMAN_ID", referencedColumnName="ID")
        )
    @OrderBy("orderNo asc")
	public Set<CarMan> getChargers() {
		return chargers;
	}

	public void setChargers(Set<CarMan> chargers) {
		this.chargers = chargers;
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
}