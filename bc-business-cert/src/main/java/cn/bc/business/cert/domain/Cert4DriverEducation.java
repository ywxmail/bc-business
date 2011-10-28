/**
 * 
 */
package cn.bc.business.cert.domain;

import java.util.Calendar;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

/**
 * 驾驶培训证
 * 
 * @author dragon
 */
@Entity
@Table(name = "BS_CERT_JSPX")
@Inheritance(strategy = InheritanceType.JOINED)
public class Cert4DriverEducation extends Cert4Man {
	private static final long serialVersionUID = 1L;
	public static final String ATTACH_TYPE = Cert4DriverEducation.class.getSimpleName();
	public static final String KEY_CODE = "cert.certCode";
	private String domain;// 培训专业
	private Calendar trainDate;// 培训时间
	private Integer trainHour;// 培训学时
	private Integer grade1;// 理论知识考核成绩
	private String grade2;// 操作技能考核成绩
	private String grade3;// 评定成绩
	private String identityNo;// 身份证件号
	
	public Cert4DriverEducation(){
		this.setCertName("驾驶培训证");
		this.setCertFullName("驾驶培训证");
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	@Column(name = "IDENTITY_NO")
	public String getIdentityNo() {
		return identityNo;
	}

	public void setIdentityNo(String identityNo) {
		this.identityNo = identityNo;
	}

	@Column(name = "TRAIN_DATE")
	public Calendar getTrainDate() {
		return trainDate;
	}

	public void setTrainDate(Calendar trainDate) {
		this.trainDate = trainDate;
	}

	@Column(name = "TRAIN_HOUR")
	public Integer getTrainHour() {
		return trainHour;
	}

	public void setTrainHour(Integer trainHour) {
		this.trainHour = trainHour;
	}

	public Integer getGrade1() {
		return grade1;
	}

	public void setGrade1(Integer grade1) {
		this.grade1 = grade1;
	}

	public String getGrade2() {
		return grade2;
	}

	public void setGrade2(String grade2) {
		this.grade2 = grade2;
	}

	public String getGrade3() {
		return grade3;
	}

	public void setGrade3(String grade3) {
		this.grade3 = grade3;
	}
}