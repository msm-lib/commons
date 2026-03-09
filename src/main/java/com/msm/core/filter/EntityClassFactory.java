package com.msm.core.filter;

import jakarta.persistence.EntityManager;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.Metamodel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class EntityClassFactory {

    private final Map<String, Class<?>> entityMap = new ConcurrentHashMap<>();

    public EntityClassFactory(EntityManager em) {
        Metamodel metamodel = em.getMetamodel();

        for (EntityType<?> entity : metamodel.getEntities()) {
            Class<?> javaType = entity.getJavaType();
            entityMap.put(entity.getName().toLowerCase(), javaType);
            entityMap.put(javaType.getSimpleName().toLowerCase(), javaType);
        }
    }

    public Class<?> resolve(String name) {
        Class<?> cls = entityMap.get(name.toLowerCase());
        if (cls == null) {
            throw new IllegalArgumentException("Unknown entity: " + name);
        }
        return cls;
    }
}
