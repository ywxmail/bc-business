/**
 * 
 */
package cn.bc.business.contract.web.struts2;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import cn.bc.web.formater.DateRangeFormater;
import cn.bc.web.formater.DateRangeFormaterEx;

/**
 * 协议日期特殊处理 如(张三)2011-01-01 12:10:10～2011-02-01 14:10:15
 * <p>
 * 如输入两个日期信息，默认显示类似“2011-01-01 12:10:10～2011-02-01 14:10:15”的格式
 * </p>
 * <p>
 * 如果两个日期的年月日相同，默认显示类似“2011-01-01 12:10:10～14:10:15”的格式
 * </p>
 * 
 * @author dragon
 * 
 */
public abstract class AgreementFormater extends DateRangeFormaterEx {
	private SimpleDateFormat dateFormat;
	private String connector = " ";// 日期和时间间的连接字符串
	private String beforeDateStr;// 视图中mapRow的key值

	/**
	 * 日期前的显示值：如 (张三)2011-01-01～2011-02-01
	 * 
	 * @param beforeDateStr
	 *            视图中mapRow的key值(不支持日期格式的key值)
	 * @return
	 */
	public DateRangeFormater setBeforeDateStr(String beforeDateStr) {
		this.beforeDateStr = beforeDateStr;
		return this;
	}

	public String getConnector() {
		return connector;
	}

	public AgreementFormater setConnector(String connector) {
		this.connector = connector;
		return this;
	}

	public AgreementFormater() {
		// 默认日期格式
		dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	}

	/**
	 * 使用指定的日期和时间格式
	 * 
	 * @param datePattern
	 *            日期部分的格式，如yyy-MM-dd
	 * @param timePattern
	 *            时间部分的格式，如HH:mm:ss
	 */
	public AgreementFormater(String datePattern, String timePattern) {
		this.dateFormat = new SimpleDateFormat(datePattern);
	}

	/**
	 * 使用指定的时间格式，日期部分的格式自动设置为yyyy-MM-dd
	 * 
	 * @param timePattern
	 *            时间部分的格式，如HH:mm:ss
	 */
	public AgreementFormater(String timePattern) {
		this.dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	}

	public String format(Object context, Object value) {
		Date _fromDate = getFromDate(context, value);
		Date _toDate = getToDate(context, value);
		Calendar fromDate = null;
		@SuppressWarnings("rawtypes")
		Map contract = (Map) context;
		String strValue = "";// 日期前的值
		String chargers = null;
		String returnValue = null;
		// 如果日期前需要设值
		if (this.beforeDateStr != null) {
			strValue = (String) contract.get(this.beforeDateStr);
		}

		// 责任人
		if (contract.get("ext_str2") != null) {
			chargers = (String) contract.get("ext_str2");
			chargers = chargers.trim();
		} else {
			return "&nbsp;";
		}
		// 分隔出每个责任人的配置：[责任人1姓名],[责任人1id];[责任人2姓名],[责任人2id];...
		String[] vvs = chargers.split(";");
		// 循环每个责任人执行格式化处理
		String[] vs;
		for (String vv : vvs) {
			vs = vv.split(",");// [0]-责任人姓名,[1]-责任人id

			if (vs.length == 2) {
				if (strValue != null) {
					// 如果存在提前终止方
					if (!vs[0].endsWith(strValue)) {

						// 添加履约方
						if (_fromDate != null) {
							fromDate = Calendar.getInstance();
							fromDate.setTime(_fromDate);
						}
						Calendar toDate = null;
						if (_toDate != null) {
							toDate = Calendar.getInstance();
							toDate.setTime(_toDate);
						}
						if (fromDate == null) {
							if (toDate == null) {
								returnValue = "(" + vs[0] + ")";
							} else {
								returnValue = "(" + vs[0] + ")" + "～"
										+ dateFormat.format(toDate.getTime());
							}
						} else {
							if (toDate == null) {
								returnValue = "(" + vs[0] + ")"
										+ dateFormat.format(fromDate.getTime())
										+ "～";
							} else {
								returnValue = "(" + vs[0] + ")"
										+ dateFormat.format(fromDate.getTime())
										+ "～"
										+ dateFormat.format(toDate.getTime());
							}
						}
					}
				} else {

					// 添加履约方
					if (_fromDate != null) {
						fromDate = Calendar.getInstance();
						fromDate.setTime(_fromDate);
					}
					Calendar toDate = null;
					if (_toDate != null) {
						toDate = Calendar.getInstance();
						toDate.setTime(_toDate);
					}
					if (fromDate == null) {
						if (toDate == null) {
							returnValue = "";
						} else {
							returnValue = "～"
									+ dateFormat.format(toDate.getTime());
						}
					} else {
						if (toDate == null) {
							returnValue = dateFormat.format(fromDate.getTime())
									+ "～";
						} else {
							returnValue = "(" + vs[0] + ")"
									+ dateFormat.format(fromDate.getTime())
									+ "～" + dateFormat.format(toDate.getTime());
						}
					}
				}
			} else {
				// 张三,1220;李四
				// 一个值时
				// 添加履约方
				if (_fromDate != null) {
					fromDate = Calendar.getInstance();
					fromDate.setTime(_fromDate);
				}
				Calendar toDate = null;
				if (_toDate != null) {
					toDate = Calendar.getInstance();
					toDate.setTime(_toDate);
				}
				if (fromDate == null) {
					if (toDate == null) {
						returnValue = "";
					} else {
						returnValue = "(" + vs[0] + ")" + "～"
								+ dateFormat.format(toDate.getTime());
					}
				} else {
					if (toDate == null) {
						returnValue = "(" + vs[0] + ")"
								+ dateFormat.format(fromDate.getTime()) + "～";
					} else {
						returnValue = "(" + vs[0] + ")"
								+ dateFormat.format(fromDate.getTime()) + "～"
								+ dateFormat.format(toDate.getTime());
					}
				}
			}

		}
		return returnValue;

	}
}
