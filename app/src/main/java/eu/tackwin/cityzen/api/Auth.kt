package eu.tackwin.cityzen.api

import android.util.Log
import eu.tackwin.cityzen.HttpTask.GetListener
import eu.tackwin.cityzen.HttpTask.GetTask
import org.json.JSONObject
import java.nio.charset.Charset

/**
 * Created by tackw on 30/11/2017.
 */
class Auth(
		private var url: String,
		private var email: String,
		private var password: String,
		private var listener: AuthListener
): GetListener {

	init {
		val json = JSONObject(
			"""
				{
					"url": "$url",
					"username": "$email",
					"password": "$password"
				}
			""".trimIndent()
		)
		GetTask(this).execute(json)
	}

	override fun getComplete(result: ByteArray) {

		val json = JSONObject(result.toString(Charset.defaultCharset()))

		listener.authComplete(
				json["access_token"] as String, json["refresh_token"] as String,
				json["id_token"] as String, json["scope"] as String, json["expires_in"] as Int,
				json["token_type"] as String
		)
	}

	override fun getFailure(error_code: Int, message: String) {
		listener.authFailure(error_code, message)
	}
}