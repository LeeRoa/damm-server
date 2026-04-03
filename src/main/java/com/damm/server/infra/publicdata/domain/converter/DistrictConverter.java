package com.damm.server.infra.publicdata.domain.converter;

import com.damm.server.infra.publicdata.domain.enums.District;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class DistrictConverter implements AttributeConverter<District, String> {

    @Override
    public String convertToDatabaseColumn(District attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.getKoreanName();
    }

    @Override
    public District convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isBlank()) {
            return null;
        }
        return District.fromKoreanName(dbData);
    }
}