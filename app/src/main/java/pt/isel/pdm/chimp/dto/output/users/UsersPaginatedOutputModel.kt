package pt.isel.pdm.chimp.dto.output.users

import pt.isel.pdm.chimp.dto.output.PaginationOutputModel

data class UsersPaginatedOutputModel(
    val users: List<UserOutputModel>,
    val pagination: PaginationOutputModel?,
)
