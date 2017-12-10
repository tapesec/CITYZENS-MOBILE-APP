package eu.tackwin.cityzen.api

import android.util.Log
import com.google.android.gms.maps.model.LatLngBounds
import eu.tackwin.cityzen.HttpTask.GetListener
import eu.tackwin.cityzen.HttpTask.GetTask
import org.json.JSONArray
import org.json.JSONObject
import java.nio.charset.Charset
import java.util.LinkedHashMap

/**
 * Created by tackw on 01/12/2017.
 */
class HotspotGet(
		url: String,
		insee: Int?,
		bounds: LatLngBounds?,
		private val hotspotGetListener: HotspotGetListener
): GetListener {

	init {
		if (insee != null){
			//todo
		} else if (bounds != null){
			val json = JSONObject("""
				{
					"url": "$url/hotspots",
					"north": "${bounds.northeast.latitude}",
					"west": "${bounds.southwest.longitude}",
					"south": "${bounds.southwest.latitude}",
					"east": "${bounds.northeast.longitude}"
				}
			""".trimIndent())

			val headers = LinkedHashMap<String, String>()
			headers.put("Authorization", "Bearer " + AuthInfo.ID_TOKEN)


			GetTask(this, headers).execute(json)
		}
	}

	override fun getComplete(result: ByteArray) {
		Log.i("complete", result.toString(Charset.defaultCharset()))

		val jsonArray = JSONArray(result.toString(Charset.defaultCharset()))
		hotspotGetListener.getAllComplete(jsonArray)
	}

	override fun getFailure(error_code: Int, message: String) {
		Log.e("error_code", error_code.toString())
		Log.e("message", message)
	}

}