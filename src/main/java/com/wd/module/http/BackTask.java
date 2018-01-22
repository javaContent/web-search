package com.wd.module.http;

import java.util.concurrent.BlockingQueue;

/**
 * 将对象延时归还到队列中
 * @author Shenfu
 *
 * @param <T>
 */
public class BackTask<T> implements Runnable{
	
	private BlockingQueue<T> queue;
	
	/**
	 * 默认冷却时间为1分钟
	 */
	private long sleepTime=1*60*1000;
	
	private T target;
	
	public BackTask(BlockingQueue<T> queue,T target){
		this.queue=queue;
		this.target=target;
	}
	
	public BackTask(BlockingQueue<T> queue,T target,long time){
		this(queue,target);
		this.sleepTime=time;
	}

	@Override
	public void run() {
		try {
			Thread.sleep(sleepTime);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		queue.offer(target);
	}

}
