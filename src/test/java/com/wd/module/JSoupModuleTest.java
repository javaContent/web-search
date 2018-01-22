package com.wd.module;

import java.io.IOException;
import java.util.Map;

import org.jsoup.Jsoup;
import org.junit.Test;

public class JSoupModuleTest {

	HttpModule jSoupModule = new HttpModule();

	@Test
	public void testSearch() throws IOException {
		Map<String, String> map = Jsoup.connect("http://www.cnki.net/").execute().cookies();
		System.out.println(map);
	}

}
