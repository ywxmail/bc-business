/**
 * 
 */
package cn.bc.business.web.struts2;

import java.util.ArrayList;
import java.util.List;

import org.springframework.util.StringUtils;

import cn.bc.business.BSConstants;
import cn.bc.web.formater.LinkFormater;

/**
 * 司机沉余信息字段的超链接格式化
 * <p>
 * 生成类似&lt;a ...&gt;张三&lt;/a&gt;(正班),&lt;a ...&gt;李四&lt;/a&gt;(副班)的格式
 * </p>
 * 
 * @author dragon
 * 
 */
public class LinkFormater4DriverInfo extends LinkFormater {
	protected String urlPattern = BSConstants.NAMESPACE + "/carMan/edit?id=";
	protected String moduleKey = "carMan";
	private boolean showTip = true;
	private String contextPath;// 链接的上下文路径

	public LinkFormater4DriverInfo(String contextPath) {
		this.contextPath = contextPath;
	}

	/**
	 * @param contextPath
	 *            链接的上下文路径
	 * @param showTip
	 *            是否显示鼠标悬停信息
	 */
	public LinkFormater4DriverInfo(String contextPath, boolean showTip) {
		this.contextPath = contextPath;
		this.showTip = showTip;
	}

	public String format(Object context, Object value) {
		String _value = (String) value;
		if (value == null || _value.trim().length() == 0) {
			return "&nbsp;";
		}
		_value = _value.trim();

		// 分隔出每个司机的配置：[司机1姓名],[司机1班次],[司机1id];[司机2姓名],[司机2班次],[司机2id];...
		String[] vvs = _value.split(";");

		// 循环每个司机执行格式化处理
		String[] vs;
		StringBuffer tpl = new StringBuffer();
		List<String> labels = new ArrayList<String>();
		String label;
		int i = 0;
		for (String vv : vvs) {
			if (i > 0)
				tpl.append(", ");

			vs = vv.split(",");// [0]-司机姓名,[1]-营运班次,[2]-司机id
			if (vs.length == 3) {
				label = vs[0] + "(" + vs[1] + ")";
				labels.add(label);

				// 链接地址、模块类型、样式控制
				tpl.append("<a class=\"bc-link\" data-mtype=\""
						+ this.moduleKey + "\" href=\"" + this.contextPath
						+ this.urlPattern + vs[2] + "\"");

				// 任务栏显示的标题：司机张三
				tpl.append(" data-title=\"司机" + vs[0] + "\"");

				// 对话框的id
				tpl.append(" data-mid=\"" + this.moduleKey + vs[2] + "\"");

				// 链接显示的文字：张三(正班)
				tpl.append(">" + vs[0] + "</a>" + "(" + vs[1] + ")");
			} else {
				tpl.append(vv);
				labels.add(vv);
			}

			i++;
		}

		if (this.showTip) {
			return "<div title=\""
					+ StringUtils.collectionToCommaDelimitedString(labels)
					+ "\">" + tpl.toString() + "</div>";
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

		// 分隔出每个司机的配置：[司机1姓名],[司机1班次],[司机1id];[司机2姓名],[司机2班次],[司机2id];...
		String[] vvs = _value.split(";");

		// 循环每个司机执行格式化处理
		String[] vs;
		String labels = "";
		int i = 0;
		for (String vv : vvs) {
			if (i > 0)
				labels += ",";

			vs = vv.split(",");// [0]-司机姓名,[1]-营运班次,[2]-司机id
			if (vs.length == 3) {
				labels += vs[0] + "(" + vs[1] + ")";
			} else {
				labels += vv;
			}

			i++;
		}
		return labels;
	}

	@Override
	public Object[] getParams(Object context, Object value) {
		return null;
	}
}
