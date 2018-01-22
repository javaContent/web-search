package com.wd.bo;

import java.util.Date;
import java.util.Properties;

import com.opensymphony.oscache.base.NeedsRefreshException;
import com.opensymphony.oscache.general.GeneralCacheAdministrator;

public class BaseCache extends GeneralCacheAdministrator {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// 过期时间(单位为秒);
	private int refreshPeriod;
	// 关键字前缀字符;
	private String keyPrefix;

	public BaseCache(String keyPrefix, int refreshPeriod, Properties properties) {

		super(properties);
		this.keyPrefix = keyPrefix;
		this.refreshPeriod = refreshPeriod;
	}

	public BaseCache(String keyPrefix, int refreshPeriod) {

		super();
		this.keyPrefix = keyPrefix;
		this.refreshPeriod = refreshPeriod;
	}

	/**
	 * 添加缓存对象
	 * 
	 * @param key
	 * @param value
	 */
	public void put(String key, Object value) {

		this.putInCache(this.keyPrefix + "_" + key, value);
	}

	/**
	 * 删除被缓存的对象
	 * 
	 * @param key
	 */
	public void remove(String key) {

		this.flushEntry(this.keyPrefix + "_" + key);
	}

	/**
	 * 删除所有被缓存的对象
	 * 
	 * @param date
	 */
	public void removeAll(Date date) {

		this.flushAll(date);
	}

	public void removeAll() {

		this.flushAll();
	}

	/**
	 * 获取被缓存的对象;
	 * 
	 * @param key
	 * @return
	 * @throws NeedsRefreshException
	 * @throws Exception
	 */
	public Object get(String key) throws NeedsRefreshException {

		try {
			return this.getFromCache(this.keyPrefix + "_" + key, this.refreshPeriod);
		} catch (NeedsRefreshException e) {
			this.cancelUpdate(this.keyPrefix + "_" + key);
			throw e;
		}

	}
}
