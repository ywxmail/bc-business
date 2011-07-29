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
 * 服务资格证
 * 
 * @author dragon
 */
@Entity
@Table(name = "BS_CERT_FWZG")
@Inheritance(strategy = InheritanceType.JOINED)
public class Cert4FuWuZiGe extends Cert4Man {
	private static final long serialVersionUID = 1L;
	public static final String ATTACH_TYPE = Cert4FuWuZiGe.class.getSimpleName();
	
	private String level;// 等级
	private String serviceUnit;// 服务单位
	
	public Cert4FuWuZiGe(){
		this.setCertName("服务资格证");
		this.setCertFullName("广州市客运出租汽车驾驶员服务资格证");
	}

	@Column(name = "LEVEL_")
	public String getLevel() {
		return level;
	}

	public void setLevel(String scope) {
		this.level = scope;
	}

	@Column(name = "SERVICE_UNIT")
	public String getServiceUnit() {
		return serviceUnit;
	}

	public void setServiceUnit(String serviceUnit) {
		this.serviceUnit = serviceUnit;
	}
}