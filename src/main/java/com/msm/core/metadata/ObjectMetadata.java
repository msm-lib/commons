package com.msm.core.metadata;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.msm.core.commons.Constants;
import com.msm.core.commons.Utils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jooq.Field;
import org.jooq.Table;
import org.jooq.impl.DSL;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ObjectMetadata {
    private String name;
    private String tableName;
    private List<Attribute> attributes;
    @JsonIgnore
    private Map<String, Attribute> attributeMap = new HashMap<>();

    @JsonIgnore
    public Table<?> getTable() {
        return DSL.table(DSL.name(tableName));
    }

    @JsonIgnore
    public List<Field<?>> getFields() {
        return Utils.CL.emptyIfNull(attributes).stream().map(Attribute::getField).collect(Collectors.toList());
    }

    public Attribute getAttributeByName(String fieldName) {
        if (!attributeMap.containsKey(fieldName)) {
            fetchAttributesToMap();
        }
        return attributeMap.get(fieldName);
    }

    private void fetchAttributesToMap() {
        Utils.CL.emptyIfNull(attributes).forEach(attribute -> {
            attributeMap.put(attribute.getFieldName(), attribute);
        });
    }

    @JsonIgnore
    public Attribute getIdAttribute() {
        if (!attributeMap.containsKey(Constants.OBJECT_PK)) {
            fetchAttributesToMap();
        }
        return attributeMap.get(Constants.OBJECT_PK);
    }

    @JsonIgnore
    public Attribute getCustomFieldAttribute() {
        return getAttributeByName(Constants.CUSTOM_VALUE_NAME);
    }

//    public Attribute getCreatedAtAttribute() {
//        if (!attributeMap.containsKey(Constants.CREATED_AT)) {
//            fetchAttributesToMap();
//        }
//        return attributeMap.get(Constants.CREATED_AT);
//    }
//
//    public Attribute getCreatedByAttribute() {
//        if (!attributeMap.containsKey(Constants.CREATED_BY)) {
//            fetchAttributesToMap();
//        }
//        return attributeMap.get(Constants.CREATED_BY);
//    }
//
//    public Attribute getCreatedByAttribute() {
//        if (!attributeMap.containsKey(Constants.CREATED_BY)) {
//            fetchAttributesToMap();
//        }
//        return attributeMap.get(Constants.CREATED_BY);
//    }
}
