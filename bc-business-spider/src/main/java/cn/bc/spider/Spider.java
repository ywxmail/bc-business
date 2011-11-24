package cn.bc.spider;

import java.util.List;
import java.util.Map;

/**
 * 网络内容抓取器接口
 * 
 * @author rongjih
 * 
 */
public interface Spider<T> {
	List<Map<String, Object>> excute();
}
