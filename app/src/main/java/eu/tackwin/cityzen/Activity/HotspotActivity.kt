package eu.tackwin.cityzen.Activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.ViewSwitcher
import eu.tackwin.cityzen.Model.Hotspots.HotspotInfo
import eu.tackwin.cityzen.Model.MessageInfo
import eu.tackwin.cityzen.R
import eu.tackwin.cityzen.api.HotspotViewPost
import eu.tackwin.cityzen.api.MessagePatch
import eu.tackwin.cityzen.api.MessagePatchListener
import eu.tackwin.cityzen.api.MessagesGet
import eu.tackwin.cityzen.api.MessagesGetListener
import java.util.*

/**
 * Created by tackw on 09/12/2017.
 */
class HotspotActivity: AppCompatActivity(), MessagesGetListener, MessagePatchListener {

	private lateinit var  hotspotInfo: HotspotInfo

	private var messages: MutableList<MessageInfo> = mutableListOf()

	override fun onCreate(savedInstance: Bundle?) {
		super.onCreate(savedInstance)
		setContentView(R.layout.activity_hotspot)

		hotspotInfo = intent.getParcelableExtra<HotspotInfo>("hotspot") as HotspotInfo
		populateHotspot(hotspotInfo)
	}

	private fun populateHotspot(hotspotInfo: HotspotInfo){
		findViewById<TextView>(R.id.hotspot_title)!!.text = hotspotInfo.title
		findViewById<TextView>(R.id.hotspot_author)!!.text = hotspotInfo.author.pseudo

		HotspotViewPost(resources.getString(R.string.base_url), hotspotInfo.id)
		MessagesGet(resources.getString(R.string.base_url), hotspotInfo.id, this)
	}

	private fun populateMessage(messageInfo: MessageInfo, view: View){
		view.findViewById<TextView>(R.id.body_label).text = messageInfo.body
		view.findViewById<EditText>(R.id.body_edit).setText(
				messageInfo.body, TextView.BufferType.EDITABLE
		)
		view.findViewById<TextView>(R.id.title_label).text = messageInfo.title
		view.findViewById<EditText>(R.id.title_edit).setText(
				messageInfo.title, TextView.BufferType.EDITABLE
		)
		view.findViewById<TextView>(R.id.author).text = messageInfo.author

		view.tag = messageInfo
		view.findViewById<Button>(R.id.body_patch).setOnClickListener {
			onMessagePatchClick(view)
		}

		view.findViewById<View>(R.id.hotbox_layout).setOnLongClickListener {
			v -> onLongMessageClick(v!!)
		}
	}

	private fun onMessagePatchClick(v: View): Boolean {
		val m = v.tag!! as MessageInfo
		MessagePatch(
			resources.getString(R.string.base_url),
			MessageInfo(
				m.id,
				m.hotspotId,
				v.findViewById<EditText>(R.id.title_edit).text.toString(),
				v.findViewById<EditText>(R.id.body_edit).text.toString(),
				m.author,
				false,
				m.views,
				Date(),
				Date()
			),
			this
		)
		return true
	}

	private fun onLongMessageClick(v: View): Boolean {
		Log.i("hey", "a")

		val switcher = v.findViewById<ViewSwitcher>(R.id.body_switch)
		if (switcher.currentView.id == R.id.edit){
			switcher.showPrevious()
		} else {
			switcher.showNext()
		}

		return true
	}

	override fun messagesGetComplete(messages: List<MessageInfo>) {
		var listMessages = findViewById<LinearLayout>(R.id.hotspot_messages_list_layout)
		var inflater = layoutInflater

		var listViews = mutableListOf<View>()

		for (m in messages){
			var v = inflater.inflate(R.layout.message_layout, null)
			populateMessage(m, v)

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