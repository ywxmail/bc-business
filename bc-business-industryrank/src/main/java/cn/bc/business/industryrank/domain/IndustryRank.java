package cn.bc.business.industryrank.domain;

import javax.persistence.Entity;
import javax.persistence.Table;

import cn.bc.identity.domain.FileEntityImpl;

/**
 * 行业排名
 * 
 * @author dragon
 */
@Entity
@Table(name = "BS_INDUSTRY_RANK")
public class IndustryRank extends FileEntityImpl {
	private static final long serialVersionUID = 1L;

	private int year;// 年份
	private int month;// 月份
	private String company;// 企业名称
	private float score1; // 督查得分
	private float score2; // 服务投诉
	private float score3; // 交通违法
	private float score4; // 营运违章
	private float score5; // 车辆查验
	private float score6; // 服务违章
	private float score7; // 信息化得分
	private float score8; // 好人好事
	private float score9; // 交通违法率奖惩记分
	private float total; // 总分

	private boolean mock; // 是否为分公司的记录：用于分公司的替换式排名
	private int rank; // 月度排名

	public boolean isMock() {
		return mock;
	}

	public void setMock(boolean mock) {
		this.mock = mock;
	}

	public int getRank() {
		return rank;
	}

	public void setRank(int rank) {
		this.rank = rank;
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

	public float getScore5() {
		return score5;
	}

	public void setScore5(float score5) {
		this.score5 = score5;
	}

	public float getScore6() {
		return score6;
	}

	public void setScore6(float score6) {
		this.score6 = score6;
	}

	public float getScore7() {
		return score7;
	}

	public void setScore7(float score7) {
		this.score7 = score7;
	}

	public float getScore8() {
		return score8;
	}

	public void setScore8(float score8) {
		this.score8 = score8;
	}

	public float getScore9() {
		return score9;
	}

	public void setScore9(float score9) {
		this.score9 = score9;
	}

	public float getTotal() {
		return total;
	}

	public void setTotal(float total) {
		this.total = total;
	}
}