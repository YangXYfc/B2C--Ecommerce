package com.team.ecommerce.auth;

import com.team.ecommerce.auth.dto.AddressRequest;
import com.team.ecommerce.auth.dto.AddressVO;
import com.team.ecommerce.auth.entity.Address;
import com.team.ecommerce.auth.mapper.AddressMapper;
import com.team.ecommerce.auth.service.AddressService;
import com.team.ecommerce.common.BizException;
import com.team.ecommerce.security.UserContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AddressServiceTest {

    @Mock
    private AddressMapper addressMapper;

    @InjectMocks
    private AddressService addressService;

    private Address address(long id, long userId, int isDefault) {
        Address a = new Address();
        a.setId(id);
        a.setUserId(userId);
        a.setName("张三");
        a.setPhone("13800000004");
        a.setProvince("北京市");
        a.setCity("北京市");
        a.setDistrict("朝阳区");
        a.setDetail("建国路88号1801室");
        a.setIsDefault(isDefault);
        return a;
    }

    private AddressRequest request(Integer isDefault) {
        return new AddressRequest("张三", "13800000004", "北京市", "北京市", "朝阳区", "建国路88号1801室", isDefault);
    }

    @Test
    void list_returnsUserAddresses_defaultFirst() {
        UserContext.set(new UserContext.LoginUser(4L, "user1", "USER"));
        try {
            when(addressMapper.findByUserId(4L)).thenReturn(List.of(address(1L, 4L, 1), address(2L, 4L, 0)));

            List<AddressVO> list = addressService.list();

            assertEquals(2, list.size());
            assertEquals(1L, list.get(0).id());
            assertEquals(1, list.get(0).isDefault());
            assertEquals(0, list.get(1).isDefault());
        } finally {
            UserContext.clear();
        }
    }

    @Test
    void add_default_clearsOthers_thenInserts() {
        UserContext.set(new UserContext.LoginUser(4L, "user1", "USER"));
        try {
            when(addressMapper.insert(any(Address.class))).thenAnswer(inv -> {
                inv.<Address>getArgument(0).setId(5L);
                return 1;
            });

            AddressVO vo = addressService.add(request(1));

            InOrder inOrder = inOrder(addressMapper);
            inOrder.verify(addressMapper).clearDefault(4L);
            inOrder.verify(addressMapper).insert(any(Address.class));
            assertEquals(5L, vo.id());
            assertEquals(1, vo.isDefault());
        } finally {
            UserContext.clear();
        }
    }

    @Test
    void add_nonDefault_doesNotClearOthers() {
        UserContext.set(new UserContext.LoginUser(4L, "user1", "USER"));
        try {
            when(addressMapper.insert(any(Address.class))).thenAnswer(inv -> {
                inv.<Address>getArgument(0).setId(5L);
                return 1;
            });

            addressService.add(request(null));

            verify(addressMapper, never()).clearDefault(4L);
        } finally {
            UserContext.clear();
        }
    }

    @Test
    void update_notOwner_throws403() {
        UserContext.set(new UserContext.LoginUser(4L, "user1", "USER"));
        try {
            when(addressMapper.findById(1L)).thenReturn(address(1L, 5L, 1));

            BizException ex = assertThrows(BizException.class, () -> addressService.update(1L, request(0)));
            assertEquals(403, ex.getCode());
        } finally {
            UserContext.clear();
        }
    }

    @Test
    void update_notFound_throws404() {
        UserContext.set(new UserContext.LoginUser(4L, "user1", "USER"));
        try {
            when(addressMapper.findById(999L)).thenReturn(null);

            BizException ex = assertThrows(BizException.class, () -> addressService.update(999L, request(0)));
            assertEquals(404, ex.getCode());
        } finally {
            UserContext.clear();
        }
    }

    @Test
    void delete_notOwner_throws403() {
        UserContext.set(new UserContext.LoginUser(4L, "user1", "USER"));
        try {
            when(addressMapper.findById(1L)).thenReturn(address(1L, 5L, 1));

            BizException ex = assertThrows(BizException.class, () -> addressService.delete(1L));
            assertEquals(403, ex.getCode());
        } finally {
            UserContext.clear();
        }
    }

    @Test
    void setDefault_clearsOthersAndSets() {
        UserContext.set(new UserContext.LoginUser(4L, "user1", "USER"));
        try {
            when(addressMapper.findById(2L)).thenReturn(address(2L, 4L, 0));

            AddressVO vo = addressService.setDefault(2L);

            InOrder inOrder = inOrder(addressMapper);
            inOrder.verify(addressMapper).clearDefault(eq(4L));
            inOrder.verify(addressMapper).setDefault(eq(2L));
            assertEquals(1, vo.isDefault());
        } finally {
            UserContext.clear();
        }
    }
}
