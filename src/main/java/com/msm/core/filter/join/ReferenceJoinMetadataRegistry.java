package com.msm.core.filter.join;


import com.msm.core.filter.domain.ReferenceJoinMetadata;

import java.util.HashMap;
import java.util.Map;

public class ReferenceJoinMetadataRegistry {

    private static final Map<Class<?>, Map<String, ReferenceJoinMetadata>> refs = new HashMap<>();

//    static {
//        register(new ReferenceJoinMetadata(
//                        "scheme",
//                        Program.class,
//                        "schemeId",
//                        Scheme.class,
//                        "id",
//                        JoinType.LEFT
//                )
//        );
//        register(new ReferenceJoinMetadata(
//                        "programRewardRule",
//                        Program.class,
//                        "schemeId",
//                        ProgramRewardRule.class,
//                        "schemeId",
//                        JoinType.LEFT
//                )
//        );
//    }


    public static void register(ReferenceJoinMetadata ref) {
        refs.computeIfAbsent(
                ref.sourceEntity(),
                k -> new HashMap<>()
        ).put(ref.name(), ref);
    }

    public static ReferenceJoinMetadata get(Class<?> source, String refName) {
        Map<String, ReferenceJoinMetadata> m = refs.get(source);
        return m != null ? m.get(refName) : null;
    }

    public static boolean has(Class<?> source, String refName) {
        return get(source, refName) != null;
    }
}
