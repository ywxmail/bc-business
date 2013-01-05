/**
 * 
 */
package cn.bc.business.runcase.domain;

import javax.persistence.Entity;
import javax.persistence.Table;

import cn.bc.identity.domain.FileEntityImpl;

/**
 * 交通违法代码管理
 * 
 * @author zxr
 */
@Entity
@Table(name = "BS_CASE_INFRACT_CODE")
public class Case4InfractCode extends FileEntityImpl {
	private static final long serialVersionUID = 1L;
	public static final String KEY_CODE = "runcase.code";
	public static final String ATTACH_TYPE = Case4InfractCode.class
			.getSimpleName();

	private String code;// 代码
	private String subject;// 违法行为
	private String according;// 违法依据
	private Float jeom;// 扣分
	private Float penalty;// 罚款金额

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getAccording() {
		return according;
	}

	public void setAccording(String according) {
		this.according = according;
	}

	public Float getJeom() {
		return jeom;
	}

	public void setJeom(Float jeom) {
		this.jeom = jeom;
	}

	public Float getPenalty() {
		return penalty;
	}

	public void setPenalty(Float penalty) {
		this.penalty = penalty;
	}

}