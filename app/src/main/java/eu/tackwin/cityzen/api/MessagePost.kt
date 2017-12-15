package eu.tackwin.cityzen.api

import android.util.Log
import eu.tackwin.cityzen.HttpTask.PostListener
import eu.tackwin.cityzen.HttpTask.PostTask
import eu.tackwin.cityzen.Model.MessageInfo
import org.json.JSONObject
import java.nio.charset.Charset
import java.util.*

/**
 * Created by tackw on 12/12/2017.
 */
class MessagePost(
	val url: String,
	val hotspotId: String,
	val title: String,
	val body: String,
	val pinned: Boolean,
	val listener: MessagePostListener? = null
) : PostListener {

	init {
		val params = JSONObject("""
			|{
				|"url": "$url/hotspots/$hotspotId/messages"
			|}
		""".trimMargin().trim('\n'))

		val b = JSONObject("""
			|{
				|"title": "$title",
				|"body": "$body",
				|"pinned": $pinned
			|}
		"""".trimMargin().trim('\n'))

		val headers = mutableMapOf<String, String>()
		headers.put("Authorization", "Bearer ${AuthInfo.ID_TOKEN}")

		PostTask(this, headers).execute(params, b)
	}

	override fun postComplete(result: ByteArray) {
		listener?.onMessagePosted(
			MessageInfo.createFromJson(JSONObject(result.toString(Charset.defaultCharset())))
		)
	}

	override fun postFailure(error_code: Int, message: String, response: ByteArray?) {
		listener?.onMessageFailed()
	}
}