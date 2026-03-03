package com.msm.core.filter.join;

import com.msm.core.filter.domain.JoinKey;

import java.util.LinkedHashSet;
import java.util.Set;

public class JoinUsageTracker {

    private final Set<JoinKey> filterJoins = new LinkedHashSet<>();
    private final Set<JoinKey> selectJoins = new LinkedHashSet<>();

    public void markFilter(JoinKey key) {
        filterJoins.add(key);
    }

    public void markSelect(JoinKey key) {
        selectJoins.add(key);
    }

    public Set<JoinKey> forSelect() {
        Set<JoinKey> all = new LinkedHashSet<>(filterJoins);
        all.addAll(selectJoins);
        return all;
    }

    public Set<JoinKey> forCount() {
        return filterJoins;
    }
}
