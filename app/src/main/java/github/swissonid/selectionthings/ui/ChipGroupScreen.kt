package github.swissonid.selectionthings.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import github.swissonid.selectionthings.ui.chipgroup.ChipAndTextField
import github.swissonid.selectionthings.ui.chipgroup.ChipConfig
import github.swissonid.selectionthings.ui.theme.SelectionThingsTheme


internal fun customOnChipSelectionBlock(
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