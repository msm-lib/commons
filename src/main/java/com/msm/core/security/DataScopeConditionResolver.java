package com.msm.core.security;

import com.msm.core.commons.Utils;
import com.msm.core.dynamicquery.ObjectMetadataFactory;
import com.msm.core.metadata.Attribute;
import com.msm.core.metadata.ObjectMetadata;
import com.msm.core.security.context.RequestContext;
import com.msm.core.security.enums.AccessScope;
import com.msm.core.security.enums.SecurityDataScopeType;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.impl.DSL;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class DataScopeConditionResolver implements DataScopeResolver {

//    @Override
//    public Condition resolve(
//            AccessScope scope,
//            RequestContext ctx,
//            Field<Object> ownerField,
//            Field<Object> orgField,
//            Field<Object> teamField) {
//
//        return switch (scope) {
//            case OWNER -> ownerField.eq(ctx.getDataScopeContext().getOwnerId());
//            case TEAM -> teamField.in(ctx.getDataScopeContext().getTeamIds());
//            case BUSINESS_UNIT -> orgField.in(ctx.getDataScopeContext().getOrgIds());
//            case PARENT_CHILD -> orgField.in(ctx.getDataScopeContext().getParentChildOrgIds());
//            case ORGANIZATION -> DSL.trueCondition();
//            case NONE -> DSL.falseCondition();
//        };
//    }

    @Override
    public Condition resolve(ObjectMetadata metadata, AccessScope scope, RequestContext context) {
        String objectName = ObjectAccessScopeResolver.resolveObjectAccessScope(metadata);
        ObjectMetadata objectMetadataResolved = ObjectMetadataFactory.getObjectMetadataByName(objectName);

        return switch (scope) {
            case OWNER -> ownerCondition(objectMetadataResolved, context);
            case TEAM -> teamCondition(objectMetadataResolved, context);
            case PARENT_CHILD -> parentChildCondition(objectMetadataResolved, context);
            case BUSINESS_UNIT -> businessUnitCondition(objectMetadataResolved, context);
            case ORGANIZATION -> DSL.trueCondition();
            default -> DSL.falseCondition();
        };
    }

    @Override
    public Condition resolve(ObjectMetadata metadata, Set<AccessScope> scopes, RequestContext context) {
        if(context.isSupperAdmin()) return DSL.trueCondition();
        String objectName = ObjectAccessScopeResolver.resolveObjectAccessScope(metadata);
        ObjectMetadata objectMetadataResolved = ObjectMetadataFactory.getObjectMetadataByName(objectName);
        return scopes.stream()
                .map(scope -> resolve(objectMetadataResolved, scope, context))
                .reduce(Condition::or)
                .orElse(DSL.falseCondition());
    }

    boolean resolve(ObjectMetadata metadata, AccessScope scope, RequestContext context, Map<String, Object> dataContext) {

        String objectName = ObjectAccessScopeResolver.resolveObjectAccessScope(metadata);
        ObjectMetadata objectMetadataResolved = ObjectMetadataFactory.getObjectMetadataByName(objectName);

        return switch (scope) {
            case OWNER -> ownerCondition(objectMetadataResolved, context, dataContext);
            case TEAM -> teamCondition(objectMetadataResolved, context, dataContext);
            case BUSINESS_UNIT -> businessUnitCondition(objectMetadataResolved, context, dataContext);
            case PARENT_CHILD -> parentChildCondition(objectMetadataResolved, context, dataContext);
            case ORGANIZATION -> Boolean.TRUE;
            default -> Boolean.FALSE;
        };
    }

    private boolean ownerCondition(ObjectMetadata metadata, RequestContext context, Map<String, Object> dataContext) {

        Attribute attr = metadata.getSecuredAttribute(SecurityDataScopeType.OWNER);
        if (attr == null) {
            return false;
        }
        Object data = dataContext.get(attr.getFieldName());
        if (data == null) {
            return false;
        }

        return Objects.equals(context.getDataScopeContext().getOwnerId(), UUID.fromString(data.toString()));
    }

    private boolean teamCondition(ObjectMetadata metadata, RequestContext context, Map<String, Object> dataContext) {

        Attribute attr = metadata.getSecuredAttribute(SecurityDataScopeType.TEAM);
        if (attr == null) {
            return false;
        }
        Object data = dataContext.get(attr.getFieldName());
        if (data == null) {
            return false;
        }

        return Utils.CL.emptyIfNull(context.getDataScopeContext().getTeamIds()).contains(UUID.fromString(data.toString()));
    }

    private boolean businessUnitCondition(ObjectMetadata metadata, RequestContext context, Map<String, Object> dataContext) {

        Attribute attr = metadata.getSecuredAttribute(SecurityDataScopeType.BUSINESS_UNIT);
        if (attr == null) {
            return false;
        }
        Object data = dataContext.get(attr.getFieldName());
        if (data == null) {
            return false;
        }

        return Utils.CL.emptyIfNull(context.getDataScopeContext().getOrgIds()).contains(UUID.fromString(data.toString()));
    }

    private boolean parentChildCondition(ObjectMetadata metadata, RequestContext context, Map<String, Object> dataContext) {

        Attribute attr = metadata.getSecuredAttribute(SecurityDataScopeType.PARENT_CHILD);
        if (attr == null) {
            return false;
        }
        Object data = dataContext.get(attr.getFieldName());
        if (data == null) {
            return false;
        }

        return Utils.CL.emptyIfNull(context.getDataScopeContext().getParentChildOrgIds()).contains(UUID.fromString(data.toString()));
    }


    @Override
    public boolean resolve(ObjectMetadata metadata, Set<AccessScope> scopes, RequestContext context, Map<String, Object> dataContext) {
        if(context.isSupperAdmin()) return Boolean.TRUE;
        String objectName = ObjectAccessScopeResolver.resolveObjectAccessScope(metadata);
        ObjectMetadata objectMetadataResolved = ObjectMetadataFactory.getObjectMetadataByName(objectName);
        return scopes.stream()
                .anyMatch(scope -> resolve(objectMetadataResolved, scope, context, dataContext));
    }




    private Condition ownerCondition(ObjectMetadata metadata, RequestContext context) {

        Attribute attr = metadata.getSecuredAttribute(SecurityDataScopeType.OWNER);

        if (attr == null) {
            return DSL.falseCondition();
        }
        Field<Object> field = (Field<Object>) attr.getField();
        return field.eq(context.getDataScopeContext().getOwnerId());
    }

    private Condition teamCondition(ObjectMetadata metadata, RequestContext context) {

        Attribute attr = metadata.getSecuredAttribute(SecurityDataScopeType.TEAM);

        if (attr == null) {
            return DSL.falseCondition();
        }
        Field<Object> field = (Field<Object>) attr.getField();
        return field.in(context.getDataScopeContext().getTeamIds());
    }

    private Condition businessUnitCondition(ObjectMetadata metadata, RequestContext context) {

        Attribute attr = metadata.getSecuredAttribute(SecurityDataScopeType.BUSINESS_UNIT);

        if (attr == null) {
            return DSL.falseCondition();
        }
        Field<Object> field = (Field<Object>) attr.getField();
        return field.in(context.getDataScopeContext().getOrgIds());
    }

    private Condition parentChildCondition(ObjectMetadata metadata, RequestContext context) {

        Attribute attr = metadata.getSecuredAttribute(SecurityDataScopeType.PARENT_CHILD);

        if (attr == null) {
            return DSL.falseCondition();
        }
        Field<Object> field = (Field<Object>) attr.getField();
        return field.in(context.getDataScopeContext().getParentChildOrgIds());
    }

    private Condition ogCondition(ObjectMetadata metadata) {

        Attribute attr = metadata.getSecuredAttribute(SecurityDataScopeType.ORGANIZATION);

        if (attr == null) {
            return DSL.falseCondition();
        }

        return DSL.trueCondition();
    }

}
