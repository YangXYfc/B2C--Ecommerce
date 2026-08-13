package com.team.ecommerce.auth.service;

import com.team.ecommerce.auth.dto.LoginRequest;
import com.team.ecommerce.auth.dto.LoginResponse;
import com.team.ecommerce.auth.dto.MerchantApplyRequest;
import com.team.ecommerce.auth.dto.MerchantApplyResponse;
import com.team.ecommerce.auth.dto.MerchantVO;
import com.team.ecommerce.auth.dto.ProfileVO;
import com.team.ecommerce.auth.dto.RegisterRequest;
import com.team.ecommerce.auth.dto.RegisterResponse;
import com.team.ecommerce.auth.dto.UserVO;
import com.team.ecommerce.auth.entity.Merchant;
import com.team.ecommerce.auth.entity.User;
import com.team.ecommerce.auth.mapper.MerchantMapper;
import com.team.ecommerce.auth.mapper.UserMapper;
import com.team.ecommerce.common.BizException;
import com.team.ecommerce.common.ResultCode;
import com.team.ecommerce.security.JwtUtil;
import com.team.ecommerce.security.UserContext;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 账号服务：注册、登录、商家入驻申请、当前用户资料。
 */
@Service
public class AuthService {

    private final UserMapper userMapper;
    private final MerchantMapper merchantMapper;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(UserMapper userMapper, MerchantMapper merchantMapper,
                       BCryptPasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userMapper = userMapper;
        this.merchantMapper = merchantMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    /** 1.1 注册：创建消费者账号，用户名/手机号唯一。 */
    @Transactional
    public RegisterResponse register(RegisterRequest req) {
        if (userMapper.findByUsername(req.username()) != null) {
            throw new BizException(ResultCode.BAD_REQUEST, "用户名已存在");
        }
        String phone = blankToNull(req.phone());
        if (phone != null && userMapper.findByPhone(phone) != null) {
            throw new BizException(ResultCode.BAD_REQUEST, "手机号已被注册");
        }

        String nickname = (req.nickname() == null || req.nickname().isBlank()) ? req.username() : req.nickname();

        User user = new User();
        user.setUsername(req.username());
        user.setPassword(passwordEncoder.encode(req.password()));
        user.setPhone(phone);
        user.setEmail(req.email());
        user.setNickname(nickname);
        user.setGender(req.gender() == null ? 0 : req.gender());
        user.setStatus(1);
        user.setRole("USER");
        userMapper.insert(user);

        return new RegisterResponse(user.getId(), user.getUsername(), user.getNickname(), user.getPhone(), user.getRole());
    }

    /** 1.2 登录：校验密码，返回 JWT 与用户信息。 */
    public LoginResponse login(LoginRequest req) {
        User user = userMapper.findByUsername(req.username());
        if (user == null || !passwordEncoder.matches(req.password(), user.getPassword())) {
            throw new BizException(ResultCode.UNAUTHORIZED, "用户名或密码错误");
        }
        if (user.getStatus() != null && user.getStatus() == 0) {
            throw new BizException(ResultCode.FORBIDDEN, "账号已被禁用");
        }

        String token = jwtUtil.generateToken(user.getId(), user.getRole());
        UserVO userVO = new UserVO(user.getId(), user.getUsername(), user.getNickname(),
                user.getAvatar(), user.getRole(), user.getStatus());
        return new LoginResponse(token, userVO);
    }

    /** 1.4 当前登录者资料；MERCHANT 角色附带店铺信息。 */
    public ProfileVO profile() {
        Long userId = UserContext.getUserId();
        User user = userMapper.findById(userId);
        if (user == null) {
            throw new BizException(ResultCode.NOT_FOUND, "用户不存在");
        }

        MerchantVO merchantVO = null;
        if ("MERCHANT".equals(user.getRole())) {
            Merchant merchant = merchantMapper.findByUserId(userId);
            if (merchant != null) {
                merchantVO = new MerchantVO(merchant.getId(), merchant.getShopName(),
                        merchant.getShopLogo(), merchant.getAuditStatus());
            }
        }

        return new ProfileVO(user.getId(), user.getUsername(), user.getNickname(), user.getPhone(),
                user.getEmail(), user.getAvatar(), user.getGender(), user.getRole(), user.getStatus(), merchantVO);
    }

    /** 1.3 商家入驻申请：仅 USER 角色，生成待审核商家记录。 */
    @Transactional
    public MerchantApplyResponse merchantApply(MerchantApplyRequest req) {
        Long userId = UserContext.getUserId();
        if (merchantMapper.findByUserId(userId) != null) {
            throw new BizException(ResultCode.BAD_REQUEST, "已提交过入驻申请或已是商家");
        }
        User user = userMapper.findById(userId);
        if (user == null || !"USER".equals(user.getRole())) {
            throw new BizException(ResultCode.FORBIDDEN, "仅普通用户可申请入驻");
        }

        Merchant merchant = new Merchant();
        merchant.setUserId(userId);
        merchant.setShopName(req.shopName());
        merchant.setShopLogo(req.shopLogo());
        merchant.setDescription(req.description());
        merchant.setContactPhone(req.contactPhone());
        merchant.setStatus(0);
        merchant.setAuditStatus(0);
        merchantMapper.insert(merchant);

        return new MerchantApplyResponse(merchant.getId(), merchant.getAuditStatus());
    }

    private String blankToNull(String s) {
        return (s == null || s.isBlank()) ? null : s;
    }
}
