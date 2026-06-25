package com.msm.core.metadata.ref;

import java.util.Collection;

public class ProjectionRefEngine {
    public static <S, T> T project(
            S source,
            T target,
            Collection<String> paths) {

        for (String path : paths) {
            Object value = ObjectRefUtils.getProperty(source, path);
            if (value != null) {
                ObjectRefUtils.setProperty(target, path, value, MissingFieldStrategy.IGNORE);
            }
        }

        return target;
    }
}
