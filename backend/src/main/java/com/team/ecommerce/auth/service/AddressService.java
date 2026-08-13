package com.team.ecommerce.auth.service;

import com.team.ecommerce.auth.dto.AddressRequest;
import com.team.ecommerce.auth.dto.AddressVO;
import com.team.ecommerce.auth.entity.Address;
import com.team.ecommerce.auth.mapper.AddressMapper;
import com.team.ecommerce.common.BizException;
import com.team.ecommerce.common.ResultCode;
import com.team.ecommerce.security.UserContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 收货地址服务：列表、新增、编辑、删除、设为默认。
 * 所有写操作按用户隔离，他人地址一律 403、不存在一律 404。
 */
@Service
public class AddressService {

    private final AddressMapper addressMapper;

    public AddressService(AddressMapper addressMapper) {
        this.addressMapper = addressMapper;
    }

    /** 2.1 当前用户全部地址（默认在前）。 */
    public List<AddressVO> list() {
        Long userId = UserContext.getUserId();
        return addressMapper.findByUserId(userId).stream().map(this::toVO).toList();
    }

    /** 2.2 新增地址；isDefault=1 时先把同用户其他地址置 0。 */
    @Transactional
    public AddressVO add(AddressRequest req) {
        Long userId = UserContext.getUserId();
        int isDefault = defaultOf(req.isDefault());

        if (isDefault == 1) {
            addressMapper.clearDefault(userId);
        }

        Address address = new Address();
        address.setUserId(userId);
        apply(address, req, isDefault);
        addressMapper.insert(address);
        return toVO(address);
    }

    /** 2.3 编辑自己的地址；他人 403、不存在 404。 */
    @Transactional
    public AddressVO update(Long id, AddressRequest req) {
        Address address = requireOwned(id);
        int isDefault = defaultOf(req.isDefault());

        if (isDefault == 1) {
            addressMapper.clearDefault(address.getUserId());
        }

        apply(address, req, isDefault);
        addressMapper.update(address);
        return toVO(address);
    }

    /** 2.4 删除自己的地址。 */
    @Transactional
    public void delete(Long id) {
        Address address = requireOwned(id);
        addressMapper.deleteById(address.getId());
    }

    /** 2.5 设为默认；同用户其他地址置 0。 */
    @Transactional
    public AddressVO setDefault(Long id) {
        Address address = requireOwned(id);
        addressMapper.clearDefault(address.getUserId());
        addressMapper.setDefault(address.getId());
        address.setIsDefault(1);
        return toVO(address);
    }

    /**
     * 按 id 加载并校验归属：不存在 → 404，非本人 → 403（对齐契约 404→403 顺序）。
     */
    private Address requireOwned(Long id) {
        Address address = addressMapper.findById(id);
        if (address == null) {
            throw new BizException(ResultCode.NOT_FOUND, "地址不存在");
        }
        if (!address.getUserId().equals(UserContext.getUserId())) {
            throw new BizException(ResultCode.FORBIDDEN, "无权限");
        }
        return address;
    }

    private int defaultOf(Integer isDefault) {
        return isDefault == null ? 0 : isDefault;
    }

    private void apply(Address address, AddressRequest req, int isDefault) {
        address.setName(req.name());
        address.setPhone(req.phone());
        address.setProvince(req.province());
        address.setCity(req.city());
        address.setDistrict(req.district());
        address.setDetail(req.detail());
        address.setIsDefault(isDefault);
    }

    private AddressVO toVO(Address a) {
        return new AddressVO(a.getId(), a.getName(), a.getPhone(), a.getProvince(),
                a.getCity(), a.getDistrict(), a.getDetail(), a.getIsDefault());
    }
}
