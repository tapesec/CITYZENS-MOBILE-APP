package eu.tackwin.cityzen.api

/**
 * Created by tackw on 15/12/2017.
 */
interface MessageDeleteListener {

	fun onMessageDeleted() {}
	fun onMessageDeleteFailed() {}

}