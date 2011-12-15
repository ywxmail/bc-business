package cn.bc.business.arrange.domain;

import java.util.Calendar;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import cn.bc.core.EntityImpl;

/**
 * 签到记录
 * 
 * @author dragon
 */
@Entity
@Table(name = "BS_SIGN_RECORD")
public class SignRecord extends EntityImpl {
	private static final long serialVersionUID = 1L;
	public static final String KEY_UID = "signrecord.uid";
	
	/** 数据类型：安全学习 */
	public static final int DATATYPE_DRIVER = 0;
	/** 数据类型：回场检 */
	public static final int DATATYPE_CAR = 1;
	/** 数据类型：会议签到 */
	public static final int DATATYPE_MEETING = 2;

	/** 签到方式：补录 */
	public static final int SIGNTYPE_USER = 0;
	/** 签到方式：刷卡 */
	public static final int SIGNTYPE_CARD = 1;
	/** 签到方式：指纹 */
	public static final int SIGNTYPE_FINGER = 2;

	private Long pid;// 隶属项的id,对应ArrangeMember的id
	private int signType;// 签到方式：参考常数SIGNTYPE_XXX的定义
	private Calendar signDate;// 签到时间
	private String code;// 工号
	private String cardId;// 卡号
	private String name;// 签到人
	private String patchNo;// 批号,每次导入操作导入的考勤记录批号相同

	public Long getPid() {
		return pid;
	}

	public void setPid(Long pid) {
		this.pid = pid;
	}

	@Column(name = "SIGN_TYPE")
	public int getSignType() {
		return signType;
	}

	public void setSignType(int signType) {
		this.signType = signType;
	}

	@Column(name = "SIGN_DATE")
	public Calendar getSignDate() {
		return signDate;
	}

	public void setSignDate(Calendar signDate) {
		this.signDate = signDate;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	@Column(name = "CARD_ID")
	public String getCardId() {
		return cardId;
	}

	public void setCardId(String cardId) {
		this.cardId = cardId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "PATCH_NO")
	public String getPatchNo() {
		return patchNo;
	}

	public void setPatchNo(String patchNo) {
		this.patchNo = patchNo;
	}
}