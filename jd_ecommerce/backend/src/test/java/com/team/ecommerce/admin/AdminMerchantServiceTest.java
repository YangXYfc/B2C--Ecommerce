package com.team.ecommerce.admin;

import com.team.ecommerce.admin.dto.AdminMerchantDetailVO;
import com.team.ecommerce.admin.dto.AdminMerchantPendingVO;
import com.team.ecommerce.admin.dto.MerchantAuditVO;
import com.team.ecommerce.admin.service.AdminMerchantService;
import com.team.ecommerce.auth.entity.Merchant;
import com.team.ecommerce.auth.entity.User;
import com.team.ecommerce.auth.mapper.MerchantMapper;
import com.team.ecommerce.auth.mapper.UserMapper;
import com.team.ecommerce.common.BizException;
import com.team.ecommerce.security.UserContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminMerchantServiceTest {

    @Mock
    private MerchantMapper merchantMapper;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private AdminMerchantService adminMerchantService;

    @BeforeEach
    void setUp() {
        UserContext.set(new UserContext.LoginUser(1L, "admin", "ADMIN"));
    }

    @AfterEach
    void tearDown() {
        UserContext.clear();
    }

    private Merchant merchant(long id, long userId, int auditStatus) {
        Merchant m = new Merchant();
        m.setId(id);
        m.setUserId(userId);
        m.setShopName("店铺" + id);
        m.setShopLogo("https://img.jd-demo.com/shop/logo" + id + ".png");
        m.setDescription("描述" + id);
        m.setContactPhone("1380000000" + id);
        m.setStatus(auditStatus == 0 ? 0 : 1);
        m.setAuditStatus(auditStatus);
        m.setCreatedAt(LocalDateTime.of(2026, 7, 1, 9, 0));
        return m;
    }

    private User user(long id, String username, String nickname, String phone, String role) {
        User u = new User();
        u.setId(id);
        u.setUsername(username);
        u.setNickname(nickname);
        u.setPhone(phone);
        u.setRole(role);
        return u;
    }

    @Test
    void listPending_mapsRowsWithApplicant() {
        when(merchantMapper.findPending()).thenReturn(List.of(merchant(3, 7, 0)));
        when(userMapper.findById(7L)).thenReturn(user(7, "merchant3", "待审核商家", "13800000007", "MERCHANT"));

        List<AdminMerchantPendingVO> list = adminMerchantService.listPending();

        assertEquals(1, list.size());
        AdminMerchantPendingVO vo = list.get(0);
        assertEquals(3L, vo.id());
        assertEquals("店铺3", vo.shopName());
        assertEquals("13800000003", vo.contactPhone());
        assertEquals("merchant3", vo.applicant().username());
        assertEquals("待审核商家", vo.applicant().nickname());
        assertEquals(LocalDateTime.of(2026, 7, 1, 9, 0), vo.createdAt());
    }

    @Test
    void detail_found_mapsAllFields() {
        when(merchantMapper.findById(3L)).thenReturn(merchant(3, 7, 0));
        when(userMapper.findById(7L)).thenReturn(user(7, "merchant3", "待审核商家", "13800000007", "MERCHANT"));

        AdminMerchantDetailVO vo = adminMerchantService.detail(3L);

        assertEquals(3L, vo.id());
        assertEquals(7L, vo.userId());
        assertEquals("店铺3", vo.shopName());
        assertEquals("https://img.jd-demo.com/shop/logo3.png", vo.shopLogo());
        assertEquals(0, vo.auditStatus());
        assertNull(vo.auditRemark());
        assertEquals(7L, vo.applicant().id());
        assertEquals("merchant3", vo.applicant().username());
        assertEquals("待审核商家", vo.applicant().nickname());
        assertEquals("13800000007", vo.applicant().phone());
    }

    @Test
    void detail_notFound_throwsNotFound() {
        when(merchantMapper.findById(999L)).thenReturn(null);

        BizException e = assertThrows(BizException.class, () -> adminMerchantService.detail(999L));
        assertEquals(404, e.getCode());
    }

    @Test
    void audit_approve_updatesMerchantAndRole() {
        when(merchantMapper.findById(3L)).thenReturn(merchant(3, 7, 0));

        MerchantAuditVO vo = adminMerchantService.audit(3L, true, "资料齐全，审核通过");

        assertEquals(3L, vo.merchantId());
        assertEquals(1, vo.auditStatus());
        assertEquals("资料齐全，审核通过", vo.remark());
        verify(merchantMapper).updateAudit(3L, 1, 1, "资料齐全，审核通过");
        verify(userMapper).updateRole(7L, "MERCHANT");
    }

    @Test
    void audit_reject_updatesMerchantOnly() {
        when(merchantMapper.findById(3L)).thenReturn(merchant(3, 7, 0));

        MerchantAuditVO vo = adminMerchantService.audit(3L, false, "资料不足");

        assertEquals(3L, vo.merchantId());
        assertEquals(2, vo.auditStatus());
        assertEquals("资料不足", vo.remark());
        verify(merchantMapper).updateAudit(3L, 2, 0, "资料不足");
        verify(userMapper, never()).updateRole(any(), any());
    }

    @Test
    void audit_approveNull_throwsBadRequest() {
        BizException e = assertThrows(BizException.class, () -> adminMerchantService.audit(3L, null, "x"));
        assertEquals(400, e.getCode());
    }

    @Test
    void audit_notFound_throwsNotFound() {
        when(merchantMapper.findById(999L)).thenReturn(null);

        BizException e = assertThrows(BizException.class, () -> adminMerchantService.audit(999L, true, "x"));
        assertEquals(404, e.getCode());
    }

    @Test
    void audit_alreadyAudited_throwsBadRequest() {
        when(merchantMapper.findById(3L)).thenReturn(merchant(3, 7, 1));

        BizException e = assertThrows(BizException.class, () -> adminMerchantService.audit(3L, true, "x"));
        assertEquals(400, e.getCode());
    }

    @Test
    void audit_approve_remarkNull() {
        when(merchantMapper.findById(3L)).thenReturn(merchant(3, 7, 0));

        MerchantAuditVO vo = adminMerchantService.audit(3L, true, null);

        assertEquals(1, vo.auditStatus());
        assertNull(vo.remark());
        verify(merchantMapper).updateAudit(3L, 1, 1, null);
        verify(userMapper).updateRole(7L, "MERCHANT");
    }
}
