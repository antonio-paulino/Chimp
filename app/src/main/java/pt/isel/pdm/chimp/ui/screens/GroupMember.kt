package pt.isel.pdm.chimp.ui.screens

class GroupMember(private val number: String, private val name: String,
                  private val github: String, val email: String
) {
    override fun toString(): String {
        return "GroupMember(number='$number', name='$name', github='$github', email='$email')"
    }
}
