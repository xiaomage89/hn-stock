package com.hn.market.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * @author majj
 * @create 2023-04-02 14:05
 */
@Configuration
@PropertySource(value = "classpath:stock.properties",encoding = "UTF-8")
@ConfigurationProperties(prefix = "df")
public class MyStockConfig {
}
