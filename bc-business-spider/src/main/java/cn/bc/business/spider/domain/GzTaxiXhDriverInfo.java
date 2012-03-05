/**
 * 
 */
package cn.bc.business.spider.domain;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 从广州市出租汽车协会网抓取的驾驶员信誉档案
 * 
 * @author dragon
 */
@Entity
@Table(name = "BS_SYNC_JINDUN_JTWF")
public class GzTaxiXhDriverInfo {
	private String msg;// 异常信息
	private String simple;// 查到的简单信息:table
	private String detail;// 查到的详细信息:table
	private String pic;// 图片复制到的路径:相对于附件目录,如"spider/gztaxixh/aa.jpg"

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getSimple() {
		return simple;
	}

	public void setSimple(String simple) {
		this.simple = simple;
	}

	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}

	public String getPic() {
		return pic;
	}

	public void setPic(String pic) {
		this.pic = pic;
	}
}