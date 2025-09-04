import androidx.room.Room
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

        // --- Categories assertions ---
        db.query("SELECT COUNT(*) FROM categories").use { cursor ->
            assertThat(cursor.moveToFirst()).isTrue()
            // 11 categories inserted
            assertThat(cursor.getInt(0)).isEqualTo(11)
        }

        db.query("SELECT id, name, [index], color FROM categories ORDER BY id").use { cursor ->
            assertThat(cursor.count).isEqualTo(11)
            cursor.moveToFirst()
            do {
                val id = cursor.getLong(0)
                val name = cursor.getString(1)
                val index = cursor.getInt(2)
                val color = cursor.getString(3)

                assertThat(name).isEqualTo("Category $id")
                assertThat(index).isEqualTo(id.toInt())
                assertThat(color).isEqualTo("black")
            } while (cursor.moveToNext())
        }

        // --- Tasks assertions ---
        db.query("SELECT COUNT(*) FROM task").use { cursor ->
            assertThat(cursor.moveToFirst()).isTrue()
            // 11 categories * 11 tasks each = 121 tasks
            assertThat(cursor.getInt(0)).isEqualTo(11 * 11)
        }

        db.query("SELECT categoryId, title, status, [index] FROM task ORDER BY categoryId, [index]").use { cursor ->
            assertThat(cursor.count).isEqualTo(121)
            cursor.moveToFirst()
            do {
                val categoryId = cursor.getLong(0)
                val title = cursor.getString(1)
                val status = cursor.getInt(2)
                val index = cursor.getInt(3)

                // Title pattern
                assertThat(title).isEqualTo("Task $index")
                // Default status
                assertThat(status).isEqualTo(0)
                // Index matches the looped task value
                assertThat(index).isAtLeast(0)
                assertThat(index).isAtMost(10)
                // CategoryId matches existing categories
                assertThat(categoryId).isAtLeast(0)
                assertThat(categoryId).isAtMost(10)
            } while (cursor.moveToNext())
        }
    }

    @Test
    fun testAllMigrations() {
        helper.createDatabase(DB_NAME, 4).apply { close() }

        Room.databaseBuilder(
            InstrumentationRegistry.getInstrumentation().targetContext,
            TaskDatabase::class.java,
            DB_NAME
        ).build().apply {
            openHelper.writableDatabase.close()
        }
    }
}