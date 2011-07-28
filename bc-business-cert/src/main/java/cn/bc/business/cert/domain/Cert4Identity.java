/**
 * 
 */
package cn.bc.business.cert.domain;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

/**
 * 身份证
 * 
 * @author dragon
 */
@Entity
@Table(name = "BS_CERT_IDENTITY")
@Inheritance(strategy = InheritanceType.JOINED)
public class Cert4Identity extends Cert {
	private static final long serialVersionUID = 1L;

	private String address;// 身份证地址
	
	public Cert4Identity(){
		this.setName("身份证");
		this.setFullName("中华人民共和国居民身份证");
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}
}