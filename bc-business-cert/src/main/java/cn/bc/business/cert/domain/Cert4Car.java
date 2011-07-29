/**
 * 
 */
package cn.bc.business.cert.domain;

import javax.persistence.MappedSuperclass;

/**
 * 与车有关的证件信息
 * 
 * @author dragon
 */
@MappedSuperclass
public abstract class Cert4Car extends Cert {
	private static final long serialVersionUID = 1L;

	private String factory;// 品牌型号，如“桑塔纳SVW7182QQD”
	private String plate;// 车牌及号码，如“粤AC4X74”
	
	public String getFactory() {
		return factory;
	}
	public void setFactory(String factory) {
		this.factory = factory;
	}
	
	public String getPlate() {
		return plate;
	}
	public void setPlate(String plate) {
		this.plate = plate;
	}
}