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
package com.shub39.grit.core.tasks.presentation.ui.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions.Companion.Default
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonShapes
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.shub39.grit.core.shared_ui.GritBottomSheet
import com.shub39.grit.core.tasks.domain.Category
import grit.shared.core.generated.resources.Res
import grit.shared.core.generated.resources.add
import grit.shared.core.generated.resources.add_category
import grit.shared.core.generated.resources.done
import grit.shared.core.generated.resources.edit
import grit.shared.core.generated.resources.edit_categories
import grit.shared.core.generated.resources.save
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun CategoryUpsertSheet(
    isEditSheet: Boolean = false,
    modifier: Modifier = Modifier,
    category: Category,
    onDismiss: () -> Unit,
    onUpsertCategory: (Category) -> Unit,
) {
    var newCategory by remember { mutableStateOf(category) }

    GritBottomSheet(
        modifier = modifier.imePadding(),
        padding = 0.dp,
        onDismissRequest = onDismiss,
    ) {
        val keyboardController = LocalSoftwareKeyboardController.current
        val focusRequester = remember { FocusRequester() }

        LaunchedEffect(Unit) {
            delay(200)
            focusRequester.requestFocus()
            keyboardController?.show()
        }

        Icon(
            imageVector = vectorResource(if (isEditSheet) Res.drawable.edit else Res.drawable.add),
            contentDescription = "Add",
        )
        Text(
            text =
                stringResource(
                    if (isEditSheet) Res.string.edit_categories else Res.string.add_category
                ),
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center,
        )
        HorizontalDivider()

        OutlinedTextField(
            value = newCategory.name,
            onValueChange = { newCategory = category.copy(name = it) },
            shape = MaterialTheme.shapes.medium,
            keyboardOptions =
                Default.copy(
                    capitalization = KeyboardCapitalization.Sentences,
                    imeAction = ImeAction.Done,
                ),
            singleLine = true,
            modifier = Modifier.fillMaxWidth().padding(16.dp).focusRequester(focusRequester),
        )

        HorizontalDivider()
        Button(
            onClick = { onUpsertCategory(newCategory) },
            shapes =
                ButtonShapes(
                    shape = MaterialTheme.shapes.extraLarge,
                    pressedShape = MaterialTheme.shapes.small,
                ),
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            enabled = newCategory.name.isNotBlank() && newCategory.name.length <= 20,
        ) {
            Text(text = stringResource(if (isEditSheet) Res.string.done else Res.string.save))
        }
    }
}
