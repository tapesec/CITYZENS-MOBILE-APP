package eu.tackwin.cityzen.api

import android.util.Log
import eu.tackwin.cityzen.HttpTask.GetListener
import eu.tackwin.cityzen.HttpTask.GetTask
import org.json.JSONObject

/**
 * Created by tackw on 10/12/2017.
 */
class MessagesGet(
		url: String,
		hotspotId: String,
		private val listener: MessagesGetListener
): GetListener {

	init {

		val params = JSONObject("""
			|{
				|"url": "$url",
				|"hotspotId": "$hotspotId"
			|}
		""".trimIndent())

		val headers = mutableMapOf<String, String>()
		headers.put("authorization", "Bearer ${AuthInfo.ID_TOKEN}")

		GetTask(this, headers).execute(params)
	}

	override fun getComplete(result: ByteArray) {
		Log.i("m", result.toString())
	}

	override fun getFailure(error_code: Int, message: String) {
		Log.i("m", message.toString())
	}

}