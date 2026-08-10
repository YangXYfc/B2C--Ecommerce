package com.team.ecommerce.admin.service;

import com.team.ecommerce.admin.dto.AdminUserVO;
import com.team.ecommerce.admin.dto.UserStatusVO;
import com.team.ecommerce.auth.entity.User;
import com.team.ecommerce.auth.mapper.UserMapper;
import com.team.ecommerce.common.BizException;
import com.team.ecommerce.common.PageResult;
import com.team.ecommerce.common.ResultCode;
import com.team.ecommerce.security.UserContext;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 平台用户管理服务（6.1 / 6.2）。
 */
@Service
public class AdminUserService {

    private static final int DEFAULT_SIZE = 10;
    private static final int MAX_SIZE = 100;

    private final UserMapper userMapper;

    public AdminUserService(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    /** 6.1 用户列表：keyword 模糊搜用户名/昵称/手机号，role/status 过滤，分页，不含密码。 */
    public PageResult<AdminUserVO> list(String keyword, String role, Integer status, Integer page, Integer size) {
        int p = page == null || page < 1 ? 1 : page;
        int s = sizeOf(size);
        long total = userMapper.count(keyword, role, status);
        List<AdminUserVO> list = userMapper.findPage(keyword, role, status, (p - 1) * s, s)
                .stream().map(this::toVO).toList();
        return new PageResult<>(total, p, s, list);
    }

    /** 6.2 禁用/启用用户；不能禁用自己。 */
    public UserStatusVO updateStatus(Long id, Integer status) {
        if (status == null || (status != 0 && status != 1)) {
            throw new BizException(ResultCode.BAD_REQUEST, "状态非法");
        }
        User user = userMapper.findById(id);
        if (user == null) {
            throw new BizException(ResultCode.NOT_FOUND, "用户不存在");
        }
        if (status == 0 && id.equals(UserContext.getUserId())) {
            throw new BizException(ResultCode.BAD_REQUEST, "不能禁用自己");
        }
        userMapper.updateStatus(id, status);
        return new UserStatusVO(id, status);
    }

    private AdminUserVO toVO(User u) {
        return new AdminUserVO(u.getId(), u.getUsername(), u.getNickname(), u.getPhone(),
                u.getEmail(), u.getRole(), u.getStatus(), u.getCreatedAt());
    }

    /** size 校验：默认 10，最大 100。 */
    private int sizeOf(Integer size) {
        if (size == null || size < 1) {
            return DEFAULT_SIZE;
        }
        if (size > MAX_SIZE) {
            throw new BizException(ResultCode.BAD_REQUEST, "size 不能超过100");
        }
        return size;
    }
}
