package eu.tackwin.cityzen.api

import eu.tackwin.cityzen.Model.MessageInfo

/**
 * Created by tackw on 12/12/2017.
 */
interface MessagePostListener {

	fun onMessagePosted(msg: MessageInfo) {}
	fun onMessageFailed() {}

}