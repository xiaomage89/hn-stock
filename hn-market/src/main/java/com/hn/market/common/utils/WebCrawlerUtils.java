package com.hn.market.common.utils;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @author majj
 * @create 2023-04-02 11:01
 */
@Component
public class WebCrawlerUtils {

    /**
     * 网盘爬取最新个股行情（包含沪深京A股） ，东方财富网
     *
     * @param url
     * @return
     */
    public String getCrawler(String url) {

        CloseableHttpClient client = HttpClientBuilder.create().build();
        HttpGet httpGet = new HttpGet(url);
        try {
            // 很奇怪，使用CloseableHttpClient来请求澎湃新闻的首页，GTE请求也必须加上下面这个Header，但是使用HTTPClient则不需要
            httpGet.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.81 Safari/537.36");
            HttpResponse response = client.execute(httpGet);
            if(response.getStatusLine().getStatusCode()==200){
                String content = EntityUtils.toString(response.getEntity(), "utf-8");
                return content;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
