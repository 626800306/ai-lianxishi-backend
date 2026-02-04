package com.atguigu.examsystem.upload.service;

import com.atguigu.examsystem.vo.Result;
import org.springframework.web.multipart.MultipartFile;

public interface UploadFileService {

    /**
     * 上传文件
     * @param file
     * @return
     */
    Result<String> uploadFile(MultipartFile file);

    /**
     * 文件删除
     * @param fileName
     */
    Result<Void> deleteFile(String fileName);
}
