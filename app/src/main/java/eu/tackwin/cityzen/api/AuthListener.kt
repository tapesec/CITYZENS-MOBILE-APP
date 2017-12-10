package eu.tackwin.cityzen.api

/**
 * Created by tackw on 30/11/2017.
 */
interface AuthListener {

	fun authComplete(
			access_token: String, refresh_token: String, id_token: String,
			scope: String, expires_in: Int, token_type: String
	) {}
	fun authFailure(error_code: Int, message: String) {}

}