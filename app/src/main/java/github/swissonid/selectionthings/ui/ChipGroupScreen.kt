package github.swissonid.selectionthings.ui

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import github.swissonid.selectionthings.ui.chipgroup.ChipAndTextField
import github.swissonid.selectionthings.ui.chipgroup.ChipConfig
import github.swissonid.selectionthings.ui.chipgroup.defaultOnChipSelectionBlock
import github.swissonid.selectionthings.ui.theme.SelectionThingsTheme


internal fun customOnChipSelectionBlock(
    chipConfig: ChipConfig,
    chipConfigs: List<ChipConfig>,
    setInternalTextValue: (String) -> Unit,
    setInternalSelectedConfig: (ChipConfig?) -> Unit,
    onChipSelected: ((ChipConfig) -> Unit)? = null
) {
    if (chipConfig.isSelected) {
        var text = ""
        chipConfigs
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

internal fun customOnTextChange(
    currentText: String,
    chipConfigs: List<ChipConfig>,
    setInternalTextValue: (String) -> Unit,
    setInternalSelectedConfig: (ChipConfig?) -> Unit,
) {
    setInternalTextValue(currentText.uppercase())
    if (currentText.length < 6) return
    val foundIndex =
        chipConfigs.indexOfFirst { it.text.lowercase() == currentText.lowercase() }
    if (foundIndex == -1) setInternalSelectedConfig(null)
    else setInternalSelectedConfig(chipConfigs[foundIndex])
}

internal fun otherCustomOnChipSelectionBlock(
    chipConfig: ChipConfig,
    chipConfigs: List<ChipConfig>,
    setInternalTextValue: (String) -> Unit,
    setInternalSelectedConfig: (ChipConfig?) -> Unit,
    onChipSelected: ((ChipConfig) -> Unit)? = null
) {
    defaultOnChipSelectionBlock(
        chipConfig,
        chipConfigs,
        {value -> setInternalTextValue(value.uppercase())},
        setInternalSelectedConfig,
        onChipSelected
    )
}

@Preview
@Composable
fun ChipGroupScreen() {
    val chipConfigs = (1..3).map { ChipConfig(text = "Chip $it") }
    val localContxt = LocalContext.current
    SelectionThingsTheme {
        Surface {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                ChipAndTextField(
                    chipConfigs + ChipConfig(
                        text="what ever",
                        onClick = {
                            Toast.makeText(localContxt,"What ever", Toast.LENGTH_SHORT).show()
                        }
                    ) ,
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
                ChipAndTextField(
                    chipConfigs,
                    textValue = "ALL UPPER CASE",
                    onChipSelectionBlock = ::otherCustomOnChipSelectionBlock,
                    onTextValueChangeBlock = ::customOnTextChange
                )
            }
        }
    }
}