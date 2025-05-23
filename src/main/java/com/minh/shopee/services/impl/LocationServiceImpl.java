package com.minh.shopee.services.impl;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
import com.minh.shopee.services.utils.files.data.LocationData;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "locationService")
public class LocationServiceImpl implements LocationService {
    private final ProvinceRepository provinceRepository;
    private final DistrictRepository districtRepository;
    private final WardRepository wardRepository;
    private final ExcelHelper excelHelper;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public void addAllLocation(MultipartFile locationFile) throws IOException {
        log.info("==> Starting import location data from file: {}", locationFile.getOriginalFilename());

        LocationData locationData = excelHelper.readExcelLocationFile(locationFile);

        log.info("Parsed data - Provinces: {}, Districts: {}, Wards: {}",
                locationData.getProvinces().size(),
                locationData.getDistricts().size(),
                locationData.getWards().size());

        // B1: Save provinces
        log.info("Saving provinces...");
        List<Province> savedProvinces = provinceRepository.saveAll(locationData.getProvinces());
        log.info("Saved {} provinces.", savedProvinces.size());

        Map<Long, Province> provinceMap = savedProvinces.stream()
                .collect(Collectors.toMap(Province::getId, p -> p));

        // B2: Set province reference for districts
        for (District district : locationData.getDistricts()) {
            Province province = provinceMap.get(district.getProvince().getId());
            district.setProvince(province);
        }

        log.info("Saving districts...");
        List<District> savedDistricts = districtRepository.saveAll(locationData.getDistricts());
        log.info("Saved {} districts.", savedDistricts.size());

        Map<Long, District> districtMap = savedDistricts.stream()
                .collect(Collectors.toMap(District::getId, d -> d));

        // B3: Set district reference for wards
        for (Ward ward : locationData.getWards()) {
            District district = districtMap.get(ward.getDistrict().getId());
            ward.setDistrict(district);
        }

        log.info("Saving wards...");
        wardRepository.saveAll(locationData.getWards());
        log.info("Saved {} wards.", locationData.getWards().size());

        log.info("==> Import location data completed successfully.");
    }

    @Override
    @Transactional
    public void deleteAllLocations() {
        log.warn("==> Deleting all location data...");

        wardRepository.deleteAllInBatch();
        log.info("All wards deleted.");

        districtRepository.deleteAllInBatch();
        log.info("All districts deleted.");

        provinceRepository.deleteAllInBatch();
        log.info("All provinces deleted.");

        log.warn("==> All location data has been deleted.");
    }

    @Override
    public <T> Page<T> getListProvinces(Class<T> type , Pageable pageable) {
        log.debug("Fetching list of provinces with projection type: {}", type.getSimpleName());
        return this.provinceRepository.findAllBy(type, pageable);
    }

    @Override
    public <T> Page<T> getListDistricts(Long provinceId, Class<T> type ,Pageable pageable) {
        log.debug("Fetching list of districts with projection type: {}", type.getSimpleName());
        return this.districtRepository.findByProvinceId(provinceId, type, pageable);
    }

    @Override
    public <T> Page<T> getListWards(Long provinceId, Class<T> type ,Pageable pageable) {
        log.debug("Fetching list of wards with projection type: {}", type.getSimpleName());
        return this.wardRepository.findByDistrictId(provinceId, type, pageable);
    }

}
