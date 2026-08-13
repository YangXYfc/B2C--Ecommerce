package com.team.ecommerce.admin;

import com.team.ecommerce.admin.dto.AdminUserVO;
import com.team.ecommerce.admin.dto.UserStatusVO;
import com.team.ecommerce.admin.service.AdminLogService;
import com.team.ecommerce.admin.service.AdminUserService;
import com.team.ecommerce.auth.entity.User;
import com.team.ecommerce.auth.mapper.UserMapper;
import com.team.ecommerce.common.BizException;
import com.team.ecommerce.common.PageResult;
import com.team.ecommerce.security.UserContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminUserServiceTest {

    @Mock
    private UserMapper userMapper;

    @Mock
    private AdminLogService adminLogService;

    @InjectMocks
    private AdminUserService adminUserService;

    @BeforeEach
    void setUp() {
        UserContext.set(new UserContext.LoginUser(1L, "admin", "ADMIN"));
    }

    @AfterEach
    void tearDown() {
        UserContext.clear();
    }

    private User user(long id, String username, String role, int status) {
        User u = new User();
        u.setId(id);
        u.setUsername(username);
        u.setNickname("昵称" + id);
        u.setPhone("1380000000" + id);
        u.setEmail(username + "@jd-demo.com");
        u.setRole(role);
        u.setStatus(status);
        return u;
    }

    @Test
    void list_pagination_mapsRows() {
        when(userMapper.count(eq("user"), eq("USER"), eq(1))).thenReturn(2L);
        when(userMapper.findPage(eq("user"), eq("USER"), eq(1), eq(0), eq(10)))
                .thenReturn(List.of(user(4, "user1", "USER", 1), user(5, "user2", "USER", 1)));

        PageResult<AdminUserVO> result = adminUserService.list("user", "USER", 1, 1, 10);

        assertEquals(2L, result.total());
        assertEquals(1, result.page());
        assertEquals(10, result.size());
        assertEquals(2, result.list().size());
        assertEquals("user1", result.list().get(0).username());
        assertEquals("13800000004", result.list().get(0).phone());
        assertEquals("USER", result.list().get(0).role());
    }

    @Test
    void list_sizeOverMax_throwsBadRequest() {
        BizException e = assertThrows(BizException.class,
                () -> adminUserService.list(null, null, null, 1, 101));
        assertEquals(400, e.getCode());
    }

    @Test
    void updateStatus_exists_updates() {
        when(userMapper.findById(4L)).thenReturn(user(4, "user1", "USER", 1));
        when(userMapper.updateStatus(4L, 0)).thenReturn(1);

        UserStatusVO vo = adminUserService.updateStatus(4L, 0);

        assertEquals(4L, vo.id());
        assertEquals(0, vo.status());
        verify(userMapper).updateStatus(4L, 0);
        verify(adminLogService).record(eq("USER_DISABLE"), eq("USER"), eq(4L), any());
    }

    @Test
    void updateStatus_notExists_throwsNotFound() {
        when(userMapper.findById(999L)).thenReturn(null);

        BizException e = assertThrows(BizException.class, () -> adminUserService.updateStatus(999L, 0));
        assertEquals(404, e.getCode());
    }

    @Test
    void updateStatus_self_throwsBadRequest() {
        when(userMapper.findById(1L)).thenReturn(user(1, "admin", "ADMIN", 1));

        BizException e = assertThrows(BizException.class, () -> adminUserService.updateStatus(1L, 0));
        assertEquals(400, e.getCode());
    }

    @Test
    void updateStatus_invalidStatus_throwsBadRequest() {
        BizException e = assertThrows(BizException.class, () -> adminUserService.updateStatus(4L, 5));
        assertEquals(400, e.getCode());
    }
}
