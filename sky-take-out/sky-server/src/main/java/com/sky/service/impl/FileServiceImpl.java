package com.sky.service.impl;

import com.sky.constant.MessageConstant;
import com.sky.service.FileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service
@Slf4j
public class FileServiceImpl implements FileService {

    //本地上传文件的磁盘目录（开发期替代OSS）
    @Value("${sky.upload.dir:D:/sky-take-out-upload/}")
    private String uploadDir;

    @Override
    public String upload(MultipartFile file, HttpServletRequest request) {
        try {
            //原始文件名
            String originalFilename = file.getOriginalFilename();
            //截取原始文件名后缀
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            //构造新的文件名
            String fileName = UUID.randomUUID().toString() + extension;

            //目录不存在则创建
            File dir = new File(uploadDir);
            if (!dir.exists() && !dir.mkdirs()) {
                log.error("创建上传目录失败:{}", uploadDir);
                return null;
            }
            //保存到本地磁盘（NIO写入，避免transferTo对路径解析的容器差异）
            File dest = new File(dir, fileName);
            java.nio.file.Files.copy(file.getInputStream(), dest.toPath());

            //图片超过200K时压缩（小程序体验评分要求图片不超过200K）
            dest = compressImageIfNecessary(dest);

            //只入库相对路径，不拼接具体域名：小程序端按当前baseUrl补全、管理端经nginx代理访问，
            //避免cpolar等内网穿透域名变化导致历史图片全部失效
            return "/upload/" + dest.getName();
        } catch (IOException e) {
            log.error("文件上传失败", e);
            return null;
        }
    }

    /**
     * 图片超过200K时压缩：等比缩放到最大边800像素并转为jpg存储
     */
    private File compressImageIfNecessary(File file) {
        try {
            //非图片或未超过200K不处理
            String name = file.getName().toLowerCase();
            if (file.length() <= 200 * 1024 || !(name.endsWith(".png") || name.endsWith(".jpg") || name.endsWith(".jpeg"))) {
                return file;
            }
            java.awt.image.BufferedImage image = javax.imageio.ImageIO.read(file);
            if (image == null) {
                return file;
            }
            int w = image.getWidth(), h = image.getHeight();
            int max = 800;
            if (w > max || h > max) {
                double scale = (double) max / Math.max(w, h);
                w = (int) (w * scale);
                h = (int) (h * scale);
            }
            java.awt.image.BufferedImage newImage = new java.awt.image.BufferedImage(w, h, java.awt.image.BufferedImage.TYPE_INT_RGB);
            newImage.getGraphics().drawImage(image.getScaledInstance(w, h, java.awt.Image.SCALE_SMOOTH), 0, 0, null);

            //转为jpg文件存储
            File jpgFile = new File(file.getParent(), name.substring(0, name.lastIndexOf(".")) + ".jpg");
            javax.imageio.ImageIO.write(newImage, "jpg", jpgFile);
            file.delete();
            log.info("图片已压缩:{} -> {}", name, jpgFile.getName());
            return jpgFile;
        } catch (Exception e) {
            log.error("图片压缩失败", e);
            return file;
        }
    }
}
