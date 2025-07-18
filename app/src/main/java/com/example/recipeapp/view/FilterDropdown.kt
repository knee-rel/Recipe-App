package com.example.recipeapp.view

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterDropdown(
    label: String, selectedValue: String?, options: List<String>, onValueSelected: (String?) -> Unit
) {
    var expanded by remember(selectedValue) { mutableStateOf(false) }

    println("Filter dropdown: $label - recompose")
    println("   selectedValue: '$selectedValue'")

    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
        OutlinedTextField(
            value = selectedValue ?: "No selection",
            onValueChange = { },
            readOnly = true,
            label = { Text(label) },
//            placeholder = {
//                if (selectedValue.isNullOrEmpty()) {
//                    Text("Select $label")
//                }
//            },
            trailingIcon = {
                Row {
                    if (!selectedValue.isNullOrEmpty()) {
                        IconButton(onClick = {
                            println("🔴 FilterDropdown '$label' - CLEAR clicked")
                            onValueSelected(null)
                            expanded = false
                        }
                        ) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear $label")
                        }
                    }
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                }
            },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
        )

        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        println("🟢 FilterDropdown '$label' - OPTION CLICKED: '$option'")
                        println("   About to call onValueSelected with: '$option'")
                        onValueSelected(option)
                        println("   onValueSelected called successfully")
                        expanded = false
                    }
                )
            }
        }
    }
}