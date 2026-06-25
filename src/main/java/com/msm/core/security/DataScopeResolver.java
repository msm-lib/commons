package com.msm.core.security;

import com.msm.core.metadata.ObjectMetadata;
import com.msm.core.security.context.RequestContext;
import com.msm.core.security.enums.AccessScope;
import org.jooq.Condition;
import org.jooq.Field;

import java.util.Map;
import java.util.Set;

public interface DataScopeResolver {

    Condition resolve(
            AccessScope scope,
            RequestContext context,
            Field<Object> ownerField,
            Field<Object> orgField,
            Field<Object> teamField);

    Condition resolve(
            ObjectMetadata metadata,
            AccessScope scope,
            RequestContext context);

    Condition resolve(
            ObjectMetadata metadata,
            Set<AccessScope> scopes,
            RequestContext context);

    boolean resolve(
            ObjectMetadata metadata,
            Set<AccessScope> scopes,
            RequestContext context,
            Map<String, Object> dataContext);
}