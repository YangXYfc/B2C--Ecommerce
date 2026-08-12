package com.team.ecommerce.api;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static org.hamcrest.Matchers.matchesPattern;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

/**
 * 图片上传接口集成测试（契约 4.3）。
 * 上传会真实落盘（事务无法回滚），因此 {@link AfterEach} 清理本次测试产生的文件。
 */
class FileUploadApiTest extends AbstractApiTest {

    /** URL 形如 http://localhost/upload/20260812/<32位hex>.png */
    private static final Pattern URL_PATTERN =
            Pattern.compile("^http://localhost/upload/\\d{8}/[0-9a-f]{32}\\.png$");

    /** 记录本次测试创建的文件相对路径，@AfterEach 删除。 */
    private final List<String> created = new ArrayList<>();

    @AfterEach
    void cleanupUploadedFiles() throws IOException {
        for (String rel : created) {
            Files.deleteIfExists(Paths.get("upload", rel));
        }
        created.clear();
    }

    @Test
    void upload_success_returnsDateDirAndHexFilename() throws Exception {
        MvcResult result = expectOk(doUpload(tokenOf(MERCHANT1), image("a.png")))
                .andExpect(jsonPath("$.message").value("上传成功"))
                .andExpect(jsonPath("$.data.url").value(matchesPattern(URL_PATTERN)))
                .andReturn();
        track(result);
    }

    @Test
    void upload_wrongExtension_400() throws Exception {
        expectError(doUpload(tokenOf(MERCHANT1), image("a.txt")), 400);
    }

    @Test
    void upload_emptyFile_400() throws Exception {
        expectError(doUpload(tokenOf(MERCHANT1),
                new MockMultipartFile("file", "a.png", MediaType.IMAGE_PNG_VALUE, new byte[0])), 400);
    }

    @Test
    void upload_noToken_401() throws Exception {
        expectError(doUpload(null, image("a.png")), 401);
    }

    /** 上传后记录相对路径，便于 @AfterEach 清理。 */
    private void track(MvcResult result) {
        String url = readJson(result, "$.data.url");
        created.add(url.substring(url.indexOf("/upload/") + "/upload/".length()));
    }

    private MockMultipartFile image(String filename) {
        // FileService 只校验扩展名与大小，不解析图片内容，字节内容随意即可。
        return new MockMultipartFile("file", filename, MediaType.IMAGE_PNG_VALUE, new byte[]{1, 2, 3});
    }

    private ResultActions doUpload(String token, MockMultipartFile file) throws Exception {
        MockMultipartHttpServletRequestBuilder b = multipart("/api/files/images").file(file);
        if (token != null) {
            b.header("Authorization", "Bearer " + token);
        }
        return mockMvc.perform(b);
    }
}
