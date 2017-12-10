package eu.tackwin.cityzen.Model

import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import org.json.JSONObject
import java.io.Serializable
import java.util.*

/**
 * Created by tackw on 04/12/2017.
 */
class HotspotInfo(
	val id: String,
	val title: String = "",
	val id_city: String = "",
	val scope: Scope = Scope.PUBLIC,
	val position: LatLng = LatLng(0.0, 0.0),
	val address_city: String = "", // temp need to make a address object
	val address_name: String = "",
	val author_pseudo: String = "", // temp need to make an author object
	val messages: MutableList<MessageInfo>,
	val message: String = "", // temp need to make a message object
	val message_created_at: Date = Date(),
	val message_updated_at: Date? = null
) : Serializable {

	companion object {
		fun createFromJson(json: JSONObject): HotspotInfo {
			val pos = json["position"] as JSONObject
			val address = json["address"] as JSONObject
			val author = json["author"] as JSONObject
			val content = json["content"] as JSONObject

			val scope = if ((json["scope"] as String) == "public")
				HotspotInfo.Scope.PUBLIC
			else
				HotspotInfo.Scope.PRIVATE

			return HotspotInfo(
				json["id"] as String,
				json["title"] as String,
				json["idCity"] as String,
				scope,
				LatLng(pos["latitude"] as Double, pos["longitude"] as Double),
				address["city"] as String,
				address["name"] as String,
				author["pseudo"] as String,
				mutableListOf(),
				content["message"] as String,
				Date(), // create an algorithm to convert from String
				if (content.has("updatedAt")) Date() else null
			)
		}

		fun createMarkerOption(hotspotInfo: HotspotInfo): MarkerOptions
			= MarkerOptions().position(hotspotInfo.position).title(hotspotInfo.title)
	}

	enum class Scope {
		PUBLIC,
		PRIVATE
	}
}