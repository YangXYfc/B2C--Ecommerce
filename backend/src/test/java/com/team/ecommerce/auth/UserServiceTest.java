package com.team.ecommerce.auth;

import com.team.ecommerce.auth.dto.ChangePasswordRequest;
import com.team.ecommerce.auth.dto.UpdateProfileRequest;
import com.team.ecommerce.auth.dto.UpdateProfileResponse;
import com.team.ecommerce.auth.entity.User;
import com.team.ecommerce.auth.mapper.UserMapper;
import com.team.ecommerce.auth.service.UserService;
import com.team.ecommerce.common.BizException;
import com.team.ecommerce.security.UserContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserMapper userMapper;
    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User user(long id, String username, String phone) {
        User u = new User();
        u.setId(id);
        u.setUsername(username);
        u.setNickname(username);
        u.setPhone(phone);
        u.setPassword("$2a$10$stored");
        return u;
    }

    @Test
    void changePassword_wrongOldPassword_throws400() {
        UserContext.set(new UserContext.LoginUser(4L, "user1", "USER"));
        try {
            when(userMapper.findById(4L)).thenReturn(user(4L, "user1", "13800000004"));
            when(passwordEncoder.matches("wrong", "$2a$10$stored")).thenReturn(false);

            BizException ex = assertThrows(BizException.class,
                    () -> userService.changePassword(new ChangePasswordRequest("wrong", "654321")));
            assertEquals(400, ex.getCode());
        } finally {
            UserContext.clear();
        }
    }

    @Test
    void changePassword_success_updatesPassword() {
        UserContext.set(new UserContext.LoginUser(4L, "user1", "USER"));
        try {
            when(userMapper.findById(4L)).thenReturn(user(4L, "user1", "13800000004"));
            when(passwordEncoder.matches("123456", "$2a$10$stored")).thenReturn(true);
            when(passwordEncoder.encode("654321")).thenReturn("$2a$10$newHash");

            userService.changePassword(new ChangePasswordRequest("123456", "654321"));

            verify(userMapper).updatePassword(4L, "$2a$10$newHash");
        } finally {
            UserContext.clear();
        }
    }

    @Test
    void updateProfile_allEmpty_throws400() {
        UserContext.set(new UserContext.LoginUser(4L, "user1", "USER"));
        try {
            BizException ex = assertThrows(BizException.class,
                    () -> userService.updateProfile(new UpdateProfileRequest(null, null, null)));
            assertEquals(400, ex.getCode());
        } finally {
            UserContext.clear();
        }
    }

    @Test
    void updateProfile_phoneTakenByOther_throws400() {
        UserContext.set(new UserContext.LoginUser(4L, "user1", "USER"));
        try {
            when(userMapper.findByPhone("13800000005")).thenReturn(user(5L, "user2", "13800000005"));

            BizException ex = assertThrows(BizException.class,
                    () -> userService.updateProfile(new UpdateProfileRequest(null, null, "13800000005")));
            assertEquals(400, ex.getCode());
        } finally {
            UserContext.clear();
        }
    }

    @Test
    void updateProfile_success_mergesFields() {
        UserContext.set(new UserContext.LoginUser(4L, "user1", "USER"));
        try {
            User current = user(4L, "user1", "13800000004");
            when(userMapper.findById(4L)).thenReturn(current);

            UpdateProfileResponse resp = userService.updateProfile(
                    new UpdateProfileRequest("张三丰", "https://img.jd-demo.com/avatar/new.jpg", null));

            assertEquals("张三丰", resp.nickname());
            assertEquals("https://img.jd-demo.com/avatar/new.jpg", resp.avatar());
            assertEquals("13800000004", resp.phone());
            verify(userMapper).updateProfile(current);
        } finally {
            UserContext.clear();
        }
    }
}
