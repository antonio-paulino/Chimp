package pt.isel.pdm.chimp.ui.screens.home.createChannel.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material3.Button
import pt.isel.pdm.chimp.R
import pt.isel.pdm.chimp.domain.Either
import pt.isel.pdm.chimp.domain.Success
import pt.isel.pdm.chimp.domain.channel.ChannelRole
import pt.isel.pdm.chimp.domain.success
import pt.isel.pdm.chimp.domain.wrappers.name.Name
import pt.isel.pdm.chimp.domain.wrappers.name.NameValidationError
import pt.isel.pdm.chimp.domain.wrappers.name.NameValidator
import pt.isel.pdm.chimp.ui.components.inputs.ChannelPrivacyInput
import pt.isel.pdm.chimp.ui.components.inputs.NameTextField
import pt.isel.pdm.chimp.ui.components.inputs.RoleInput
import pt.isel.pdm.chimp.ui.theme.ChIMPTheme

@Composable
fun CreateChannelForm(
    initialName: String,
    initialIsPublic: Boolean,
    initialRole: ChannelRole,
    onCreateChannel: (Name, Boolean, ChannelRole) -> Unit,
) {
    val (name, setName) = remember { mutableStateOf(TextFieldValue(initialName)) }
    val (isPublic, setIsPublic) = remember { mutableStateOf(initialIsPublic) }
    val (role, setRole) = remember { mutableStateOf(initialRole) }

    val (nameValidation, setNameValidation) = remember { mutableStateOf<Either<List<NameValidationError>, Unit>>(success(Unit)) }
    val nameValidator = NameValidator()

    ChIMPTheme {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(16.dp).background(Color.Transparent),
        ) {
            NameTextField(
                name = name,
                onNameChange = {
                    setName(it)
                    setNameValidation(nameValidator.validate(it.text))
                },
                nameValidation = nameValidation,
            )
            ChannelPrivacyInput(isPublic = isPublic, setIsPublic = setIsPublic)
            RoleInput(role = role, setRole = setRole)
            Button(
                onClick = { onCreateChannel(Name(name.text), isPublic, role) },
                modifier = Modifier.align(Alignment.CenterHorizontally),
                enabled = nameValidation is Success && name.text.isNotBlank(),
            ) {
                Text(
                    text = stringResource(id = R.string.create_channel),
                )
            }
        }
    }
}

@Preview
@Composable
fun CreateChannelFormPreview() {
    CreateChannelForm(
        initialName = "Channel Name",
        initialIsPublic = true,
        initialRole = ChannelRole.MEMBER,
        onCreateChannel = { _, _, _ -> },
    )
}
