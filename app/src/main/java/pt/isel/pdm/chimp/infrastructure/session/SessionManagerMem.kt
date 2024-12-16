package pt.isel.pdm.chimp.infrastructure.session

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import pt.isel.pdm.chimp.domain.sessions.Session

class SessionManagerMem : SessionManager {
    private var _session: MutableStateFlow<Session?> = MutableStateFlow(null)
    private var _currentSession: Session? = null

    override val session: Flow<Session?> = _session
    override val currentSession: Session?
        get() = _currentSession

    override fun set(session: Session) {
        _session.value = session
        _currentSession = session
    }

    override fun clear() {
        _session.value = null
        _currentSession = null
    }
}
