package pt.isel.pdm.chimp.infrastructure.session

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import pt.isel.pdm.chimp.domain.sessions.Session

class SessionManagerMem : SessionManager {
    private var _session: MutableStateFlow<Session?> = MutableStateFlow(null)

    override val session: Flow<Session?> = _session

    override fun set(session: Session) {
        _session.value = session
    }

    override fun clear() {
        _session.value = null
    }
}
