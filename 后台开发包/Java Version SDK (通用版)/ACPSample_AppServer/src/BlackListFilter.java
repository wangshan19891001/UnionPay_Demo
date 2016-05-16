import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLDecoder;
import java.util.HashSet;
import java.util.Properties;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.log4j.Logger;

public class BlackListFilter implements Filter {

	private HashSet<String> merblacklist = new HashSet<String>();
	private HashSet<String> instblacklist = new HashSet<String>();
	private HashSet<String> orderlacklist = new HashSet<String>();

	private static final Logger logger = Logger.getLogger(BlackListFilter.class);
	private static final long SLEEPTIME = 10;
	private Thread loadConfThread;
	
    public BlackListFilter() {

    }
    
    private boolean loadConfig(InputStream in){
    	
		if (in == null){
			logger.error("cannot find blacklist.properties.");
			return false;
		}
		
    	HashSet<String> tmpmerblacklist = merblacklist;
    	HashSet<String> tmpinstblacklist = instblacklist;
    	HashSet<String> tmporderlacklist = orderlacklist;
    	merblacklist = new HashSet<String>();
    	instblacklist = new HashSet<String>();
    	orderlacklist = new HashSet<String>();
    	
    	try {
    		
	    	Properties prop = new Properties();
	    	prop.load(in);
				
	    	String merId = prop.getProperty("merId");
	    	if(merId != null){
	    		String[] merIds = merId.split(",");
	    		for (int i=0; i<merIds.length; i++){
	    			merblacklist.add(merIds[i]);
	    		}
	    	}
	    	
	    	String acqInsCode = prop.getProperty("acqInsCode");
	    	if(acqInsCode != null){
	    		String[] acqInsCodes = acqInsCode.split(",");
	    		for (int i=0; i<acqInsCodes.length; i++){
	    			instblacklist.add(acqInsCodes[i]);
	    		}
	    	}
	    	
	    	String orderId = prop.getProperty("orderId");
	    	if(orderId != null){
	    		String[] orderIds = orderId.split(",");
	    		for (int i=0; i<orderIds.length; i++){
	    			orderlacklist.add(orderIds[i]);
	    		}
	    	}
	    	return true;
		} catch (IOException e) {
			logger.error("load conf error: "+e.getMessage());
	    	merblacklist = tmpmerblacklist;
	    	instblacklist = tmpinstblacklist;
	    	orderlacklist = tmporderlacklist;
			return false;
		}
    }

	public void destroy() {
		loadConfThread.interrupt();
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

		String merId = request.getParameter("merId");
		String acqInsCode = request.getParameter("acqInsCode");
		String orderId = request.getParameter("orderId");
		
		if(merblacklist.contains(merId)){
			response.getWriter().write("You have been added to the blacklist. Please don't do stress testing.");
			return;
		}
		
		if(instblacklist.contains(acqInsCode)){
			response.getWriter().write("You have been added to the blacklist. Please don't do stress testing.");
			return;
		}
		
		if(orderlacklist.contains(orderId)){
			response.getWriter().write("You have been added to the blacklist. Please don't do stress testing.");
			return;
		}
		
		chain.doFilter(request, response);
	}

	public void init(FilterConfig fConfig) throws ServletException {
		loadConfThread = new Thread(new Runnable() {
			public void run() {
				long lastModified = 0;
				while (true){
					try {
						URL url = BlackListFilter.class.getResource("/blacklist.properties");
						if(url != null){
							File file = new File (URLDecoder.decode(url.getPath(),"utf-8"));
							if(file.lastModified() > lastModified){
								loadConfig(new FileInputStream(file));
								lastModified = file.lastModified();
							}
						} else {
							logger.error("cannot find blacklist.properties: "+BlackListFilter.class.getResource("/").getPath());
						}
					} catch (Exception e) {
						logger.error("load blacklist.properties error: "+e.getMessage());
					}
					try {
						Thread.sleep(SLEEPTIME * 1000);
					} catch (InterruptedException e) {
						logger.error("Interrupt sleep: "+e.getMessage());
					}
				}
			}
		});
		loadConfThread.start();
	}

}
