package ru.emkn.kotlin.sms.view.tables

import ru.emkn.kotlin.sms.ObjectFields
import ru.emkn.kotlin.sms.controller.Controller
import ru.emkn.kotlin.sms.controller.Editor
import ru.emkn.kotlin.sms.model.Event
import ru.emkn.kotlin.sms.view.PathChooser
import ru.emkn.kotlin.sms.view.View
import java.time.format.DateTimeFormatter

class EventTable : Table<Event>() {

    private val event: List<Event>
        get() =
            Event.all().toList()

    override val header = TableHeader<Event>(
        listOf(
            TableColumn(
                "Name",
                ObjectFields.Name, visible = true, readOnly = false,
                comparator = TableComparing.compareByString(ObjectFields.Name),
                getterGenerator = { { it.name } }
            ),
            TableColumn(
                "Date",
                ObjectFields.Date,
                visible = true, readOnly = false,
                comparator = TableComparing.compareByLocalDate(ObjectFields.Date),
                getterGenerator = {
                    {
                        val pattern = "dd.MM.yyyy"
                        val formatter = DateTimeFormatter.ofPattern(pattern)
                        it.date.format(formatter)
                    }
                }
            )
        ),
        iconsBar = false,
        filtering = false
    )

    inner class EventTableRow(private val event: Event) : TableRow() {

        override val id = event.id.value
        override val cells = header.makeTableCells(event, ::saveChanges)

        override fun saveChanges() {
            Editor.editEvent(event, changes)
        }
    }

    override var addButton: Boolean = true
        get() = sortedFilteredRows.isEmpty()

    override val creatingState: View.State = View.State.CreateEvent

    override val rows: List<TableRow>
        get() = event.map { EventTableRow(it) }

    override val loadAction = {
        val eventFile = PathChooser("Choose event", ".csv", "Event").choose()
        Controller.loadEvent(eventFile?.toPath())
        state = State.Outdated
    }
}
