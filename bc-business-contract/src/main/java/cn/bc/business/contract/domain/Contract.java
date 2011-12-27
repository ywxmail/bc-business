/**
 * 
 */
package cn.bc.business.contract.domain;

import java.util.Calendar;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

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
	
	/**状态：正常*/
	public static final int STATUS_NORMAL	= 0;
	/**状态：失效*/
	public static final int STATUS_FAILURE	= 1;
	/**状态：离职*/
	public static final int STATUS_RESGIN	= 2;
	
	/**操作类型  新建*/
	public static final int OPTYPE_CREATE	= 1;
	/**操作类型  维护*/
	public static final int OPTYPE_MAINTENANCE		= 2;
	/**操作类型 转车*/
	public static final int OPTYPE_CHANGECAR	= 3;
	/**操作类型  续约*/
	public static final int OPTYPE_RENEW	= 4;
	/**操作类型  离职*/
	public static final int OPTYPE_RESIGN	= 5;
	
	/**主版本号默认值*/
	public static final int MAJOR_DEFALUT	= 1;
	/**次版本号默认值*/
	public static final int MINOR_DEFALUT	= 0;
	
	/**主体当前版本*/
	public static final int MAIN_NOW	    = 0;
	/**主体历史版本*/
	public static final int MAIN_HISTORY 	= 1;
	

	private String code;// 合同编号
	private int    type;// 合同类型，参考常数TYPE_XXX的定义，如劳动合同、经济合同等
	private Long   Pid;// 父级ID
	private Integer verMajor;//主版本号
	private Integer verMinor;//次版本号
	private int     main; //主体： 0-当前版本,1-历史版本
	private String  patchNo;//批号 
	private int     opType; //操作类型：1-新建,2-维护,3-转车,4-续约,5-离职
	
	private String wordNo;// 文书号
	private Long transactorId;// 经办人ID
	private String transactorName;// 经办人姓名

	private Calendar signDate;// 签订日期
	private Calendar startDate;// 生效日期
	private Calendar endDate;// 到期日期
	private String content;// 内容

	private String ext_str1;// 扩展域(车牌)	
	private String ext_str2;// 扩展域(司机)
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

	public Long getPid() {
		return Pid;
	}

	public void setPid(Long pid) {
		Pid = pid;
	}

	@Column(name = "VER_MAJOR")
	public Integer getVerMajor() {
		return verMajor;
	}

	public void setVerMajor(Integer verMajor) {
		this.verMajor = verMajor;
	}

	@Column(name = "VER_MINOR")
	public Integer getVerMinor() {
		return verMinor;
	}

	public void setVerMinor(Integer verMinor) {
		this.verMinor = verMinor;
	}

	public int getMain() {
		return main;
	}

	public void setMain(int main) {
		this.main = main;
	}

	@Column(name = "PATCH_NO")
	public String getPatchNo() {
		return patchNo;
	}

	public void setPatchNo(String patchNo) {
		this.patchNo = patchNo;
	}

	@Column(name = "OP_TYPE")
	public int getOpType() {
		return opType;
	}

	public void setOpType(int opType) {
		this.opType = opType;
	}

	@Column(name = "TRANSACTOR_ID")
	public Long getTransactorId() {
		return transactorId;
	}

	public void setTransactorId(Long transactorId) {
		this.transactorId = transactorId;
	}

	@Column(name = "TRANSACTOR_NAME")
	public String getTransactorName() {
		return transactorName;
	}

	public void setTransactorName(String transactorName) {
		this.transactorName = transactorName;
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