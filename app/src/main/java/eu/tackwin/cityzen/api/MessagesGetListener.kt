package eu.tackwin.cityzen.api

import eu.tackwin.cityzen.Model.MessageInfo

/**
 * Created by tackw on 10/12/2017.
 */
interface MessagesGetListener {

	fun messagesGetComplete(messages: List<MessageInfo>) {}
	fun messagesGetFailure() {}
}