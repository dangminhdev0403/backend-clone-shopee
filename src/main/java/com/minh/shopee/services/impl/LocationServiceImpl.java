package com.minh.shopee.services.impl;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.minh.shopee.domain.model.location.District;
import com.minh.shopee.domain.model.location.Province;
import com.minh.shopee.domain.model.location.Ward;
import com.minh.shopee.repository.DistrictRepository;
import com.minh.shopee.repository.ProvinceRepository;
import com.minh.shopee.repository.WardRepository;
import com.minh.shopee.services.LocationService;
import com.minh.shopee.services.utils.files.ExcelHelper;
import com.minh.shopee.services.utils.files.LocationData;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LocationServiceImpl implements LocationService {
    private final ProvinceRepository provinceRepository;
    private final DistrictRepository districtRepository;
    private final WardRepository wardRepository;
    private final ExcelHelper excelHelper;
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public void addLocation(MultipartFile locationFile) throws IOException {
        // B1: Lưu Provinces và lấy danh sách đã lưu (managed entity)
        LocationData locationData = excelHelper.readExcelLocationFile(locationFile);
        List<Province> savedProvinces = provinceRepository.saveAll(locationData.getProvinces());

        // B2: Tạo map để dễ lấy province từ id
        Map<Long, Province> provinceMap = savedProvinces.stream()
                .collect(Collectors.toMap(Province::getId, p -> p));

        // B3: Gán Province đã lưu vào District
        for (District district : locationData.getDistricts()) {
            Province province = provinceMap.get(district.getProvince().getId());
            district.setProvince(province);
        }

        List<District> savedDistricts = districtRepository.saveAll(locationData.getDistricts());

        // B4: Tạo map District
        Map<Long, District> districtMap = savedDistricts.stream()
                .collect(Collectors.toMap(District::getId, d -> d));

        for (Ward ward : locationData.getWards()) {
            District district = districtMap.get(ward.getDistrict().getId());
            ward.setDistrict(district);
        }

        wardRepository.saveAll(locationData.getWards());
    }

}
