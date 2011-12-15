package cn.bc.business.arrange.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import cn.bc.identity.domain.FileEntityImpl;

/**
 * 考勤成员
 * 
 * @author dragon
 */
@Entity
@Table(name = "BS_MEMBER")
public class Member extends FileEntityImpl {
	private static final long serialVersionUID = 1L;
	public static final String KEY_UID = "member.uid";

	/** 成员类型：用户 */
	public static final int MEMBERTYPE_USER = 4;
	/** 成员类型：司机 */
	public static final int MEMBERTYPE_DRIVER = 10;
	/** 成员类型：车辆 */
	public static final int MEMBERTYPE_CAR = 11;

	/** 考勤验证方式：刷卡 */
	public static final int AUTHTYPE_CARD = 1;
	/** 考勤验证方式：指纹 */
	public static final int AUTHTYPE_FINGER = 2;

	private Long memberId;// 成员ID,视成员类型的不同对应相应表的ID
	private int memberType;// 成员类型：参考常数MEMBERTYPE_XXX的定义
	private String memberName;// 成员名称
	private int authType;// 考勤验证方式：参考常数AUTHTYPE_XXX的定义
	private String code;// 工号
	private String cardId;// 卡号
	private String description;// 补充说明

	@Column(name = "MEMBER_ID")
	public Long getMemberId() {
		return memberId;
	}

	public void setMemberId(Long memberId) {
		this.memberId = memberId;
	}

	@Column(name = "MEMBER_TYPE")
	public int getMemberType() {
		return memberType;
	}

	public void setMemberType(int memberType) {
		this.memberType = memberType;
	}

	@Column(name = "MEMBER_NAME")
	public String getMemberName() {
		return memberName;
	}

	public void setMemberName(String memberName) {
		this.memberName = memberName;
	}

	@Column(name = "AUTH_TYPE")
	public int getAuthType() {
		return authType;
	}

	public void setAuthType(int authType) {
		this.authType = authType;
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

	@Column(name = "DESC_")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}