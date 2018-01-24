package com.wd.module.http;
import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.http.HeaderElement;
import org.apache.http.HeaderElementIterator;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;

import com.wd.bo.ProxyInfo;
import com.wd.bo.ProxyQueue;

public class HttpClientPool {

	private String url;
	
	private int maxConn;
	
	private PoolingHttpClientConnectionManager cm;
	
	private ArrayBlockingQueue<CloseableHttpClient> clients;
	
	private IdleConnectionMonitorThread monitorThread;
	
	public ProxyQueue proxyQueueModule;
	
	private static  HttpClientBuilder clientBulder;

	public HttpClientPool(String url, int maxConn) {
		this.url = url;
		this.maxConn = maxConn;
		init();
		monitorThread = new IdleConnectionMonitorThread(cm);
		monitorThread.start();
	}

	private void init(){
		cm = new PoolingHttpClientConnectionManager();
		cm.setMaxTotal(maxConn);
		HttpHost localhost = new HttpHost(url, 80);
		cm.setMaxPerRoute(new HttpRoute(localhost), maxConn);
		clients = new ArrayBlockingQueue<CloseableHttpClient>(maxConn);
		ConnectionKeepAliveStrategy myStrategy = new ConnectionKeepAliveStrategy() {
	        public long getKeepAliveDuration(HttpResponse response, HttpContext context) {
	                // Honor 'keep-alive' header
	        	    HeaderElementIterator it = new BasicHeaderElementIterator(
	                     response.headerIterator(HTTP.CONN_KEEP_ALIVE));	            while (it.hasNext()) {
	                HeaderElement he = it.nextElement();
	                String param = he.getName();
	                String value = he.getValue();
	                if (value != null && param.equalsIgnoreCase("timeout")) {
	                    try {
	                        return Long.parseLong(value) * 1000;
	                    } catch(NumberFormatException ignore) {
	                    }
	                }
	            }
	            HttpHost target = (HttpHost) context.getAttribute(
	                    HttpClientContext.HTTP_TARGET_HOST);
	            if ("www.naughty-server.com".equalsIgnoreCase(target.getHostName())) {
	                // Keep alive for 5 seconds only
	                return 5 * 1000;
	            } else {
	                // otherwise keep alive for 30 seconds
	                return 30 * 1000;
	            }
	        }

	    };
	    clientBulder = HttpClients.custom()
	            .setConnectionManager(cm)
	            .setKeepAliveStrategy(myStrategy);
	    CloseableHttpClient httpClient;
		for (int i = 0; i < maxConn; i++) {
			if (proxyQueueModule!=null) {
				ProxyInfo proxyInfo = proxyQueueModule.pollProxy();
				HttpHost proxy = new HttpHost(proxyInfo.getIp(), Integer.parseInt(proxyInfo.getPort()));
			    DefaultProxyRoutePlanner routePlanner = new DefaultProxyRoutePlanner(proxy);
				clientBulder.setRoutePlanner(routePlanner);
			}
			httpClient = clientBulder.build();		
			clients.add(httpClient);
		}		
	}
	
	public void close() {
		monitorThread.shutdown = true;
		for (CloseableHttpClient client : clients) {
			try {
				client.close();
			} catch (IOException e) {
			}
		}
		cm.close();
	}
	
	public CloseableHttpClient getClient() {
		return clients.poll();
	}
	
	public void pushBack(CloseableHttpClient client, boolean success){
		CloseableHttpClient httpClient = client;
		if (!success&&proxyQueueModule!=null) {
			ProxyInfo proxyInfo = proxyQueueModule.pollProxy();
			HttpHost proxy = new HttpHost(proxyInfo.getIp(), Integer.parseInt(proxyInfo.getPort()));
		    DefaultProxyRoutePlanner routePlanner = new DefaultProxyRoutePlanner(proxy);
			clientBulder.setRoutePlanner(routePlanner);
			try {
				httpClient.close();
			} catch (IOException e) {
			}
			httpClient = clientBulder.build();
		}
		clients.offer(httpClient);			
	}
	
	public static class IdleConnectionMonitorThread extends Thread {

        private final HttpClientConnectionManager connMgr;
        private volatile boolean shutdown;

        public IdleConnectionMonitorThread(HttpClientConnectionManager connMgr) {
            this.connMgr = connMgr;
        }

        @Override
        public void run() {
            try {
                while (!shutdown) {
                    synchronized (this) {
                        wait(5000);
                        // 关闭失效的连接
                        connMgr.closeExpiredConnections();
                        // 可选的, 关闭30秒内不活动的连接
                        connMgr.closeIdleConnections(30, TimeUnit.SECONDS);
                    }
                }
            } catch (InterruptedException ex) {
                // terminate
            }
        }

        public void shutdown() {
            shutdown = true;
            synchronized (this) {
                notifyAll();
            }
        }

    }

	public void setProxyQueueModule(ProxyQueue proxyQueueModule) {
		this.proxyQueueModule = proxyQueueModule;
	}
	
}
