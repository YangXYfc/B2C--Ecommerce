package com.team.ecommerce.common.controller;

import com.team.ecommerce.common.Result;
import com.team.ecommerce.common.dto.UploadVO;
import com.team.ecommerce.common.service.FileService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

/**
 * 图片上传接口（契约 4.3）：登录用户可调用，返回可访问的图片 URL。
 */
@RestController
@RequestMapping("/api/files")
public class FileController {

    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping("/images")
    public Result<UploadVO> upload(@RequestParam("file") MultipartFile file) {
        String relativePath = fileService.save(file);
        String url = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/upload/")
                .path(relativePath)
                .toUriString();
        return Result.success(new UploadVO(url), "上传成功");
    }
}
