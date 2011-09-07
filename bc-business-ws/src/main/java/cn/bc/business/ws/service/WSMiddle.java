/**
 * 
 */
package cn.bc.business.ws.service;

import java.util.Calendar;

import cn.bc.web.ws.dotnet.DataSet;

/**
 * 交委WebService接口方法的Java接口
 * <p>
 * http://61.144.39.126/middle/WSMiddle.asmx
 * </p>
 * 
 * @author dragon
 * 
 */
public interface WSMiddle {
	/**
	 * 返回DataSet格式接口的通用调用方法
	 * 
	 * @param bodyXml 请求的Body内的Xml
	 * @param soapAction 
	 * @param strMsg
	 * @return
	 */
	DataSet findDataSet(String bodyXml, String soapAction, StringBuffer strMsg);

	/**
	 * 返回Xml格式接口的通用调用方法
	 * 
	 * @param bodyXml 请求的Body内的Xml
	 * @param soapAction 
	 * @param strMsg
	 * @return
	 */
	String findXml(String bodyXml, String soapAction, StringBuffer strMsg);

	/**
	 * 查询企业的交通违章信息
	 * 
	 * @param qyId
	 *            企业ID
	 * @param fromDate
	 *            违章日期从
	 * @param toDate
	 *            违章日期到
	 * @param strMsg
	 *            出错时返回的信息
	 * @return <ol>
	 *         返回的DataSet的列名参考 WSConstants.KEY_JJWZ_XXXX 常数的定义
	 *         </ol>
	 */
	DataSet findBreachOfTraffic(String qyId, Calendar fromDate,
			Calendar toDate, StringBuffer strMsg);

	/**
	 * 查询企业的营运违章信息
	 * 
	 * @param qyId
	 *            企业ID
	 * @param fromDate
	 *            违章日期从
	 * @param toDate
	 *            违章日期到
	 * @param strMsg
	 *            出错时返回的信息
	 * @return <ol>
	 *         返回的DataSet的列名参考KEY_YYWZ_XXX常数的定义
	 *         </ol>
	 */
	DataSet findBreachOfBusiness(String qyId, Calendar fromDate,
			Calendar toDate, StringBuffer strMsg);

	/**
	 * 查询企业的投诉和建议信息
	 * 
	 * @param qyId
	 *            企业ID
	 * @param fromDate
	 *            开始日期
	 * @param toDate
	 *            结束日期
	 * @param strMsg
	 *            出错时返回的信息
	 * @return <ol>
	 *         返回的DataSet的列名参考KEY_TSJY_XXX常数的定义
	 *         </ol>
	 */
	DataSet findAccuseAndAdvice(String qyId, Calendar fromDate,
			Calendar toDate, StringBuffer strMsg);

	/**
	 * 企业协查投诉建议查询
	 * 
	 * @param qyId
	 *            企业ID
	 * @param fromDate
	 *            开始日期
	 * @param toDate
	 *            结束日期
	 * @param status
	 *            状态:1-未处理,2-已处理,3-已结案
	 * @param man
	 *            经办人
	 * @param strMsg
	 *            出错时返回的信息
	 * @return
	 */
	DataSet findAccuseAndAdvice(String qyId, Calendar fromDate,
			Calendar toDate, String status, String man, StringBuffer strMsg);

	/**
	 * 查询人车备案司机列表信息
	 * <p>
	 * 返回的司机信息中仅包含身份证、姓名、服务资格证、司机类型
	 * </p>
	 * 
	 * @param qyId
	 *            企业ID
	 * @param type
	 *            司机类型：主班1,主班2,公共替班,固定车辆组替班,固定车辆替班,不可营运替班,待岗替班；
	 *            设为null返回的是所有在岗但无司机类型的司机， 设为""返回的是所有在岗司机。
	 * @param strMsg
	 *            出错时返回的信息
	 * @return
	 */
	DataSet findDriverByType(String qyId, String type, StringBuffer strMsg);

	/**
	 * 根据司机姓名或者服务资格证查询从业人员基本资料
	 * <p>
	 * 返回的司机信息比较详细
	 * </p>
	 * 
	 * @param qyId
	 *            企业ID
	 * @param fuWuZiGeZheng
	 *            司机类型：主班1,主司机服务资格证
	 * @param driverName
	 *            司机姓名
	 * @param strMsg
	 *            出错时返回的信息
	 * @return
	 */
	DataSet findDriverDetail(String qyId, String fuWuZiGeZheng,
			String driverName, StringBuffer strMsg);

	/**
	 * 根据车牌号码查询车辆基本资料
	 * <p>
	 * 返回的车牌信息比较详细
	 * </p>
	 * 
	 * @param qyId
	 *            企业ID
	 * @param plate
	 *            车牌号码
	 * @param strMsg
	 *            出错时返回的信息
	 * @return
	 */
	DataSet findCarDetail(String qyId, String plate, StringBuffer strMsg);
}
