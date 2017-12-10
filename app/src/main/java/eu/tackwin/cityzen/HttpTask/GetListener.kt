package eu.tackwin.cityzen.HttpTask

/**
 * Created by tackw on 30/11/2017.
 */
interface GetListener {

	fun getComplete(result: ByteArray) {}
	fun getFailure(error_code: Int, message: String) {}

}