package com.team.ecommerce.auth.service;

import com.team.ecommerce.auth.dto.ChangePasswordRequest;
import com.team.ecommerce.auth.dto.UpdateProfileRequest;
import com.team.ecommerce.auth.dto.UpdateProfileResponse;
import com.team.ecommerce.auth.entity.User;
import com.team.ecommerce.auth.mapper.UserMapper;
import com.team.ecommerce.common.BizException;
import com.team.ecommerce.common.ResultCode;
import com.team.ecommerce.security.UserContext;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 用户资料服务：更新个人资料、修改密码。
 */
@Service
public class UserService {

    private final UserMapper userMapper;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserService(UserMapper userMapper, BCryptPasswordEncoder passwordEncoder) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    /** 1.5 更新自己的昵称/头像/手机号。 */
    @Transactional
    public UpdateProfileResponse updateProfile(UpdateProfileRequest req) {
        Long userId = UserContext.getUserId();

        boolean empty = (req.nickname() == null || req.nickname().isBlank())
                && (req.avatar() == null || req.avatar().isBlank())
                && (req.phone() == null || req.phone().isBlank());
        if (empty) {
            throw new BizException(ResultCode.BAD_REQUEST, "更新内容不能为空");
        }

        if (req.phone() != null && !req.phone().isBlank()) {
            User existing = userMapper.findByPhone(req.phone());
            if (existing != null && !existing.getId().equals(userId)) {
                throw new BizException(ResultCode.BAD_REQUEST, "手机号已被占用");
            }
        }

        User user = userMapper.findById(userId);
        if (user == null) {
            throw new BizException(ResultCode.NOT_FOUND, "用户不存在");
        }

        if (req.nickname() != null) {
            user.setNickname(req.nickname());
        }
        if (req.avatar() != null) {
            user.setAvatar(req.avatar());
        }
        if (req.phone() != null) {
            user.setPhone(req.phone().isBlank() ? null : req.phone());
        }
        userMapper.updateProfile(user);

        return new UpdateProfileResponse(user.getId(), user.getUsername(),
                user.getNickname(), user.getAvatar(), user.getPhone());
    }

    /** 1.6 修改密码：核对旧密码后保存新密码。 */
    @Transactional
    public void changePassword(ChangePasswordRequest req) {
        Long userId = UserContext.getUserId();
        User user = userMapper.findById(userId);
        if (user == null) {
            throw new BizException(ResultCode.NOT_FOUND, "用户不存在");
        }
        if (!passwordEncoder.matches(req.oldPassword(), user.getPassword())) {
            throw new BizException(ResultCode.BAD_REQUEST, "旧密码错误");
        }
        userMapper.updatePassword(userId, passwordEncoder.encode(req.newPassword()));
    }
}
