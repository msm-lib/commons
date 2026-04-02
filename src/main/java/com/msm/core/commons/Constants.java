package com.msm.core.commons;

public final class Constants {

    public static final String OBJECT_PK = "id";
    public static final String OBJECT_HOOK_KEY = "{0}:{1}:{2}";
    public static final String ACTION_PREFIX = "Action";
    public static final String HOOK_PREFIX = "Hook";
    public static final String GENERIC_OBJECT_NAME = "GenericObject";
    public static final String GENERIC_OBJECT_RECORD_TYPE = "RecordTypeHook";

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
    };

    private Constants() {}
}
