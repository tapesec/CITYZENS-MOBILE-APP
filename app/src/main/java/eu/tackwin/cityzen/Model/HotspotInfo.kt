package eu.tackwin.cityzen.Model

import android.os.Parcel
import android.os.Parcelable
import com.google.android.gms.maps.model.LatLng
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
	val author_pseudo: String = "" // temp need to make an author object
) : Parcelable {

	constructor(parcel: Parcel) : this(
			parcel.readString(),
			parcel.readString(),
			parcel.readString(),
			if (parcel.readInt() == 0)
				HotspotInfo.Scope.PUBLIC
			else
				HotspotInfo.Scope.PRIVATE,
			LatLng(
					parcel.readDouble(),
					parcel.readDouble()
			),
			parcel.readString(),
			parcel.readString(),
			parcel.readString()
	)

	companion object CREATOR: Parcelable.Creator<HotspotInfo> {

		fun createFromJson(json: JSONObject): HotspotInfo {
			val pos = json["position"] as JSONObject
			val address = json["address"] as JSONObject
			val author = json["author"] as JSONObject

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
				author["pseudo"] as String
			)
		}

		fun createMarkerOption(hotspotInfo: HotspotInfo): MarkerOptions
			= MarkerOptions().position(hotspotInfo.position).title(hotspotInfo.title)

		override fun createFromParcel(parcel: Parcel): HotspotInfo {
			return HotspotInfo(parcel)
		}

		override fun newArray(size: Int): Array<HotspotInfo?> {
			return arrayOfNulls(size)
		}
	}

	enum class Scope {
		PUBLIC,
		PRIVATE
	}

	override fun writeToParcel(
			parcel: Parcel,
			flags: Int
	) {
		parcel.writeString(id)
		parcel.writeString(title)
		parcel.writeString(id_city)
		parcel.writeInt(if (scope == Scope.PUBLIC) 0 else 1)
		parcel.writeDouble(position.latitude)
		parcel.writeDouble(position.longitude)
		parcel.writeString(address_city)
		parcel.writeString(address_name)
		parcel.writeString(author_pseudo)
	}

	override fun describeContents(): Int {
		return 0
	}


}