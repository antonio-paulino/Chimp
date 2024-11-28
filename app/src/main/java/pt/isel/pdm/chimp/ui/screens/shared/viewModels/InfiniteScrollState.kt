package pt.isel.pdm.chimp.ui.screens.shared.viewModels

import pt.isel.pdm.chimp.domain.Identifiable
import pt.isel.pdm.chimp.domain.pagination.Pagination
import pt.isel.pdm.chimp.infrastructure.services.media.problems.Problem

sealed class InfiniteScrollState<T : Identifiable>(
    val pagination: Pagination<T> = Pagination(),
) {
    data class Loading<T : Identifiable>(val paginationState: Pagination<T>) : InfiniteScrollState<T>(paginationState)

    data class Loaded<T : Identifiable>(val paginationState: Pagination<T>) : InfiniteScrollState<T>(paginationState)

    data class Error<T : Identifiable>(
        val paginationState: Pagination<T>,
        val problem: Problem,
    ) : InfiniteScrollState<T>(paginationState)
}
