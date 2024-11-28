package pt.isel.pdm.chimp.infrastructure.storage.firestore

import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import pt.isel.pdm.chimp.domain.pagination.Pagination
import pt.isel.pdm.chimp.domain.pagination.PaginationInfo
import pt.isel.pdm.chimp.domain.pagination.PaginationRequest
import pt.isel.pdm.chimp.domain.pagination.Sort

internal fun Sort.toFirestoreSort(): Query.Direction =
    when (this) {
        Sort.ASC -> Query.Direction.ASCENDING
        Sort.DESC -> Query.Direction.DESCENDING
    }

internal fun <T, R> QuerySnapshot.getPagination(
    mapClazz: Class<T>,
    mapFunction: (T) -> R,
    pageRequest: PaginationRequest,
): Pagination<R> {
    return Pagination(
        items = this.toObjects(mapClazz).map(mapFunction),
        info = this.getPaginationInfo(pageRequest),
    )
}

internal fun QuerySnapshot.getPaginationInfo(pageRequest: PaginationRequest): PaginationInfo {
    val total = if (pageRequest.getCount) this.size().toLong() else null
    val totalPages = if (total != null) (total + pageRequest.limit - 1) / pageRequest.limit else null
    val currentPage = pageRequest.offset / pageRequest.limit + 1
    val nextPage = if (totalPages != null && currentPage < totalPages) currentPage + 1 else null
    val prevPage = if (currentPage > 1) currentPage - 1 else null

    return PaginationInfo(
        total = total,
        currentPage = currentPage.toInt(),
        totalPages = totalPages?.toInt(),
        nextPage = nextPage?.toInt(),
        prevPage = prevPage?.toInt(),
    )
}
