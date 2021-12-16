package ru.emkn.kotlin.sms.model

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column
import ru.emkn.kotlin.sms.MAX_TEXT_FIELD_SIZE
import ru.emkn.kotlin.sms.io.MultilineWritable

object Groups : IntIdTable("teams") {
    val name: Column<String> = varchar("name", MAX_TEXT_FIELD_SIZE)
}

/**
 *  Class for saving data about one sports group whose members follow the same route
 */
class Group(id: EntityID<Int>): IntEntity(id), MultilineWritable {

    companion object : IntEntityClass<Group>(Groups)

//    TODO()
//    val route =
//        Route.byName[routeName] ?: throw IllegalArgumentException("There is no appropriate route for $routeName")

    val name by Groups.name
    val members by Participant referrersOn Participants.groupID

//    TODO()
//    constructor(name: String, routeName: String, participants: List<Participant>) : this(name, routeName) {
//        members.addAll(participants)
//    }
//
//    companion object {
//        val byName: MutableMap<String, Group> = mutableMapOf()
//    }

    override fun toString() = this.name

    override fun toMultiline(): List<List<Any?>> {
        val res: MutableList<List<Any?>> = mutableListOf(listOf(this))
        res.addAll(this.members.toList().map { it.toLine() })
        return res
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Group

        if (name != other.name) return false
        if (route != other.route) return false
        if (members != other.members) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + route.hashCode()
        result = 31 * result + members.hashCode()
        return result
    }

}
