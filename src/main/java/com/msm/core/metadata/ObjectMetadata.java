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
import java.util.Objects;
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

    @JsonIgnore
    public List<Field<?>> getFieldAlias() {
        return Utils.CL.emptyIfNull(attributes)
                .stream()
                .map(attribute -> attribute.getField().as(attribute.getFieldName()))
                .collect(Collectors.toList());
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

    @JsonIgnore
    public List<Attribute> getAttributeRefs() {
        return Utils.CL.emptyIfNull(getAttributes())
                .stream()
                .filter(attribute -> Objects.nonNull(attribute.getAttributeRef()) && Utils.STR.isNotBlank(attribute.getAttributeRef().getFieldName()))
                .collect(Collectors.toList());
    }
}
