package com.team.ecommerce.auth;

import com.team.ecommerce.auth.dto.LoginRequest;
import com.team.ecommerce.auth.dto.LoginResponse;
import com.team.ecommerce.auth.dto.MerchantApplyRequest;
import com.team.ecommerce.auth.dto.MerchantApplyResponse;
import com.team.ecommerce.auth.dto.ProfileVO;
import com.team.ecommerce.auth.dto.RegisterRequest;
import com.team.ecommerce.auth.dto.RegisterResponse;
import com.team.ecommerce.auth.entity.Merchant;
import com.team.ecommerce.auth.entity.User;
import com.team.ecommerce.auth.mapper.MerchantMapper;
import com.team.ecommerce.auth.mapper.UserMapper;
import com.team.ecommerce.auth.service.AuthService;
import com.team.ecommerce.common.BizException;
import com.team.ecommerce.security.JwtUtil;
import com.team.ecommerce.security.UserContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserMapper userMapper;
    @Mock
    private MerchantMapper merchantMapper;
    @Mock
    private BCryptPasswordEncoder passwordEncoder;
    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthService authService;

    private User user(long id, String username, String role) {
        User u = new User();
        u.setId(id);
        u.setUsername(username);
        u.setNickname(username);
        u.setRole(role);
        u.setStatus(1);
        return u;
    }

    // ---- 注册 ----

    @Test
    void register_duplicateUsername_throws400() {
        when(userMapper.findByUsername("user1")).thenReturn(user(4L, "user1", "USER"));
        RegisterRequest req = new RegisterRequest("user1", "123456", null, null, null, null);

        BizException ex = assertThrows(BizException.class, () -> authService.register(req));
        assertEquals(400, ex.getCode());
        verify(userMapper, never()).insert(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void register_phoneTaken_throws400() {
        when(userMapper.findByUsername("user_new")).thenReturn(null);
        when(userMapper.findByPhone("13800000004")).thenReturn(user(4L, "user1", "USER"));
        RegisterRequest req = new RegisterRequest("user_new", "123456", "13800000004", null, null, null);

        BizException ex = assertThrows(BizException.class, () -> authService.register(req));
        assertEquals(400, ex.getCode());
    }

    @Test
    void register_success_returnsResponse() {
        when(userMapper.findByUsername("user4")).thenReturn(null);
        when(userMapper.findByPhone("13800000008")).thenReturn(null);
        when(passwordEncoder.encode("123456")).thenReturn("$2a$10$hash");
        when(userMapper.insert(org.mockito.ArgumentMatchers.any(User.class))).thenAnswer(inv -> {
            inv.<User>getArgument(0).setId(8L);
            return 1;
        });
        RegisterRequest req = new RegisterRequest("user4", "123456", "13800000008", "u4@jd.com", "赵六", 1);

        RegisterResponse resp = authService.register(req);

        assertEquals(8L, resp.id());
        assertEquals("user4", resp.username());
        assertEquals("赵六", resp.nickname());
        assertEquals("USER", resp.role());
    }

    // ---- 登录 ----

    @Test
    void login_success_returnsTokenAndUser() {
        User u = user(4L, "user1", "USER");
        u.setPassword("$2a$10$stored");
        when(userMapper.findByUsername("user1")).thenReturn(u);
        when(passwordEncoder.matches("123456", "$2a$10$stored")).thenReturn(true);
        when(jwtUtil.generateToken(4L, "USER")).thenReturn("tok");

        LoginResponse resp = authService.login(new LoginRequest("user1", "123456"));

        assertEquals("tok", resp.token());
        assertEquals("USER", resp.user().role());
        assertEquals(4L, resp.user().id());
    }

    @Test
    void login_wrongPassword_throws401() {
        User u = user(4L, "user1", "USER");
        u.setPassword("$2a$10$stored");
        when(userMapper.findByUsername("user1")).thenReturn(u);
        when(passwordEncoder.matches("wrong", "$2a$10$stored")).thenReturn(false);

        BizException ex = assertThrows(BizException.class,
                () -> authService.login(new LoginRequest("user1", "wrong")));
        assertEquals(401, ex.getCode());
    }

    @Test
    void login_disabled_throws403() {
        User u = user(4L, "user1", "USER");
        u.setPassword("$2a$10$stored");
        u.setStatus(0);
        when(userMapper.findByUsername("user1")).thenReturn(u);
        when(passwordEncoder.matches("123456", "$2a$10$stored")).thenReturn(true);

        BizException ex = assertThrows(BizException.class,
                () -> authService.login(new LoginRequest("user1", "123456")));
        assertEquals(403, ex.getCode());
    }

    // ---- 商家入驻 ----

    @Test
    void merchantApply_existingMerchant_throws400() {
        UserContext.set(new UserContext.LoginUser(2L, "merchant1", "MERCHANT"));
        try {
            when(merchantMapper.findByUserId(2L)).thenReturn(new Merchant());
            BizException ex = assertThrows(BizException.class,
                    () -> authService.merchantApply(new MerchantApplyRequest("新店", "13900000000", null, null)));
            assertEquals(400, ex.getCode());
        } finally {
            UserContext.clear();
        }
    }

    @Test
    void merchantApply_nonUserRole_throws403() {
        UserContext.set(new UserContext.LoginUser(1L, "admin", "ADMIN"));
        try {
            when(merchantMapper.findByUserId(1L)).thenReturn(null);
            when(userMapper.findById(1L)).thenReturn(user(1L, "admin", "ADMIN"));
            BizException ex = assertThrows(BizException.class,
                    () -> authService.merchantApply(new MerchantApplyRequest("新店", "13900000000", null, null)));
            assertEquals(403, ex.getCode());
        } finally {
            UserContext.clear();
        }
    }

    @Test
    void merchantApply_success_returnsPending() {
        UserContext.set(new UserContext.LoginUser(8L, "user_new", "USER"));
        try {
            when(merchantMapper.findByUserId(8L)).thenReturn(null);
            when(userMapper.findById(8L)).thenReturn(user(8L, "user_new", "USER"));
            when(merchantMapper.insert(org.mockito.ArgumentMatchers.any(Merchant.class))).thenAnswer(inv -> {
                inv.<Merchant>getArgument(0).setId(4L);
                return 1;
            });

            MerchantApplyResponse resp = authService.merchantApply(
                    new MerchantApplyRequest("数码新店", "13900000000", "主营手机配件", "https://img.jd-demo.com/shop/logo4.png"));

            assertEquals(4L, resp.merchantId());
            assertEquals(0, resp.auditStatus());
        } finally {
            UserContext.clear();
        }
    }

    // ---- 个人资料 ----

    @Test
    void profile_merchantRole_returnsNestedMerchant() {
        UserContext.set(new UserContext.LoginUser(2L, "merchant1", "MERCHANT"));
        try {
            User u = user(2L, "merchant1", "MERCHANT");
            when(userMapper.findById(2L)).thenReturn(u);
            Merchant m = new Merchant();
            m.setId(1L);
            m.setShopName("数码旗舰店");
            m.setShopLogo("https://img.jd-demo.com/shop/logo1.png");
            m.setAuditStatus(1);
            when(merchantMapper.findByUserId(2L)).thenReturn(m);

            ProfileVO vo = authService.profile();

            assertEquals("MERCHANT", vo.role());
            assertEquals(1L, vo.merchant().id());
            assertEquals("数码旗舰店", vo.merchant().shopName());
            assertEquals(1, vo.merchant().auditStatus());
        } finally {
            UserContext.clear();
        }
    }

    @Test
    void profile_userRole_merchantNull() {
        UserContext.set(new UserContext.LoginUser(4L, "user1", "USER"));
        try {
            when(userMapper.findById(4L)).thenReturn(user(4L, "user1", "USER"));

            ProfileVO vo = authService.profile();

            assertNull(vo.merchant());
        } finally {
            UserContext.clear();
        }
    }
}
