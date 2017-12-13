package eu.tackwin.cityzen.Activity

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Spinner

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import eu.tackwin.cityzen.Model.Hotspots.HotspotInfo
import eu.tackwin.cityzen.R
import eu.tackwin.cityzen.api.HotspotGet
import eu.tackwin.cityzen.api.HotspotGetListener
import eu.tackwin.cityzen.api.HotspotPost
import eu.tackwin.cityzen.api.HotspotPostListener
import org.json.JSONArray
import org.json.JSONObject

class MapsActivity : AppCompatActivity(), OnMapReadyCallback,
		HotspotGetListener, HotspotPostListener {

	private lateinit var google_map: GoogleMap
	private var hotspots: MutableList<Pair<Marker, HotspotInfo>> = mutableListOf()

	private var potentiel_hotspot: Marker? = null


	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_maps)
		// Obtain the SupportMapFragment and get notified when the map is ready to be used.
		val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
		mapFragment.getMapAsync(this)

		val cardList = (findViewById<LinearLayout>(R.id.card_list) as LinearLayout)
		cardList.translationY = cardList.height.toFloat()
	}

	/**
	 * Manipulates the map once available.
	 * This callback is triggered when the map is ready to be used.
	 * This is where we can add markers or lines, add listeners or move the camera. In this case,
	 * we just add a marker near Sydney, Australia.
	 * If Google Play services is not installed on the device, the user will be prompted to install
	 * it inside the SupportMapFragment. This method will only be triggered once the user has
	 * installed Google Play services and returned to the app.
	 */
	override fun onMapReady(googleMap: GoogleMap) {
		this.google_map = googleMap

		// Add a marker in Sydney and move the camera
		val paris = LatLng(
				48.86666,
				2.3333333
		)
		this.google_map.moveCamera(CameraUpdateFactory.newLatLng(paris))
		this.google_map.setOnMarkerClickListener { marker -> onMarkerFocus(marker) }
		this.google_map.setOnMapClickListener { _ -> onMapClicked() }
		this.google_map.setOnMapLongClickListener { pos -> onLongClick(pos) }
	}


	fun onFetch(view: View?){
		val bounds = google_map.projection.visibleRegion.latLngBounds
		HotspotGet(
			getString(R.string.base_url),
			null,
			bounds,
			this
		)
	}

	override fun getAllComplete(jsonArray: JSONArray) {

		var hotspotsToAdd = mutableListOf<HotspotInfo>()

		for (i in 0..(jsonArray.length() - 1)){
			val json = jsonArray[i] as JSONObject

			Log.i("j", json.toString(4))

			hotspotsToAdd.add(HotspotInfo.createFromJson(json))
		}

		runOnUiThread {

			val it = hotspots.iterator()
			while (it.hasNext()) {
				val el = it.next()
				if (google_map.projection.visibleRegion.latLngBounds.contains(el.first.position)) {
					el.first.remove()
					it.remove()
				}
			}

			for (i in hotspotsToAdd) {

				hotspots.add(
					Pair(
						google_map.addMarker(HotspotInfo.createMarkerOption(i)),
						i
					)
				)
			}
		}
	}

	override fun getAllFailure() {
	}

	private fun onMarkerFocus(marker: Marker): Boolean {
		val v = hotspots.find {
			element: Pair<Marker, HotspotInfo> -> element.first.id == marker.id
		}
		if (v == null) return false

		val intent = Intent(this, HotspotActivity::class.java)
		intent.putExtra("hotspot", v.second)
		startActivity(intent)

		return true
	}

	private fun onMapClicked() {
		val a = (findViewById<LinearLayout>(R.id.card_list) as LinearLayout)
		a.animate().translationY(a.height.toFloat())
	}

	private fun onLongClick(pos: LatLng) {
		if (potentiel_hotspot != null) {
			potentiel_hotspot!!.remove()
			potentiel_hotspot = null
		}

		potentiel_hotspot = this.google_map.addMarker(MarkerOptions().position(pos))
		this.google_map.moveCamera(CameraUpdateFactory.newLatLng(pos))

		val li = application.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
		val promptView = li.inflate(
				R.layout.post_hotspot_prompt,
				null
		) as View

		val alert = AlertDialog.Builder(this)
		alert.setView(promptView)
		alert.setPositiveButton(
				R.string.post_hotspot_send, {
			_, _ ->
			val title = promptView.findViewById<EditText>(R.id.post_title).text.toString()
			val id_city = promptView.findViewById<EditText>(R.id.post_insee).text.toString()
			val scope = promptView.findViewById<Spinner>(R.id.post_scope).selectedItem.toString()
			val message = promptView.findViewById<EditText>(R.id.post_message).text.toString()

			HotspotPost(
				getString(R.string.base_url),
				title,
				id_city,
				message,
				pos,
				if (scope == "Public") HotspotInfo.Scope.PUBLIC else HotspotInfo.Scope.PRIVATE
			)
		})
		alert.setNegativeButton(
				R.string.post_hotspot_cancel, {
			_, _ ->
			Log.i("Bouh", "ler")
		})
		alert.create().show()
	}
}



