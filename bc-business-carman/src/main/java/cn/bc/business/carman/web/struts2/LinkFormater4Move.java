/**
 * 
 */
package cn.bc.business.carman.web.struts2;

import java.text.MessageFormat;
import java.util.Map;

import cn.bc.core.util.StringUtils;
import cn.bc.web.formater.LinkFormater4Id;

/**
 * 迁移记录的车辆样式化处理器
 *  针对通过id打开超链接的格式化
 * <p>
 * 生成类似&lt;a ...&gt;XXXX&lt;/a&gt;的格式
 * </p>
 * 
 * @author dragon
 * 
 */
public class LinkFormater4Move extends LinkFormater4Id {
	protected String unit;
	protected String motorcade;
	protected String plate;
	protected String carId;
	protected String classes;
	protected Map<String, String> type;
	private boolean showTip = true;

	/**
	 * 
	 * @param urlPattern
	 *            url格式，不要包含上下文路径，如"/bc/user/open?id={0}"
	 * @param moduleKey
	 *            所链接到模块的标识键
	 * @param unit
	 *            视图中车辆的所属单位setRowMapper的key
	 * @param motorcade
	 *            视图中车辆的所属车队setRowMapper的key
	 * @param plate
	 *            视图中车辆的车牌号码setRowMapper的key
	 * @param carId
	 *            视图中车辆IDsetRowMapper的key
	 * @param classes
	 *            视图中车辆的驾驶状态IDsetRowMapper的key
	 * @param type
	 *            驾驶状态转换器
	 * @param showTip
	 *            是否鼠标提示
	 */
	public LinkFormater4Move(String urlPattern, String moduleKey, String unit,
			String motorcade, String plate, String carId, String classes,
			Map<String, String> type, boolean showTip) {

		super(urlPattern, moduleKey);
		this.unit = unit;
		this.motorcade = motorcade;
		this.carId = carId;
		this.classes = classes;
		this.plate = plate;
		this.type = type;
		this.showTip = showTip;
	}

	@SuppressWarnings("unchecked")
	@Override
	public String getIdValue(Object context, Object value) {
		return StringUtils.toString(((Map<String, Object>) context).get(carId));
	}

	@Override
	public String getTaskbarTitle(Object context, Object value) {
		@SuppressWarnings("unchecked")
		Map<String, Object> map = (Map<String, Object>) context;
		return "车辆" + " - " + map.get(plate);

	}

	// 导出视图时，列的内容
	@Override
	public String getLinkText(Object context, Object value) {
		@SuppressWarnings("unchecked")
		Map<String, Object> map = (Map<String, Object>) context;
		// 如果车牌号码不空时就导出 如：广发/四分二队/粤A.J4B41(正班)
		if (map.get(plate) != null) {
			return map.get(unit)
					+ "/"
					+ map.get(motorcade)
					+ "/"
					+ map.get(plate)
					+ (!("0".toString()).equals(map.get(classes).toString()) ? "("
							+ type.get(map.get(classes).toString()) + ")"
							: "");
		} else {
			return "";
		}
	}

	@Override
	public String format(Object context, Object value) {
		@SuppressWarnings("unchecked")
		Map<String, Object> map = (Map<String, Object>) context;
		Object _value = value;
		String label = this.getLinkText(context, value);
		// 如果车牌号码不空时将该列内容格式化为 如：广发/四分二队/粤A.J4B41(正班)
		if (map.get(plate) != null) {
			if (label != null && label.length() > 0) {
				Object[] params = getParams(context, _value);
				String href = MessageFormat.format(this.urlPattern, params);
				String t;
				String tpl = map.get(unit) + "/" + map.get(motorcade) + "/"
						+ "<a href=\"" + href
						+ "\" class=\"bc-link\" data-mtype=\"" + this.moduleKey
						+ "\"";

				// 任务栏显示的标题
				t = this.getTaskbarTitle(context, value);
				if (t != null)
					tpl += " data-title=\"" + "\"";

				// 对话框的id
				t = this.getWinId(context, value);
				if (t != null)
					tpl += " data-mid=\"" + t + "\"";
				tpl += ">"
						+ map.get(plate)
						+ "</a>"
						+ (!("0".toString())
								.equals(map.get(classes).toString()) ? "("
								+ type.get(map.get(classes).toString()) + ")"
								: "");
				// 鼠标移到单元格时的提示内容
				if (this.showTip) {
					return "<div title=\"" + getLinkText(context, value)
							+ "\">" + tpl.toString() + "</div>";
				} else {
					return tpl.toString();
				}
			}
		}
		return "";

	}

}
