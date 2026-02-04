package com.atguigu.examsystem.kimi;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@ConfigurationProperties(prefix = "kimi")
@EnableConfigurationProperties(value = KimiConfig.class)
@Component
public class KimiConfig {
    // Kimi api_key配置
    private String apiKey;
    // Kimi base_url配置
    private String baseUrl;
    // Kimi 模型配置
    private String model;
    // Kimi temperature配置
    private Double temperature;
}
