package pt.isel.pdm.chimp.infrastructure.services.http

internal const val LOGIN_ROUTE = "auth/login"
internal const val REGISTER_ROUTE = "auth/register"
internal const val LOGOUT_ROUTE = "auth/logout"
internal const val REFRESH_ROUTE = "auth/refresh"
internal const val INVITATIONS_ROUTE = "auth/invitations"

internal const val CHANNEL_ID_PARAM = "{channelId}"
internal const val INVITE_ID_PARAM = "{inviteId}"
internal const val USER_ID_PARAM = "{userId}"
internal const val MESSAGE_ID_PARAM = "{messageId}"

internal const val CHANNELS_ROUTE = "channels"
internal const val CHANNEL_ROUTE = "channels/{channelId}"
internal const val CHANNEL_MEMBERS_ROUTE = "channels/${CHANNEL_ID_PARAM}/members/${USER_ID_PARAM}"

internal const val CHANNEL_INVITATIONS_ROUTE = "channels/$CHANNEL_ID_PARAM/invitations"
internal const val CHANNEL_INVITATION_ROUTE = "channels/$CHANNEL_ID_PARAM/invitations/$INVITE_ID_PARAM"
internal const val USER_INVITATIONS_ROUTE = "users/$USER_ID_PARAM/invitations"
internal const val USER_INVITATION_ROUTE = "users/$USER_ID_PARAM/invitations/$INVITE_ID_PARAM"

internal const val CHANNEL_MESSAGES_ROUTE = "channels/$CHANNEL_ID_PARAM/messages"
internal const val CHANNEL_MESSAGE_ROUTE = "channels/$CHANNEL_ID_PARAM/messages/$MESSAGE_ID_PARAM"

internal const val USERS_ROUTE = "users"
internal const val USER_ROUTE = "users/$USER_ID_PARAM"
internal const val USER_CHANNELS_ROUTE = "users/$USER_ID_PARAM/channels"
