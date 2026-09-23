package com.sky.service;

import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

public interface FileService {

    /**
     * 上传文件，返回可访问的文件URL
     * @param file
     * @param request
     * @return
     */
    String upload(MultipartFile file, HttpServletRequest request);
}
