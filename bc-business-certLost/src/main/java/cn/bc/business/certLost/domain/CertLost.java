/**
 * 
 */
package cn.bc.business.certLost.domain;

import java.util.Calendar;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import cn.bc.business.car.domain.Car;
import cn.bc.business.carman.domain.CarMan;
import cn.bc.identity.domain.ActorHistory;
import cn.bc.identity.domain.FileEntityImpl;

/**
 * 证照遗失
 * 
 * 
 * @author zxr
 */
@Entity
@Table(name = "BS_CERT_LOST")
public class CertLost extends FileEntityImpl {
	private static final long serialVersionUID = 1L;
	private Car car;// 车辆Id
	private Long motorcadeId;// 车队ID
	private CarMan driver;// 司机
	private String driverNane;// 司机姓名
	private String subject;// 标题
	private Calendar lostDate;// 遗失日期
	private ActorHistory transactor;// 经办人
	private String transactorName;// 经办人姓名
	private String description;// 备注
	private Set<CertLostItem> certLostItem;// 遗失证照

	@ManyToOne(fetch = FetchType.EAGER, optional = true)
	@JoinColumn(name = "CAR_ID", referencedColumnName = "ID")
	public Car getCar() {
		return car;
	}

	public void setCar(Car car) {
		this.car = car;
	}

	@Column(name = "MOTORCADE_ID")
	public Long getMotorcadeId() {
		return motorcadeId;
	}

	public void setMotorcadeId(Long motorcadeId) {
		this.motorcadeId = motorcadeId;
	}

	@ManyToOne(fetch = FetchType.EAGER, optional = true)
	@JoinColumn(name = "DRIVER_ID", referencedColumnName = "ID")
	public CarMan getDriver() {
		return driver;
	}

	public void setDriver(CarMan driver) {
		this.driver = driver;
	}

	@Column(name = "DRIVER")
	public String getDriverNane() {
		return driverNane;
	}

	public void setDriverNane(String driverNane) {
		this.driverNane = driverNane;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	@Column(name = "LOST_DATE")
	public Calendar getLostDate() {
		return lostDate;
	}

	public void setLostDate(Calendar lostDate) {
		this.lostDate = lostDate;
	}

	@Column(name = "TRANSACTOR_NAME")
	public String getTransactorName() {
		return transactorName;
	}

	@ManyToOne(fetch = FetchType.EAGER, optional = true)
	@JoinColumn(name = "TRANSACTOR_ID", referencedColumnName = "ID")
	public ActorHistory getTransactor() {
		return transactor;
	}

	public void setTransactor(ActorHistory transactor) {
		this.transactor = transactor;
	}

	public void setTransactorName(String transactorName) {
		this.transactorName = transactorName;
	}

	@Column(name = "DESC_")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@OneToMany(mappedBy = "certLost", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
	public Set<CertLostItem> getCertLostItem() {
		return certLostItem;
	}

	public void setCertLostItem(Set<CertLostItem> certLostItem) {
		this.certLostItem = certLostItem;
	}

}