package com.atguigu.examsystem.upload.service.impl;

import cn.hutool.core.io.FileUtil;
import com.atguigu.examsystem.upload.config.MinioConfig;
import com.atguigu.examsystem.upload.service.UploadFileService;
import com.atguigu.examsystem.vo.Result;
import io.minio.*;
import io.minio.http.Method;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class UploadFileServiceImpl implements UploadFileService {

    private final MinioConfig config;
    private final MinioClient minioClient;

    public UploadFileServiceImpl(MinioConfig config,
                                 MinioClient minioClient) {
        this.config = config;
        this.minioClient = minioClient;
    }


    @Override
    public Result<String> uploadFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new RuntimeException("请上传文件");
        }
        String fileName = file.getOriginalFilename();
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        String prefix = fileName.substring(0, fileName.lastIndexOf(".") - 1);
        String newFileName = generateFileName(prefix, suffix);

        return uploadFile(newFileName, file);
    }

    @Override
    public Result<Void> deleteFile(String fileName) {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder().bucket(config.getBucketName()).object(fileName).build());
            log.info("删除文件成功：{}", fileName);
            return Result.ok();
        } catch (Exception e) {
            log.error("删除文件失败：{}", e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    private Result<String> uploadFile(String newFileName, MultipartFile file) {
        if (file.isEmpty() || StringUtils.isEmpty(newFileName)) {
            throw new RuntimeException("文件或者新文件名不能为空");
        }
        String originalFilename = file.getOriginalFilename();
        String fileExtName = FileUtil.extName(originalFilename);
        long fileSize = file.getSize();
        String contentType = file.getContentType();

        // 验证文件类型
        validateFileExtName(fileExtName);

        try {
            InputStream in = file.getInputStream();
            // 构建上传参数
            PutObjectArgs putObjectArgs = PutObjectArgs.builder()
                    .bucket(config.getBucketName())
                    .object(newFileName)
                    .stream(in, fileSize, -1)
                    .contentType(contentType)
                    .build();
            // 上传接口
            ObjectWriteResponse response = minioClient.putObject(putObjectArgs);
            log.info("上传文件成功：bucket={}, object={}, size={}, etag={}",
                    config.getBucketName(), newFileName, fileSize, response.etag());

            return getFileUrl(newFileName);
        } catch (Exception e) {
            throw new RuntimeException("文件上传失败：" + e.getMessage());
        }


    }

    private Result<String> getFileUrl(String newFileName) {
        try {
            String fileUrl = minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .bucket(config.getBucketName())
                            .object(newFileName)
                            .method(Method.GET)
                            .expiry(7, TimeUnit.DAYS)
                            .build());
            return Result.okData(fileUrl);
        } catch (Exception e) {
            log.error("获取文件地址错误：{}", e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    private void validateFileExtName(String fileExtName) {
        List<String> fileExtNameList = Arrays.asList(
                "jpg", "jpeg", "png", "gif", "bmp", // 图片
                "pdf", "doc", "docx", "xls", "ppt", "pptx", // 文档
                "txt", "csv", // 文本
                "mp4", "avi", "mov", // 视频
                "mp3", "wav", "wma", "wmv", "flv" // 音频
        );
        if (!fileExtNameList.contains(fileExtName)) {
            throw new RuntimeException("不支持的文件类型: " + fileExtName);
        }
    }

    /**
     * 生成新文件名称方法
     *
     * @param prefix
     * @param suffix
     * @return
     */
    private String generateFileName(String prefix, String suffix) {
        return prefix + "-" + System.currentTimeMillis() + suffix;
    }
}
