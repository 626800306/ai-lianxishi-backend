package com.atguigu.examsystem.util;

import io.minio.*;
import io.minio.http.Method;
import io.minio.messages.DeleteError;
import io.minio.messages.DeleteObject;
import io.minio.messages.Item;
import io.minio.messages.Tags;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class MinioUtil {
    @Value("${minio.endpoint}")
    private String endpoint;
    @Value("${minio.accessKey}")
    private String accessKey;
    @Value("${minio.secretKey}")
    private String secretKey;
    @Value("${minio.bucketName}")
    private String defaultBucketName;
    @Value("${minio.connectTimeout}")
    private Long connectTimeout;
    @Value("${minio.readTimeout}")
    private Long readTimeout;
    @Value("${minio.writeTimeout}")
    private Long writeTimeout;
    private static MinioClient minioClient;

    /**
     * 1
     * 初始化 MinIO 客户端
     *
     * @PostConstruct要与@Configuration或@Component一起使用
     */
    @PostConstruct
    public void init() {
        try {
            // 创建okHttpClient
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(connectTimeout, TimeUnit.MILLISECONDS)
                    .readTimeout(readTimeout, TimeUnit.MILLISECONDS)
                    .writeTimeout(writeTimeout, TimeUnit.MILLISECONDS)
                    .build();

            this.minioClient = MinioClient.builder()
                    .endpoint(endpoint)
                    .credentials(accessKey, secretKey)
                    .httpClient(okHttpClient)
                    .build();
            log.info("MinIO 客户端初始化成功: {}", endpoint);
        } catch (Exception e) {
            log.error("MinIO 客户端初始化失败", e);
            throw new RuntimeException("MinIO 客户端初始化失败", e);
        }
    }

    /**
     * 1
     * 创建存储桶
     */
    public static boolean createBucket(String bucketName) {
        try {
            boolean exists = bucketExists(bucketName);
            if (!exists) {
                minioClient.makeBucket(MakeBucketArgs.builder()
                        .bucket(bucketName)
                        .build());

                // 设置存储桶策略为公开读
                setBucketPolicy(bucketName, "public");

                log.info("存储桶创建成功: {}", bucketName);
                return true;
            }
            log.warn("存储桶已存在: {}", bucketName);
            return false;
        } catch (Exception e) {
            log.error("创建存储桶失败: {}", bucketName, e);
            return false;
        }
    }

    /**
     * 1
     * 检查存储桶是否存在
     */
    public static boolean bucketExists(String bucketName) {
        try {
            return minioClient.bucketExists(BucketExistsArgs.builder()
                    .bucket(bucketName)
                    .build());
        } catch (Exception e) {
            log.error("检查存储桶是否存在失败: {}", bucketName, e);
            return false;
        }
    }

    /**
     * 1
     * 删除存储桶
     */
    public static boolean removeBucket(String bucketName) {
        try {
            boolean exists = bucketExists(bucketName);
            if (exists) {
                // 先删除桶内所有对象
                deleteAllObjects(bucketName);

                minioClient.removeBucket(RemoveBucketArgs.builder()
                        .bucket(bucketName)
                        .build());
                log.info("存储桶删除成功: {}", bucketName);
                return true;
            }
            return false;
        } catch (Exception e) {
            log.error("删除存储桶失败: {}", bucketName, e);
            return false;
        }
    }

    /**
     * 1
     * 设置存储桶策略
     *
     * @param policyType 策略类型: public, private, readonly
     */
    public static boolean setBucketPolicy(String bucketName, String policyType) {
        try {
            String policyJson = "";
            switch (policyType.toLowerCase()) {
                case "public":
                    // 公共读写
                    policyJson = "{\n" +
                            "  \"Version\": \"2012-10-17\",\n" +
                            "  \"Statement\": [\n" +
                            "    {\n" +
                            "      \"Effect\": \"Allow\",\n" +
                            "      \"Principal\": {\"AWS\": [\"*\"]},\n" +
                            "      \"Action\": [\"s3:GetObject\", \"s3:PutObject\"],\n" +
                            "      \"Resource\": [\"arn:aws:s3:::" + bucketName + "/*\"]\n" +
                            "    }\n" +
                            "  ]\n" +
                            "}";
                    break;
                case "private":
                    // 私有
                    policyJson = "{\n" +
                            "  \"Version\": \"2012-10-17\",\n" +
                            "  \"Statement\": []\n" +
                            "}";
                    break;
                case "readonly":
                    // 公共读私有写
                    policyJson = "{\n" +
                            "  \"Version\": \"2012-10-17\",\n" +
                            "  \"Statement\": [\n" +
                            "    {\n" +
                            "      \"Effect\": \"Allow\",\n" +
                            "      \"Principal\": {\"AWS\": [\"*\"]},\n" +
                            "      \"Action\": [\"s3:GetObject\"],\n" +
                            "      \"Resource\": [\"arn:aws:s3:::" + bucketName + "/*\"]\n" +
                            "    }\n" +
                            "  ]\n" +
                            "}";
                    break;
                default:
                    throw new IllegalArgumentException("不支持的策略类型: " + policyType);
            }

            minioClient.setBucketPolicy(SetBucketPolicyArgs.builder()
                    .bucket(bucketName)
                    .config(policyJson)
                    .build());
            log.info("存储桶策略设置成功: {}-{}", bucketName, policyType);
            return true;
        } catch (Exception e) {
            log.error("设置存储桶策略失败: {}", bucketName, e);
            return false;
        }
    }

    /**
     * 1
     * 文件上传（输入流）
     */
    public static String uploadFile(String bucketName, InputStream inputStream,
                                    String filename, String contentType, long size) {
        try {
            // 如果存储桶不存在则创建
            if (!bucketExists(bucketName)) {
                createBucket(bucketName);
            }

            // 生成唯一的文件名
            String objectName = generateFilename(filename);

            // 设置元数据
            Map<String, String> headers = new HashMap<>();
            headers.put("Content-Type", contentType);

            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .stream(inputStream, size, -1)
                    .contentType(contentType)
                    .headers(headers)
                    .build());

            String url = getFileUrl(bucketName, objectName);
            log.info("文件上传成功: {} -> {}", objectName, url);
            return url;
        } catch (Exception e) {
            log.error("文件上传失败: {}", filename, e);
            throw new RuntimeException("文件上传失败", e);
        }
    }

    /**
     * 1
     * MultipartFile 文件上传
     */
    public static String uploadFile(String bucketName, MultipartFile file) {
        try {
            String originalFilename = file.getOriginalFilename();
            String contentType = file.getContentType();

            InputStream inputStream = file.getInputStream();
            return uploadFile(bucketName, inputStream, originalFilename,
                    contentType, file.getSize());
        } catch (Exception e) {
            log.error("MultipartFile 上传失败", e);
            throw new RuntimeException("文件上传失败", e);
        }
    }

    /**
     * 1
     * 文件下载
     */
    public static InputStream downloadFile(String bucketName, String objectName) {
        try {
            return minioClient.getObject(GetObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .build());
        } catch (Exception e) {
            log.error("文件下载失败: {}/{}", bucketName, objectName, e);
            throw new RuntimeException("文件下载失败", e);
        }
    }

    /**
     * 1
     * 获取文件URL
     */
    public static String getFileUrl(String bucketName, String objectName) {
//        return endpoint + "/" + bucketName + "/" + objectName;
        return "http://124.221.77.154:9001" + "/" + bucketName + "/" + objectName;
    }

    /**1
     * 生成预签名URL（用于临时访问）
     *
     * @param expires 过期时间（分钟）
     */
    public static String getPresignedUrl(String bucketName, String objectName, int expires) {
        try {
            return minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                    .method(Method.GET)
                    .bucket(bucketName)
                    .object(objectName)
                    .expiry(expires, TimeUnit.MINUTES)
                    .build());
        } catch (Exception e) {
            log.error("生成预签名URL失败: {}/{}", bucketName, objectName, e);
            throw new RuntimeException("生成预签名URL失败", e);
        }
    }


    /**
     * 1
     * 删除文件
     */
    public static boolean deleteFile(String bucketName, String objectName) {
        try {
            minioClient.removeObject(RemoveObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .build());
            log.info("文件删除成功: {}/{}", bucketName, objectName);
            return true;
        } catch (Exception e) {
            log.error("文件删除失败: {}/{}", bucketName, objectName, e);
            return false;
        }
    }

    /**
     *
     * 批量删除文件
     */
    public static boolean deleteFiles(String bucketName, List<String> objectNames) {
        try {
            List<DeleteObject> objects = new ArrayList<>();
            for (String objectName : objectNames) {
                objects.add(new DeleteObject(objectName));
            }

            Iterable<Result<DeleteError>> results =
                    minioClient.removeObjects(RemoveObjectsArgs.builder()
                            .bucket(bucketName)
                            .objects(objects)
                            .build());

            for (Result<DeleteError> result : results) {
                DeleteError error = result.get();
                log.error("删除文件失败: {}", error.objectName());
            }

            log.info("批量删除完成，共删除 {} 个文件", objectNames.size());
            return true;
        } catch (Exception e) {
            log.error("批量删除文件失败", e);
            return false;
        }
    }

    /**
     * 删除存储桶内所有文件
     */
    public static void deleteAllObjects(String bucketName) {
        try {
            List<String> objectNames = new ArrayList<>();
            Iterable<Result<Item>> results = minioClient.listObjects(
                    ListObjectsArgs.builder()
                            .bucket(bucketName)
                            .recursive(true)
                            .build()
            );

            for (Result<Item> result : results) {
                Item item = result.get();
                objectNames.add(item.objectName());
            }

            if (!objectNames.isEmpty()) {
                deleteFiles(bucketName, objectNames);
            }
        } catch (Exception e) {
            log.error("删除存储桶内所有文件失败: {}", bucketName, e);
        }
    }

    /**
     * 复制文件
     */
    public static boolean copyFile(String sourceBucket, String sourceObject,
                                   String targetBucket, String targetObject) {
        try {
            minioClient.copyObject(CopyObjectArgs.builder()
                    .source(CopySource.builder()
                            .bucket(sourceBucket)
                            .object(sourceObject)
                            .build())
                    .bucket(targetBucket)
                    .object(targetObject)
                    .build());
            log.info("文件复制成功: {}/{} -> {}/{}",
                    sourceBucket, sourceObject, targetBucket, targetObject);
            return true;
        } catch (Exception e) {
            log.error("文件复制失败", e);
            return false;
        }
    }

    /**
     * 1
     * 获取文件信息
     */
    public static StatObjectResponse getFileInfo(String bucketName, String objectName) {
        try {
            return minioClient.statObject(StatObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .build());
        } catch (Exception e) {
            log.error("获取文件信息失败: {}/{}", bucketName, objectName, e);
            throw new RuntimeException("获取文件信息失败", e);
        }
    }

    /**
     * 1
     * 检查文件是否存在
     */
    public static boolean fileExists(String bucketName, String objectName) {
        try {
            getFileInfo(bucketName, objectName);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 1
     * 获取存储桶文件列表
     */
    public static List<FileItem> listFiles(String bucketName, boolean recursive) {
        List<FileItem> fileList = new ArrayList<>();
        try {
            Iterable<Result<Item>> results = minioClient.listObjects(
                    ListObjectsArgs.builder()
                            .bucket(bucketName)
                            .recursive(recursive)
                            .build()
            );

            for (Result<Item> result : results) {
                Item item = result.get();
                FileItem fileItem = new FileItem();
                fileItem.setObjectName(item.objectName());
                fileItem.setSize(item.size());
                fileItem.setLastModified(item.lastModified());
                fileItem.setIsDir(item.isDir());
                fileItem.setEtag(item.etag());
                fileList.add(fileItem);
            }
        } catch (Exception e) {
            log.error("获取文件列表失败: {}", bucketName, e);
        }
        return fileList;
    }

    /**
     * 1
     * 生成唯一的文件名
     */
    private static String generateFilename(String originalFilename) {
        String uuid = UUID.randomUUID().toString().replace("-", "");
        int index = originalFilename.lastIndexOf(".");
        String extension = "";
        if (index != -1) {
            extension = originalFilename.substring(index);
        }
        return uuid + extension;
    }

    /**
     * 获取文件内容类型
     */
    public static String getContentType(String filename) {
        String contentType = "application/octet-stream";
        String extension = filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();

        switch (extension) {
            case "jpg":
            case "jpeg":
                contentType = "image/jpeg";
                break;
            case "png":
                contentType = "image/png";
                break;
            case "gif":
                contentType = "image/gif";
                break;
            case "pdf":
                contentType = "application/pdf";
                break;
            case "txt":
                contentType = "text/plain";
                break;
            case "doc":
                contentType = "application/msword";
                break;
            case "docx":
                contentType = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
                break;
            case "xls":
                contentType = "application/vnd.ms-excel";
                break;
            case "xlsx":
                contentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
                break;
            case "zip":
                contentType = "application/zip";
                break;
            case "rar":
                contentType = "application/x-rar-compressed";
                break;
        }
        return contentType;
    }

    /**
     * 设置文件标签
     */
    public static boolean setFileTags(String bucketName, String objectName, Map<String, String> tags) {
        try {
            Map<String, String> existingTags = new HashMap<>();

            // 获取现有标签
            try {
                Tags existing = minioClient.getObjectTags(GetObjectTagsArgs.builder()
                        .bucket(bucketName)
                        .object(objectName)
                        .build());
                existingTags.putAll(existing.get());
            } catch (Exception e) {
                // 如果文件没有标签，继续
            }

            // 合并标签
            existingTags.putAll(tags);

            minioClient.setObjectTags(SetObjectTagsArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .tags(existingTags)
                    .build());

            return true;
        } catch (Exception e) {
            log.error("设置文件标签失败: {}/{}", bucketName, objectName, e);
            return false;
        }
    }

    /**
     * 获取文件标签
     */
    public static Map<String, String> getFileTags(String bucketName, String objectName) {
        try {
            Tags tags = minioClient.getObjectTags(GetObjectTagsArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .build());
            return tags.get();
        } catch (Exception e) {
            log.error("获取文件标签失败: {}/{}", bucketName, objectName, e);
            return new HashMap<>();
        }
    }

    /**
     * 格式化文件大小
     */
    private static String formatFileSize(long size) {
        if (size < 1024) {
            return size + " B";
        } else if (size < 1024 * 1024) {
            return String.format("%.2f KB", size / 1024.0);
        } else if (size < 1024 * 1024 * 1024) {
            return String.format("%.2f MB", size / (1024.0 * 1024));
        } else {
            return String.format("%.2f GB", size / (1024.0 * 1024 * 1024));
        }
    }

    /**
     * 文件项类
     */
    @Data
    public static class FileItem {
        private String objectName;
        private Long size;
        private ZonedDateTime lastModified;
        private Boolean isDir;
        private String etag;
    }

    /**
     * 存储桶使用情况类
     */
    @Data
    public static class BucketUsageInfo {
        private String bucketName;
        private Integer fileCount;
        private Long totalSize;
        private String totalSizeFormatted;
    }


}