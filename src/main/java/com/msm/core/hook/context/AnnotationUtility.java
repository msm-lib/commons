package com.msm.core.hook.context;

import com.msm.core.commons.Condition;
import com.msm.core.commons.Utils;
import com.msm.core.hook.anontation.ActionType;
import com.msm.core.hook.anontation.ExtendContextKey;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Data
@RequiredArgsConstructor
public class AnnotationUtility {

    private Map<String, Object> attributes;

    public AnnotationUtility(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    public static AnnotationUtility getAnnotationConfig(AnnotatedElement method) {
        Map<String, Object> attributes = getMergedAttributes(method, ActionType.class);
        return new AnnotationUtility(attributes);
    }

    public static Map<String, Object> getMergedAttributes(AnnotatedElement element, Class<? extends Annotation> targetMetaAnnotation) {
        Map<String, Object> mergedAttributes = new HashMap<>();
        Set<Class<? extends Annotation>> visited = new HashSet<>();
        Set<Class<? extends Annotation>> readValued = new HashSet<>();

        for (Annotation ann : element.getAnnotations()) {
            scanRecursive(ann, targetMetaAnnotation, mergedAttributes, visited, readValued);
        }

        return mergedAttributes;
    }

    private static void scanRecursive(Annotation ann,
                                      Class<? extends Annotation> targetMeta,
                                      Map<String, Object> attributes,
                                      Set<Class<? extends Annotation>> visited,
                                      Set<Class<? extends Annotation>> readValued) {

        Class<? extends Annotation> type = ann.annotationType();
        if (visited.contains(type) || isSystemAnnotation(type)) return;
        visited.add(type);
        if (type.equals(targetMeta) || hasMetaAnnotation(type, targetMeta)) {
            if(!readValued.contains(type)) {
                putAttributeValue(ann, attributes);
                readValued.add(type);
            }

            for (Annotation parentAnn : type.getAnnotations()) {
                Class<? extends Annotation> typeParentAnn = parentAnn.annotationType();
                if(!isSystemAnnotation(typeParentAnn) && !readValued.contains(typeParentAnn)) {
                    putAttributeValue(parentAnn, attributes);
                    readValued.add(typeParentAnn);
                }
            }

            for (Annotation meta : type.getAnnotations()) {
                scanRecursive(meta, targetMeta, attributes, visited, readValued);
            }
        }
    }

    public static void putAttributeValue(Annotation ann, Map<String, Object> attributes) {
        Class<? extends Annotation> type = ann.annotationType();
        for (Method method : type.getDeclaredMethods()) {
            try {
                Object value = method.invoke(ann);
                Object currentVale = attributes.get(method.getName());
                if (!hasValue(currentVale)) {
                    attributes.put(method.getName(), value);
                }
            } catch (Exception ignored) {}
        }
    }

    public static boolean hasValue(Object currentVale) {
        if(currentVale instanceof String strCurrentValue) {
            return Utils.STR.isNotBlank(strCurrentValue);
        }
        return currentVale != null;
    }

    private static boolean hasMetaAnnotation(Class<? extends Annotation> type, Class<? extends Annotation> target) {
        for (Annotation ann : type.getAnnotations()) {
            if (ann.annotationType().equals(target)) return true;
            if (!isSystemAnnotation(ann.annotationType()) && hasMetaAnnotation(ann.annotationType(), target)) return true;
        }
        return false;
    }

    private static boolean isSystemAnnotation(Class<? extends Annotation> type) {
        String name = type.getName();
        return name.startsWith("java.lang") || name.startsWith("java.annotation");
    }

    public String getString(Object value) {
        if (value == null) return null;
        return String.valueOf(value);
    }

    public String getType() {
        Object value = attributes.get("type");
        return getString(value);
    }

    public String getResource() {
        Object value = attributes.get("resource");
        return getString(value);
    }

    public String getAction() {
        Object value = attributes.get("action");
        return getString(value);
    }

    public String getPhase() {
        Object value = attributes.get("phase");
        return getString(value);
    }

    public int getOrder() {
        Object value = attributes.get("order");
        if (value == null) return 0;
        return Integer.parseInt(value.toString());
    }

    public ExtendContextKey[] getExtendContextKey() {
        //keyContexts -> {ExtendContextKey[0]@28969}
        Object value = attributes.get("keyContexts");
        if (value == null) return null;
        return (ExtendContextKey[]) value;
    }

    public Class<? extends Condition> getCondition() {
        Object value = attributes.get("condition");
        if (value == null) return null;
        return (Class<? extends Condition>) value;
    }

    public boolean isStopOnError() {
        Object value = attributes.get("stopOnError");
        if (value == null) return false;
        return Boolean.parseBoolean(value.toString());
    }
}
