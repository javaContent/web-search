package com.wd.module.http;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.protocol.HttpContext;

public class HttpGetTest {

	public static void main(String[] args) {
		String[] urisToGet = {
		        "http://xueshu.baidu.com/s?wd=%E5%A4%A7%E6%95%B0%E6%8D%AE",
		        "http://xueshu.baidu.com/s?wd=%E9%87%8F%E5%AD%90",
		        "http://xueshu.baidu.com/s?wd=%E8%83%BD%E6%BA%90",
		        "http://xueshu.baidu.com/s?wd=%E5%8C%96%E5%AD%A6"
		};
		
		HttpClientPool httClientPool = new HttpClientPool("http://xueshu.baidu.com/s", 2);
		GetThread[] threads = new GetThread[urisToGet.length];
	    for (int i = 0; i < threads.length; i++) {
	        HttpGet httpget = new HttpGet(urisToGet[i]);
	        threads[i] = new GetThread(httClientPool, httpget);
	    }

	    // 启动线程
	    for (int j = 0; j < threads.length; j++) {
	        threads[j].start();
	        try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {

			}
	    }

	    // join the threads
	    for (int j = 0; j < threads.length; j++) {
	        try {
				threads[j].join();
			} catch (InterruptedException e) {
			}
	    }
	    httClientPool.close();
	}
	
	static class GetThread extends Thread {

        private final HttpClientPool httClientPool;
        private final HttpContext context;
        private final HttpGet httpget;

        public GetThread(HttpClientPool httClientPool, HttpGet httpget) {
            this.httClientPool = httClientPool;
            this.context = HttpClientContext.create();
            this.httpget = httpget;
        }

        @Override
        public void run() {
            try {
            	CloseableHttpClient httpClient = httClientPool.getClient();
                CloseableHttpResponse response = httpClient.execute(
                        httpget, context);
                if (response.getStatusLine().getStatusCode()==200) {
					System.out.println(httpClient);
				}
                try {
                    HttpEntity entity = response.getEntity();
                } finally {
                    response.close();
                    httClientPool.pushBack(httpClient, true);
                }
            } catch (ClientProtocolException ex) {
                // Handle protocol errors
            } catch (IOException ex) {
                // Handle I/O errors
            }
        }

    }

}
