/**
 * 
 */
package cn.bc.business.web.struts2;

import cn.bc.business.BSConstants;
import cn.bc.web.formater.LinkFormater;

/**
 * 车辆沉余信息字段的超链接格式化
 * <p>
 * 生成类似&lt;a ...&gt;粤A.XXXXX&lt;/a&gt;的格式
 * </p>
 * 
 * @author dragon
 * 
 */
public class LinkFormater4CarInfo extends LinkFormater {
	protected String urlPattern = BSConstants.NAMESPACE + "/car/open?id=";
	protected String moduleKey = "car";
	private boolean showTip = true;
	private String contextPath;// 链接的上下文路径

	public LinkFormater4CarInfo(String contextPath) {
		this.contextPath = contextPath;
	}

	/**
	 * @param contextPath
	 *            链接的上下文路径
	 * @param showTip
	 *            是否显示鼠标悬停信息
	 */
	public LinkFormater4CarInfo(String contextPath, boolean showTip) {
		this.contextPath = contextPath;
		this.showTip = showTip;
	}

	public String format(Object context, Object value) {
		String _value = (String) value;
		if (value == null || _value.trim().length() == 0) {
			return "&nbsp;";
		}
		StringBuffer tpl = new StringBuffer();
		String vv = _value.trim();
		String[] vs = vv.split(",");// [0]-车牌号，如粤A.XXXXX,[1]-车辆id
		String label = "";
		if (vs.length == 2) {
			label += vs[0];
			// 链接地址、模块类型、样式控制
			tpl.append("<a class=\"bc-link\" data-mtype=\"" + this.moduleKey
					+ "\" href=\"" + this.contextPath + this.urlPattern + vs[1]
					+ "\"");

			// 任务栏显示的标题：粤A.XXXXX
			tpl.append(" data-title=\"" + vs[0] + "\"");

			// 对话框的id
			tpl.append(" data-mid=\"" + this.moduleKey + vs[1] + "\"");

			// 链接显示的文字：粤A.XXXXX
			tpl.append(">" + vs[0] + "</a>");
		} else {
			tpl.append(vv);
			label += vv;
		}

		if (this.showTip) {
			return "<div title=\"" + label + "\">" + tpl.toString() + "</div>";
		} else {
			return tpl.toString();
		}
	}

	@Override
	public String getLinkText(Object context, Object value) {
		String _value = (String) value;
		if (value == null || _value.trim().length() == 0) {
			return "&nbsp;";
		}
		String vv = _value.trim();
		String label = "";
		String[] vs = vv.split(",");// [0]-车牌号，如粤A.XXXXX,[1]-车辆id
		if (vs.length == 2) {
			label += vs[0];
		} else {
			label += vv;
		}
		return label;
	}

	@Override
	public Object[] getParams(Object context, Object value) {
		return null;
	}
}
