package eu.tackwin.cityzen.api

import android.util.Log
import eu.tackwin.cityzen.HttpTask.PostListener
import eu.tackwin.cityzen.HttpTask.PostTask
import org.json.JSONObject

/**
 * Created by tackw on 12/12/2017.
 */
class HotspotViewPost(
		url: String,
		id: String
): PostListener{

	init {
		val params = JSONObject("""
			|{
				|"url": "$url/hotspots/$id/views"
			|}
		""".trimMargin().trim('\n'))

		val headers = mutableMapOf<String, String>()
		headers.put("authorization", "Bearer " + AuthInfo.ID_TOKEN)

		PostTask(this, headers).execute(params, JSONObject())
	}

	override fun postFailure(
			error_code: Int,
			message: String,
			response: ByteArray?
	) {
		Log.e("error", message)
	}
}