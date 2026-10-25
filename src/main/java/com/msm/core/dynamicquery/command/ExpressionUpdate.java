package com.msm.core.dynamicquery.command;

import org.jooq.Condition;

import java.util.Map;

/**
 * One row of {@link DynamicUpdate#batchUpdateWithExpressions}: the row with this id gets these values, a value being
 * a static value or an expression ({@link org.jooq.Field}, e.g. {@code quantity + 5}), when the guard (if any) holds.
 *
 * @param id     primary key of the row
 * @param values field name to value or expression; the primary key and a version are ignored
 * @param guard  extra condition the row must meet to be updated (e.g. {@code exchanged + 5 <= quantity}), or {@code null}
 */
public record ExpressionUpdate(Object id, Map<String, Object> values, Condition guard) {

    public static ExpressionUpdate of(Object id, Map<String, Object> values) {
        return new ExpressionUpdate(id, values, null);
    }
}
