import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Properties;


public class Test {

	public static void main(String[] args) {
		final long sleeptime = 10;
		Thread t = new Thread(new Runnable() {
			public void run() {
				long lastModified = 0;
				while (true){
					try {
						if(BlackListFilter.class.getResource("/blacklist.properties")!=null){
							File file = new File (URLDecoder.decode(BlackListFilter.class.getResource("/blacklist.properties").getPath(),"utf-8"));
							if(file.lastModified() > lastModified){
								loadConfig(new FileInputStream(file));
								lastModified = file.lastModified();
							}
						} else {
							System.out.println("卧槽文件没读到。");
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					System.out.println("诶我又循环了一次。");
					try {
						Thread.sleep(sleeptime * 1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		});
		t.start();
	}

	private static boolean loadConfig(InputStream in) {
		System.out.println("假装读了配置吧= =");
		return true;
	}
}
