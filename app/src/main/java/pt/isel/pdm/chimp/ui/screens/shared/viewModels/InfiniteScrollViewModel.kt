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
import pt.isel.pdm.chimp.domain.wrappers.identifier.Identifier
import pt.isel.pdm.chimp.infrastructure.services.media.problems.Problem

class InfiniteScrollViewModel<T : Identifiable>(
    private val fetchItemsRequest: suspend (pageRequest: PaginationRequest, currentItems: List<T>) -> Either<Problem, Pagination<T>>,
    private val limit: Int = 50,
    private val getCount: Boolean = false,
    private val useOffset: Boolean = true,
    initialState: InfiniteScrollState<T> = InfiniteScrollState.Initial(),
) : ViewModel() {
    private val _state = MutableStateFlow(initialState)
    private val updateQueue = mutableListOf<T>()
    private val deleteQueue = mutableListOf<Identifier>()

    val state: Flow<InfiniteScrollState<T>> = _state

    private val semaphore = Semaphore(1)

    init {
        this.viewModelScope.launch {
            state.collect {
                if (updateQueue.isNotEmpty() && it !is InfiniteScrollState.Loading) {
                    semaphore.withPermit {
                        updateQueue.forEach { item -> handleItemUpdate(item) }
                        updateQueue.clear()
                    }
                }
                if (deleteQueue.isNotEmpty() && it !is InfiniteScrollState.Loading) {
                    semaphore.withPermit {
                        deleteQueue.forEach { itemId -> handleItemDelete(itemId) }
                        deleteQueue.clear()
                    }
                }
                if (it is InfiniteScrollState.Initial) {
                    loadMore()
                }
            }
        }
    }

    fun loadMore() {
        if (_state.value !is InfiniteScrollState.Loading) {
            val savedState = _state.value
            if (savedState.pagination.info.nextPage == null && savedState !is InfiniteScrollState.Initial) return
            _state.value = InfiniteScrollState.Loading(savedState.pagination)
            this.viewModelScope.launch {
                val res = fetchItemsRequest(
                        PaginationRequest(
                            offset = if (useOffset) savedState.pagination.items.size.toLong() else 0,
                            limit = limit.toLong(),
                            getCount = getCount,
                        ),
                        savedState.pagination.items,
                    )
                if (_state.value != InfiniteScrollState.Loading(savedState.pagination)) return@launch
                when (res) {
                    is Success -> _state.emit(InfiniteScrollState.Loaded(res.value.copy(items = savedState.pagination.items + res.value.items)))
                    is Failure -> _state.emit(InfiniteScrollState.Error(savedState.pagination, res.value))
                }
            }
        }
    }

    fun reset() {
        _state.value = InfiniteScrollState.Initial()
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
                        InfiniteScrollState.Error(
                            currentState.pagination.copy(items = updatedItems),
                            currentState.problem
                        ),
                    )

                is InfiniteScrollState.Initial -> {
                    _state.emit(
                        InfiniteScrollState.Initial(currentState.pagination.copy(items = updatedItems)),
                    )
                }
            }
        }
    }

    suspend fun handleItemDelete(itemId: Identifier) {
        val currentState = _state.value

        if (currentState is InfiniteScrollState.Loading && !currentState.paginationState.items.any { it.id.value == itemId.value }) {
            semaphore.withPermit { deleteQueue.add(itemId) }
            return
        }

        val updatedItems = currentState.pagination.items.filter { it.id.value != itemId.value }

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
                        InfiniteScrollState.Error(
                            currentState.pagination.copy(items = updatedItems),
                            currentState.problem
                        ),
                    )

                is InfiniteScrollState.Initial -> {
                    _state.emit(
                        InfiniteScrollState.Initial(currentState.pagination.copy(items = updatedItems)),
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
                is InfiniteScrollState.Initial -> {
                    _state.emit(
                        InfiniteScrollState.Initial(currentState.pagination.copy(items = currentState.pagination.items + item)),
                    )
                }
            }
        }
    }
}
