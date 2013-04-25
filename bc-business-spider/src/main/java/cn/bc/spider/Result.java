package cn.bc.spider;

/**
 * 异步线程返回数据结果的封装
 * 
 * @author dragon
 * 
 * @param <V>
 */
public class Result<V> {
	private boolean success;// 标识数据是否获取成功
	private V data;// 成功获取到的数据,如果success=false,则为null
	private Throwable error;// 失败时的异常信息
	private String html;// 请求响应的内容

	public String getHtml() {
		return html;
	}

	public void setHtml(String html) {
		this.html = html;
	}

	public Result(boolean success, V data) {
		this.success = success;
		this.data = data;
	}

	public Result(boolean success, Throwable e) {
		this.success = success;
		this.error = e;
	}

	public boolean isSuccess() {
		return success;
	}

	public V getData() {
		return data;
	}

	public Throwable getError() {
		return error;
	}
}