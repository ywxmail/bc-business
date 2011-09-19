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
 * 道路运输证
 * 
 * @author dragon
 */
@Entity
@Table(name = "BS_CERT_ROADTRANSPORT")
@Inheritance(strategy = InheritanceType.JOINED)
public class Cert4RoadTransport extends Cert4Car {
	private static final long serialVersionUID = 1L;
	public static final String ATTACH_TYPE = Cert4RoadTransport.class
			.getSimpleName();

	private String owner;// 业户名称
	private String address;// 地址
	private String businessCertNo;// 经营许可证号
	private String seat;// 吨（座）位
	private int dimLen;// 外廓尺寸：长，单位mm
	private int dimWidth;// 外廓尺寸：宽，单位mm
	private int dimHeight;// 外廓尺寸：高，单位mm
	private String scope;// 经营范围
	private String level;// 技术等级
	private String desc;// 备注

	public Cert4RoadTransport() {
		this.setCertName("道路运输证");
		this.setCertFullName("中华人民共和国机道路运输证");
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	@Column(name = "BS_CERT_NO")
	public String getBusinessCertNo() {
		return businessCertNo;
	}

	public void setBusinessCertNo(String businessCertNo) {
		this.businessCertNo = businessCertNo;
	}

	public String getSeat() {
		return seat;
	}

	public void setSeat(String seat) {
		this.seat = seat;
	}

	@Column(name = "DIM_LEN")
	public int getDimLen() {
		return dimLen;
	}

	public void setDimLen(int dimLen) {
		this.dimLen = dimLen;
	}

	@Column(name = "DIM_WIDTH")
	public int getDimWidth() {
		return dimWidth;
	}

	public void setDimWidth(int dimWidth) {
		this.dimWidth = dimWidth;
	}

	@Column(name = "DIM_HEIGHT")
	public int getDimHeight() {
		return dimHeight;
	}

	public void setDimHeight(int dimHeight) {
		this.dimHeight = dimHeight;
	}

	@Column(name = "SCOPE_")
	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}

	@Column(name = "LEVEL_")
	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	@Column(name = "DESC_")
	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}
	
	
}