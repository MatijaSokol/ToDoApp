package hr.ferit.matijasokol.todoapp.other

object Constants {

    object TaskKeys {
        const val TASK = "task"
        const val TASK_NAME = "taskName"
        const val TASK_IMPORTANCE = "taskImportance"

        const val TASK_TABLE = "task_table"
        const val TASK_DATABASE = "task_database"
        const val ADD_EDIT_REQUEST_KEY = "add_edit_request"
    }

    object FilterKeys {
        const val SEARCH_QUERY_KEY = "searchQuery"
    }

    object DataStoreKeys {
        const val USER_PREFERENCES_KEY = "user_preferences"
        const val SORT_ORDER_KEY = "sort_order"
        const val HIDE_COMPLETED_KEY = "hide_completed"
    }
}