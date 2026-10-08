package com.msm.core.action.context;

import com.msm.core.commons.Utils;
import com.msm.core.dynamicquery.ObjectMetadataFactory;
import com.msm.core.metadata.Attribute;
import com.msm.core.metadata.ObjectMetadata;
import com.msm.core.metadata.typesafe.DataRecord;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;


@Data
public class PersistenceContext {
    private String objectName;
    private List<Map<String, Object>> sourceObjects;
    private List<Map<String, Object>> targetObjects;

    private PersistenceContext(List<Map<String, Object>> sourceObjects, List<Map<String, Object>> targetObjects) {
        this.sourceObjects = Utils.CL.emptyIfNull(sourceObjects);
        this.targetObjects = Utils.CL.emptyIfNull(targetObjects);
    }

    private PersistenceContext() {}

    public List<List<Map<String, Object>>> getDiff() {
        return getDiff(Set.of());
    }

    public List<List<Map<String, Object>>> getDiff(Set<String> ignoreAuditFieldNames) {
        ObjectMetadata objectMetadata = ObjectMetadataFactory.getObjectMetadataByName(objectName);
        Attribute idAttribute = objectMetadata.getIdAttribute();
        Map<UUID, Map<String, Object>> targetObjectGroupById = Utils.D.groupBy(
                Utils.CL.emptyIfNull(targetObjects),
                objectKey -> UUID.fromString(String.valueOf(objectKey.get(idAttribute.getFieldName()))),
                Function.identity()
        );

        return Utils.CL.emptyIfNull(sourceObjects)
                .stream()
                .map(sourceObject -> {
                    UUID val = idAttribute.cast(sourceObject.get(idAttribute.getFieldName()));
                    Map<String, Object> targetObject = targetObjectGroupById.get(val);
                    if(Utils.CL.isEmpty(targetObject)) {
                        return null;
                    }
                    return diffObjects(objectName, sourceObject, targetObject, ignoreAuditFieldNames);
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public static PersistenceContext of(List<Map<String, Object>> sourceObjects, List<Map<String, Object>> targetObjects) {
        return new PersistenceContext(sourceObjects, targetObjects);
    }

    public static PersistenceContext of() {
        return new PersistenceContext(new ArrayList<>(), new ArrayList<>());
    }

    public List<Map<String, Object>> diffObjects(
            String objectName,
            Map<String, Object> oldValues,
            Map<String, Object> newValues,
            Set<String> ignoreAuditFieldNames
    ) {

        ObjectMetadata objectMetadata = ObjectMetadataFactory.getObjectMetadataByName(objectName);
        List<Map<String, Object>> diffs = new ArrayList<>();
        if (Objects.isNull(oldValues) || Objects.isNull(newValues)) {
            return diffs;
        }

        objectMetadata.getAttributes().forEach(attribute -> {
            String propertyName = attribute.getFieldName();
            if (ignoreAuditFieldNames.contains(propertyName)) {
                return;
            }
            Object oldVal = attribute.cast(oldValues.get(propertyName));
            if(oldVal != null) {
                Object newVal = attribute.cast(newValues.get(propertyName));
                addFieldChanged(propertyName, oldVal, newVal, diffs);
            }
        });

        return diffs;
    }

    private void addFieldChanged(String propertyName, Object oldVal, Object newVal, List<Map<String, Object>> diffs) {
        if (oldVal instanceof BigDecimal oldValBigDecimal && newVal instanceof BigDecimal newValBigDecimal) {
            if (newValBigDecimal.subtract(oldValBigDecimal).abs().compareTo(BigDecimal.ZERO) > 0) {

                diffs.add(DataRecord
                        .of()
                        .with(FieldChangedMeta.NAME, propertyName)
                        .with(FieldChangedMeta.BEFORE, oldVal)
                        .with(FieldChangedMeta.AFTER, newVal)
                        .getValues()
                );
            }
        } else if (!Objects.equals(oldVal, newVal)) {
            diffs.add(DataRecord
                    .of()
                    .with(FieldChangedMeta.NAME, propertyName)
                    .with(FieldChangedMeta.BEFORE, oldVal)
                    .with(FieldChangedMeta.AFTER, newVal)
                    .getValues()
            );
        }
    }





//    public static List<FieldChange> diff(Map<String, Object> before, Map<String, Object> after,
//                                         Set<String> ignore) {
//        List<FieldChange> out = new ArrayList<>();
//        Set<String> keys = new TreeSet<>();
//        keys.addAll(before.keySet());
//        keys.addAll(after.keySet());
//        for (String k : keys) {
//            if (!ignore.contains(k)) walk(k, before.get(k), after.get(k), out);
//        }
//        return out;
//    }
//
//    @SuppressWarnings("unchecked")
//    private static void walk(String path, Object o, Object n, List<FieldChange> out) {
//        if (o instanceof Map && n instanceof Map) {              // vào sâu object lồng nhau
//            Set<String> keys = new TreeSet<>();
//            keys.addAll(((Map<String, Object>) o).keySet());
//            keys.addAll(((Map<String, Object>) n).keySet());
//            for (String k : keys) {
//                walk(path + "." + k, ((Map<String, Object>) o).get(k), ((Map<String, Object>) n).get(k), out);
//            }
//        } else if (!same(o, n)) {
//            out.add(new FieldChange(path, o, n));
//        }
//    }
//
//    private static boolean same(Object a, Object b) {
//        if (a instanceof Number x && b instanceof Number y) {   // 1.0 và 1.00 coi là bằng nhau
//            return new BigDecimal(x.toString()).compareTo(new BigDecimal(y.toString())) == 0;
//        }
//        return Objects.equals(a, b);
//    }
}
