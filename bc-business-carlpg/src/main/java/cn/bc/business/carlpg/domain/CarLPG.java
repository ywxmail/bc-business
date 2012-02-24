/**
 * 
 */
package cn.bc.business.carlpg.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import cn.bc.identity.domain.FileEntityImpl;

/**
 * LPG配置类
 * 
 * @author lbj
 */
@Entity
@Table(name = "BS_CAR_LPGMODEL")
public class CarLPG extends FileEntityImpl {
	private static final long serialVersionUID = 1L;
	public static final String KEY_UID = CarLPG.class.getSimpleName();
	public static final String KEY_CODE = "carlpg.code";
	
	private String order;//排序号
	private int status;// 状态
	private String name;//'名称，如：兰天达';
	private String fullname;//'专用装置供应商，如:北京兰天达';
	private String model;// '专用装置品牌型号';
	private String gpmodel;//'钢瓶品牌型号';
	private String jcfmodel;// '集成阀品牌型号';
	private String qhqmodel;// '汽化器品牌型号';
	private String psqmodel;//  '混合/喷射器品牌型号';
	private String desc;//'描述';


	@Column(name = "ORDER_")
	public String getOrder() {
		return order;
	}

	public void setOrder(String order) {
		this.order = order;
	}
	
	@Column(name = "STATUS_")
	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}
	
	@Column(name="NAME_")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name="FULL_NAME")
	public String getFullname() {
		return fullname;
	}

	public void setFullname(String fullname) {
		this.fullname = fullname;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	@Column(name="GP_MODEL")
	public String getGpmodel() {
		return gpmodel;
	}

	public void setGpmodel(String gpmodel) {
		this.gpmodel = gpmodel;
	}

	@Column(name="JCF_MODEL")
	public String getJcfmodel() {
		return jcfmodel;
	}

	public void setJcfmodel(String jcfmodel) {
		this.jcfmodel = jcfmodel;
	}

	@Column(name="QHQ_MODEL")
	public String getQhqmodel() {
		return qhqmodel;
	}

	public void setQhqmodel(String qhqmodel) {
		this.qhqmodel = qhqmodel;
	}

	@Column(name="PSQ_MODEL")
	public String getPsqmodel() {
		return psqmodel;
	}

	public void setPsqmodel(String psqmodel) {
		this.psqmodel = psqmodel;
	}

	@Column(name="DESC_")
	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}
	
	
	
	
}