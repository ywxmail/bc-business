package cn.bc.business.arrange.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import cn.bc.identity.domain.FileEntityImpl;

/**
 * 安排的考勤成员实例
 * 
 * @author dragon
 */
@Entity
@Table(name = "BS_ARRANGE_MEMBER")
public class ArrangeMember extends FileEntityImpl {
	private static final long serialVersionUID = 1L;

	// 状态字段使用下面定义的两个值，不使用基类的
	/** 状态：未签到 */
	public static final int STATUS_UNSIGN = 0;
	/** 状态：已签到 */
	public static final int STATUS_SIGNED = 1;

	private Long pid;// 所属安排的ID
	private int status;//状态：参考常数STATUS_XXX的定义
	private Long memberId;// 成员ID,视成员类型的不同对应相应表的ID
	private int memberType;// 成员类型：参考常数MEMBERTYPE_XXX的定义
	private String memberName;// 成员名称
	private String signType;// 签到方式：多种签到方式间用逗号连接,如 刷卡+补录
	// 签到时间：多个签到时间间用逗号连接,如 2011-01-01,2011-01-10,顺序要与签到方式对应
	private String signDate;
	private int signCount;// 签到次数
	private String description;// 补充说明
	
	private String ext1;// 扩展域:回场检时,保存刷卡司机的id,多个就用逗号连接
	private String ext2;// 扩展域:回场检时,保存刷卡司机的姓名,多个就用逗号连接
	private String ext3;// 扩展域:回场检时,保存刷卡司机的服务资格证号,多个就用逗号连接
	private String ext4;// 扩展域:保留

	public Long getPid() {
		return pid;
	}

	public void setPid(Long pid) {
		this.pid = pid;
	}

	@Column(name = "STATUS_")
	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

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

	@Column(name = "SIGN_TYPE")
	public String getSignType() {
		return signType;
	}

	public void setSignType(String signType) {
		this.signType = signType;
	}

	@Column(name = "SIGN_DATE")
	public String getSignDate() {
		return signDate;
	}

	public void setSignDate(String signDate) {
		this.signDate = signDate;
	}

	@Column(name = "SIGN_COUNT")
	public int getSignCount() {
		return signCount;
	}

	public void setSignCount(int signCount) {
		this.signCount = signCount;
	}

	@Column(name = "DESC_")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getExt1() {
		return ext1;
	}

	public void setExt1(String ext1) {
		this.ext1 = ext1;
	}

	public String getExt2() {
		return ext2;
	}

	public void setExt2(String ext2) {
		this.ext2 = ext2;
	}

	public String getExt3() {
		return ext3;
	}

	public void setExt3(String ext3) {
		this.ext3 = ext3;
	}

	public String getExt4() {
		return ext4;
	}

	public void setExt4(String ext4) {
		this.ext4 = ext4;
	}
}