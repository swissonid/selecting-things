package github.swissonid.selectionthings.ui.chipgroup

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * The block to be invoked when a chip is selected
 * @param chips: List<ChipConfig> The list of chips
 * @param chipConfig: ChipConfig The selected chip
 * @param setInternalTextValue: (String) -> Unit The function to update the text value
 * @param setInternalSelectedConfig: (ChipConfig?) -> Unit The function to update the selectedConfig
 * @param onChipSelected: ((ChipConfig) -> Unit)? The callback to be invoked after the selection
 */
typealias OnChipSelectionBlock = (
    chipConfig: ChipConfig,
    chipConfigs: List<ChipConfig>,
    setInternalTextValue: (String) -> Unit,
    setInternalSelectedConfig: (ChipConfig?) -> Unit,
    onChipSelected: ((ChipConfig) -> Unit)?
) -> Unit

/**
 * The default implementation of onChipSelectionBlock
 * It updates the text value and the selectedConfig based on the chip selection
 * @param chipConfigs: List<ChipConfig> The list of chips
 * @param chipConfig: ChipConfig The selected chip
 * @param setInternalTextValue: (String) -> Unit The function to update the text value
 * @param setInternalSelectedConfig: (ChipConfig?) -> Unit The function to update the selectedConfig
 * @param onChipSelected: ((ChipConfig) -> Unit)? The callback to be invoked after the selection
 */
fun defaultOnChipSelectionBlock(
    chipConfig: ChipConfig,
    chipConfigs: List<ChipConfig>,//unused
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

typealias OnTextValueChangeBlock = (
    currentText: String,
    chipConfigs: List<ChipConfig>,
    setInternalTextValue: (String) -> Unit,
    setInternalSelectedConfig: (ChipConfig?) -> Unit,
) -> Unit
fun defaultOnTextValueChangeBlock(
    currentText: String,
    chipConfigs: List<ChipConfig>,
    setInternalTextValue: (String) -> Unit,
    setInternalSelectedConfig: (ChipConfig?) -> Unit
) {
    setInternalTextValue(currentText)
    if (currentText.length < 6) return
    val foundIndex =
        chipConfigs.indexOfFirst { it.text.lowercase() == currentText.lowercase() }
    if (foundIndex == -1) setInternalSelectedConfig(null)
    else setInternalSelectedConfig(chipConfigs[foundIndex])
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
            OurChip(internalConfig)
        }
    }
}

@Composable
fun ChipAndTextField(
    chipConfigs: List<ChipConfig>,
    textValue: String = "",
    selectedConfig: ChipConfig? = null,
    onChipSelected: ((ChipConfig) -> Unit)? = null,
    onChipSelectionBlock: OnChipSelectionBlock = ::defaultOnChipSelectionBlock,
    onTextValueChangeBlock: OnTextValueChangeBlock = ::defaultOnTextValueChangeBlock
) {
    val (internalTextValue, setInternalTextValue) = remember {
        mutableStateOf(
            selectedConfig?.text ?: textValue
        )
    }
    val (internalSelectedConfig, setInternalSelectedConfig) = remember {
        mutableStateOf(
            selectedConfig
        )
    }
    TextField(
        modifier = Modifier.fillMaxWidth(),
        value = internalTextValue,
        onValueChange = {
            onTextValueChangeBlock(
                it,
                chipConfigs,
                setInternalTextValue,
                setInternalSelectedConfig
            )
        }
    )
    ChipsGroup(
        selected = internalSelectedConfig,
        onChipSelected = {
            onChipSelectionBlock(
                it,
                chipConfigs,
                setInternalTextValue,
                setInternalSelectedConfig,
                onChipSelected
            )
        },
        chips = chipConfigs
    )
}
