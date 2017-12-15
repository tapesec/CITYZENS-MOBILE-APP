package eu.tackwin.cityzen.Model.Hotspots

import com.google.android.gms.maps.model.LatLng
import eu.tackwin.cityzen.Model.AuthorInfo
import java.util.*

/**
 * Created by tackw on 12/12/2017.
 */
class EventHotspotInfo(
	id: String,
	title: String,
	id_city: String,
	scope: HotspotInfo.Scope,
	position: LatLng,
	address_city: String,
	address_name: String,
	author: AuthorInfo,
	dateEnd: Date,
	description: String
) : HotspotInfo(
	id,
	id_city,
	scope,
	Type.EVENT,
	IconType.EVENT,
	position,
	address_city,
	address_name,
	author
) {

}