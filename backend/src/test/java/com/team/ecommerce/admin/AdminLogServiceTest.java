package com.team.ecommerce.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team.ecommerce.admin.entity.AdminLog;
import com.team.ecommerce.admin.mapper.AdminLogMapper;
import com.team.ecommerce.admin.service.AdminLogService;
import com.team.ecommerce.security.UserContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AdminLogServiceTest {

    @Mock
    private AdminLogMapper adminLogMapper;

    @Spy
    private ObjectMapper objectMapper = new ObjectMapper();

    @InjectMocks
    private AdminLogService adminLogService;

    @BeforeEach
    void setUp() {
        UserContext.set(new UserContext.LoginUser(1L, "admin", "ADMIN"));
    }

    @AfterEach
    void tearDown() {
        UserContext.clear();
    }

    @Test
    void record_mapsFieldsAndSerializesDetail() {
        Map<String, Object> detail = new LinkedHashMap<>();
        detail.put("action", "approve");
        detail.put("remark", "资料齐全，审核通过");

        adminLogService.record("MERCHANT_AUDIT", "MERCHANT", 3L, detail);

        ArgumentCaptor<AdminLog> captor = ArgumentCaptor.forClass(AdminLog.class);
        verify(adminLogMapper).insert(captor.capture());
        AdminLog log = captor.getValue();
        assertEquals(1L, log.getAdminId());
        assertEquals("MERCHANT_AUDIT", log.getAction());
        assertEquals("MERCHANT", log.getTargetType());
        assertEquals(3L, log.getTargetId());
        assertEquals("{\"action\":\"approve\",\"remark\":\"资料齐全，审核通过\"}", log.getDetail());
        assertNull(log.getIpAddress());
    }

    @Test
    void record_nullDetail_writesNullJson() {
        adminLogService.record("USER_DISABLE", "USER", 4L, null);

        ArgumentCaptor<AdminLog> captor = ArgumentCaptor.forClass(AdminLog.class);
        verify(adminLogMapper).insert(captor.capture());
        assertEquals("USER_DISABLE", captor.getValue().getAction());
        assertNull(captor.getValue().getDetail());
    }
}
