package eu.tackwin.cityzen.Model.Hotspots

import com.google.android.gms.maps.model.LatLng
import eu.tackwin.cityzen.Model.AuthorInfo

/**
 * Created by tackw on 12/12/2017.
 */
class EventHotspotInfo(
	id: String,
	title: String,
	id_city: String,
	scope: Scope,
	position: LatLng,
	address_city: String,
	address_name: String,
	author: AuthorInfo
) : HotspotInfo(
	id,
	title,
	id_city,
	scope,
	Type.EVENT,
	position,
	address_city,
	address_name,
	author
) {

}