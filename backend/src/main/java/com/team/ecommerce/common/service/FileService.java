package com.team.ecommerce.common.service;

import com.team.ecommerce.common.BizException;
import com.team.ecommerce.common.ResultCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

/**
 * 图片上传服务（契约 4.3）：校验类型与大小后存入上传目录，返回相对路径。
 */
@Service
public class FileService {

    private static final long MAX_SIZE = 5L * 1024 * 1024;
    private static final Set<String> ALLOWED_EXT = Set.of("jpg", "jpeg", "png", "webp");

    private final Path uploadDir;

    public FileService(@Value("${upload.dir:upload/}") String uploadDir) {
        this.uploadDir = Path.of(uploadDir).toAbsolutePath().normalize();
    }

    /**
     * 保存图片文件，返回相对路径（如 {@code 20260810/xxx.jpg}），由调用方拼完整 URL。
     */
    public String save(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BizException(ResultCode.BAD_REQUEST, "文件不能为空");
        }
        String ext = extensionOf(file);
        if (!ALLOWED_EXT.contains(ext)) {
            throw new BizException(ResultCode.BAD_REQUEST, "仅支持 jpg/png/webp 格式");
        }
        if (file.getSize() > MAX_SIZE) {
            throw new BizException(ResultCode.BAD_REQUEST, "文件大小不能超过5MB");
        }
        String yyyyMMdd = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        Path dir = uploadDir.resolve(yyyyMMdd);
        try {
            Files.createDirectories(dir);
            String filename = UUID.randomUUID().toString().replace("-", "") + "." + ext;
            file.transferTo(dir.resolve(filename));
            return yyyyMMdd + "/" + filename;
        } catch (IOException e) {
            throw new BizException(ResultCode.INTERNAL_SERVER_ERROR, "文件保存失败");
        }
    }

    private String extensionOf(MultipartFile file) {
        String original = file.getOriginalFilename();
        String ext = original != null && original.contains(".")
                ? original.substring(original.lastIndexOf('.') + 1) : null;
        return ext == null ? "" : ext.toLowerCase(Locale.ROOT);
    }
}
