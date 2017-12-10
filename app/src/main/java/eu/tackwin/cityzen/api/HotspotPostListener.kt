package eu.tackwin.cityzen.api

import org.json.JSONObject

/**
 * Created by tackw on 06/12/2017.
 */
interface HotspotPostListener {

	fun postComplete(json: JSONObject) {}
	fun postFailure() {}

}