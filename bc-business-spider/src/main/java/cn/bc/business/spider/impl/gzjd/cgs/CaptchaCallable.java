package cn.bc.business.spider.impl.gzjd.cgs;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;

import org.springframework.util.FileCopyUtils;

import cn.bc.docs.domain.Attach;
import cn.bc.spider.StreamCallable;

/**
 * 网上车管所 登录验证码的获取
 * 
 * @author dragon
 * 
 */
public class CaptchaCallable extends StreamCallable<String> {
	private String key;// 验证码保存到的文件
	private String captcha; // 用户输入的验证码值

	public CaptchaCallable(String key) {
		super();
		this.setId("wscgs");
		this.setUrl("http://www.gzjd.gov.cn/cgs/captcha.jpg");// 验证码图片的url
		this.key = key;
	}

	@Override
	protected void parseStream(InputStream stream) throws Exception {
		// 复制流到指定的文件
		String dir = Attach.DATA_REAL_PATH + "/spider/captcha/wscgs";
		File file = new File(dir);
		if (!file.exists()) {
			file.mkdirs();
		}
		file = new File(dir + "/" + key + ".jpg");
		FileOutputStream output = new FileOutputStream(file);
		FileCopyUtils.copy(stream, output);

		// 创建保存验证码答案的文件
		file = new File(dir + "/" + key + ".txt");
		output = new FileOutputStream(file);
		output.write("".getBytes());
		output.flush();
		output.close();

		// 循环读取直至等待用户输入结果后再向下执行
		repeatGetUserInput(file);
	}

	/**
	 * 循环读取文件的内容直至文件内容不为空或超过指定的时限
	 * 
	 * @param file
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws InterruptedException
	 */
	private void repeatGetUserInput(File file) throws FileNotFoundException,
			IOException, InterruptedException {
		FileReader in;
		int c = 0;
		while (c < 24) {// 2分钟内
			in = new FileReader(file);
			this.captcha = FileCopyUtils.copyToString(in);
			System.out.println("time=" + c * 5 + ",captcha=" + captcha);
			if (!this.captcha.isEmpty()) {
				break;
			} else {
				Thread.sleep(5000);// 等待5秒
				c++;
			}
		}
	}

	@Override
	protected String parseData() {
		// 返回用户输入的验证码
		return this.captcha;
	}
}