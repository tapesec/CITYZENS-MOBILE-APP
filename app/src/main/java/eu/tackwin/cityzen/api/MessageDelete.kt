package eu.tackwin.cityzen.api

import eu.tackwin.cityzen.HttpTask.DeleteTask
import eu.tackwin.cityzen.HttpTask.DeleteTaskListener
import org.json.JSONObject

/**
 * Created by tackw on 15/12/2017.
 */
class MessageDelete(
	 url: String,
	 hotspotId: String,
	 messageId: String,
	private val listener: MessageDeleteListener? = null
) : DeleteTaskListener {

	init {
		val params = JSONObject("""
			|{
				|"url": "$url/hotspots/${hotspotId}/messages/${messageId}"
			|}
		""".trimMargin().trim('\n'))

			val headers = mutableMapOf<String, String>()
			headers.put("Authorization", "Bearer ${AuthInfo.ID_TOKEN}")

			DeleteTask(this, headers).execute(params)
	}

	override fun deleteComplete() {
		listener?.onMessageDeleted()
	}

	override fun deleteFailure() {
		listener?.onMessageDeleteFailed()
	}

}