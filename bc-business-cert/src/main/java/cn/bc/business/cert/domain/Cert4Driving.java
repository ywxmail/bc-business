/**
 * 
 */
package cn.bc.business.cert.domain;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

/**
 * 驾驶证
 * 
 * @author dragon
 */
@Entity
@Table(name = "BS_CERT_DRIVING")
@Inheritance(strategy = InheritanceType.JOINED)
public class Cert4Driving extends Cert {
	private static final long serialVersionUID = 1L;

	private String model;// 准驾车型
	
	public Cert4Driving(){
		this.setName("驾驶证");
		this.setFullName("中华人民共和国机动车驾驶证");
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}
}