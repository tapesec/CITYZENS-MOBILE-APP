package eu.tackwin.cityzen.api

import eu.tackwin.cityzen.HttpTask.PatchListener
import eu.tackwin.cityzen.HttpTask.PatchTask
import eu.tackwin.cityzen.Model.MessageInfo
import org.json.JSONObject

/**
 * Created by tackw on 12/12/2017.
 */
class MessagePatch(
	url: String,
	messageInfo: MessageInfo,
	private val listener: MessagePatchListener? = null
) : PatchListener {

	init {
		val params = JSONObject("""
			|{
				|"url": "$url/hotspots/${messageInfo.hotspotId}/messages/${messageInfo.id}"
			|}
		""".trimMargin().trim('\n'))

		val body = JSONObject("""
			|{
				|"title": "${messageInfo.title}",
				|"body": "${messageInfo.body}",
				|"pinned": true
			|}
		"""".trimMargin().trim('\n'))

		val headers = mutableMapOf<String, String>()
		headers.put("Authorization", "Bearer ${AuthInfo.ID_TOKEN}")

		PatchTask(this, headers).execute(params, body)
	}

	override fun patchComplete() {
		listener?.onPatchApplied()
	}

	override fun patchFailure() {
		listener?.onPatchApplied()
	}
}