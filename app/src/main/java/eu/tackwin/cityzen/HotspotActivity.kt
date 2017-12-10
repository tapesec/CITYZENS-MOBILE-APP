package eu.tackwin.cityzen

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.TextView
import eu.tackwin.cityzen.Model.HotspotInfo

/**
 * Created by tackw on 09/12/2017.
 */
class HotspotActivity: AppCompatActivity() {

	private lateinit var  hotspotInfo: HotspotInfo

	override fun onCreate(savedInstance: Bundle?) {
		super.onCreate(savedInstance)
		setContentView(R.layout.activity_hotspot)

		hotspotInfo = intent.getSerializableExtra("hotspot") as HotspotInfo
		populateHotspot(hotspotInfo)
	}

	fun populateHotspot(hotspotInfo: HotspotInfo){
		findViewById<TextView>(R.id.hotspot_title)!!.text = hotspotInfo.title
		findViewById<TextView>(R.id.hotspot_author)!!.text = hotspotInfo.author_pseudo


	}

}