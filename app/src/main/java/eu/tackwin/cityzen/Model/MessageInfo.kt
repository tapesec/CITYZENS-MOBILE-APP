package eu.tackwin.cityzen.Model

import org.json.JSONObject
import java.io.Serializable
import java.util.*

/**
 * Created by tackw on 09/12/2017.
 */
class MessageInfo(
		val id: String,
		val hotspotId: String,
		val title: String,
		val body: String,
		val author: String,//temp todo make an authorInfo
		val pinned: Boolean,
		val createdAt: Date,
		val updatedAt: Date
) : Serializable {

	companion object {
		fun createFromJson(json: JSONObject): MessageInfo{
			var m = MessageInfo(
				json["id"] as String,
				json["hotspotId"] as String,
				json["title"] as String,
				json["body"] as String,
				"god",
				json["pinned"] as String == "true",
				Date(),
				Date()
			)

			return m
		}
	}

}