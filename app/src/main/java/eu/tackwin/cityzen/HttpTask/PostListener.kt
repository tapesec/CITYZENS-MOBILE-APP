package eu.tackwin.cityzen.HttpTask

/**
 * Created by tackw on 05/12/2017.
 */
interface PostListener {

	fun postComplete(result: ByteArray) {}
	fun postFailure(error_code: Int, message: String, response: ByteArray? = null) {}

}