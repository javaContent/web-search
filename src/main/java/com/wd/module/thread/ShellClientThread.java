package com.wd.module.thread;

import com.wd.bo.Hosts;
import com.wd.module.task.ShellClient;


public class ShellClientThread implements Runnable {

	private ShellClient shellClient;
	private Hosts host;
	
	

	public ShellClientThread(ShellClient shellClient, Hosts host) {
		this.shellClient = shellClient;
		this.host = host;
	}

	@Override
	public void run() {
		shellClient.execute(host);
	}

}
