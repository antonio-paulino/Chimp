package pt.isel.pdm.chimp.dto.output.channel

import kotlinx.serialization.Serializable
import pt.isel.pdm.chimp.dto.output.users.UserOutputModel

@Serializable
data class ChannelOutputModel(
    val id: Long,
    val name: String,
    val defaultRole: String,
    val owner: UserOutputModel,
    val isPublic: Boolean,
    val members: List<ChannelMemberOutputModel>,
    val createdAt: String,
)
