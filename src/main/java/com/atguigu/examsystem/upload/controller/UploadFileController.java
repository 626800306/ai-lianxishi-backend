package com.atguigu.examsystem.upload.controller;

import cn.hutool.core.io.IoUtil;
import com.atguigu.examsystem.upload.service.UploadFileService;
import com.atguigu.examsystem.util.MinioUtil;
import com.atguigu.examsystem.vo.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;

@Tag(name = "文件管理", description = "文件接口")
@RestController
@RequestMapping("file")
@Slf4j
public class UploadFileController {

    private final UploadFileService uploadFileService;

    public UploadFileController(UploadFileService uploadFileService) {
        this.uploadFileService = uploadFileService;
    }


    @Operation(summary = "上传文件", description = "上传文件")
    @PostMapping("/upload")
    public Result<String> uploadFile(
            @Parameter(description = "文件")
            @RequestParam("file") MultipartFile file) {
        Result<String> res = uploadFileService.uploadFile(file);
        return res;
    }

    @Operation(summary = "删除文件", description = "删除文件")
    @PostMapping("/delete")
    public Result deleteFile(
            @Parameter(description = "文件名")
            @RequestParam("fileName") String fileName) {
        Result<Void> res = uploadFileService.deleteFile(fileName);
        return res;
    }


    @Operation(summary = "检查存储桶是否存在", description = "检查存储桶是否存在")
    @PostMapping("/existsBucket")
    public Result existsBucket(@RequestParam String bucketName) {
        boolean exists = MinioUtil.bucketExists(bucketName);
        return Result.okData(exists);

    }

    @Operation(summary = "创建存储桶", description = "创建存储桶")
    @PostMapping("/createBucket")
    public Result<Void> createBucket(@RequestParam String bucketName) {
        MinioUtil.createBucket(bucketName);
        return Result.okMsg("创建成功");
    }

    @PostMapping("/removeBucket")
    @Operation(summary = "删除存储桶", description = "删除存储桶")
    public Result<Void> removeBucket(@RequestParam String bucketName) {
        MinioUtil.removeBucket(bucketName);
        return Result.okMsg("删除成功");
    }

    @PostMapping("/uploadFile")
    @Operation(summary = "上传文件到指定存储桶", description = "上传文件到指定存储桶")
    public Result<String> uploadFile(@RequestParam String bucketName,
                                     @RequestParam MultipartFile file) {
        String url = MinioUtil.uploadFile(bucketName, file);
        return Result.okData(url);
    }

    @GetMapping("/downloadFile")
    @Operation(summary = "下载文件", description = "下载文件")
    public Result<Void> downloadFile(HttpServletResponse response,
                                     @RequestParam("bucketName") String bucketName,
                                     @RequestParam("objectName") String objectName) {
        try {
            InputStream in = MinioUtil.downloadFile(bucketName, objectName);
            // 设置响应头
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment; filename=\"" + objectName + "\"");
            // 将输入流写入到响应流中
            IoUtil.copy(in, response.getOutputStream());
            response.flushBuffer();
        } catch (Exception e) {
            throw new RuntimeException("文件下载失败", e);
        }
        return Result.okMsg("下载成功");
    }

    @PostMapping("/deleteFile")
    @Operation(summary = "删除文件", description = "删除文件")
    public Result<String> deleteFile(@RequestParam String bucketName,
                                     @RequestParam String ObjectName) {
        MinioUtil.deleteFile(bucketName, ObjectName);
        return Result.okMsg("删除成功");
    }

    @PostMapping("/deleteAllObjects")
    @Operation(summary = "删除桶内所有文件", description = "删除桶内所有文件")
    public Result<Void> deleteAllObjects(@RequestParam String bucketName) {
        MinioUtil.deleteAllObjects(bucketName);
        return Result.okMsg("删除桶内所有文件成功");
    }

    @PostMapping("/listFiles")
    @Operation(summary = "列出文件", description = "列出文件")
    public Result<List<MinioUtil.FileItem>> listFiles(@RequestParam String bucketName,
                                                      @RequestParam boolean recursive) {
        List<MinioUtil.FileItem> fileItems = MinioUtil.listFiles(bucketName, recursive);
        return Result.okData(fileItems);
    }


    @PostMapping("/fileExists")
    @Operation(summary = "文件是否存在", description = "文件是否存在")
    public Result<Boolean> fileExists(@RequestParam String bucketName,
                                      @RequestParam String objectName) {
        boolean exists = MinioUtil.fileExists(bucketName, objectName);
        return Result.okData(exists);
    }


    @Operation(summary = "生成预签名URL", description = "生成预签名URL")
    @PostMapping("/getPresignedUrl")
    public Result<String> getPresignedUrl(@RequestParam String bucketName,
                                          @RequestParam String objectName,
                                          @RequestParam int minutes) {
        String url = MinioUtil.getPresignedUrl(bucketName, objectName, minutes);
        return Result.okData(url);
    }
}
