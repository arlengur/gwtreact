import keyMirror from 'key-mirror-nested';

export default keyMirror({
    OVERVIEW_CHANGE_VIEW: null,
    CHANNELS_OPTIMISTIC_ADD: null,
    CHANNELS_PAGE_SIZE: null,
    CHANNELS_SET_PAGE: null,
    CHANNELS_TOGGLE_SEVERITY: null,
    CHANNELS_SET_ORDER: null,
    CHANNELS_SET_FAVOURITE: null,
    TREE_CLICK: null,
    DETAILED_TREE_UNSELECT_ALL: null,
    DETAILED_TREE_SELECT_TASKS: null,
    DETAILED_TREE_UNSELECT_TASKS: null,
    GROUP_FILTER_TOGGLE: null,
    SEVERITY_FILTER_TOGGLE: null,
    ACTIVITY_FILTER_TOGGLE: null,
    TOGGLE_CREATE_CHANNEL: null,
    TOGGLE_EDIT_CHANNEL: null,
    TOGGLE_ACTIVATE_CHANNEL: null,
    SELECT_INTERVAL: null,
    CHANNEL_CRUD_LOAD_PROBES: null,
    CHANNEL_CRUD_SELECT_TASKS: null,
    CHANNEL_CRUD_UNSELECT_TASKS: null,
    CHANNEL_CRUD_SET_NAME: null,
    CHANNEL_CRUD_SET_INTERVAL: null,
    CHANNEL_CRUD_SET_LOGO: null,
    CHANNEL_CRUD_NEW_IMAGE: null,
    CLEAR_DATA_LOAD_INDICATOR: null,
    CRUD_UPDATE_SET: null,
    CRUD_CREATE_SET: null,
    ALERT_UPDATE: null,
    ADD_COMMENT: null,
    CONFIRM_MODAL_SHOW: null,
    CONFIRM_MODAL_HIDE: null,
    UPDATE_PROBE_STATE: null,
    COMMENT_POPUP_HIDE: null,
    COMMENT_POPUP_LOAD_COMMENTS: null,
    COMMENT_POPUP_OPEN: null,
    ERROR_MODAL_SHOW: null,
    ERROR_MODAL_HIDE: null,
    
    PROBE_CONFIG: {
        TREE: {
            PROBE_SELECT: null,
            TASK_SELECT: null,
            TOGGLE_ALL: null,
            TOGGLE_EXPANDED: null,
            UPDATE_FILTER: null,
            START_RESIZE: null,
            STOP_RESIZE: null,
            RESIZE_COLUMNS: null
        },
        RESPONSE: {
            PROBE_STATS: null,
            CONFIG_UPDATE: null,
            SW_RESTART: null,
            HW_RESTART: null,
            CONFIG_ROLLBACK: null,
            SW_UPDATE: null,
            SW_LIST: null
        },
        MODAL: {
            CONFIG_EDITOR: null,
            UPDATE_SW: null
        }
    },

    POLICY: {
        DIALOG: {
            TOGGLE_CREATE: null,
            LOAD_MODULES: null,
            MODULES_LOADED: null,
            SELECT_MODULE: null,
            PARAMS_LOADED: null,
            SELECT_PARAM: null,
            TASKS_LOADED: null,
            SELECT_TASK: null,
            PROBES_LOADED: null,
            SELECT_TASKS: null,
            SELECT_PROBES: null,
            SET_CONDITIONS: null,
            ADD_NOTIFICATION: null,
            REMOVE_NOTIFICATION: null
        }
    },

    RECORD_SCHEDULE: {
        AGENTS_LOADED: null,
        PROBE_SELECT: null,
        TOGGLE_ALL_PROBES: null,
        TASK_SELECT: null,
        TOGGLE_ALL_TASKS: null,
        TASKS_LOADED: null,
        TOGGLE_CREATE: null,
        TOGGLE_EDIT: null,
        TOGGLE_CLOSE: null,
        TIMEZONES_LOADED: null,
        ADD_EVENT_LINE: null,
        REMOVE_EVENT_LINE: null,
        UPDATE_SCHEDULE: null,
        UPDATE_PROBE_FILTER: null,
        UPDATE_TASK_FILTER: null
    }
});
