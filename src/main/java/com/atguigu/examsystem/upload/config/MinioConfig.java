package com.atguigu.examsystem.upload.config;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.SetBucketPolicyArgs;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Data
@Configuration
@Slf4j
@ConfigurationProperties(prefix = "minio")
public class MinioConfig {

    // 服务地址
    private String endpoint;

    // 访问秘钥
    private String accessKey;

    // 秘密秘钥
    private String secretKey;

    // 存储桶名称
    private String bucketName;

    // 是否启用HTTPS
    private Boolean secure;

    // 连接超时时间（毫秒）
    private Long connectTimeout;

    // 写入超时时间（毫秒）
    private Long writeTimeout;

    // 读取超时时间（毫秒）
    private Long readTimeout;

    // 存储桶策略（可选）
    private String bucketPolicy;

    /**
     * 创建minioClient
     *
     * @return
     */
    @Bean
    public MinioClient minioClient() {
        try {
            // 创建okHttpClient
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(connectTimeout, TimeUnit.MILLISECONDS)
                    .readTimeout(readTimeout, TimeUnit.MILLISECONDS)
                    .writeTimeout(writeTimeout, TimeUnit.MILLISECONDS)
                    .build();

            // 创建minioClient
            MinioClient minioClient = MinioClient.builder()
                    .endpoint(endpoint)
                    .credentials(accessKey, secretKey)
                    .httpClient(okHttpClient)
                    .build();

            if (secure) {
                // 用于配置客户端在建立HTTPS连接时忽略SSL/TLS证书验证
                minioClient.ignoreCertCheck();
            }
            log.info("创建minioClient成功, 访问地址：{}", endpoint);
            return minioClient;
        } catch (Exception e) {
            log.error("创建clientClient失败，原因是：{}", e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * 初始化方法
     */
    @PostConstruct
    public void init() {
        try {
            MinioClient minioClient = minioClient();
            boolean bucketExists = minioClient.bucketExists(
                    BucketExistsArgs.builder().bucket(bucketName).build());
            if (bucketExists) {
                log.info("存储桶:{} 已存在", bucketName);
            } else {
                // 不存在，则创建
                minioClient.makeBucket(
                        MakeBucketArgs.builder().bucket(bucketName).build());
                // 设置存储桶策略
                if ("public".equals(bucketPolicy)) {
                    String policy = "{\n" +
                            "  \"Version\": \"2012-10-17\",\n" +
                            "  \"Statement\": [\n" +
                            "    {\n" +
                            "      \"Effect\": \"Allow\",\n" +
                            "      \"Principal\": \"*\",\n" +
                            "      \"Action\": [\n" +
                            "        \"s3:GetObject\"\n" +
                            "      ],\n" +
                            "      \"Resource\": [\n" +
                            "        \"arn:aws:s3:::" + bucketName + "/*\"\n" +
                            "      ]\n" +
                            "    }\n" +
                            "  ]\n" +
                            "}";
                    minioClient.setBucketPolicy(
                            SetBucketPolicyArgs.builder().bucket(bucketName).config(policy).build());
                    log.info("设置存储桶：{} 为公开访问策略", bucketName);
                }
                log.info("存储桶：{} 已创建完成", bucketName);
            }

        } catch (Exception e) {
            log.error("创建存储桶：{}失败，失败原因：{}", bucketName, e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

}
