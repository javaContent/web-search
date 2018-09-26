package com.wd.module.task;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.wd.bo.Hosts;


/**
 * 调用（谷歌镜像）拨号脚本
 * @author Administrator
 */
@Service("shellClient")
public class ShellClient {
	
	private static final Logger log=Logger.getLogger(ShellClient.class);
	
	@Value("${shellPath}")
	String shellPath;
	
	@Value("${fixedShellPath}")
	String fixedShellPath;
	
	public void execute(Hosts host) {
		String name = host.getName();
		log.info("ShellClient:" + name);
		try {
			String shpath= shellPath + " " + name;   //程序路径
			if(name.contains("Fixed")) {
				shpath = fixedShellPath +" "+ name;   //程序路径
			}
			log.info(shpath);
		    Process process =null;
	    	process = Runtime.getRuntime().exec(shpath);
	    	process.waitFor();
	    	
	    	BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));  
            StringBuffer sb = new StringBuffer();  
            String line;  
            while ((line = br.readLine()) != null) {  
                sb.append(line).append("\n");  
            }  
            String result = sb.toString();  
            log.info("ShellClient--->result:" + result);
	    } catch (Exception e1) {
	    	log.info("ShellClient--->"+host.getName()+":拨号失败");
	    }
	}
	
}
