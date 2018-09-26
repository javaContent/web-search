package com.wd.util;


import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.impl.conn.SystemDefaultDnsResolver;

public class CustomDnsResolver extends SystemDefaultDnsResolver {
	
	private Map<String, InetAddress[]> mappings = new HashMap<String, InetAddress[]>();

    public CustomDnsResolver(String ip) {
        try {
            mappings.put("xs.hnwd.com", new InetAddress[]{InetAddress.getByName(ip)});
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    @Override
    public InetAddress[] resolve(String host) throws UnknownHostException {
        return mappings.containsKey(host) ? mappings.get(host) : new InetAddress[0];
    }

}
