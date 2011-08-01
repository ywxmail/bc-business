/**
 * 
 */
package cn.bc.business.cert.domain;

import java.util.Calendar;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

/**
 * 机动车驾驶证
 * 
 * @author dragon
 */
@Entity
@Table(name = "BS_CERT_DRIVING")
@Inheritance(strategy = InheritanceType.JOINED)
public class Cert4Driving extends Cert4Man {
	private static final long serialVersionUID = 1L;
	public static final String ATTACH_TYPE = Cert4Driving.class.getSimpleName();

	private String model;// 准驾车型
	private Calendar receiveDate;// 初次领证日期
	private String archiveNo;// 档案编号
	private String record;// 记录
	private String validFor;// 有效期限
	
	public Cert4Driving(){
		this.setCertName("机动车驾驶证");
		this.setCertFullName("中华人民共和国机动车驾驶证");
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public Calendar getReceiveDate() {
		return receiveDate;
	}

	public void setReceiveDate(Calendar receiveDate) {
		this.receiveDate = receiveDate;
	}

	public String getArchiveNo() {
		return archiveNo;
	}

	public void setArchiveNo(String archiveNo) {
		this.archiveNo = archiveNo;
	}

	public String getRecord() {
		return record;
	}

	public void setRecord(String record) {
		this.record = record;
	}

	public String getValidFor() {
		return validFor;
	}

	public void setValidFor(String validFor) {
		this.validFor = validFor;
	}
}