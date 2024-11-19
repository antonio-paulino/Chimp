package pt.isel.pdm.chimp.domain

import pt.isel.pdm.chimp.domain.wrappers.identifier.Identifier

interface Identifiable {
    val id: Identifier
}
