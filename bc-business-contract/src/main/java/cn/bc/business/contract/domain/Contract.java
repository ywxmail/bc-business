/**
 * 
 */
package cn.bc.business.contract.domain;

import java.util.Calendar;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import cn.bc.identity.domain.ActorHistory;
import cn.bc.identity.domain.ActorHistory;
import cn.bc.identity.domain.RichFileEntityImpl;

/**
 * 合同基类（继承关系使用Join Strategy：每个子类对应一张表，但此表中不包含基类的属性,仅仅是此子类的扩展属性,共享基类的属性）
 * 
 * @author dragon
 */
@Entity
@Table(name = "BS_CONTRACT")
@Inheritance(strategy = InheritanceType.JOINED)
public class Contract extends RichFileEntityImpl {
	private static final long serialVersionUID = 1L;
	public static final String ATTACH_TYPE = Contract.class.getSimpleName();
	/**合同类型：司机劳动合同*/
	public static final int TYPE_LABOUR 	= 1;
	/**合同类型：责任人合同*/
	public static final int TYPE_CHARGER 	= 2;

	private String code;// 合同编号
	private int    type;// 合同类型：如劳动合同、承包合同等
	private String wordNo;// 文书号
	private ActorHistory transactor;// 经办人

	private Calendar signDate;// 签订日期
	private Calendar startDate;// 生效日期
	private Calendar endDate;// 到期日期
	private String content;// 内容

	private String ext_str1;// 扩展域
	private String ext_str2;// 扩展域
	private String ext_str3;// 扩展域
	private Integer ext_num1;// 扩展域
	private Integer ext_num2;// 扩展域
	private Integer ext_num3;// 扩展域

	@Column(name = "WORD_NO")
	public String getWordNo() {
		return wordNo;
	}

	public void setWordNo(String wordNo) {
		this.wordNo = wordNo;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String contend) {
		this.content = contend;
	}

	@Column(name = "TYPE_")
	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	@JoinColumn(name = "TRANSACTOR_ID", referencedColumnName = "ID")
	public ActorHistory getTransactor() {
		return transactor;
	}

	public void setTransactor(ActorHistory transactor) {
		this.transactor = transactor;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String certCode) {
		this.code = certCode;
	}

	@Column(name = "SIGN_DATE")
	public Calendar getSignDate() {
		return signDate;
	}

	public void setSignDate(Calendar issueDate) {
		this.signDate = issueDate;
	}

	@Column(name = "START_DATE")
	public Calendar getStartDate() {
		return startDate;
	}

	public void setStartDate(Calendar startDate) {
		this.startDate = startDate;
	}

	@Column(name = "END_DATE")
	public Calendar getEndDate() {
		return endDate;
	}

	public void setEndDate(Calendar endDate) {
		this.endDate = endDate;
	}

	public String getExt_str1() {
		return ext_str1;
	}

	public void setExt_str1(String ext_str1) {
		this.ext_str1 = ext_str1;
	}

	public String getExt_str2() {
		return ext_str2;
	}

	public void setExt_str2(String ext_str2) {
		this.ext_str2 = ext_str2;
	}

	public String getExt_str3() {
		return ext_str3;
	}

	public void setExt_str3(String ext_str3) {
		this.ext_str3 = ext_str3;
	}

	public Integer getExt_num1() {
		return ext_num1;
	}

	public void setExt_num1(Integer ext_num1) {
		this.ext_num1 = ext_num1;
	}

	public Integer getExt_num2() {
		return ext_num2;
	}

	public void setExt_num2(Integer ext_num2) {
		this.ext_num2 = ext_num2;
	}

	public Integer getExt_num3() {
		return ext_num3;
	}

	public void setExt_num3(Integer ext_num3) {
		this.ext_num3 = ext_num3;
	}
}