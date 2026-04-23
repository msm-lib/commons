package com.msm.core.hook.context;

import com.msm.core.commons.Utils;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
public class LogContext {

    private List<String> logs = new ArrayList<>();

    public LogContext() {
        logs.add("[COMMAND-PIPELINE] - Start execution for: " + this.getClass().getSimpleName());
        logs.add("---");
    }

    public void add(String cmd) {
        logs.add(cmd);
    }

    public void writeLogDiff(Object oldState, Object newState, String actionName) {
        Map<String, Object> oldMap = Utils.CL.emptyIfNull(Utils.O.toMap(oldState));
        Map<String, Object> newMap = Utils.CL.emptyIfNull(Utils.O.toMap(newState));

        Map<String, String> diffs = new HashMap<>();
        newMap.forEach((key, val) -> {
            if (!Objects.equals(val, oldMap.get(key))) {
                diffs.put(key, oldMap.get(key) + " -> " + val);
            }
        });

        if (diffs.isEmpty()) {
            logs.add(String.format("%d. [%s] -> No Change%n", logs.size() + 1, actionName));
        } else {
            logs.add(String.format("%d. [%s] -> Changed: %s%n", logs.size() + 1, actionName, diffs));
        }
    }

}
