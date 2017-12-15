package eu.tackwin.cityzen.Model.Hotspots

import android.os.Parcel
import android.os.Parcelable
import com.google.android.gms.maps.model.LatLng
import eu.tackwin.cityzen.Model.AuthorInfo

/**
 * Created by tackw on 12/12/2017.
 */
class WallHotspotInfo: HotspotInfo, Parcelable {

	var title: String

	constructor(
		id: String,
		title: String,
		cityId: String,
		scope: Scope,
		position: LatLng,
		address_city: String,
		address_name: String,
		author: AuthorInfo
	): super(
		id, cityId, scope, Type.WALL, IconType.WALL, position, address_city, address_name, author
	) {
		this.title = title
	}

	constructor(parcel: Parcel): super(parcel) {
		title = parcel.readString()
	}

	companion object CREATOR: Parcelable.Creator<WallHotspotInfo>{
		override fun createFromParcel(source: Parcel?): WallHotspotInfo {
			return WallHotspotInfo(source!!)
		}

		override fun newArray(size: Int): Array<WallHotspotInfo> {
			return arrayOf()
		}
	}

	override fun writeToParcel(
			parcel: Parcel,
			flags: Int
	) {
		super.writeToParcel(parcel, flags)
		parcel.writeString(title)
	}

}