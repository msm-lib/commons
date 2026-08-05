package com.msm.core.metadata;

import com.msm.core.commons.Utils;
import com.msm.core.dynamicquery.ObjectMetadataFactory;
import com.msm.core.metadata.annotation.AttributeDefinition;
import com.msm.core.metadata.annotation.AttributeDefinitionRef;
import com.msm.core.metadata.annotation.ObjectDefinitionRef;
import com.msm.core.security.annotations.IgnorePermission;
import com.msm.core.security.annotations.SecuredField;
import com.msm.core.security.enums.SecurityDataScopeType;
import jakarta.persistence.GenerationType;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.Metamodel;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ObjectMetadataBuilder {

    public static void buildFromMetamodel(Metamodel metamodel) {
        for (EntityType<?> entity : metamodel.getEntities()) {
            Map<SecurityDataScopeType, Attribute> securedAttributeMap = new HashMap<>();
            List<Attribute> attributeList = getAllAttributes(entity, securedAttributeMap);

            ObjectMetadata objectMetadata = ObjectMetadata
                    .builder()
                    .name(entity.getName().toLowerCase())
                    .tableName(getTableName(entity))
                    .attributes(attributeList)
                    .securedAttributes(securedAttributeMap)
                    .objectRelation(getObjectRelation(entity))
                    .securityEnabled(isSecurityEnabled(entity))
                    .build();
            ObjectMetadataFactory.registerObjectMetadata(objectMetadata);
        }
    }

    private static boolean isSecurityEnabled(EntityType<?> entity) {
        Class<?> javaClass = entity.getJavaType();
        return !javaClass.isAnnotationPresent(IgnorePermission.class);
    }

    private static ObjectRelation getObjectRelation(EntityType<?> entity) {
        Class<?> javaClass = entity.getJavaType();
        if (javaClass.isAnnotationPresent(ObjectDefinitionRef.class)) {
            ObjectDefinitionRef objectDefinitionRef = javaClass.getAnnotation(ObjectDefinitionRef.class);
            return ObjectRelation
                    .builder()
                    .relationType(objectDefinitionRef.relationType())
                    .targetObject(objectDefinitionRef.targetObject())
                    .foreignKeyAttribute(objectDefinitionRef.foreignKeyAttribute())
                    .targetAttribute(objectDefinitionRef.targetAttribute())
                    .build();
        }

        return null;
    }

    private static String getTableName(EntityType<?> entity) {
        Class<?> javaClass = entity.getJavaType();
        if (javaClass.isAnnotationPresent(jakarta.persistence.Table.class)) {
            String name = javaClass.getAnnotation(jakarta.persistence.Table.class).name();
            if (!name.isEmpty()) return name;
        }
        return javaClass.getSimpleName();
    }

    private static List<Attribute> getAllAttributes(EntityType<?> entityType, Map<SecurityDataScopeType, Attribute> securedAttributeMap) {
        return entityType.getAttributes().stream()
                .filter(a -> a.getJavaMember() instanceof Field)
                .map(attribute -> buildFromJpaAttribute(attribute, securedAttributeMap))
                .collect(Collectors.toList());
    }

    private static Attribute buildFromJpaAttribute(jakarta.persistence.metamodel.Attribute<?, ?> jpaAttr, Map<SecurityDataScopeType, Attribute> securedAttributeMap) {
        Field field = (Field) jpaAttr.getJavaMember();
        return buildAttributeFromField(field, securedAttributeMap);
    }

    private static Attribute buildAttributeFromField(Field field, Map<SecurityDataScopeType, Attribute> securedAttributeMap) {
        Attribute result = new Attribute();

        result.setFieldName(field.getName());
        //JavaType type = mapper.getTypeFactory().constructType(field.getGenericType());
        String columnType = formatType(field.getGenericType());
//        result.setFieldType(field.getGenericType().getTypeName());
        result.setFieldType(columnType);

        if (field.isAnnotationPresent(AttributeDefinition.class)) {
            var ann = field.getAnnotation(AttributeDefinition.class);
            result.setFieldType(ann.fieldType().isEmpty() ? result.getFieldType() : ann.fieldType());
            if(!ann.columnName().isEmpty()) {
                result.setColumnName(ann.columnName());
            }
            result.setIsRequired(ann.required());
            result.setIsFreeText(ann.freeText());
            if(Utils.STR.isNotBlank(ann.regex())) {
                result.setRegex(ann.regex());
            }
            if(ann.maxLength() != -1) {
                result.setMaxLength(ann.maxLength());
            }
            if(ann.maxValue() != -1) {
                result.setMaxValue(ann.maxValue());
            }
            if(ann.minValue() != -1) {
                result.setMinValue(ann.minValue());
            }
            if(ann.maxSize() != -1) {
                result.setMaxSize(ann.maxSize());
            }
            if(!ann.defaultValue().isEmpty()) {
                result.setDefaultValue(ann.defaultValue());
            }

            AttributeDefinitionRef attributeDefinitionRef = ann.attributeRef();
            result.setAttributeRef(AttributeRef.of(attributeDefinitionRef.fieldName(), attributeDefinitionRef.objectRef(), attributeDefinitionRef.usageType()));
        }

        if (field.isAnnotationPresent(SecuredField.class)) {
            var securedField = field.getAnnotation(SecuredField.class);
            SecurityDataScopeType[] securityDataScopeTypes = securedField.value();
            for (SecurityDataScopeType securityDataScopeType : securityDataScopeTypes) {
                securedAttributeMap.put(securityDataScopeType, result);
            }
        }

        // Check @Column (JPA)
        if (field.isAnnotationPresent(jakarta.persistence.Column.class)) {
            var col = field.getAnnotation(jakarta.persistence.Column.class);
            if (result.getColumnName() == null) result.setColumnName(col.name());
            if (result.getIsRequired() == null && !isIdAutoGenerated(field)) result.setIsRequired(!col.nullable());
        }

        // Check @Id
        if (field.isAnnotationPresent(jakarta.persistence.Id.class)) {
            result.setIsRequired(!isIdAutoGenerated(field));
            result.setIsSystem(true);
        }

        // Check Bean Validation (@NotNull, @Size, @Min, @Max)
        if (field.isAnnotationPresent(jakarta.validation.constraints.NotNull.class)
                && !isIdAutoGenerated(field)) {
            result.setIsRequired(true);
        }

        if (field.isAnnotationPresent(jakarta.validation.constraints.Size.class)) {
            var size = field.getAnnotation(jakarta.validation.constraints.Size.class);
            result.setMaxLength((long) size.max());
        }

        if (field.isAnnotationPresent(jakarta.validation.constraints.Max.class)) {
            result.setMaxValue(field.getAnnotation(jakarta.validation.constraints.Max.class).value());
        }

        if (field.isAnnotationPresent(JdbcTypeCode.class)) {
            int value = field.getAnnotation(JdbcTypeCode.class).value();
            if(SqlTypes.JSON == value) {
                result.setIsJson(Boolean.TRUE);
            }
        }

        // 4. Default for ColumnName if empty
        if (result.getColumnName() == null || result.getColumnName().isEmpty()) {
            result.setColumnName(field.getName());
        }

        return result;
    }

    private static boolean isIdAutoGenerated(Field field) {
        if (field.isAnnotationPresent(jakarta.persistence.Id.class)) {
            var col = field.getAnnotation(jakarta.persistence.GeneratedValue.class);
            var colColumnDefault = field.getAnnotation(ColumnDefault.class);
//            if(col != null) {
//                return GenerationType.AUTO.equals(col.strategy()) || "gen_random_uuid()".equals(colColumnDefault.value());
//            }
            return (col != null && GenerationType.AUTO.equals(col.strategy())) || (colColumnDefault != null && "gen_random_uuid()".equals(colColumnDefault.value()));
        }
        return false;
    }

    private static String formatType(Type type) {
        String typeName = type.getTypeName();
        StringBuilder sb = new StringBuilder();
        String[] parts = typeName.split("(?<=[<> ,])|(?=[<> ,])");
        for (String part : parts) {
            // java, javax
            if (part.startsWith("java.")) {
                sb.append(part.substring(part.lastIndexOf('.') + 1));
            } else {
                sb.append(part);
            }
        }
        return sb.toString();
    }

}
