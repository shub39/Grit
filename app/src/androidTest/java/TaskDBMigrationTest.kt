import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.common.truth.Truth.assertThat
import com.shub39.grit.tasks.data.database.TaskDatabase
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

private const val DB_NAME = "tasks_test.db"

@RunWith(AndroidJUnit4::class)
class TaskDBMigrationTest {
    @get:Rule
    val helper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        TaskDatabase::class.java,
        listOf(),
        FrameworkSQLiteOpenHelperFactory()
    )

    @Test
    fun migration4to5_containsCorrectData() {
        helper.createDatabase(DB_NAME, 4).apply {
            (0..10).forEach { category ->
                execSQL("""
            INSERT INTO categories (id, name, [index], color)
            VALUES (${category.toLong()}, 'Category $category', $category, 'black')
        """.trimIndent())

                (0..10).forEach { task ->
                    execSQL("""
                INSERT INTO task (categoryId, title, status, [index])
                VALUES (${category.toLong()}, 'Task $task', 0, $task)
            """.trimIndent())
                }
            }
        }

        val db = helper.runMigrationsAndValidate(DB_NAME, 5, true)

        db.query("SELECT * FROM categories").apply {
            assertThat(moveToFirst()).isTrue()
        }
    }
}