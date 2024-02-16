package github.swissonid.selectionthings.ui.chipgroup

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

data class ChipConfig(
    val text: String,
    val key: String = text,
    val isSelected: Boolean = false,
    val onClick: (() -> Unit)? = null,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OurChip(config: ChipConfig) {
    FilterChip(
        selected = config.isSelected,
        label = { Text(config.text) },
        onClick = { config.onClick?.invoke() }
    )
}