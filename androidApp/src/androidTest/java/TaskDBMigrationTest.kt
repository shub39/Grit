/*
 * Copyright (C) 2026  Shubham Gorai
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
import androidx.room3.Room
import androidx.room3.testing.MigrationTestHelper
import androidx.sqlite.driver.AndroidSQLiteDriver
import androidx.sqlite.execSQL
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.common.truth.Truth.assertThat
import com.shub39.grit.tasks.data.database.TaskDatabase
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

private const val DB_NAME = "tasks_test.db"

@RunWith(AndroidJUnit4::class)
class TaskDBMigrationTest {
    @get:Rule
    val helper =
        MigrationTestHelper(
            InstrumentationRegistry.getInstrumentation(),
            InstrumentationRegistry.getInstrumentation().targetContext.getDatabasePath(DB_NAME),
            AndroidSQLiteDriver(),
            TaskDatabase::class,
        )

    @Before
    fun setup() {
        InstrumentationRegistry.getInstrumentation().targetContext.deleteDatabase(DB_NAME)
    }

    @Test
    fun migration4to5_containsCorrectData() = runBlocking {
        helper
            .createDatabase(4)
            .apply {
                (0..10).forEach { category ->
                    execSQL(
                        """
            INSERT INTO categories (id, name, [index], color)
            VALUES (${category.toLong()}, 'Category $category', $category, 'black')
        """
                            .trimIndent()
                    )

                    (0..10).forEach { task ->
                        execSQL(
                            """
                INSERT INTO task (categoryId, title, status, [index])
                VALUES (${category.toLong()}, 'Task $task', 0, $task)
            """
                                .trimIndent()
                        )
                    }
                }
            }
            .close()

        val db = helper.runMigrationsAndValidate(5, listOf())

        // --- Categories assertions ---
        db.prepare("SELECT COUNT(*) FROM categories").use { stmt ->
            assertThat(stmt.step()).isTrue()
            // 11 categories inserted
            assertThat(stmt.getLong(0)).isEqualTo(11L)
        }

        db.prepare("SELECT id, name, [index], color FROM categories ORDER BY id").use { stmt ->
            var count = 0
            while (stmt.step()) {
                count++
                val id = stmt.getLong(0)
                val name = stmt.getText(1)
                val index = stmt.getLong(2).toInt()
                val color = stmt.getText(3)

                assertThat(name).isEqualTo("Category $id")
                assertThat(index).isEqualTo(id.toInt())
                assertThat(color).isEqualTo("black")
            }
            assertThat(count).isEqualTo(11)
        }

        // --- Tasks assertions ---
        db.prepare("SELECT COUNT(*) FROM task").use { stmt ->
            assertThat(stmt.step()).isTrue()
            // 11 categories * 11 tasks each = 121 tasks
            assertThat(stmt.getLong(0)).isEqualTo(121L)
        }

        db.prepare(
                "SELECT categoryId, title, status, [index] FROM task ORDER BY categoryId, [index]"
            )
            .use { stmt ->
                var count = 0
                while (stmt.step()) {
                    count++
                    val categoryId = stmt.getLong(0)
                    val title = stmt.getText(1)
                    val status = stmt.getLong(2).toInt()
                    val index = stmt.getLong(3).toInt()

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
                }
                assertThat(count).isEqualTo(121)
            }
        db.close()
    }

    @Test
    fun testAllMigrations() = runBlocking {
        helper.createDatabase(4).close()

        Room.databaseBuilder(
                InstrumentationRegistry.getInstrumentation().targetContext,
                TaskDatabase::class.java,
                DB_NAME,
            )
            .build()
            .close()
    }
}
