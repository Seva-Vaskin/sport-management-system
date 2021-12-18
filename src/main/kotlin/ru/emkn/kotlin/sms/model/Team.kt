package ru.emkn.kotlin.sms.model

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column
import ru.emkn.kotlin.sms.MAX_TEXT_FIELD_SIZE
import ru.emkn.kotlin.sms.io.MultilineWritable
import ru.emkn.kotlin.sms.io.SingleLineWritable


object TeamTable : IntIdTable("teams") {
    val name: Column<String> = varchar("name", MAX_TEXT_FIELD_SIZE)
}

/**
 * Class for representing all information about one team, with read from single application file
 */
class Team(id: EntityID<Int>) : IntEntity(id), MultilineWritable, SingleLineWritable {
    companion object : IntEntityClass<Team>(TeamTable) {
        fun formatterForApplications(team: Team) = listOf(
            listOf(team.name) + listOf(
                listOf(
                    "Имя",
                    "Фамилия",
                    "Г.р.",
                    "Группа",
                    "Разр."
                )
            ) + team.members.map(Participant::formatterParticipantForApplications)
        )

        fun findByName(name: String): Team {
            return Team.find { TeamTable.name eq name }.first()
        }

        fun checkByName(name: String): Boolean = !Team.find { TeamTable.name eq name }.empty()

        fun create(name: String): Team {
            return Team.new {
                this.name = name
            }
        }
    }

    var name: String by TeamTable.name
    val members by Participant referrersOn ParticipantTable.teamID

    val score
        get() = Competition.teamResult.getScore(this)


    fun change(name: String) {
        this.name = name
    }

    override fun toMultiline(): List<List<Any?>> = listOf(
        listOf(name) + listOf(
            listOf(
                "Номер",
                "Имя",
                "Фамилия",
                "Г.р.",
                "Команда",
                "Разр."
            )
        ) + members.map(Participant::toLine)
    )

    override fun toLine(): List<Any?> = listOf(
        name, score
    )

    override fun toString() = this.name
}
