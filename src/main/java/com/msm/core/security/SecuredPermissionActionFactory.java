package com.msm.core.security;

import com.msm.core.commons.Utils;
import com.msm.core.security.enums.PermissionAction;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

@Slf4j
public class SecuredPermissionActionFactory {

    private final static Map<String, List<PermissionAction>> INSTANCES = new ConcurrentHashMap<>();

    public static void register(String action, PermissionAction permissionAction) {
        INSTANCES.computeIfAbsent(action, key -> new ArrayList<>()).add(permissionAction);
    }

    public static void register(String action, PermissionAction[] permissionActions) {
        (INSTANCES.computeIfAbsent(action, (key) -> new ArrayList<>())).addAll(Stream.of(permissionActions).toList());
    }

    public static List<PermissionAction> getByAction(String action) {
        List<PermissionAction> permissionActions = INSTANCES.get(action);
        if (Objects.isNull(permissionActions)) {
            return Utils.CL.newArrayList();
        }

        return permissionActions;
    }

    public static boolean contains(String action) {
        return INSTANCES.containsKey(action);
    }
}
