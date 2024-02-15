package github.swissonid.selectionthings.ui

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import github.swissonid.selectionthings.ui.theme.SelectionThingsTheme

data class ChipConfig(
    val text: String,
    val key: String = text,
    val isSelected: Boolean = false,
    val onClick: (() -> Unit)? = null,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun InternalChip(config: ChipConfig) {
    FilterChip(
        selected = config.isSelected,
        label = { Text(config.text) },
        onClick = { config.onClick?.invoke() }
    )
}

@Composable
fun ChipsGroup(
    chips: List<ChipConfig>,
    selected: ChipConfig? = null,
    onChipSelected: ((ChipConfig) -> Unit)? = null,
) {
    Log.d("ChipsGroup", "\n")
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(8.dp),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        items(
            count = chips.size,
        ) { index ->
            var currentConfig = chips[index]
            if (currentConfig.key == selected?.key) {
                currentConfig = currentConfig.copy(isSelected = true)
            }
            val internalConfig = currentConfig.copy(onClick = {
                val newConfig = currentConfig.copy(isSelected = !currentConfig.isSelected)
                onChipSelected?.invoke(newConfig)
                currentConfig.onClick?.invoke()
            })
            Log.d(
                "ChipsGroup",
                "SelectedConfig: ${selected?.key} isSelected:${selected?.isSelected}${internalConfig.key} isSelected=${internalConfig.isSelected}"
            )
            InternalChip(internalConfig)
        }
    }
}


val chipConfigs = (1..3).map { ChipConfig(text = "Chip $it") }

@Composable
fun ChipAndTextField() {
    val (textValue, setTextValue) = remember { mutableStateOf("") }
    val (selectedConfig, setSelectedConfig) = remember { mutableStateOf<ChipConfig?>(null) }
    TextField(
        modifier = Modifier.fillMaxWidth(),
        value = textValue, onValueChange = { currentText ->
            setTextValue(currentText)
            if (currentText.length < 6) return@TextField
            val foundIndex =
                chipConfigs.indexOfFirst { it.text.lowercase() == currentText.lowercase() }
            if (foundIndex == -1) setSelectedConfig(null)
            else setSelectedConfig(chipConfigs[foundIndex])
        })
    ChipsGroup(
        selected = selectedConfig,
        onChipSelected = {
            if (it.isSelected) {
                setTextValue(it.text)
                setSelectedConfig(it)
            } else {
                setTextValue("")
                setSelectedConfig(null)
            }
        },
        chips = chipConfigs
    )
}

@Preview
@Composable
fun ChipGroupScreen() {
    SelectionThingsTheme {
        Surface {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                ChipAndTextField()
            }
        }
    }
}

