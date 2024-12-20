package pt.isel.pdm.chimp.infrastructure.session

import kotlinx.coroutines.flow.Flow
import pt.isel.pdm.chimp.domain.sessions.Session

/**
 * Interface that defines the operations that can be performed on the session manager
 * @see Session
 */
interface SessionManager {
    val session: Flow<Session?>

    suspend fun set(session: Session)

    suspend fun clear()
}
