package ru.emkn.kotlin.sms.view.creators

import ru.emkn.kotlin.sms.ObjectFields
import ru.emkn.kotlin.sms.controller.Editor
import ru.emkn.kotlin.sms.model.Participant
import ru.emkn.kotlin.sms.view.ItemCreator
import ru.emkn.kotlin.sms.view.ItemCreatorInputField

class ParticipantCreator : ItemCreator<Participant>() {

    override val fields = listOf(
        ItemCreatorInputField("Name", ObjectFields.Name),
        ItemCreatorInputField("Surname", ObjectFields.Surname),
        ItemCreatorInputField("Group", ObjectFields.Group),
        ItemCreatorInputField("Birthday year", ObjectFields.BirthdayYear),
        ItemCreatorInputField("Grade", ObjectFields.Grade),
        ItemCreatorInputField("Team", ObjectFields.Team)
    )

    override fun createAction(input: Map<ObjectFields, String>) {
        Editor.createParticipantFrom(input)
    }
}