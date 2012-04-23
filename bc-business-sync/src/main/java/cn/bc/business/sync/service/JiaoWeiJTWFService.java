/**
 * 
 */
package cn.bc.business.sync.service;

import java.util.Calendar;

import cn.bc.business.sync.domain.JiaoWeiJTWF;
import cn.bc.core.service.CrudService;

/**
 * 交委交通违章Service
 * 
 * @author wis
 */
public interface JiaoWeiJTWFService extends CrudService<JiaoWeiJTWF> {
	/**
	 * 获取金盾交通违章的违章地点
	 * 
	 * @param syncCode
	 *            违章顺序号
	 * @param plateNo
	 *            车牌号
	 * @param happenDate
	 *            违章事发时间
	 * @return
	 */
	String getJinDunAddress(String syncCode, String plateNo, Calendar happenDate);
}