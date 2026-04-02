package com.msm.core.filter;

import jakarta.persistence.EntityManager;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.Metamodel;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class EntityClassFactory {

    private final Map<String, EntityType<?>> entityTypeMap = new ConcurrentHashMap<>();

    public EntityClassFactory(EntityManager em) {
        Metamodel metamodel = em.getMetamodel();
        for (EntityType<?> entity : metamodel.getEntities()) {
            entityTypeMap.put(entity.getName().toLowerCase(), entity);
        }
    }

    public EntityType<?> getEntityType(String name) {
        EntityType<?> type = entityTypeMap.get(name.toLowerCase());
        if (Objects.isNull(type)) {
            throw new IllegalArgumentException("Unknown entity type: " + name);
        }
        return type;
    }
}
