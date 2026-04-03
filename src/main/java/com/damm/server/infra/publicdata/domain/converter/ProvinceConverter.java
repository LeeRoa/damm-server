package com.damm.server.infra.publicdata.domain.converter;

import com.damm.server.infra.publicdata.domain.enums.Province;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class ProvinceConverter implements AttributeConverter<Province, String> {

    @Override
    public String convertToDatabaseColumn(Province province) {
        // 엔티티(Enum) -> DB(String): "서울특별시" 저장
        return (province != null) ? province.getKoreanName() : null;
    }

    @Override
    public Province convertToEntityAttribute(String dbData) {
        // DB(String) -> 엔티티(Enum): "서울특별시"를 보고 Province.SEOUL 반환
        return (dbData != null) ? Province.fromKoreanName(dbData) : null;
    }
}