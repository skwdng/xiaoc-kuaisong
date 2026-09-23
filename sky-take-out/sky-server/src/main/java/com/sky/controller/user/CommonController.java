package com.sky.controller.user;

import com.sky.constant.MessageConstant;
import com.sky.result.Result;
import com.sky.service.FileService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

@RestController("userCommonController")
@RequestMapping("/user/common")
@Tag(name = "C端通用接口")
@Slf4j
public class CommonController {

    @Autowired
    private FileService fileService;

    /**
     * C端文件上传（用户头像等）
     * @param file
     * @param request
     * @return
     */
    @PostMapping("/upload")
    @Operation(summary = "文件上传")
    public Result<String> upload(MultipartFile file, HttpServletRequest request) {
        log.info("C端文件上传:{}", file);
        String filePath = fileService.upload(file, request);
        return filePath == null ? Result.error(MessageConstant.UPLOAD_FAILED) : Result.success(filePath);
    }
}
