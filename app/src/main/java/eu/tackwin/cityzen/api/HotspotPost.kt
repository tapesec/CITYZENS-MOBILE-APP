package eu.tackwin.cityzen.api

import android.util.Log
import com.google.android.gms.maps.model.LatLng
import eu.tackwin.cityzen.HttpTask.PostListener
import eu.tackwin.cityzen.HttpTask.PostTask
import eu.tackwin.cityzen.Model.HotspotInfo
import org.json.JSONObject

/**
 * Created by tackw on 06/12/2017.
 */
class HotspotPost(
		private val url: String,
		private val title: String,
		private val id_city: String,
		private val message: String,
		private val pos: LatLng,
		private val scope: HotspotInfo.Scope
): PostListener {
	init {
		val params = JSONObject("""
			|{
				|"url": "$url/hotspots"
			|}
		""".trimMargin().trim('\n'))

				val body = JSONObject("""
					|{
						|"title": "$title",
						|"id_city": "$id_city",
						|"message": "$message",
						|"position": {
							|"latitude": ${pos.latitude},
							|"longitude": ${pos.longitude}
						|},
						|"address": {
							|"city": "string",
							|"name": "string"
						|},
						|"scope": "${if (scope == HotspotInfo.Scope.PUBLIC) "public" else "private" }"
					|}
				""".trimMargin().replace("[\n]".toRegex(), ""))

		val headers = mutableMapOf<String, String>()
		headers.put("authorization", "Bearer " + AuthInfo.ID_TOKEN)

		PostTask(this, headers).execute(params, body)
	}
}