package com.team.ecommerce.admin.service;

import com.team.ecommerce.admin.dto.AdminMerchantDetailVO;
import com.team.ecommerce.admin.dto.AdminMerchantPendingVO;
import com.team.ecommerce.admin.dto.MerchantAuditVO;
import com.team.ecommerce.auth.entity.Merchant;
import com.team.ecommerce.auth.entity.User;
import com.team.ecommerce.auth.mapper.MerchantMapper;
import com.team.ecommerce.auth.mapper.UserMapper;
import com.team.ecommerce.common.BizException;
import com.team.ecommerce.common.ResultCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 平台商家审核服务（7.1 / 7.2 / 7.3）。
 */
@Service
public class AdminMerchantService {

    private static final int AUDIT_PENDING = 0;
    private static final int AUDIT_APPROVED = 1;
    private static final int AUDIT_REJECTED = 2;

    private final MerchantMapper merchantMapper;
    private final UserMapper userMapper;
    private final AdminLogService adminLogService;

    public AdminMerchantService(MerchantMapper merchantMapper, UserMapper userMapper,
                                AdminLogService adminLogService) {
        this.merchantMapper = merchantMapper;
        this.userMapper = userMapper;
        this.adminLogService = adminLogService;
    }

    /** 7.1 待审核商家列表（无分页，纯数组）。 */
    public List<AdminMerchantPendingVO> listPending() {
        return merchantMapper.findPending().stream().map(this::toPendingVO).toList();
    }

    /** 7.2 商家申请详情。 */
    public AdminMerchantDetailVO detail(Long id) {
        Merchant merchant = merchantMapper.findById(id);
        if (merchant == null) {
            throw new BizException(ResultCode.NOT_FOUND, "商家申请不存在");
        }
        User applicant = userMapper.findById(merchant.getUserId());
        return new AdminMerchantDetailVO(
                merchant.getId(), merchant.getUserId(), merchant.getShopName(),
                merchant.getShopLogo(), merchant.getDescription(), merchant.getContactPhone(),
                merchant.getAuditStatus(), merchant.getAuditRemark(),
                new AdminMerchantDetailVO.Applicant(applicant.getId(), applicant.getUsername(),
                        applicant.getNickname(), applicant.getPhone()));
    }

    /** 7.3 审核商家：通过→audit_status=1,status=1,角色改MERCHANT；驳回→audit_status=2,status保持停业。 */
    @Transactional
    public MerchantAuditVO audit(Long id, Boolean approve, String remark) {
        if (approve == null) {
            throw new BizException(ResultCode.BAD_REQUEST, "approve不能为空");
        }
        Merchant merchant = merchantMapper.findById(id);
        if (merchant == null) {
            throw new BizException(ResultCode.NOT_FOUND, "商家申请不存在");
        }
        if (merchant.getAuditStatus() == null || merchant.getAuditStatus() != AUDIT_PENDING) {
            throw new BizException(ResultCode.BAD_REQUEST, "商家不在待审核状态");
        }
        int auditStatus;
        if (approve) {
            merchantMapper.updateAudit(id, AUDIT_APPROVED, 1, remark);
            userMapper.updateRole(merchant.getUserId(), "MERCHANT");
            auditStatus = AUDIT_APPROVED;
        } else {
            merchantMapper.updateAudit(id, AUDIT_REJECTED, 0, remark);
            auditStatus = AUDIT_REJECTED;
        }

        Map<String, Object> detail = new LinkedHashMap<>();
        detail.put("action", approve ? "approve" : "reject");
        detail.put("remark", remark);
        adminLogService.record("MERCHANT_AUDIT", "MERCHANT", id, detail);
        return new MerchantAuditVO(id, auditStatus, remark);
    }

    private AdminMerchantPendingVO toPendingVO(Merchant m) {
        User applicant = userMapper.findById(m.getUserId());
        return new AdminMerchantPendingVO(
                m.getId(), m.getShopName(), m.getDescription(), m.getContactPhone(),
                new AdminMerchantPendingVO.Applicant(applicant.getUsername(), applicant.getNickname()),
                m.getCreatedAt());
    }
}
