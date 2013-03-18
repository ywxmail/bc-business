package cn.bc.business.industryrank.domain;

import javax.persistence.Entity;
import javax.persistence.Table;

import cn.bc.identity.domain.FileEntityImpl;

/**
 * 行业排名原始数据
 * 
 * @author dragon
 */
@Entity
@Table(name = "BS_IR_ORIGIN")
public class IROrigin extends FileEntityImpl {
	private static final long serialVersionUID = 1L;

	private int year;// 年份
	private int month;// 月份
	private String company;// 企业名称
	private float score1; // 服务投诉
	private float score2; // 交通违法
	private float score3; // 营运违章
	private float score4; // 服务违章
	private float total; // 总分
	private boolean inner; // 是否为我司记录
	private int rank; // 月度排名

	public boolean isInner() {
		return inner;
	}

	public void setInner(boolean inner) {
		this.inner = inner;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public int getMonth() {
		return month;
	}

	public void setMonth(int month) {
		this.month = month;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public float getScore1() {
		return score1;
	}

	public void setScore1(float score1) {
		this.score1 = score1;
	}

	public float getScore2() {
		return score2;
	}

	public void setScore2(float score2) {
		this.score2 = score2;
	}

	public float getScore3() {
		return score3;
	}

	public void setScore3(float score3) {
		this.score3 = score3;
	}

	public float getScore4() {
		return score4;
	}

	public void setScore4(float score4) {
		this.score4 = score4;
	}

	public float getTotal() {
		return total;
	}

	public void setTotal(float total) {
		this.total = total;
	}

	public int getRank() {
		return rank;
	}

	public void setRank(int rank) {
		this.rank = rank;
	}
}