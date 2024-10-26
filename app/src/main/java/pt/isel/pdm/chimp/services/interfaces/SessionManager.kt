package pt.isel.pdm.chimp.services.interfaces

import pt.isel.pdm.chimp.domain.sessions.Session

/**
 * Interface that defines the operations that can be performed on the session manager
 * @see Session
 */
interface SessionManager {
    val session: Session?

    fun set(session: Session)

    fun clear()
}
