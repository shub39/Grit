import androidx.room.Room
import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.common.truth.Truth.assertThat
import com.shub39.grit.habits.data.database.HabitDatabase
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset

private const val DB_NAME = "habits_test.db"

@RunWith(AndroidJUnit4::class)
class HabitDBMigrationTest {
    @get:Rule
    val helper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        HabitDatabase::class.java,
        listOf(),
        FrameworkSQLiteOpenHelperFactory()
    )

    @Test
    fun migration4to5_containsCorrectData() {
        helper.createDatabase(DB_NAME, 4).apply {
            (1..5).forEach { habit ->

                val timeEpoch = LocalDateTime.now().minusDays(habit.toLong())
                    .toEpochSecond(ZoneOffset.UTC)

                execSQL("""
                INSERT INTO habit_index (title, description, [index], days, time)
                VALUES (
                    'Habit $habit',
                    'Description for habit $habit',
                    $habit,
                    'MONDAY,TUESDAY',              
                    $timeEpoch  
                )
            """.trimIndent())

                (1..3).forEach { offset ->

                    val dateEpoch = LocalDate.now().minusDays(offset.toLong()).toEpochDay()

                    execSQL("""
                    INSERT INTO habit_status (habitId, date)
                    VALUES (
                        $habit,
                        $dateEpoch        
                    )
                """.trimIndent())
                }
            }
        }

        val db = helper.runMigrationsAndValidate(DB_NAME, 5, true)

        // --- Habits assertions ---
        db.query("SELECT COUNT(*) FROM habit_index").use { cursor ->
            assertThat(cursor.moveToFirst()).isTrue()
            assertThat(cursor.getInt(0)).isEqualTo(5) // inserted 5 habits
        }

        db.query("SELECT id, title, description, [index], days, time, reminder FROM habit_index ORDER BY id")
            .use { cursor ->
                assertThat(cursor.count).isEqualTo(5)
                cursor.moveToFirst()
                do {
                    val id = cursor.getLong(0)
                    val title = cursor.getString(1)
                    val description = cursor.getString(2)
                    val index = cursor.getInt(3)
                    val days = cursor.getString(4)
                    val time = cursor.getLong(5)
                    val reminder = cursor.getInt(6)

                    // Title & description patterns
                    assertThat(title).isEqualTo("Habit $id")
                    assertThat(description).isEqualTo("Description for habit $id")

                    // Index matches habit number
                    assertThat(index).isEqualTo(id.toInt())

                    // Days stored as string
                    assertThat(days).isEqualTo("MONDAY,TUESDAY")

                    // Time is a positive epoch seconds value
                    assertThat(time).isGreaterThan(0)

                    // Reminder default
                    assertThat(reminder).isEqualTo(1)
                } while (cursor.moveToNext())
            }

        // --- HabitStatus assertions ---
        db.query("SELECT COUNT(*) FROM habit_status").use { cursor ->
            assertThat(cursor.moveToFirst()).isTrue()
            // 5 habits × 3 status rows each = 15
            assertThat(cursor.getInt(0)).isEqualTo(15)
        }

        db.query("SELECT habitId, date FROM habit_status ORDER BY habitId, date")
            .use { cursor ->
                assertThat(cursor.count).isEqualTo(15)
                cursor.moveToFirst()
                do {
                    val habitId = cursor.getLong(0)
                    val dateEpoch = cursor.getLong(1)

                    // HabitId must reference a valid habit
                    assertThat(habitId).isAtLeast(1L)
                    assertThat(habitId).isAtMost(5L)

                    // Date should be a valid epochDay (not in the future)
                    assertThat(dateEpoch).isAtMost(LocalDate.now().toEpochDay())
                } while (cursor.moveToNext())
            }
    }

    @Test
    fun testAllMigrations() {
        helper.createDatabase(DB_NAME, 4).apply { close() }

        Room.databaseBuilder(
            InstrumentationRegistry.getInstrumentation().targetContext,
            HabitDatabase::class.java,
            DB_NAME
        ).build().apply {
            openHelper.writableDatabase.close()
        }
    }
}