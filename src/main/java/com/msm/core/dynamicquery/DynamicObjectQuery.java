package com.msm.core.dynamicquery;

import com.msm.core.dynamicquery.command.DynamicDelete;
import com.msm.core.dynamicquery.command.DynamicInsert;
import com.msm.core.dynamicquery.command.DynamicUpdate;
import com.msm.core.dynamicquery.query.DynamicFilterQuery;

/**
 * Unified dynamic data access API for metadata-driven entities.
 *
 * <p>Combines all supported CRUD operations:
 * <ul>
 *     <li>Query and filtering ({@link DynamicFilterQuery})</li>
 *     <li>Insert operations ({@link DynamicInsert})</li>
 *     <li>Update operations ({@link DynamicUpdate})</li>
 *     <li>Delete operations ({@link DynamicDelete})</li>
 * </ul>
 *
 * <p>This interface serves as the primary entry point for
 * dynamic object persistence and retrieval, allowing consumers
 * to perform metadata-driven CRUD operations without requiring
 * compile-time entity classes.
 */
public interface DynamicObjectQuery
        extends DynamicFilterQuery,
        DynamicInsert,
        DynamicUpdate,
        DynamicDelete {
}