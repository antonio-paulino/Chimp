package isel.pdm.chimp.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import pt.isel.pdm.chimp.R

@Composable
fun montserratFamily() =
    FontFamily(
        Font(R.font.montserrat_regular),
        Font(R.font.montserrat_medium),
        Font(R.font.montserrat_semibold),
        Font(R.font.montserrat_bold),
        Font(R.font.montserrat_light),
    )

@Composable
fun productSansFamily() =
    FontFamily(
        Font(R.font.productsans_bold),
        Font(R.font.productsans_italic),
        Font(R.font.productsans_bolditalic),
        Font(R.font.productsans_medium),
        Font(R.font.productsans_regular),
    )
