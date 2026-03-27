package com.msm.core.hook;
import com.msm.core.hook.common.ActionHandler;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public final class ActionHandlerFactory {

    private ActionHandlerFactory() {}
    private static final Map<String, ActionHandler> INSTANCES = new ConcurrentHashMap<>();

    public static void register(ActionHandler instance) {
        INSTANCES.putIfAbsent(instance.getAction(), instance);
    }

    public static ActionHandler getHandler(String action) {
        return INSTANCES.get(action);
    }

    public static boolean contains(String compositeKey) {
        return INSTANCES.containsKey(compositeKey);
    }

}
