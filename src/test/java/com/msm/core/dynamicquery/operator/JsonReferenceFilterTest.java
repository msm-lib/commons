package com.msm.core.dynamicquery.operator;

import com.msm.core.dynamicquery.FieldResolver;
import com.msm.core.exceptions.FieldNotFoundException;
import com.msm.core.exceptions.FieldValueInvalidException;
import com.msm.core.exceptions.UnsupportedException;
import com.msm.core.filter.domain.FilterCondition;
import com.msm.core.filter.domain.FilterOperator;
import com.msm.core.metadata.Attribute;
import com.msm.core.metadata.AttributeRef;
import com.msm.core.metadata.ObjectMetadata;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.QueryPart;
import org.jooq.SQLDialect;
import org.jooq.exception.DataTypeException;
import org.jooq.impl.DSL;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Filters on a JSON reference of custom_values written without the customValues prefix
 * ({@code customerIdReference.name}), next to the forms that already worked (columns, {@code customValues.x.y}).
 */
class JsonReferenceFilterTest {

    private static final DSLContext DSL_CONTEXT = DSL.using(SQLDialect.POSTGRES);

    private static final String NAME_PATH = "((\"custom_values\" -> 'customerIdReference') ->> 'name')";

    private static final ObjectMetadata CASE = ObjectMetadata.builder()
            .name("complaintcase")
            .tableName("complaint_case")
            .attributes(List.of(
                    attribute("id", "id", "UUID", null),
                    attribute("code", "code", "String", null),
                    attribute("tags", "tags", "List<String>", null),
                    attribute("customerId", "customer_id", "UUID", AttributeRef.of("customerIdReference", "account")),
                    attribute("customValues", "custom_values", "Map<String, Object>", null)))
            .build();

    private static Attribute attribute(String fieldName, String columnName, String fieldType, AttributeRef ref) {
        return Attribute.builder().fieldName(fieldName).columnName(columnName).fieldType(fieldType).attributeRef(ref).build();
    }

    private static Condition condition(String field, FilterOperator operator, Object value) {
        FilterCondition filter = FilterCondition.builder().field(field).operator(operator).value(value).build();
        return OperatorFactory.get(operator).handle(CASE, filter);
    }

    private static String sql(QueryPart part) {
        return DSL_CONTEXT.renderInlined(part);
    }

    private static List<Object> bindValues(QueryPart part) {
        return DSL_CONTEXT.extractBindValues(part);
    }

    @Test
    void declaredReferenceResolvesInsideCustomValues() {
        FieldResolver.ResolvedField target = FieldResolver.resolveTarget(CASE, "customerIdReference.name");

        assertThat(target.json()).isTrue();
        assertThat(target.path()).isTrue();
        assertThat(target.attribute().getFieldName()).isEqualTo("customValues");
        assertThat(sql(target.field())).isEqualTo(NAME_PATH);
    }

    @Test
    void customValuesPrefixGivesTheSameField() {
        assertThat(sql(FieldResolver.resolveTarget(CASE, "customValues.customerIdReference.name").field())).isEqualTo(NAME_PATH);
    }

    @Test
    void columnIsResolvedAsBefore() {
        FieldResolver.ResolvedField target = FieldResolver.resolveTarget(CASE, "code");

        assertThat(target.json()).isFalse();
        assertThat(target.path()).isFalse();
        assertThat(sql(target.field())).isEqualTo("\"code\"");
    }

    @Test
    void wholeJsonColumnStaysAJsonField() {
        FieldResolver.ResolvedField target = FieldResolver.resolveTarget(CASE, "customValues");

        assertThat(target.json()).isTrue();
        assertThat(target.path()).isFalse();
    }

    @Test
    void undeclaredReferenceIsNotFound() {
        assertThatThrownBy(() -> condition("fooIdReference.name", FilterOperator.EQUALS, "x"))
                .isInstanceOf(FieldNotFoundException.class);
    }

    @Test
    void nestedFieldOfAColumnFiltersOnTheColumnAsBefore() {
        assertThat(sql(condition("code.name", FilterOperator.EQUALS, "x"))).isEqualTo("\"code\" = 'x'");
    }

    @Test
    void arrayAndWholeJsonColumnsKeepTheirFormerConditions() {
        // A List column is a JSON field for the lib: only paths inside a JSON column get the text comparison
        assertThat(sql(condition("tags", FilterOperator.EQUALS, null))).isEqualTo("\"tags\" = null");
        assertThat(sql(condition("customValues", FilterOperator.NOT_EQUALS, null))).isEqualTo("\"custom_values\" <> cast(null as jsonb)");
        assertThatThrownBy(() -> condition("tags", FilterOperator.IN, List.of("a", "b"))).isInstanceOf(DataTypeException.class);
    }

    @ParameterizedTest
    @ValueSource(strings = {"customerIdReference.na'me", "customerIdReference.name--", "customerIdReference..name", "a.b.c.d.e.f"})
    void pathWithAnUnexpectedSegmentIsInvalid(String path) {
        assertThatThrownBy(() -> FieldResolver.resolveTarget(CASE, path)).isInstanceOf(FieldValueInvalidException.class);
    }

    @Test
    void equalsOnAReferenceComparesText() {
        UUID id = UUID.fromString("00000000-0000-0000-0000-00000000000a");

        Condition condition = condition("customerIdReference.id", FilterOperator.EQUALS, id);

        assertThat(sql(condition)).isEqualTo("((\"custom_values\" -> 'customerIdReference') ->> 'id') = '00000000-0000-0000-0000-00000000000a'");
        assertThat(bindValues(condition)).containsExactly(id.toString());
    }

    @Test
    void equalsNullOnAReferenceIsIsNull() {
        assertThat(sql(condition("customerIdReference.name", FilterOperator.EQUALS, null))).isEqualTo(NAME_PATH + " is null");
    }

    @Test
    void notEqualsOnAReferenceComparesText() {
        assertThat(sql(condition("customerIdReference.code", FilterOperator.NOT_EQUALS, 12)))
                .isEqualTo("((\"custom_values\" -> 'customerIdReference') ->> 'code') <> '12'");
    }

    @Test
    void inOnAReferenceComparesTexts() {
        UUID id = UUID.fromString("00000000-0000-0000-0000-00000000000b");

        Condition condition = condition("customerIdReference.id", FilterOperator.IN, List.of(id, "C001"));

        assertThat(bindValues(condition)).containsExactly(id.toString(), "C001");
        assertThat(sql(condition)).startsWith("((\"custom_values\" -> 'customerIdReference') ->> 'id') in (");
    }

    @Test
    void notInOnAReferenceComparesTexts() {
        assertThat(bindValues(condition("customerIdReference.code", FilterOperator.NOT_IN, List.of(1, "C002"))))
                .containsExactly("1", "C002");
    }

    @Test
    void likeOnAReferenceMatchesItsTextWithoutAccents() {
        assertThat(sql(condition("customerIdReference.name", FilterOperator.LIKE, "Masan")))
                .isEqualTo("unaccent(lower(" + NAME_PATH + ")) like unaccent('%masan%')");
    }

    @Test
    void containsOnAReferenceMatchesThePathNotTheWholeColumn() {
        assertThat(sql(condition("customerIdReference.name", FilterOperator.CONTAINS, "Masan")))
                .isEqualTo("unaccent(lower(" + NAME_PATH + ")) like unaccent('%masan%')");
    }

    @Test
    void containsWithAListOnAReferenceIsUnsupported() {
        assertThatThrownBy(() -> condition("customerIdReference.name", FilterOperator.CONTAINS, List.of("a")))
                .isInstanceOf(UnsupportedException.class);
    }

    @Test
    void containsOneOfOnAReferenceIsUnsupported() {
        assertThatThrownBy(() -> condition("customerIdReference.name", FilterOperator.CONTAINS_ONE_OF, List.of("a")))
                .isInstanceOf(UnsupportedException.class);
    }

    @Test
    void greaterThanANumberCastsTheReferenceToNumeric() {
        assertThat(sql(condition("customerIdReference.creditLimit", FilterOperator.GREATER_THAN, 1000)))
                .isEqualTo("cast(((\"custom_values\" -> 'customerIdReference') ->> 'creditLimit') as numeric) > 1000");
    }

    @Test
    void lessThanATextComparesText() {
        assertThat(sql(condition("customerIdReference.code", FilterOperator.LESS_THAN_OR_EQUAL, "C500")))
                .isEqualTo("((\"custom_values\" -> 'customerIdReference') ->> 'code') <= 'C500'");
    }

    @Test
    void betweenOnAReferenceIsResolvedToo() {
        assertThat(sql(condition("customerIdReference.code", FilterOperator.BETWEEN, List.of("C100", "C200"))))
                .isEqualTo("((\"custom_values\" -> 'customerIdReference') ->> 'code') between 'C100' and 'C200'");
    }

    @Test
    void columnConditionsAreUnchanged() {
        assertThat(sql(condition("code", FilterOperator.EQUALS, "KN01"))).isEqualTo("\"code\" = 'KN01'");
        assertThat(sql(condition("code", FilterOperator.LIKE, "kn"))).isEqualTo("unaccent(lower(\"code\")) like unaccent('%kn%')");
        assertThat(sql(condition("code", FilterOperator.CONTAINS, "kn"))).isEqualTo("unaccent(lower(\"code\")) like unaccent('%kn%')");
    }

    @Test
    void selectAndSortResolutionStaysWithoutTheReferenceWhitelist() {
        // FieldResolver.resolve (returnFields, sorts) keeps reading any <ref>Reference key, as before this change
        assertThat(sql(FieldResolver.resolve(CASE, "fooIdReference.name")))
                .isEqualTo("((\"custom_values\" -> 'fooIdReference') ->> 'name')");
    }
}
