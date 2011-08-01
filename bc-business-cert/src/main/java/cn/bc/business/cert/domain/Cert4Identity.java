/**
 * 
 */
package cn.bc.business.cert.domain;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

/**
 * 居民身份证
 * 
 * @author dragon
 */
@Entity
@Table(name = "BS_CERT_IDENTITY")
@Inheritance(strategy = InheritanceType.JOINED)
public class Cert4Identity extends Cert4Man {
	private static final long serialVersionUID = 1L;
	public static final String ATTACH_TYPE = Cert4Identity.class.getSimpleName();

	public Cert4Identity() {
		this.setCertName("居民身份证");
		this.setCertFullName("中华人民共和国居民身份证");
	}
}