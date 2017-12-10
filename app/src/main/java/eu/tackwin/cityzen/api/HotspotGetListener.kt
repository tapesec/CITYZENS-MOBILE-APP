package eu.tackwin.cityzen.api

import org.json.JSONArray
import org.json.JSONObject

/**
 * Created by tackw on 01/12/2017.
 */
interface HotspotGetListener {

	fun getAllComplete(jsonArray: JSONArray) {}
	fun getAllFailure() {}

}