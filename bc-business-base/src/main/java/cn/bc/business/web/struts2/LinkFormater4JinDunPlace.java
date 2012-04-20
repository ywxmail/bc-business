/**
 * 
 */
package cn.bc.business.web.struts2;

import java.util.ArrayList;
import java.util.List;

import cn.bc.business.BSConstants;
import cn.bc.web.formater.LinkFormater;

/**
 * 交委交通违法(金盾网的违章地点格式化)
 * 
 * @author zxr
 * 
 */
public class LinkFormater4JinDunPlace extends LinkFormater {
	protected String urlPattern = BSConstants.NAMESPACE+"/sync" + "/jinDunJTWF/edit?id=";
	protected String moduleKey = "jinDunJTWF";
	private boolean showTip = true;
	private String contextPath;// 链接的上下文路径

	public LinkFormater4JinDunPlace(String contextPath) {
		this.contextPath = contextPath;
	}

	/**
	 * @param contextPath
	 *            链接的上下文路径
	 * @param showTip
	 *            是否显示鼠标悬停信息
	 */
	public LinkFormater4JinDunPlace(String contextPath, boolean showTip) {
		this.contextPath = contextPath;
		this.showTip = showTip;
	}

	public String format(Object context, Object value) {
		String _value = (String) value;
		if (value == null || _value.trim().length() == 0) {
			return "&nbsp;";
		}
		_value = _value.trim();

		// 分隔违章地点和金盾网的交通违法相关记录的ID：[地点;金盾网的交通违法相关记录的id;...
		String[] vvs = _value.split(";");

		StringBuffer tpl = new StringBuffer();
		List<String> labels = new ArrayList<String>();
		labels.add(vvs[0]);
		// 链接地址、模块类型、样式控制
		tpl.append("<a class=\"bc-link\" data-mtype=\"" + this.moduleKey
				+ "\" href=\"" + this.contextPath + this.urlPattern + vvs[1]
				+ "\"");

		// 任务栏显示的标题：车牌号
		tpl.append(" data-title=\"" + vvs[0] + "\"");

		// 对话框的id
		tpl.append(" data-mid=\"" + this.moduleKey + vvs[1] + "\"");

		// 链接显示的文字：车牌号
		tpl.append(">" + vvs[0] + "</a>");

		if (this.showTip) {
			return "<div title=\"" + getLinkText(context, value) + "\">"
					+ tpl.toString() + "</div>";
		} else {
			return tpl.toString();
		}
	}

	@Override
	public String getLinkText(Object context, Object value) {
		String _value = (String) value;
		if (value == null || _value.trim().length() == 0) {
			return "";
		}
		_value = _value.trim();

		// 分隔违章地点和金盾网的交通违法相关记录的ID：[地点;金盾网的交通违法相关记录的id;...
		String[] vvs = _value.split(";");

		String labels = "";
		labels += vvs[0];
		return labels;
	}

	@Override
	public Object[] getParams(Object context, Object value) {
		return null;
	}
}
