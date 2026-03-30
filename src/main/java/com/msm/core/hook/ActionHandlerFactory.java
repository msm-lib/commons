package com.msm.core.hook;
import com.msm.core.commons.Utils;
import com.msm.core.exceptions.DuplicateKeyException;
import com.msm.core.exceptions.UnsupportedActionException;
import com.msm.core.hook.common.ActionHandler;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public final class ActionHandlerFactory {

    private ActionHandlerFactory() {}
    private static final Map<String, ActionHandler> INSTANCES = new ConcurrentHashMap<>();

    public static void register(ActionHandler instance) {
        if(INSTANCES.containsKey(instance.getAction())) {
            throw new DuplicateKeyException(Utils.STR.format("Duplicate for action: {0}, with handler: {1}", instance.getAction(), instance.getClass().getName()));
        }
        INSTANCES.put(instance.getAction(), instance);
    }

    public static ActionHandler getHandler(String action) {
        return INSTANCES.get(action);
    }

    public static boolean contains(String compositeKey) {
        return INSTANCES.containsKey(compositeKey);
    }

}
