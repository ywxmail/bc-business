/**
 * 
 */
package cn.bc.business.cert.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

/**
 * 从业资格证
 * 
 * @author dragon
 */
@Entity
@Table(name = "BS_CERT_CYZG")
@Inheritance(strategy = InheritanceType.JOINED)
public class Cert4CongYeZiGe extends Cert4Man {
	private static final long serialVersionUID = 1L;
	public static final String ATTACH_TYPE = Cert4CongYeZiGe.class.getSimpleName();
	public static final String KEY_CODE = "cert.certCode";
	private String scope;// 从业资格
	private String identityNo;// 身份证件号
	private String serviceUnit;// 服务单位
	
	public Cert4CongYeZiGe(){
		this.setCertName("从业资格证");
		this.setCertFullName("从业资格证");
	}

	@Column(name = "SCOPE_")
	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}

	@Column(name = "IDENTITY_NO")
	public String getIdentityNo() {
		return identityNo;
	}

	public void setIdentityNo(String identityNo) {
		this.identityNo = identityNo;
	}

	@Column(name = "SERVICE_UNIT")
	public String getServiceUnit() {
		return serviceUnit;
	}

	public void setServiceUnit(String serviceUnit) {
		this.serviceUnit = serviceUnit;
	}
}