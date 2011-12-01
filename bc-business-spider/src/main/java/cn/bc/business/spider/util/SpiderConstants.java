/**
 * 
 */
package cn.bc.business.spider.util;


/**
 * 交委WebService接口相关常数定义
 * <p>
 * http://61.144.39.126/middle/WSMiddle.asmx
 * </p>
 * 
 * @author dragon
 * 
 */
public interface SpiderConstants {
	/** 交警违章记录KEY：公司名称|string */
	public final static String KEY_JJWZ_COMPANY = "公司名称";
	/** 交警违章记录KEY：车牌号码|string */
	public final static String KEY_JJWZ_CAR_PLATE = "车牌号码";
	/** 交警违章记录KEY：当事司机姓名|string */
	public final static String KEY_JJWZ_DRIVER_NAME = "当事司机姓名";
	/** 交警违章记录KEY：服务资格证|string */
	public final static String KEY_JJWZ_FUZG_CODE = "服务资格证";
	/** 交警违章记录KEY：违章时间|dateTime */
	public final static String KEY_JJWZ_BREAK_DATE = "违章时间";
	/** 交警违章记录KEY：违章内容|string */
	public final static String KEY_JJWZ_BREAK_CONTENT = "违章内容";
	/** 交警违章记录KEY：本次扣分|decimal */
	public final static String KEY_JJWZ_JEOM = "本次扣分";
	/** 交警违章记录KEY：违章顺序号|string */
	public final static String KEY_JJWZ_ID = "违章顺序号";
	
	/** 营运违章记录KEY：|string */
	public final static String KEY_YYWZ_ID = "c_id";
	/** 营运违章记录KEY：案号|string */
	public final static String KEY_YYWZ_CODE = "案号";
	/** 营运违章记录KEY：身份证明类型|string */
	public final static String KEY_YYWZ_IDENTITY_TYPE = "身份证明类型";
	/** 营运违章记录KEY：身份证明编号|string */
	public final static String KEY_YYWZ_IDENTITY_CODE = "身份证明编号";
//	/** 营运违章记录KEY：违章主体|string */
//	public final static String KEY_YYWZ_ = "违章主体";
//	/** 营运违章记录KEY：当事人|string */
//	public final static String KEY_YYWZ_ = "当事人";
//	/** 营运违章记录KEY：扣件证号|string */
//	public final static String KEY_YYWZ_ = "扣件证号";
//	/** 营运违章记录KEY：违章日期|string */
//	public final static String KEY_YYWZ_ = "违章日期";
//	/** 营运违章记录KEY：违章内容|string */
//	public final static String KEY_YYWZ_ = "违章内容";
//	/** 营运违章记录KEY：违章地段|string */
//	public final static String KEY_YYWZ_ = "违章地段";
//	/** 营运违章记录KEY：状态|string */
//	public final static String KEY_YYWZ_ = "状态";
}
