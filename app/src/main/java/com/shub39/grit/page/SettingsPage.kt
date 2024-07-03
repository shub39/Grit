package com.shub39.grit.page

import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.shub39.grit.R
import com.shub39.grit.database.Datastore
import com.shub39.grit.ui.theme.Typography
import com.shub39.grit.viewModel.TaskListViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsPage(
    taskListViewModel: TaskListViewModel
) {

    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val currentTheme by Datastore.getTheme(context).collectAsState(initial = "Default")
    val currentClearPreference by Datastore.clearPreferences(context).collectAsState(initial = "Daily")
    var theme = currentTheme

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = stringResource(id = R.string.material_theme))
                Switch(
                    checked = currentTheme == "Material",
                    onCheckedChange = {
                        if (theme != "Material") {
                            theme = "Material"
                            coroutineScope.launch {
                                Datastore.setTheme(context, "Material")
                            }
                        } else {
                            theme = "Default"
                            coroutineScope.launch {
                                Datastore.setTheme(context, "Default")
                            }
                        }
                    }
                )
            }
        }

        Spacer(modifier = Modifier.padding(8.dp))

        Text(text = stringResource(id = R.string.clear_preference))
        Spacer(modifier = Modifier.padding(4.dp))
        SingleChoiceSegmentedButtonRow {
            listOf("Daily", "Weekly", "Monthly", "Never").forEachIndexed { index, preference ->
                SegmentedButton(
                    selected = currentClearPreference == preference,
                    onClick = {
                        coroutineScope.launch {
                            taskListViewModel.cancelScheduleDeletion(currentClearPreference)
                            Datastore.setClearPreferences(context, preference)
                            taskListViewModel.scheduleDeletion(preference)
                        }
                    },
                    shape = when (index) {
                        0 -> RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp)
                        3 -> RoundedCornerShape(topEnd = 16.dp, bottomEnd = 16.dp)
                        else -> RectangleShape
                    }
                ) {
                    Text(text = preference)
                }
            }
        }

        Spacer(modifier = Modifier.padding(8.dp))

        ElevatedCard(
            modifier = Modifier
                .padding(top = 8.dp, bottom = 8.dp),
            onClick = {
                val intent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://www.github.com/shub39/Grit")
                )
                context.startActivity(intent)
            }
        ) {
            Text(
                text = stringResource(id = R.string.made_by) + " shub39",
                style = Typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().padding(8.dp)
            )
        }
    }

}