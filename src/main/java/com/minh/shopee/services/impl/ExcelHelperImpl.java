package com.minh.shopee.services.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.minh.shopee.domain.model.location.District;
import com.minh.shopee.domain.model.location.Province;
import com.minh.shopee.domain.model.location.Ward;
import com.minh.shopee.services.utils.error.AppException;
import com.minh.shopee.services.utils.files.ExcelHelper;
import com.minh.shopee.services.utils.files.LocationData;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j(topic = "excelHelper")
public class ExcelHelperImpl implements ExcelHelper {

    @Override
    public void isExcelFile(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        log.info("Checking file excel: {}", originalFilename);

        if (originalFilename == null ||
                (!originalFilename.endsWith(".xlsx") && !originalFilename.endsWith(".xls")) ||
                !file.getContentType().equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")) {
            log.error("Invalid file excel: {}", originalFilename);
            throw new AppException(400, "Invalid file excel", "File is not an Excel file");
        }
        log.info("{} is an Excel file", originalFilename);

    }

    @Override
    public LocationData readExcelLocationFile(MultipartFile file) throws IOException {
        this.isExcelFile(file);

        Set<Province> provinces = new HashSet<>();
        Set<District> districts = new HashSet<>();
        Set<Ward> wards = new HashSet<>();

        DataFormatter formatter = new DataFormatter();

        try (InputStream is = file.getInputStream();
                Workbook workbook = WorkbookFactory.create(is)) {

            Sheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {
                if (row.getRowNum() == 0)
                    continue;

                // Đọc dữ liệu từng ô an toàn
                String provinceName = formatter.formatCellValue(row.getCell(0)).trim();
                String provinceCodeStr = formatter.formatCellValue(row.getCell(1)).trim();
                String districtName = formatter.formatCellValue(row.getCell(2)).trim();
                String districtCodeStr = formatter.formatCellValue(row.getCell(3)).trim();
                String wardName = formatter.formatCellValue(row.getCell(4)).trim();
                String wardCodeStr = formatter.formatCellValue(row.getCell(5)).trim();

                // Kiểm tra dữ liệu bắt buộc
                if (provinceName.isEmpty() || provinceCodeStr.isEmpty() ||
                        districtName.isEmpty() || districtCodeStr.isEmpty()) {
                    log.error("Missing required data in row {}", row.getRowNum());
                    throw new AppException(400, "Invalid data", "Missing required data in row " + row.getRowNum());
                }

                // Parse ID
                long provinceCode = Long.parseLong(provinceCodeStr);
                long districtCode = Long.parseLong(districtCodeStr);

                // Tạo hoặc lấy Province
                Province province = Province.builder()
                        .id(provinceCode)
                        .name(provinceName)
                        .build();
                provinces.add(province);

                // Tạo District
                District district = District.builder()
                        .id(districtCode)
                        .name(districtName)
                        .province(province)
                        .build();
                districts.add(district);

                // Nếu có dữ liệu phường
                if (!wardName.isEmpty() && !wardCodeStr.isEmpty()) {
                    long wardCode = Long.parseLong(wardCodeStr);
                    Ward ward = Ward.builder()
                            .id(wardCode)
                            .name(wardName)
                            .district(district)
                            .build();
                    wards.add(ward);
                }

                log.info("Row {} parsed: Province={}, District={}, Ward={}",
                        row.getRowNum(), provinceCode, districtCode, wardCodeStr);
            }
        }

        // Trả về toàn bộ data đã gom
        LocationData locationData = new LocationData();
        locationData.setProvinces(provinces);
        locationData.setDistricts(districts);
        locationData.setWards(wards);

        return locationData;
    }

}