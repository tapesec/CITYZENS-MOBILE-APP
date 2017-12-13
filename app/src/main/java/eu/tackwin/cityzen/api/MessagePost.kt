package eu.tackwin.cityzen.api

import eu.tackwin.cityzen.HttpTask.PostListener
import eu.tackwin.cityzen.HttpTask.PostTask
import org.json.JSONObject

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
		listener?.onMessageFailed()
	}

	override fun postFailure(error_code: Int, message: String, response: ByteArray?) {
		listener?.onMessagePosted()
	}
}