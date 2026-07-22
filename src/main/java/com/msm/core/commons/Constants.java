package com.msm.core.commons;

public final class Constants {

    public static final String OBJECT_PK = "id";
    public static final String HANDLER_KEY_TEMPLATE = "{0}:{1}:{2}";
    public static final String HOOK_KEY_TEMPLATE = "{0}:{1}:{2}:{3}";
    public static final String HANDLER_PREFIX = "handler";
    public static final String HOOK_PREFIX = "hook";
    public static final String ACTION_CONVERSION_PREFIX = "Conversion";
    public static final String RESOURCE_QUERY_PREFIX = "query-*";
    public static final String GENERIC_RESOURCE_NAME = "*";
    public static final String GENERIC_SYSTEM_OBJECT = "SystemObject-*";
    public static final String GENERIC_OBJECT_RECORD_TYPE = "RecordTypeHook";
    public static final String OBJECT_CONVERSION_ACTION = "convertObject-*";
    public static final class FilterAction {
        public static final String FILTER_OBJECT = "filterObject";
        public static final String FILTER_ALL_OBJECT = "filterAllObject";
        public static final String FILTER_ALL_OBJECT_BY_IDS = "filterAllObjectByIds";
        public static final String FILTER_OBJECT_BY_ID = "filterById";
        public static final String LOOKUP_OBJECT = "lookupObject";
    }

    public static final String IS_DELETED_FIELD = "isDeleted";

    public static final String INVALID_DATA_TYPE = "{0} loại dữ liệu không hợp lệ";
    public static final String REQUIRE_INPUT_VALUE = "{0} bắt buộc";
    public static final String INVALID_INPUT_VALUE = "{0} giá trị không hợp lệ";
    public static final String DEFAULT_TEXT_REGEX = "^[\\p{L}0-9_.(),'/$€£¥₹₽\\- \\n]*$";
    //Hook event name
    public static final String HOOK_HANDLER_BEFORE_EVENT_NAME = "objectHandlerBeforeEvent";
    public static final String HOOK_HANDLER_AFTER_EVENT_NAME = "objectHandlerAfterEvent";
    public static final String HOOK_HANDLER_AFTER_COMMIT_EVENT_NAME = "objectHandlerAfterCommitEvent";
    public static final class Action {
        public static final String CREATE = "create";
        public static final String BULK_CREATE = "bulk_create";
        public static final String UPDATE = "update";
        public static final String BULK_UPDATE = "bulk_update";
        public static final String DELETE = "delete";
        public static final String BULK_DELETE = "bulk_delete";
        public static final String IMPORT_OBJECT = "import_object";
        public static final String IMPORT_FILE_OBJECT = "import_file_object";
    }
    //For audit
    public static final String CREATED_AT = "createdAt";
    public static final String UPDATED_AT = "updatedAt";
    public static final String DELETED_AT = "deletedAt";
    public static final String CREATED_BY = "createdBy";
    public static final String UPDATED_BY = "updatedBy";
    public static final String DELETED_BY = "deletedBy";
    public static final String CREATED_BY_ID = "createdById";
    public static final String UPDATED_BY_ID = "updatedById";
    public static final String DELETED_BY_ID = "deletedById";
    public static final String IS_DELETED = "isDeleted";
    public static final String VERSION = "version";
    public static final String CUSTOM_VALUE_NAME = "customValues";
    public static final String ATTRIBUTE_REF_TEMPLATE = "{0}Reference";
    public static final String REFERENCE_SUFFIX = "Reference";
    public static final String CONSTRAINT_KEY = "{0}_{1}_{2}";

    private Constants() {}
}
