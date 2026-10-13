package com.msm.core.dynamicquery.command;

import com.msm.core.commons.Utils;
import com.msm.core.exceptions.CommonErrors;
import com.msm.core.metadata.ObjectMetadata;
import org.jooq.Field;

import java.util.List;
import java.util.Map;

/** Checks shared by the dynamic insert and update commands. */
final class WriteRows {
    private WriteRows() {}

    /**
     * Returns the columns of a row to write.
     *
     * @param values the row as given by the caller
     * @param row    its columns to write
     * @throws com.msm.core.exceptions.FieldValueRequiredException if the row has nothing to write: no key of the
     *         metadata (typically misspelled field names, listed in the message), or only keys that are not written
     *         (null values of an insert, the primary key of an update)
     */
    static Map<Field<?>, Object> requireColumns(ObjectMetadata meta, Map<String, Object> values, Map<Field<?>, Object> row, int rowIndex) {
        if (!row.isEmpty()) {
            return row;
        }
        Map<String, Object> input = Utils.CL.emptyIfNull(values);
        List<String> unknownKeys = input.keySet().stream()
                .filter(key -> meta.getAttributeByName(key) == null)
                .sorted()
                .toList();
        List<String> knownKeys = input.keySet().stream()
                .filter(key -> meta.getAttributeByName(key) != null)
                .sorted()
                .toList();

        String reason;
        if (input.isEmpty()) {
            reason = "no values";
        } else if (knownKeys.isEmpty()) {
            reason = "unknown keys " + unknownKeys;
        } else {
            reason = "nothing to write in " + knownKeys + (unknownKeys.isEmpty() ? "" : ", unknown keys " + unknownKeys);
        }
        throw CommonErrors.required("values",
                "Row " + rowIndex + " of object " + meta.getName() + " has no field to write: " + reason);
    }
}
