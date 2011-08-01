package cn.bc.business.motorcade.domain;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import cn.bc.identity.domain.RichFileEntityImpl;


/**
 * 车辆
 * 
 * @author dragon
 */
@Entity
@Table(name = "BS_HISTORY_CAR_QUANTITY")
public class HistoryCarQuantity extends RichFileEntityImpl {
	private static final long serialVersionUID = 1L;

	private Long year;//年份
	private Long month;//月份
	private Long carquantity;//车辆数
	private Motorcade motorcade;//车队
	
	public Long getYear() {
		return year;
	}
	public void setYear(Long year) {
		this.year = year;
	}
	public Long getMonth() {
		return month;
	}
	public void setMonth(Long month) {
		this.month = month;
	}
	public Long getCarquantity() {
		return carquantity;
	}
	public void setCarquantity(Long carquantity) {
		this.carquantity = carquantity;
	}
	@ManyToOne(fetch = FetchType.EAGER, optional = false, targetEntity = Motorcade.class)
	@JoinColumn(name = "MOTORCADE_ID", referencedColumnName = "ID")
	public Motorcade getMotorcade() {
		return motorcade;
	}
	public void setMotorcade(Motorcade motorcade) {
		this.motorcade = motorcade;
	}
	
	@Column(name = "MOTORCADE_NAME")
	public String getMotorcadeName() {
		if (this.motorcade != null) {
			return this.motorcade.getName();
		} else {
			return null;
		}
	}
	public void setMotorcadeName(String motorcadeName) {
		// do nothing
	}
	
}