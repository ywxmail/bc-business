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
	private Object content;// 请求响应的内容：文本或字节流
	private boolean stream;// 请求响应的内容是否是字节流

	public Object getContent() {
		return content;
	}

	public void setContent(Object content) {
		this.content = content;
	}

	public Result(boolean success, V data, boolean stream) {
		this.success = success;
		this.data = data;
		this.stream = stream;
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

	public boolean isStream() {
		return stream;
	}
}