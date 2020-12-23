package hr.ferit.matijasokol.todoapp.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import hr.ferit.matijasokol.todoapp.data.Task
import hr.ferit.matijasokol.todoapp.data.TaskDatabase
import hr.ferit.matijasokol.todoapp.other.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Provider
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object RoomModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context,
        @RoomCreateCallback callback: RoomDatabase.Callback
    ) = Room.databaseBuilder(context, TaskDatabase::class.java, Constants.TaskKeys.TASK_DATABASE)
        .fallbackToDestructiveMigration()
        .addCallback(callback)
        .build()

    @Provides
    fun provideTaskDao(taskDatabase: TaskDatabase) = taskDatabase.taskDao()

    @Provides
    @Singleton
    @RoomCreateCallback
    fun provideDatabaseCreateCallback(
        taskDatabase: Provider<TaskDatabase>,
        @ApplicationScope applicationScope: CoroutineScope
    ) = object : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)

            val dao = taskDatabase.get().taskDao()

            applicationScope.launch {
                dao.insert(Task("First task"))
                dao.insert(Task("Second task"))
                dao.insert(Task("Third task", important = true))
                dao.insert(Task("Fourth task", completed = true))
                dao.insert(Task("Fifth task"))
                dao.insert(Task("Sixth task", completed = true))
                dao.insert(Task("Seventh task"))
                dao.insert(Task("Eighth task"))
            }
        }
    }
}