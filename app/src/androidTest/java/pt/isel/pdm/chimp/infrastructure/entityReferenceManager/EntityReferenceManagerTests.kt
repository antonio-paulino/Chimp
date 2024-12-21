package pt.isel.pdm.chimp.infrastructure.entityReferenceManager

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import pt.isel.pdm.chimp.domain.channel.Channel
import pt.isel.pdm.chimp.domain.channel.ChannelMember
import pt.isel.pdm.chimp.domain.channel.ChannelRole
import pt.isel.pdm.chimp.domain.invitations.ChannelInvitation
import pt.isel.pdm.chimp.domain.messages.Message
import pt.isel.pdm.chimp.domain.user.User
import pt.isel.pdm.chimp.domain.wrappers.email.Email
import pt.isel.pdm.chimp.domain.wrappers.identifier.Identifier
import pt.isel.pdm.chimp.domain.wrappers.name.Name
import pt.isel.pdm.chimp.infrastructure.EntityReferenceManagerImpl
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

@OptIn(ExperimentalCoroutinesApi::class)
class EntityReferenceManagerImplTest {

    private lateinit var entityReferenceManager: EntityReferenceManagerImpl

    private lateinit var testUser: User
    private lateinit var testChannel: Channel
    private lateinit var testMessage: Message
    private lateinit var testInvitation: ChannelInvitation

    @Before
    fun setUp() {
        entityReferenceManager = EntityReferenceManagerImpl()

        // Initialize test data
        val identifier1 = Identifier(1)
        val identifier2 = Identifier(2)
        val identifier3 = Identifier(3)
        val name = Name("Test Name")
        val email = Email("test@example.com")

        testUser = User(
            id = identifier1,
            name = name,
            email = email
        )

        val owner = testUser
        testChannel = Channel(
            id = identifier2,
            name = Name("Test Channel"),
            defaultRole = ChannelRole.MEMBER,
            owner = owner,
            isPublic = true,
            members = listOf(
                ChannelMember(
                    id = identifier3,
                    name = Name("Channel Member"),
                    role = ChannelRole.MEMBER
                )
            )
        )

        testMessage = Message(
            id = identifier3,
            channelId = testChannel.id,
            author = testUser,
            content = "Test Message"
        )

        testInvitation = ChannelInvitation(
            id = Identifier(4),
            channel = testChannel,
            inviter = testUser,
            invitee = User(
                id = Identifier(5),
                name = Name("Invitee"),
                email = Email("invitee@example.com")
            ),
            role = ChannelRole.GUEST,
            expiresAt = LocalDateTime.now().plusDays(1).truncatedTo(ChronoUnit.MILLIS)
        )
    }

    @Test
    fun set_message_should_update_the_message_flow() = runTest {
        // Act
        entityReferenceManager.setMessage(testMessage)

        // Assert
        val result = entityReferenceManager.message.first()
        assertEquals(testMessage, result)
    }

    @Test
    fun set_channel_should_update_the_channel_flow() = runTest {
        // Act
        entityReferenceManager.setChannel(testChannel)

        // Assert
        val result = entityReferenceManager.channel.first()
        assertEquals(testChannel, result)
    }

    @Test
    fun set_user_should_update_the_user_flow() = runTest {
        // Act
        entityReferenceManager.setUser(testUser)

        // Assert
        val result = entityReferenceManager.user.first()
        assertEquals(testUser, result)
    }

    @Test
    fun set_invitation_should_update_the_invitation_flow() = runTest {
        // Act
        entityReferenceManager.setInvitation(testInvitation)

        // Assert
        val result = entityReferenceManager.invitation.first()
        assertEquals(testInvitation, result)
    }

    @Test
    fun message_flow_should_emit_null_when_message_is_cleared() = runTest {
        // Arrange
        entityReferenceManager.setMessage(testMessage)
        entityReferenceManager.setMessage(null)
        // Assert
        val result = entityReferenceManager.message.first()
        assertEquals(null, result)
    }

    @Test
    fun channel_flow_should_emit_null_when_channel_is_cleared() = runTest {
        // Arrange
        entityReferenceManager.setChannel(testChannel)
        entityReferenceManager.setChannel(null)
        // Assert
        val result = entityReferenceManager.channel.first()
        assertEquals(null, result)
    }

    @Test
    fun user_flow_should_emit_null_when_user_is_cleared() = runTest {
        // Arrange
        entityReferenceManager.setUser(testUser)
        entityReferenceManager.setUser(null)
        // Assert
        val result = entityReferenceManager.user.first()
        assertEquals(null, result)
    }

    @Test
    fun invitation_flow_should_emit_null_when_invitation_is_cleared() = runTest {
        // Arrange
        entityReferenceManager.setInvitation(testInvitation)
        entityReferenceManager.setInvitation(null)
        // Assert
        val result = entityReferenceManager.invitation.first()
        assertEquals(null, result)
    }
}
