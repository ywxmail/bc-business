/**
 * 
 */
package cn.bc.business.cert.domain;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * 与人有关的证件信息
 * 
 * @author dragon
 */
@MappedSuperclass
public abstract class Cert4Man extends Cert {
	private static final long serialVersionUID = 1L;

	private static Log logger = LogFactory.getLog(Cert4Man.class);
	/** 性别:未定义 */
	public static final int SEX_NONE = 0;
	/** 性别:男 */
	public static final int SEX_MAN = 1;
	/** 性别:女 */
	public static final int SEX_WOMAN = 2;
	
	private String address;// 住址
	private String name;// 姓名
//	private Integer sex;// 性别：参考ActorDetail类中SEX_*常数的定义
	private Calendar birthdate;// 出生日期
	private String nation;// 国籍、民族

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return 性别(0-未设置,1-男,2-女)
	 */
	@Column
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

	public Calendar getBirthdate() {
		return birthdate;
	}

	public void setBirthdate(Calendar birthdate) {
		this.birthdate = birthdate;
	}

	public String getNation() {
		return nation;
	}

	public void setNation(String nation) {
		this.nation = nation;
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