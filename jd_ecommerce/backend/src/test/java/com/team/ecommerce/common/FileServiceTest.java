package com.team.ecommerce.common;

import com.team.ecommerce.common.service.FileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FileServiceTest {

    @TempDir
    Path tempDir;

    @Mock
    private MultipartFile file;

    private FileService fileService;

    @BeforeEach
    void setUp() {
        // FileService 构造参数是 @Value String，无法用 @InjectMocks 注入，改用临时目录直接构造。
        fileService = new FileService(tempDir.toString());
    }

    @Test
    void save_nullFile_throws400() {
        BizException e = assertThrows(BizException.class, () -> fileService.save(null));
        assertEquals(400, e.getCode());
    }

    @Test
    void save_emptyFile_throws400() {
        when(file.isEmpty()).thenReturn(true);

        BizException e = assertThrows(BizException.class, () -> fileService.save(file));
        assertEquals(400, e.getCode());
    }

    @Test
    void save_unsupportedExt_throws400() {
        when(file.isEmpty()).thenReturn(false);
        when(file.getOriginalFilename()).thenReturn("a.gif");

        BizException e = assertThrows(BizException.class, () -> fileService.save(file));
        assertEquals(400, e.getCode());
    }

    @Test
    void save_oversize_throws400() {
        when(file.isEmpty()).thenReturn(false);
        when(file.getOriginalFilename()).thenReturn("a.jpg");
        when(file.getSize()).thenReturn(5L * 1024 * 1024 + 1);

        BizException e = assertThrows(BizException.class, () -> fileService.save(file));
        assertEquals(400, e.getCode());
    }

    @Test
    void save_success_writesFileToDisk() throws IOException {
        MultipartFile real = new MockMultipartFile("file", "a.jpg", "image/jpeg", new byte[]{1, 2, 3});

        String relative = fileService.save(real);

        String yyyyMMdd = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        assertTrue(relative.startsWith(yyyyMMdd + "/"), relative);
        assertTrue(relative.endsWith(".jpg"), relative);
        Path saved = tempDir.resolve(relative);
        assertTrue(Files.exists(saved), "文件应已写入磁盘");
    }

    @Test
    void save_transferIoError_throws500() throws IOException {
        when(file.isEmpty()).thenReturn(false);
        when(file.getOriginalFilename()).thenReturn("a.jpg");
        when(file.getSize()).thenReturn(1024L);
        doThrow(new IOException("disk full")).when(file).transferTo(any(Path.class));

        BizException e = assertThrows(BizException.class, () -> fileService.save(file));
        assertEquals(500, e.getCode());
    }
}
