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
 * 机动车行驶证
 * 
 * @author dragon
 */
@Entity
@Table(name = "BS_CERT_VEHICELICENSE")
@Inheritance(strategy = InheritanceType.JOINED)
public class Cert4VehiceLicense extends Cert4Car {
	private static final long serialVersionUID = 1L;
	public static final String ATTACH_TYPE = Cert4VehiceLicense.class.getSimpleName();
	public static final String KEY_CODE = "cert.certCode";
	private String owner;// 所有人
	private String address;// 住址
	private String useCharacter;//使用性质  
	private String vehiceType;// 车辆类型
	private String vin;// 车辆识别代号
	private String engineNo;// 发动机号码
	private Calendar registerDate;// 注册日期
	
	private String archiveNo;// 档案编号
	private int dimLen;// 外廓尺寸：长，单位mm
	private int dimWidth;// 外廓尺寸：宽，单位mm
	private int dimHeight;// 外廓尺寸：高，单位mm
	private int totalWeight;// 总质量，单位kg
	private int curbWeight;// 整备质量，单位kg
	private int accessWeight;// 核定载质量，单位kg
	private int pullWeight;// 准牵引总质量，单位kg
	private int accessCount;// 核定载人数
	private Calendar scrapDate;// 强制报废日期
	private String desc;// 备注
	private String record;// 检验记录

	public Cert4VehiceLicense() {
		this.setCertName("机动车行驶证");
		this.setCertFullName("中华人民共和国机动车行驶证");
	}

	@Column(name = "ARCHIVE_NO")
	public String getArchiveNo() {
		return archiveNo;
	}

	public void setArchiveNo(String archiveNo) {
		this.archiveNo = archiveNo;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	@Column(name = "USE_CHARACTER")
	public String getUseCharacter() {
		return useCharacter;
	}

	public void setUseCharacter(String useCharacter) {
		this.useCharacter = useCharacter;
	}

	@Column(name = "VEHICE_TYPE")
	public String getVehiceType() {
		return vehiceType;
	}

	public void setVehiceType(String vehiceType) {
		this.vehiceType = vehiceType;
	}

	public String getVin() {
		return vin;
	}

	public void setVin(String vin) {
		this.vin = vin;
	}

	@Column(name = "ENGINE_NO")
	public String getEngineNo() {
		return engineNo;
	}

	public void setEngineNo(String engineNo) {
		this.engineNo = engineNo;
	}

	@Column(name = "REGISTER_DATE")
	public Calendar getRegisterDate() {
		return registerDate;
	}

	public void setRegisterDate(Calendar registerDate) {
		this.registerDate = registerDate;
	}

	@Column(name = "SCRAP_DATE")
	public Calendar getScrapDate() {
		return scrapDate;
	}

	public void setScrapDate(Calendar scrapDate) {
		this.scrapDate = scrapDate;
	}

	@Column(name = "DIM_LEN")
	public int getDimLen() {
		return dimLen;
	}

	public void setDimLen(int dimLen) {
		this.dimLen = dimLen;
	}

	@Column(name = "DIM_WIDTH")
	public int getDimWidth() {
		return dimWidth;
	}

	public void setDimWidth(int dimWidth) {
		this.dimWidth = dimWidth;
	}

	@Column(name = "DIM_HEIGHT")
	public int getDimHeight() {
		return dimHeight;
	}

	public void setDimHeight(int dimHeight) {
		this.dimHeight = dimHeight;
	}

	@Column(name = "TOTAL_WEIGHT")
	public int getTotalWeight() {
		return totalWeight;
	}

	public void setTotalWeight(int totalWeight) {
		this.totalWeight = totalWeight;
	}

	@Column(name = "CURB_WEIGHT")
	public int getCurbWeight() {
		return curbWeight;
	}

	public void setCurbWeight(int curbWeight) {
		this.curbWeight = curbWeight;
	}

	@Column(name = "ACCESS_WEIGHT")
	public int getAccessWeight() {
		return accessWeight;
	}

	public void setAccessWeight(int accessWeight) {
		this.accessWeight = accessWeight;
	}
	
	@Column(name = "PULL_WEIGHT")
	public int getPullWeight() {
		return pullWeight;
	}

	public void setPullWeight(int pullWeight) {
		this.pullWeight = pullWeight;
	}

	@Column(name = "ACCESS_COUNT")
	public int getAccessCount() {
		return accessCount;
	}

	public void setAccessCount(int accessCount) {
		this.accessCount = accessCount;
	}

	@Column(name = "DESC_")
	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	@Column(name = "RECORD_")
	public String getRecord() {
		return record;
	}

	public void setRecord(String record) {
		this.record = record;
	}
}