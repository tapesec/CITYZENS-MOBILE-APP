package eu.tackwin.cityzen.Model.Hotspots

import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import eu.tackwin.cityzen.Common
import eu.tackwin.cityzen.Model.AuthorInfo
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by tackw on 04/12/2017.
 */
open class HotspotInfo : Parcelable {

	var id: String
	var cityId: String
	var scope: Scope
	var type: Type
	var iconType: IconType
	var position: LatLng
	var address_city: String
	var address_name: String
	var author: AuthorInfo

	constructor(id: String, cityId: String, scope: Scope, type: Type, iconType: IconType,
			position: LatLng, address_city: String, address_name: String, author: AuthorInfo){
		this.id = id
		this.cityId = cityId
		this.scope = scope
		this.type = type
		this.iconType = iconType
		this.position = position
		this.address_city = address_city
		this.address_name = address_name
		this.author = author
	}

	constructor(parcel: Parcel) : this(
		parcel.readString(),
		parcel.readString(),
		Scope.values()[parcel.readByte().toInt()],
		Type.values()[parcel.readByte().toInt()],
		IconType.values()[parcel.readByte().toInt()],
		LatLng(
			parcel.readDouble(),
			parcel.readDouble()
		),
		parcel.readString(),
		parcel.readString(),
		AuthorInfo(
			parcel.readString(),
			parcel.readString()
		)
	)

	companion object CREATOR: Parcelable.Creator<HotspotInfo> {

		fun createFromJson(json: JSONObject): HotspotInfo {

			val type: HotspotInfo.Type
			when (json["type"] as String){
				"WallMessage" -> type = Type.WALL
				"Event" -> type = Type.EVENT
				"Alert" -> type = Type.ALERT
				else -> type = Type.WALL
			}

			Log.i("json", json.toString(4))

			val id = json.optString("id")
			val title = json.optString("title")
			val cityId = json.optString("cityId")
			val scope = json.optString("scope")
			val pos = json.optJSONObject("position")
			val address = json.optJSONObject("address")
			val author = json.optJSONObject("author")
			val dateEnd = json.optString("dateEnd")
			val description = json.optJSONObject("description")?.optString("content")
			val message = json.optJSONObject("message")?.optString("content")

			return when (type){
				Type.WALL -> WallHotspotInfo(
					id!!,
					title!!,
					cityId!!,
					if (scope!! == "Public") Scope.PUBLIC else Scope.PRIVATE,
					LatLng(pos!!["latitude"] as Double, pos!!["longitude"] as Double),
					address!!["city"] as String,
					address!!["name"] as String,
					AuthorInfo.createFromJson(author!!)
				)
				Type.EVENT -> EventHotspotInfo(
					id!!,
					title!!,
					cityId!!,
					if (scope!! == "Public") Scope.PUBLIC else Scope.PRIVATE,
					LatLng(pos!!["latitude"] as Double, pos!!["longitude"] as Double),
					address!!["city"] as String,
					address!!["name"] as String,
					AuthorInfo.createFromJson(author!!),
					SimpleDateFormat(Common.dateFormat).parse(dateEnd as String),
					description!!
				)
				Type.ALERT -> AlertHotspotInfo(
					id!!,
					cityId!!,
					if (scope!! == "Public") Scope.PUBLIC else Scope.PRIVATE,
					LatLng(pos!!["latitude"] as Double, pos!!["longitude"] as Double),
					address!!["city"] as String,
					address!!["name"] as String,
					AuthorInfo.createFromJson(author!!),
					message!!
				)
			}
		}

		fun createMarkerOption(hotspotInfo: HotspotInfo): MarkerOptions
			= MarkerOptions().position(hotspotInfo.position)

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
	enum class Type {
		WALL,
		EVENT,
		ALERT
	}
	enum class IconType {
		WALL,
		EVENT,
		ACCIDENT,
		DETERIORATION
	}

	override fun writeToParcel(
		parcel: Parcel,
		flags: Int
	) {
		parcel.writeString(id)
		parcel.writeString(cityId)
		parcel.writeByte(scope.ordinal.toByte())
		parcel.writeByte(type.ordinal.toByte())
		parcel.writeByte(iconType.ordinal.toByte())
		parcel.writeDouble(position.latitude)
		parcel.writeDouble(position.longitude)
		parcel.writeString(address_city)
		parcel.writeString(address_name)
		parcel.writeString(author.pseudo)
		parcel.writeString(author.id)
	}

	override fun describeContents(): Int {
		return 0
	}


}