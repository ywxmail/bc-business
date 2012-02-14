/**
 * 
 */
package cn.bc.business.contract.domain;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 司机劳动合同
 * 
 * @author dragon
 */
@Entity
@Table(name = "BS_CONTRACT_LABOUR")
@Inheritance(strategy = InheritanceType.JOINED)
public class Contract4Labour extends Contract {
	private static final long serialVersionUID = 1L;
	private static Log logger = LogFactory.getLog(Contract4Labour.class);
	public static final String KEY_UID = Contract4Labour.class.getSimpleName();
	public static final String KEY_CODE = "contract.code";
	private String certNo;// 资格证号
	private String certIdentity;//身份证号
	private Calendar birthDate; //出生日期
	private Integer	age; //年龄
	private String origin; //籍贯
	private	String houseType; //户口性质
	private boolean dole;// 个人岗位补贴:0-有,1-无
	private boolean funding;// 对公社保资助:0-有,1-无
	private String	insurCode;// 社保号
	private Calendar joinDate;// 参保日期
	private Calendar registerDate;// 车辆登记日期
	private Calendar insureDate;// 申领日期
	private String bsType; // 营运性质
	private String buyUnit; // 购买单位;
	private String insuranceType; // 社保险种
	private boolean iqama;// 暂住证:0-有,1-无
	private boolean accountBook;// 户口薄:0-有,1-无
	private boolean identityCards;// 身份证:0-有,1-无
	private boolean fpc;// 计生证:0-有,1-无
	private boolean unemployed;// 就业手册:0-有,1-无
	private boolean healthForm;// 体检表:0-有,1-无
	private boolean photo;// 照片:0-有,1-无
	private Calendar breedingDate;// 生育日期
	private Calendar getStartDate;// 申领开始日期
	private Calendar getEndDate;// 申领结束日期
	private String	remark;
	private Calendar stopDate;//停保日期
	private Calendar leaveDate;//离职日期
	
	

	@Column(name = "CERT_NO")
	public String getCertNo() {
		return certNo;
	}

	public void setCertNo(String certNo) {
		this.certNo = certNo;
	}
	
	@Column(name = "CERT_IDENTITY")
	public String getCertIdentity() {
		return certIdentity;
	}

	public void setCertIdentity(String certIdentity) {
		this.certIdentity = certIdentity;
	}

	@Column(name = "BIRTHDATE")
	public Calendar getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(Calendar birthDate) {
		this.birthDate = birthDate;
	}

	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}

	public String getOrigin() {
		return origin;
	}

	public void setOrigin(String origin) {
		this.origin = origin;
	}
	
	@Column(name = "HOUSE_TYPE")
	public String getHouseType() {
		return houseType;
	}

	public void setHouseType(String houseType) {
		this.houseType = houseType;
	}

	public boolean isDole() {
		return dole;
	}

	public void setDole(boolean dole) {
		this.dole = dole;
	}

	public boolean isFunding() {
		return funding;
	}

	public void setFunding(boolean funding) {
		this.funding = funding;
	}

	public String getInsurCode() {
		return insurCode;
	}

	public void setInsurCode(String insurCode) {
		this.insurCode = insurCode;
	}

	public Calendar getJoinDate() {
		return joinDate;
	}

	public void setJoinDate(Calendar joinDate) {
		this.joinDate = joinDate;
	}

	@Column(name = "REGISTER_DATE")
	public Calendar getRegisterDate() {
		return registerDate;
	}

	public void setRegisterDate(Calendar registerDate) {
		this.registerDate = registerDate;
	}

	@Column(name = "INSURE_DATE")
	public Calendar getInsureDate() {
		return insureDate;
	}

	public void setInsureDate(Calendar insureDate) {
		this.insureDate = insureDate;
	}

	@Column(name = "BS_TYPE")
	public String getBsType() {
		return bsType;
	}

	public void setBsType(String bsType) {
		this.bsType = bsType;
	}

	@Column(name = "BUY_UNIT")
	public String getBuyUnit() {
		return buyUnit;
	}

	public void setBuyUnit(String buyUnit) {
		this.buyUnit = buyUnit;
	}

	@Column(name = "INSURANCE_TYPE")
	public String getInsuranceType() {
		return insuranceType;
	}

	public void setInsuranceType(String insuranceType) {
		this.insuranceType = insuranceType;
	}

	@Column(name = "IS_IQAMA")
	public boolean isIqama() {
		return iqama;
	}

	public void setIqama(boolean iqama) {
		this.iqama = iqama;
	}
	
	@Column(name = "IS_ACCOUNT_BOOK")
	public boolean isAccountBook() {
		return accountBook;
	}

	public void setAccountBook(boolean accountBook) {
		this.accountBook = accountBook;
	}

	@Column(name = "IS_IDENTITY_CARDS")
	public boolean isIdentityCards() {
		return identityCards;
	}
	
	public void setIdentityCards(boolean identityCards) {
		this.identityCards = identityCards;
	}

	@Column(name = "IS_FPC")	
	public boolean isFpc() {
		return fpc;
	}
	
	public void setFpc(boolean fpc) {
		this.fpc = fpc;
	}

	@Column(name = "IS_UNEMPLOYED")
	public boolean isUnemployed() {
		return unemployed;
	}

	public void setUnemployed(boolean unemployed) {
		this.unemployed = unemployed;
	}
	
	@Column(name = "IS_HEALTHFORM")
	public boolean isHealthForm() {
		return healthForm;
	}
	
	public void setHealthForm(boolean healthForm) {
		this.healthForm = healthForm;
	}

	@Column(name = "IS_PHOTO")
	public boolean isPhoto() {
		return photo;
	}
	
	public void setPhoto(boolean photo) {
		this.photo = photo;
	}

	@Column(name = "BREEDING_DATE")
	public Calendar getBreedingDate() {
		return breedingDate;
	}

	public void setBreedingDate(Calendar breedingDate) {
		this.breedingDate = breedingDate;
	}

	@Column(name = "GET_STARTDATE")
	public Calendar getGetStartDate() {
		return getStartDate;
	}

	public void setGetStartDate(Calendar getStartDate) {
		this.getStartDate = getStartDate;
	}

	@Column(name = "GET_ENDDATE")
	public Calendar getGetEndDate() {
		return getEndDate;
	}

	public void setGetEndDate(Calendar getEndDate) {
		this.getEndDate = getEndDate;
	}
	
	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}
	
	public Calendar getStopDate() {
		return stopDate;
	}

	public void setStopDate(Calendar stopDate) {
		this.stopDate = stopDate;
	}

	public Calendar getLeaveDate() {
		return leaveDate;
	}

	public void setLeaveDate(Calendar leaveDate) {
		this.leaveDate = leaveDate;
	}

	/**
	 * @return 司机性别(0-未设置,1-男,2-女)
	 */
	@Column(name = "SEX")
	public Integer getSex() {
		Integer i = getInteger("sex");
		if (i == null)
			return 1;
		else
			return i.intValue();
	}

	public void setSex(Integer sex) {
		set("sex", new Integer(sex));
	}
	
	@Transient
	private Map<String, Object> attrs;

	/**
	 * @param key
	 *            键
	 * @return 指定键的属性值
	 */
	@Transient
	public Object get(String key) {
		return (attrs != null && attrs.containsKey(key)) ? attrs.get(key)
				: null;
	}

	public void set(String key, Object value) {
		if (logger.isDebugEnabled())
			logger.debug("key=" + key + ";value=" + value + ";valueType="
					+ (value != null ? value.getClass() : "?"));
		if (key == null)
			throw new RuntimeException("key can't to be null");
		if (attrs == null)
			attrs = new HashMap<String, Object>();
		attrs.put(key, value);
	}

	@Transient
	public Integer getInteger(String key) {
		return (Integer) get(key);
	}
}