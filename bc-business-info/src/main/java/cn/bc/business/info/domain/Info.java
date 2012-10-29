/**
 * 
 */
package cn.bc.business.info.domain;

import java.util.Calendar;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import cn.bc.identity.domain.RichFileEntityImpl;

/**
 * 信息
 * 
 * @author dragon
 */
@Entity
@Table(name = "BS_INFO")
public class Info extends RichFileEntityImpl {
	private static final long serialVersionUID = 1L;
	/** 状态：草稿 */
	public static final int STATUS_DRAFT = -1;
	/** 状态：已发布 */
	public static final int STATUS_ISSUED = 0;
	/** 状态：已禁用 */
	public static final int STATUS_DISABLED = 1;

	/** 类型：公司文件 */
	public static final int TYPE_COMPANYGILE = 0;
	/** 类型：法规文件 */
	public static final int TYPE_REGULATION = 1;
	/** 类型：通知 */
	public static final int TYPE_NOTICE = 2;

	private Calendar sendDate;// 发送时间
	private Calendar endDate;// 结束日期
	private String subject;// 信息标题
	private String content;// 详细内容
	private int type;// 类型
	private String source;// 信息来源：如公告的发布部门，提醒信息的链接地址等，具体格式有相应的信息类型决定

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	@Column(name = "TYPE_")
	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	@Column(name = "SOURCE_")
	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	@Column(name = "SEND_DATE")
	public Calendar getSendDate() {
		return sendDate;
	}

	public void setSendDate(Calendar sendDate) {
		this.sendDate = sendDate;
	}

	@Column(name = "END_DATE")
	public Calendar getEndDate() {
		return endDate;
	}

	public void setEndDate(Calendar endDate) {
		this.endDate = endDate;
	}
}