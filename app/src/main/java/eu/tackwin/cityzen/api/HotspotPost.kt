package eu.tackwin.cityzen.api

import com.google.android.gms.maps.model.LatLng
import eu.tackwin.cityzen.Common
import eu.tackwin.cityzen.HttpTask.PostListener
import eu.tackwin.cityzen.HttpTask.PostTask
import eu.tackwin.cityzen.Model.Hotspots.HotspotInfo
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class WallHotspotPost(
	url: String,
	cityId: String,
	pos: LatLng,
	title: String,
	scope: HotspotInfo.Scope,
	listener: HotspotPostListener? = null
) : PostListener {
	init {
		val params = JSONObject("""
			|{
				|"url": "$url/hotspots"
			|}
		""".trimMargin().trim('\n'))

		val body = JSONObject("""
			|{
				|"title": "$title",
				|"cityId": "$cityId",
				|"position": {
					|"latitude": ${pos.latitude},
					|"longitude": ${pos.longitude}
				|},
				|"address": {
					|"city": "string",
					|"name": "string"
				|},
				|"type": "WallMessage",
				|"iconType": "WallIcon",
				|"scope": "${if (scope == HotspotInfo.Scope.PUBLIC) "public" else "private" }"
			|}
		""".trimMargin().replace("[\n]".toRegex(), ""))

		val headers = mutableMapOf<String, String>()
		headers.put("authorization", "Bearer " + AuthInfo.ID_TOKEN)

		PostTask(this, headers).execute(params, body)
	}
}

class EventHotspotPost(
		url: String,
		cityId: String,
		pos: LatLng,
		title: String,
		scope: HotspotInfo.Scope,
		dateEnd: Date,
		description: String,
		listener: HotspotPostListener? = null
) : PostListener {
	init {
		val params = JSONObject("""
			|{
				|"url": "$url/hotspots"
			|}
		""".trimMargin().trim('\n'))

		val body = JSONObject("""
			|{
				|"title": "$title",
				|"cityId": "$cityId",
				|"position": {
					|"latitude": ${pos.latitude},
					|"longitude": ${pos.longitude}
				|},
				|"address": {
					|"city": "string",
					|"name": "string"
				|},
				|"type": "Event",
				|"iconType": "EventIcon",
				|"dateEnd": "${SimpleDateFormat(Common.dateFormat).format(dateEnd)}",
				|"description": "$description",
				|"scope": "${if (scope == HotspotInfo.Scope.PUBLIC) "public" else "private" }"
			|}
		""".trimMargin().replace("[\n]".toRegex(), ""))

		val headers = mutableMapOf<String, String>()
		headers.put("authorization", "Bearer " + AuthInfo.ID_TOKEN)

		PostTask(this, headers).execute(params, body)
	}
}



class AlertHotspotPost(
		url: String,
		cityId: String,
		pos: LatLng,
		message: String,
		listener: HotspotPostListener? = null
) : PostListener {
	init {
		val params = JSONObject("""
			|{
				|"url": "$url/hotspots"
			|}
		""".trimMargin().trim('\n'))

		val body = JSONObject("""
			|{
				|"cityId": "$cityId",
				|"position": {
					|"latitude": ${pos.latitude},
					|"longitude": ${pos.longitude}
				|},
				|"address": {
					|"city": "string",
					|"name": "string"
				|},
				|"type": "Alert",
				|"iconType": "AccidentIcon",
				|"message": "$message"
			|}
		""".trimMargin().replace("[\n]".toRegex(), ""))

		val headers = mutableMapOf<String, String>()
		headers.put("authorization", "Bearer " + AuthInfo.ID_TOKEN)

		PostTask(this, headers).execute(params, body)
	}
}