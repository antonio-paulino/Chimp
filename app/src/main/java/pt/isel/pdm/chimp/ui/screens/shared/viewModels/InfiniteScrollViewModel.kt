package pt.isel.pdm.chimp.ui.screens.shared.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import pt.isel.pdm.chimp.domain.Either
import pt.isel.pdm.chimp.domain.Failure
import pt.isel.pdm.chimp.domain.Identifiable
import pt.isel.pdm.chimp.domain.Success
import pt.isel.pdm.chimp.domain.pagination.Pagination
import pt.isel.pdm.chimp.domain.pagination.PaginationRequest
import pt.isel.pdm.chimp.infrastructure.services.media.problems.Problem

class InfiniteScrollViewModel<T : Identifiable>(
    private val fetchItemsRequest: suspend (pageRequest: PaginationRequest, currentItems: List<T>) -> Either<Problem, Pagination<T>>,
    private val limit: Int = 50,
    private val getCount: Boolean = false,
    private val useOffset: Boolean = true,
    initialState: InfiniteScrollState<T> = InfiniteScrollState.Loading(Pagination()),
) : ViewModel() {
    private val _state = MutableStateFlow(initialState)
    private val updateQueue = mutableListOf<T>()

    val state: Flow<InfiniteScrollState<T>> = _state

    private val semaphore = Semaphore(1)

    init {
        this.viewModelScope.launch {
            state.collect {
                if (updateQueue.isNotEmpty() && it !is InfiniteScrollState.Loading) {
                    // Block adding items to the queue while processing updates / Wait while adding updates
                    semaphore.withPermit {
                        updateQueue.forEach { item -> handleItemUpdate(item) }
                        updateQueue.clear()
                    }
                }
            }
        }
    }

    fun loadMore() {
        val currentState = _state.value
        if (currentState !is InfiniteScrollState.Loading) {
            this.viewModelScope.launch {
                val res =
                    fetchItemsRequest(
                        PaginationRequest(
                            offset = if (useOffset) currentState.pagination.items.size.toLong() else 0,
                            limit = limit.toLong(),
                            getCount = getCount,
                        ),
                        currentState.pagination.items,
                    )
                when (res) {
                    is Success -> _state.emit(InfiniteScrollState.Loaded(res.value))
                    is Failure -> _state.emit(InfiniteScrollState.Error(currentState.pagination, res.value))
                }
            }
        }
    }

    suspend fun handleItemUpdate(item: T) {
        val currentState = _state.value

        if (currentState is InfiniteScrollState.Loading && !currentState.paginationState.items.any { it.id == item.id }) {
            semaphore.withPermit { updateQueue.add(item) }
            return
        }

        val updatedItems = currentState.pagination.items.map { if (it.id == item.id) item else it }

        this.viewModelScope.launch {
            when (currentState) {
                is InfiniteScrollState.Loading ->
                    _state.emit(
                        InfiniteScrollState.Loading(currentState.pagination.copy(items = updatedItems)),
                    )
                is InfiniteScrollState.Loaded ->
                    _state.emit(
                        InfiniteScrollState.Loaded(currentState.pagination.copy(items = updatedItems)),
                    )
                is InfiniteScrollState.Error ->
                    _state.emit(
                        InfiniteScrollState.Error(currentState.pagination.copy(items = updatedItems), currentState.problem),
                    )
            }
        }
    }

    fun handleItemDelete(item: T) {
        val currentState = _state.value
        if (currentState is InfiniteScrollState.Loading && !currentState.paginationState.items.any { it.id == item.id }) {
            updateQueue.add(item)
        } else {
            val updatedItems = currentState.pagination.items.filter { it.id != item.id }
            this.viewModelScope.launch {
                when (currentState) {
                    is InfiniteScrollState.Loading ->
                        _state.emit(
                            InfiniteScrollState.Loading(currentState.pagination.copy(items = updatedItems)),
                        )
                    is InfiniteScrollState.Loaded ->
                        _state.emit(
                            InfiniteScrollState.Loaded(currentState.pagination.copy(items = updatedItems)),
                        )
                    is InfiniteScrollState.Error ->
                        _state.emit(
                            InfiniteScrollState.Error(currentState.pagination.copy(items = updatedItems), currentState.problem),
                        )
                }
            }
        }
    }

    fun handleItemCreate(item: T) {
        val currentState = _state.value
        this.viewModelScope.launch {
            when (currentState) {
                is InfiniteScrollState.Loading ->
                    _state.emit(
                        InfiniteScrollState.Loading(currentState.pagination.copy(items = currentState.pagination.items + item)),
                    )
                is InfiniteScrollState.Loaded ->
                    _state.emit(
                        InfiniteScrollState.Loaded(currentState.pagination.copy(items = currentState.pagination.items + item)),
                    )
                is InfiniteScrollState.Error ->
                    _state.emit(
                        InfiniteScrollState.Error(
                            currentState.pagination.copy(items = currentState.pagination.items + item),
                            currentState.problem,
                        ),
                    )
            }
        }
    }
}
