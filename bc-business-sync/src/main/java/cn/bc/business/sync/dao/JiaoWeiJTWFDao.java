/**
 * 
 */
package cn.bc.business.sync.dao;

import java.util.Calendar;

import cn.bc.business.sync.domain.JiaoWeiJTWF;
import cn.bc.core.dao.CrudDao;

/**
 * 金盾网交通违章Dao
 * 
 * @author wis
 */
public interface JiaoWeiJTWFDao extends CrudDao<JiaoWeiJTWF> {

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
	String findJinDunAddress(String syncCode, String plateNo,
			Calendar happenDate);

}