package eu.tackwin.cityzen.Activity

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import android.support.constraint.ConstraintLayout
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.MultiAutoCompleteTextView
import android.widget.Spinner
import android.widget.ViewFlipper

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import eu.tackwin.cityzen.Model.Hotspots.HotspotInfo
import eu.tackwin.cityzen.Model.Hotspots.WallHotspotInfo
import eu.tackwin.cityzen.R
import eu.tackwin.cityzen.api.AlertHotspotPost
import eu.tackwin.cityzen.api.EventHotspotPost
import eu.tackwin.cityzen.api.HotspotGet
import eu.tackwin.cityzen.api.HotspotGetListener
import eu.tackwin.cityzen.api.HotspotPostListener
import eu.tackwin.cityzen.api.WallHotspotPost
import org.json.JSONArray
import org.json.JSONObject
import java.util.*

class MapsActivity : AppCompatActivity(), OnMapReadyCallback,
		HotspotGetListener, HotspotPostListener, AdapterView.OnItemSelectedListener {

	private lateinit var google_map: GoogleMap
	private var hotspots: MutableList<Pair<Marker, HotspotInfo>> = mutableListOf()

	private var potentiel_hotspot: Marker? = null

	private var prompt_post: AlertDialog? = null

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_maps)
		// Obtain the SupportMapFragment and get notified when the map is ready to be used.
		val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
		mapFragment.getMapAsync(this)

		val cardList = (findViewById<LinearLayout>(R.id.card_list) as LinearLayout)
		cardList.translationY = cardList.height.toFloat()
	}

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

				var marker = HotspotInfo.createMarkerOption(i)

				marker.icon(BitmapDescriptorFactory.fromResource(
					when(i.type){
						HotspotInfo.Type.WALL -> R.drawable.ic_wall
						HotspotInfo.Type.EVENT -> R.drawable.ic_event
						HotspotInfo.Type.ALERT -> R.drawable.ic_alert
					}
				))
				hotspots.add(Pair(
					google_map.addMarker(marker),
					i
				))
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

		when (v.second.type){
			HotspotInfo.Type.WALL -> {
				val intent = Intent(this, HotspotActivity::class.java)
				val wall: WallHotspotInfo = v.second as WallHotspotInfo

				intent.putExtra("hotspot", wall as Parcelable)
				startActivity(intent)
			}
			HotspotInfo.Type.ALERT -> {

			}
			HotspotInfo.Type.EVENT -> {

			}
		}

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
		promptView.findViewById<Spinner>(R.id.type_spinner).onItemSelectedListener = this

		val alert = AlertDialog.Builder(this)
		alert.setView(promptView)
		alert.setPositiveButton(
				R.string.post_hotspot_send, { _, _ ->

			val strings_type = resources.getStringArray(R.array.post_hotspot_type)
			val title = promptView.findViewById<EditText>(R.id.title).text.toString()
			val scope =
				if (promptView.findViewById<Spinner>(R.id.post_scope).selectedItem.toString() == "Public")
					HotspotInfo.Scope.PUBLIC
				else
					HotspotInfo.Scope.PRIVATE

			val prompt = prompt_post?.findViewById<ConstraintLayout>(R.id.prompt_post_hotspot)!!
			val type = prompt.findViewById<Spinner>(R.id.type_spinner).selectedItem.toString()
			val message = prompt.findViewById<MultiAutoCompleteTextView>(R.id.message)
			val description = prompt.findViewById<MultiAutoCompleteTextView>(R.id.description)
			val dateEnd = prompt.findViewById<EditText>(R.id.date)

			when (type) {
				strings_type[0] -> WallHotspotPost(
					getString(R.string.base_url),
					"33170",
					pos,
					title,
					scope,
					this
				)
				strings_type[1] -> AlertHotspotPost(
					getString(R.string.base_url),
					"33170",
					pos,
					message.text.toString(),
					this
				)
				strings_type[2] -> EventHotspotPost(
					getString(R.string.base_url),
					"33170",
					pos,
					title,
					scope,
					Date(),
					description.text.toString()
				)
			}
		})
		alert.setNegativeButton(
				R.string.post_hotspot_cancel, {
			_, _ ->
			Log.i("Bouh", "ler")
		})
		prompt_post = alert.create()
		prompt_post?.show()
	}

	override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
		val prompt = prompt_post?.findViewById<ConstraintLayout>(R.id.prompt_post_hotspot)!!

		val item = parent?.getItemAtPosition(position).toString()
		val strings = resources.getStringArray(R.array.post_hotspot_type)

		val view_flipper = prompt.findViewById<ViewFlipper>(R.id.view_flipper)

		when(item){
			strings[0] -> view_flipper.displayedChild = 0
			strings[1] -> view_flipper.displayedChild = 1
			strings[2] -> view_flipper.displayedChild = 2
		}
	}

	override fun onNothingSelected(parent: AdapterView<*>?) {
	}
}



