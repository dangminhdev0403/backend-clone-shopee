package com.minh.shopee.services.impl;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.stereotype.Service;

import com.minh.shopee.domain.dto.mappers.AddressMapper;
import com.minh.shopee.domain.dto.request.AddAddressDTO;
import com.minh.shopee.domain.dto.request.EditAddressDTO;
import com.minh.shopee.domain.dto.response.AddressFullResponse;
import com.minh.shopee.domain.model.Address;
import com.minh.shopee.domain.model.User;
import com.minh.shopee.repository.AddressRepository;
import com.minh.shopee.services.AddressService;
import com.minh.shopee.services.LocationService;
import com.minh.shopee.services.utils.error.AppException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "AddressServiceImpl")
public class AddressServiceImpl implements AddressService {
    private final AddressRepository addressRepository;
    private final LocationService locationService;

    @Override
    public void addAddress(AddAddressDTO request, Long userId) {
        log.info("Adding new address for user with ID: {}", userId);
        User user = User.builder()
                .id(userId)
                .build(); // Assuming User is already fetched or created
        Address address = Address.builder()
                .name(request.getName())
                .phone(request.getPhone())
                .addressDetail(request.getAddressDetail())
                .provinceId(request.getProvinceId())
                .districtId(request.getDistrictId())
                .wardId(request.getWardId())
                .isDefault(request.getIsDefault())
                .type(request.getType())
                .user(user) // Assuming user will be set later
                .build();
        this.addressRepository.save(address);
        log.info("Address added successfully for user with ID: {}", userId);
    }

    @Override
    public void updateAddress(EditAddressDTO dto) {
        Address address = addressRepository.findById(dto.getId())
                .orElseThrow(() -> new IllegalArgumentException("Address not found"));

        BeanUtils.copyProperties(dto, address, getNullPropertyNames(dto));
        addressRepository.save(address);
    }

    @Override
    public void deleteAddress(Long addressId, Long userId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteAddress'");
    }

    private String[] getNullPropertyNames(Object source) {
        final BeanWrapper src = new BeanWrapperImpl(source);
        return Arrays.stream(src.getPropertyDescriptors())
                .map(pd -> pd.getName())
                .filter(name -> src.getPropertyValue(name) == null)
                .toArray(String[]::new);
    }

    @Override
    public AddressFullResponse getAddressFullById(Long addressId) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new AppException(404, "Không tìm thấy địa chỉ", "Không tìm thay địa chỉ"));
        String provinceName = locationService.getProvinceById(address.getProvinceId()).getName();
        String districtName = locationService.getDistrictById(address.getDistrictId()).getName();
        String wardName = locationService.getWardById(address.getWardId()).getName();
        return AddressMapper.toFullResponse(address, provinceName, districtName, wardName);

    }

    @Override
    public List<AddressFullResponse> getAllAddresses(Long userId) {

        log.info("Fetching all addresses for user with ID: {}", userId);
        List<Address> addresses = this.addressRepository.findAllByUserId(userId);

        return addresses.stream()
                .map(address -> this.getAddressFullById(address.getId()))
                .toList();
    }

}
