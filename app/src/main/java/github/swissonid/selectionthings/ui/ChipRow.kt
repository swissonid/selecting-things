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

/**
 * The default implementation of onChipSelectionBlock
 * It updates the text value and the selectedConfig based on the chip selection
 * @param chips: List<ChipConfig> The list of chips
 * @param chipConfig: ChipConfig The selected chip
 * @param setInternalTextValue: (String) -> Unit The function to update the text value
 * @param setInternalSelectedConfig: (ChipConfig?) -> Unit The function to update the selectedConfig
 * @param onChipSelected: ((ChipConfig) -> Unit)? The callback to be invoked after the selection
 */
fun defaultOnChipSelectionBlock(
    chips: List<ChipConfig>,//unused
    chipConfig: ChipConfig,
    setInternalTextValue: (String) -> Unit,
    setInternalSelectedConfig: (ChipConfig?) -> Unit,
    onChipSelected: ((ChipConfig) -> Unit)? = null
) {
    if (chipConfig.isSelected) {
        setInternalTextValue(chipConfig.text)
        setInternalSelectedConfig(chipConfig)
    } else {
        setInternalTextValue("")
        setInternalSelectedConfig(null)
    }
    onChipSelected?.invoke(chipConfig)
}

/**
 * The block to be invoked when a chip is selected
 * @param chips: List<ChipConfig> The list of chips
 * @param chipConfig: ChipConfig The selected chip
 * @param setInternalTextValue: (String) -> Unit The function to update the text value
 * @param setInternalSelectedConfig: (ChipConfig?) -> Unit The function to update the selectedConfig
 * @param onChipSelected: ((ChipConfig) -> Unit)? The callback to be invoked after the selection
 */
typealias OnChipSelectionBlock = (
    chips: List<ChipConfig>,
    chipConfig: ChipConfig,
    setInternalTextValue: (String) -> Unit,
    setInternalSelectedConfig: (ChipConfig?) -> Unit,
    onChipSelected: ((ChipConfig) -> Unit)?
) -> Unit


@Composable
fun ChipAndTextField(
    chipConfigs: List<ChipConfig>,
    textValue: String = "",
    selectedConfig: ChipConfig? = null,
    onChipSelected: ((ChipConfig) -> Unit)? = null,
    onChipSelectionBlock: OnChipSelectionBlock = ::defaultOnChipSelectionBlock
) {
    val (internalTextValue, setInternalTextValue) = remember { mutableStateOf(selectedConfig?.text ?: textValue) }
    val (internalSelectedConfig, setInternalSelectedConfig) = remember {
        mutableStateOf(
            selectedConfig
        )
    }
    TextField(
        modifier = Modifier.fillMaxWidth(),
        value = internalTextValue, onValueChange = { currentText ->
            setInternalTextValue(currentText)
            if (currentText.length < 6) return@TextField
            val foundIndex =
                chipConfigs.indexOfFirst { it.text.lowercase() == currentText.lowercase() }
            if (foundIndex == -1) setInternalSelectedConfig(null)
            else setInternalSelectedConfig(chipConfigs[foundIndex])
        })
    ChipsGroup(
        selected = internalSelectedConfig,
        onChipSelected = {
            onChipSelectionBlock(
                chipConfigs,
                it,
                setInternalTextValue,
                setInternalSelectedConfig,
                onChipSelected
            )
        },
        chips = chipConfigs
    )
}


@Preview
@Composable
fun ChipGroupScreen() {
    val chipConfigs = (1..3).map { ChipConfig(text = "Chip $it") }
    SelectionThingsTheme {
        Surface {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                ChipAndTextField(
                    chipConfigs,
                )
                ChipAndTextField(
                    chipConfigs,
                    textValue = "With init text value"
                )
                ChipAndTextField(
                    chipConfigs,
                    selectedConfig = chipConfigs[2]
                )
                ChipAndTextField(
                    chipConfigs,
                    textValue = "Custom Chip selection",
                    onChipSelectionBlock = ::customOnChipSelectionBlock
                )
            }
        }
    }

}

fun customOnChipSelectionBlock(
    chips: List<ChipConfig>,
    chipConfig: ChipConfig,
    setInternalTextValue: (String) -> Unit,
    setInternalSelectedConfig: (ChipConfig?) -> Unit,
    onChipSelected: ((ChipConfig) -> Unit)? = null
) {
    if (chipConfig.isSelected) {
        var text = ""
        chips
            .filter { it.key != chipConfig.key }
            .forEach { text += " ${it.key}" }
        setInternalTextValue(text)
        setInternalSelectedConfig(chipConfig)
    } else {
        setInternalTextValue("")
        setInternalSelectedConfig(null)
    }
    onChipSelected?.invoke(chipConfig)
}

