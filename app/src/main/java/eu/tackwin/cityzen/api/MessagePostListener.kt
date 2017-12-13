package eu.tackwin.cityzen.api

/**
 * Created by tackw on 12/12/2017.
 */
interface MessagePostListener {

	fun onMessagePosted() {}
	fun onMessageFailed() {}

}