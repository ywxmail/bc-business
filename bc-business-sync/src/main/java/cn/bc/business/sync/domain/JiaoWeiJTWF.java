/**
 * 
 */
package cn.bc.business.sync.domain;

import java.util.Calendar;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import cn.bc.sync.domain.SyncBase;

/**
 * 从交委WebService接口同步的交通违章信息
 * 
 * @author dragon
 */
@Entity
@Table(name = "BS_SYNC_JIAOWEI_JTWF")
public class JiaoWeiJTWF extends SyncBase {
	private static final long serialVersionUID = 1L;
	/** UUID的前缀，实际的uid使用KEY_UID + "-" + id */
	public static final String KEY_UID = SyncBase.class.getSimpleName();

	// 违章顺序号使用基类的SyncId字段记录，作为同类信息的唯一标识

	private Calendar happenDate;// 违章时间
	private String content;// 违章内容
	private String carPlate;// 车牌号码
	private String driverName;// 当事司机姓名
	private String driverCert;// 服务资格证
	private Float jeom;// 本次扣分
	private String companyName;// 公司名称

	@Column(name = "HAPPEN_DATE")
	public Calendar getHappenDate() {
		return happenDate;
	}

	public void setHappenDate(Calendar happenDate) {
		this.happenDate = happenDate;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	@Column(name = "CAR_PLATE")
	public String getCarPlate() {
		return carPlate;
	}

	public void setCarPlate(String carPlate) {
		this.carPlate = carPlate;
	}

	@Column(name = "DRIVER_NAME")
	public String getDriverName() {
		return driverName;
	}

	public void setDriverName(String driverName) {
		this.driverName = driverName;
	}

	@Column(name = "DRIVER_CERT")
	public String getDriverCert() {
		return driverCert;
	}

	public void setDriverCert(String driverCert) {
		this.driverCert = driverCert;
	}

	public Float getJeom() {
		return jeom;
	}

	public void setJeom(Float jeom) {
		this.jeom = jeom;
	}

	@Column(name = "COMPANY_NAME")
	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
}