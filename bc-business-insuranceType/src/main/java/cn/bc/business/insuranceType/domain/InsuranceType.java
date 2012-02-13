package cn.bc.business.insuranceType.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import cn.bc.identity.domain.FileEntityImpl;

/**
 * 车保险种
 * 
 * @author dragon
 */
@Entity
@Table(name = "BS_INSURANCE_TYPE")
public class InsuranceType extends FileEntityImpl {
	private static final long serialVersionUID = 1L;
	
	/** 险种 **/
	public static final int TYPE_PLANT=0;//
	/** 模板 **/
	public static final int TYPE_TEMPLATE=1;//模板
	
	private String Name;// 险种名称
	private String coverage;// 保额
	private String description;// 备注
	private int status;// 状态
	private int type;//类型 0-险种，1-模板
	private Long pid;//所属模板id
	private String orderNo;//排序号

	public String getName() {
		return Name;
	}

	public void setName(String name) {
		Name = name;
	}

	public String getCoverage() {
		return coverage;
	}

	public void setCoverage(String coverage) {
		this.coverage = coverage;
	}

	@Column(name = "DESC_")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Column(name = "STATUS_")
	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	@Column(name = "TYPE_")
	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public Long getPid() {
		return pid;
	}

	public void setPid(Long pid) {
		this.pid = pid;
	}
	
	@Column(name = "ORDER_")
	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	

	
}