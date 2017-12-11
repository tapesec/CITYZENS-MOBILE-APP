package eu.tackwin.cityzen

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import eu.tackwin.cityzen.Model.HotspotInfo
import eu.tackwin.cityzen.Model.MessageInfo
import eu.tackwin.cityzen.api.MessagesGet
import eu.tackwin.cityzen.api.MessagesGetListener

/**
 * Created by tackw on 09/12/2017.
 */
class HotspotActivity: AppCompatActivity(), MessagesGetListener {

	private lateinit var  hotspotInfo: HotspotInfo

	override fun onCreate(savedInstance: Bundle?) {
		super.onCreate(savedInstance)
		setContentView(R.layout.activity_hotspot)

		hotspotInfo = intent.getParcelableExtra<HotspotInfo>("hotspot") as HotspotInfo
		populateHotspot(hotspotInfo)
	}

	private fun populateHotspot(hotspotInfo: HotspotInfo){
		findViewById<TextView>(R.id.hotspot_title)!!.text = hotspotInfo.title
		findViewById<TextView>(R.id.hotspot_author)!!.text = hotspotInfo.author_pseudo

		MessagesGet(resources.getString(R.string.base_url), hotspotInfo.id, this)
	}

	private fun populateMessage(messageInfo: MessageInfo, view: View){
		view.findViewById<TextView>(R.id.body).text = messageInfo.body
	}

	override fun messagesGetComplete(messages: List<MessageInfo>) {
		var listMessages = findViewById<LinearLayout>(R.id.hotspot_messages_list_layout)
		var inflater = layoutInflater

		var listViews = mutableListOf<View>()

		for (m in messages){
			var v = inflater.inflate(R.layout.message_layout, null)
			v.findViewById<TextView>(R.id.body).text = m.body
			Log.i("i", m.body)
			//populateMessage(m, v)

			listViews.add(v)
		}

		runOnUiThread{
			for (i in listViews)
				listMessages.addView(i)
		}
	}

	override fun messagesGetFailure() {

	}

}