package cn.bc.business.mix.domain;

import javax.persistence.Entity;
import javax.persistence.Table;

import cn.bc.identity.domain.FileEntityImpl;

/**
 * 信息中心综合查询
 * 
 * @author dragon
 */
@Entity
@Table(name = "BS_INSURANCE_TYPE")
public class InfoCenter extends FileEntityImpl {
	private static final long serialVersionUID = 1L;
	/** 搜索类型：车牌-车牌 */
	public final static String TYPE_CAR_PLATE = "plate";
	/** 搜索类型：车辆-管理号 */
	public final static String TYPE_CAR_MANAGENO = "manageNo";
	/** 搜索类型：车辆-自编号 */
	public final static String TYPE_CAR_CODE = "code";
	/** 搜索类型：车牌-发动机号 */
	public final static String TYPE_CAR_ENGINENO = "engineNo";
	/** 搜索类型：车牌-车架号 */
	public final static String TYPE_CAR_VIN = "vin";
	/** 搜索类型：车牌-购置税发票号 */
	public final static String TYPE_CAR_INVOICENO = "invoiceNo";
	/** 搜索类型：司机或责任人-服务资格证 */
	public final static String TYPE_MAN_CERT_FWZG = "manCert4fwzg";
	/** 搜索类型：司机或责任人-姓名 */
	public final static String TYPE_MAN_NAME = "manName";
}