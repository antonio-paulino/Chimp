package pt.isel.pdm.chimp.services

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import pt.isel.pdm.chimp.domain.sessions.Session
import pt.isel.pdm.chimp.services.interfaces.SessionManager

class SessionManagerMem : SessionManager {
    override var session: Session? by mutableStateOf(null)
        private set

    override fun set(session: Session) {
        this.session = session
    }

    override fun clear() {
        session = null
    }
}
