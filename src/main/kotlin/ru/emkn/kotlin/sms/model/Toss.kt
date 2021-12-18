package ru.emkn.kotlin.sms.model

import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.javatime.time
import ru.emkn.kotlin.sms.io.Loader
import java.time.LocalTime
import kotlin.random.Random

object TossTable : Table("toss") {
    val tossID: Column<Int> = integer("toss")
    val participantID: Column<EntityID<Int>> = reference("participant", ParticipantTable)
    val startTime: Column<LocalTime> = time("startTime")
}

open class Toss {

    companion object {
        protected var lastId: Int = 1
    }

    val id = lastId++

    enum class State {
        PREPARING, TOSSED
    }

    var state = State.PREPARING
        protected set
    protected val participants = mutableSetOf<Participant>()
    val startTimeByParticipant = mutableMapOf<Participant, LocalTime>()

    fun addParticipant(participant: Participant) {
        require(state == State.PREPARING)
        participants.add(participant)
    }

    fun build(loader: Loader) {
        loader.loadToss()
        state = State.TOSSED
    }

    fun addAllParticipant() {
        require(state == State.PREPARING)
        Participant.all().forEach { this.addParticipant(it) }
    }

    open fun build() {
        var currentTime = LocalTime.NOON
        val deltaMinutes = 5L
        participants.groupBy { it.group }.forEach { (groupID, members) ->
            members.shuffled(Random(0)).forEach { participant ->
                TossTable.insert {
                    it[tossID] = id
                    it[participantID] = participant.id
                    it[startTime] = currentTime
                }
                currentTime = currentTime.plusMinutes(deltaMinutes)
            }
        }
        state = State.TOSSED
    }
}
