package eu.tackwin.cityzen.api

import android.util.Log
import eu.tackwin.cityzen.HttpTask.GetListener
import eu.tackwin.cityzen.HttpTask.GetTask
import eu.tackwin.cityzen.Model.MessageInfo
import org.json.JSONArray
import org.json.JSONObject
import java.nio.charset.Charset

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
				|"url": "$url/hotspots/$hotspotId/messages"
			|}
		""".trimMargin().trim('\n'))

		val headers = mutableMapOf<String, String>()
		headers.put("authorization", "Bearer ${AuthInfo.ID_TOKEN}")

		GetTask(this, headers).execute(params)
	}

	override fun getComplete(result: ByteArray) {
		val str = result.toString(Charset.defaultCharset())
		val jsonArray = JSONArray(str)

		val mArray = mutableListOf<MessageInfo>()
		for (i in 0..(jsonArray.length() - 1)){
			mArray.add(MessageInfo.createFromJson(jsonArray[i] as JSONObject))
		}

		listener.messagesGetComplete(mArray.toList())
	}

	override fun getFailure(error_code: Int, message: String) {
		Log.i("m", message)
		listener.messagesGetFailure()
	}

}