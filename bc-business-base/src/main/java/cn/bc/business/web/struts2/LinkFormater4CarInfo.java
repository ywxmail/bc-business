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
 * 车辆沉余信息字段的超链接格式化
 * <p>
 * 生成类似&lt;a ...&gt;粤A.XXXX1&lt;/a&gt;,&lt;a ...&gt;粤A.XXXX2&lt;/a&gt;的格式
 * </p>
 * 
 * @author dragon
 * 
 */
public class LinkFormater4CarInfo extends LinkFormater {
	protected String urlPattern = BSConstants.NAMESPACE + "/car/edit?id=";
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
		_value = _value.trim();

		// 分隔出每台车的配置：[车牌号1，如粤A.XXXXX],[车辆1id];[车牌号2，如粤A.XXXXX],[车辆2id];...
		String[] vvs = _value.split(";");

		// 循环每台车执行格式化处理
		String[] vs;
		StringBuffer tpl = new StringBuffer();
		List<String> labels = new ArrayList<String>();
		int i = 0;
		for (String vv : vvs) {
			if (i > 0)
				tpl.append(",");

			vs = vv.split(",");// [0]-车牌号,[1]-车辆id

			if (vs.length == 2) {
				labels.add(vs[0]);
				// 链接地址、模块类型、样式控制
				tpl.append("<a class=\"bc-link\" data-mtype=\""
						+ this.moduleKey + "\" href=\"" + this.contextPath
						+ this.urlPattern + vs[1] + "\"");

				// 任务栏显示的标题：车牌号
				tpl.append(" data-title=\"" + vs[0] + "\"");

				// 对话框的id
				tpl.append(" data-mid=\"" + this.moduleKey + vs[1] + "\"");

				// 链接显示的文字：车牌号
				tpl.append(">" + vs[0] + "</a>");
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
			return "&nbsp;";
		}
		_value = _value.trim();

		// 分隔出每台车的配置：[车牌号1，如粤A.XXXXX],[车辆1id];[车牌号2，如粤A.XXXXX],[车辆2id];...
		String[] vvs = _value.split(";");

		// 循环每台车执行格式化处理
		String[] vs;
		String labels = "";
		int i = 0;
		for (String vv : vvs) {
			if (i > 0)
				labels += ",";

			vs = vv.split(",");// [0]-车牌号,[1]-车辆id
			if (vs.length == 2) {
				labels += vs[0];
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
