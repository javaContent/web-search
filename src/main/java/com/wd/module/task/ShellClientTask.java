package com.wd.module.task;


import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.wd.bo.Hosts;
import com.wd.email.MailModuleI;
import com.wd.hostQuery.AbstractHostQueue;
import com.wd.module.thread.ShellClientThread;

/**
 * vps拨号控制器
 * @author Administrator
 *
 */
@Service("shellClientTask")
@EnableScheduling
public class ShellClientTask {
	
	private static final Logger log=Logger.getLogger(ShellClientTask.class);

	@Autowired
	private AbstractHostQueue hostQueue;
	
	@Autowired
	private AbstractHostQueue fixedhostQueue;
	
	@Autowired
	private ShellClient shellClient;
	
	@Autowired
	private MailModuleI mailModule;
	
	Thread thread;
	
	@Value("${path}")
	private String path;
	
	@Value("${fixedPath}")
	private String fixedPath;
	
	/*初始vps主机*/
	private static final Map<String,Integer> map = new HashMap<String, Integer>();
	
	private static final List<String> hostList = new ArrayList<String>();
	
	/**
	 * 拨号判定（没有vps进行拨号）
	 * @param host
	 * @return true是进行拨号
	 */
	public boolean execute(Hosts host) {
		if(thread == null || !thread.isAlive()) {
			log.info("重新拨号：" + host.getName());
			host.setType(2);
			ShellClientThread shell = new ShellClientThread(shellClient, host);
			thread = new Thread(shell);
			thread.start();
			host.setDialingCount();
			host.setDialingTime(System.currentTimeMillis()/1000);
			return true;
		}
		log.info("以有vps正在拨号！请稍等.....");
		return false;
	}
	
	@PostConstruct
	private void init() {
		hostQueue.init();
		fixedhostQueue.init();
		File file = new File(path);  
		readFile(file);
		
		File fileFixed = new File(fixedPath);  
		readFile(fileFixed);
	}
	
	private void readFile(File file) {
		InputStream in = null;  
        try {  
            in = new FileInputStream(file);  
            byte[] data = new byte[1024];  
            in.read(data);  
            String content = new String(data);
            String[] names = content.split("\\n");
            for (String name : names) {
            	if(StringUtils.isNotEmpty(name.trim())) {
            		String hostName = name.substring(0, name.indexOf("="));
            		hostList.add(hostName);
//            		map.put(name.substring(0, name.indexOf("=")), 0);
            		log.info("name:"+name);
            		ShellClientThread shell = new ShellClientThread(shellClient, new Hosts(hostName,null));
            		Thread thread = new Thread(shell);
        			thread.start();
//        			thread.join();
//            		execute(new Hosts(name.substring(0, 4),null));
            	}
			}
        } catch (Exception e) {  
            e.printStackTrace();  
        } finally {  
            try {  
                in.close();  
            } catch (Exception e) {  
                e.printStackTrace();  
            }  
        }  
	}
	
	//定时扫描vps主机是否都可用
	@Scheduled(cron = "0 0/2 * * * ?")
    public void scanning(){
		for (int i = 0; i < hostList.size(); i++) {
			boolean isLose = true;
			for (int j = 0; j < hostQueue.getSize(); j++) {
				Hosts hosts = hostQueue.getHostHeart(j);
				if(hosts.getName().equals(hostList.get(i))) {
					check(hosts);
					isLose = false;
					break;
				}
			}
			if(isLose) {
				check(new Hosts(hostList.get(i),null));
			}
		}
    }
	
	/**
	 * 检测是否需要拨号
	 * @param hosts
	 */
	private void check(Hosts hosts) {
		long nowTime = System.currentTimeMillis()/1000;
		long intervalTime = nowTime - hosts.getDialingTime();
		if(hosts.getDialingCount() > 30) {
			mailModule.sendSimpleMail(hosts);
//			return;
		}
		if(hosts != null && hosts.getType() != 1 && intervalTime > 180 && hosts.getUserCount() <= 0) {
			log.info("vps主机可用检测--》重新拨号：" + hosts.getName() + " 以拨号次数：" + hosts.getDialingCount());
			executeAll(hosts);
		}
	}
	/**
	 * 同时多线程拨号
	 * @param host
	 * @return
	 */
	public boolean executeAll(Hosts host) {
		ShellClientThread shell = new ShellClientThread(shellClient, host);
		Thread thread = new Thread(shell);
		thread.start();
		host.setDialingCount();
		host.setDialingTime(System.currentTimeMillis()/1000);
		return true;
	}
	
	//每天定时初始化配置
	@Scheduled(cron = "0 59 0/23 * * ?")
    public void initialization(){
		init();
    }
	
}
