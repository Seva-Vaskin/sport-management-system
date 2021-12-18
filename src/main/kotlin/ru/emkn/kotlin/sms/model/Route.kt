package ru.emkn.kotlin.sms.model

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import ru.emkn.kotlin.sms.MAX_TEXT_FIELD_SIZE
import ru.emkn.kotlin.sms.io.SingleLineWritable

object RouteTable : IntIdTable("routes") {
    val name: Column<String> = varchar("name", MAX_TEXT_FIELD_SIZE)
}

/**
 * A class for storing a route along which one group of participants runs.
 */
class Route(id: EntityID<Int>) : IntEntity(id), SingleLineWritable {
    companion object : IntEntityClass<Route>(RouteTable) {
        fun findByName(name: String): Route =
            Route.find { RouteTable.name eq name }.first()

        fun checkByName(name: String): Boolean =
            !Route.find { RouteTable.name eq name }.empty()

        fun create(name: String): Route =
            Route.new {
                this.name = name
            }

        fun create(name: String, checkpoints: List<Checkpoint>): Route {
            val res = create(name)
            checkpoints.forEach { it.addToRoute(res) }
            return res
        }
    }

    var name by RouteTable.name
    var checkPoints by Checkpoint via RouteCheckpointsTable

    fun change(name: String, checkpoints: List<Checkpoint>) {
        this.name = name
        RouteCheckpointsTable.deleteWhere { RouteCheckpointsTable.route eq this@Route.id }
        checkpoints.forEachIndexed { index, checkpoint ->
            RouteCheckpointsTable.insert {
                it[this.route] = this@Route.id
                it[this.positionInRoute] = index
                it[this.checkpoint] = checkpoint.id
            }
        }
    }

    override fun toLine(): List<String?> = listOf(name) + checkPoints.map { it.id.toString() }
}

object RouteCheckpointsTable : IntIdTable("route_checkpoints") {
    val route: Column<EntityID<Int>> = reference("routes", RouteTable)
    val checkpoint: Column<EntityID<Int>> = reference("checkpoints", CheckpointTable)
    val positionInRoute = integer("position")
}

object CheckpointTable : IntIdTable("checkpoints") {
    val weight: Column<Int> = integer("weight")
    val name: Column<String> = varchar("name", MAX_TEXT_FIELD_SIZE)
}

class Checkpoint(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Checkpoint>(CheckpointTable) {
        fun create(name: String, weight: Int): Checkpoint =
            Checkpoint.new {
                this.name = name
                this.weigth = weight
            }


        fun findByName(name: String): Checkpoint =
            Checkpoint.find { CheckpointTable.name eq name }.first()

        fun checkByName(name: String): Boolean =
            !Checkpoint.find { CheckpointTable.name eq name }.empty()
    }

    var weigth by CheckpointTable.weight
    var name by CheckpointTable.name
    var routes by Route via RouteCheckpointsTable

    fun addToRoute(route: Route) {
        val positionInRoute = route.checkPoints.toList().size
        RouteCheckpointsTable.insert {
            it[this.checkpoint] = this@Checkpoint.id
            it[this.route] = route.id
            it[this.positionInRoute] = positionInRoute
        }
    }
}