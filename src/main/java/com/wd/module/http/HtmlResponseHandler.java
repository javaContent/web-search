package com.wd.module.http;

import java.io.IOException;
import java.nio.charset.Charset;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.entity.ContentType;
import org.apache.http.util.EntityUtils;

/**
 * 处理请求，如果请求返回的是HTML
 * @author Administrator
 *
 */
public class HtmlResponseHandler implements ResponseHandler<String>{

	@Override
	public String handleResponse(HttpResponse resp)
			throws ClientProtocolException, IOException {
        HttpEntity entity = resp.getEntity();
        if (entity == null) {
            throw new ClientProtocolException("返回结果中没有内容!");
        }
        ContentType contentType = ContentType.getOrDefault(entity);
        Charset charset = contentType.getCharset();
        if(charset==null){
        	charset=Charset.forName("UTF-8");
        }
        byte[] bytes = EntityUtils.toByteArray(entity);
		return new String(bytes,charset);
	}

}
