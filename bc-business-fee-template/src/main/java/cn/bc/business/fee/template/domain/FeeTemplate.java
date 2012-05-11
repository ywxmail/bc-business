package cn.bc.business.fee.template.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import cn.bc.BCConstants;
import cn.bc.identity.domain.FileEntityImpl;

/**
 * 费用模板
 * 
 * @author admin
 * 
 */

@Entity
@Table(name = "BS_FEE_TEMPLATE")
public class FeeTemplate extends FileEntityImpl {
	private static final long serialVersionUID = 1L;

	/** 收费方式：每月 */
	public static final int PAY_TYPE_MONTH = 1;
	/** 收费方式：每季 */
	public static final int PAY_TYPE_SEASON = 2;
	/** 收费方式：每年 */
	public static final int PAY_TYPE_YEAR = 3;
	/** 收费方式:一次性 */
	public static final int PAY_TYPE_ALL = 4;

	/** 类型:费用 */
	public static final int TYPE_FEE = 1;
	/** 类型:模板 */
	public static final int TYPE_TEMPLATE = 0;

	private int status = BCConstants.STATUS_ENABLED;
	private String module;// 所属模块
	private int type;// 类型
	private Long pid;// 所属模板
	private String order;// 排序号
	private String name;// 名称
	private Float price;// 金额
	private Integer count;// 数量
	private Integer payType;// 收费方式
	private String desc;// 描述
	private String spec;// 特殊配置

	public String getSpec() {
		return spec;
	}

	public void setSpec(String spec) {
		this.spec = spec;
	}

	@Column(name = "STATUS_")
	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	@Column(name = "MODULE_")
	public String getModule() {
		return module;
	}

	public void setModule(String module) {
		this.module = module;
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
	public String getOrder() {
		return order;
	}

	public void setOrder(String order) {
		this.order = order;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Float getPrice() {
		return price;
	}

	public void setPrice(Float price) {
		this.price = price;
	}

	@Column(name = "COUNT_")
	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

	@Column(name = "PAY_TYPE")
	public Integer getPayType() {
		return payType;
	}

	public void setPayType(Integer payType) {
		this.payType = payType;
	}

	@Column(name = "DESC_")
	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

}
