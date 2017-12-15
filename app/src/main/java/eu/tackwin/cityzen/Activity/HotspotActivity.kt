package eu.tackwin.cityzen.Activity

import android.support.v7.app.AlertDialog
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Switch
import android.widget.TextView
import android.widget.ViewSwitcher
import eu.tackwin.cityzen.Model.Hotspots.WallHotspotInfo
import eu.tackwin.cityzen.Model.MessageInfo
import eu.tackwin.cityzen.R
import eu.tackwin.cityzen.api.HotspotViewPost
import eu.tackwin.cityzen.api.MessageDelete
import eu.tackwin.cityzen.api.MessagePatch
import eu.tackwin.cityzen.api.MessagePatchListener
import eu.tackwin.cityzen.api.MessagePost
import eu.tackwin.cityzen.api.MessagePostListener
import eu.tackwin.cityzen.api.MessagesGet
import eu.tackwin.cityzen.api.MessagesGetListener
import java.util.*

/**
 * Created by tackw on 09/12/2017.
 */
class HotspotActivity: AppCompatActivity(), MessagesGetListener, MessagePatchListener, MessagePostListener {

	private var deleting = false

	private lateinit var  hotspotInfo: WallHotspotInfo

	override fun onCreate(savedInstance: Bundle?) {
		super.onCreate(savedInstance)
		setContentView(R.layout.activity_hotspot)

		findViewById<SwipeRefreshLayout>(R.id.swipe_refresh).setOnRefreshListener {
			onMessageRefreshed()
		}

		hotspotInfo = intent.getParcelableExtra("hotspot")!!
		populateHotspot(hotspotInfo)
	}

	override fun onCreateOptionsMenu(menu: Menu?): Boolean {
		menuInflater.inflate(R.menu.action_hotspot, menu)
		return true
	}

	private fun onMessageRefreshed(){
		populateHotspot(hotspotInfo)
	}

	override fun onOptionsItemSelected(item: MenuItem?): Boolean {
		when(item!!.itemId){
			R.id.action_new -> onAddMessage()
			R.id.action_delete -> onDeleteMessage()
		}

		return true
	}

	private fun onAddMessage() {
		val view = layoutInflater.inflate(R.layout.post_message_alert, null)
		val alert = AlertDialog.Builder(this)
		alert.setView(view)
		alert.setPositiveButton(R.string.post_message_post, { _, _ ->

			val title = view.findViewById<EditText>(R.id.date).text.toString()
			val body = view.findViewById<EditText>(R.id.body).text.toString()
			val pinned = view.findViewById<Switch>(R.id.pinned).isActivated

			MessagePost(
				resources.getString(R.string.base_url), hotspotInfo.id, title, body, pinned,this
			)
		})
		alert.setNegativeButton(R.string.post_message_cancel, { _, _ ->
			Log.i(":(", "...")
		})

		alert.create().show()
	}

	private fun onDeleteMessage() {
		val linear_layout = findViewById<LinearLayout>(R.id.hotspot_messages_list_layout)

		if (deleting){
			val views_to_delete = mutableListOf<View>()

			for (i in 0..(linear_layout.childCount - 1)) {
				val child = linear_layout.getChildAt(i)

				val checkbox = child.findViewById<CheckBox>(R.id.checkbox_delete)
				if (checkbox.isChecked) {
					val mInfo = child.tag!! as MessageInfo
					views_to_delete.add(child)
					MessageDelete(resources.getString(R.string.base_url), mInfo.hotspotId, mInfo.id)
				}
				checkbox.visibility = View.GONE
				checkbox.isChecked = false
			}

			for (i in views_to_delete) {
				linear_layout.removeView(i)
			}
		} else {
			for (i in 0..(linear_layout.childCount - 1)) {
				val child = linear_layout.getChildAt(i)

				val checkbox = child.findViewById<CheckBox>(R.id.checkbox_delete)
				checkbox.visibility = View.VISIBLE
			}
		}

		deleting = !deleting
	}

	private fun populateHotspot(hotspotInfo: WallHotspotInfo){
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
			listMessages.removeViews(0, listMessages.childCount)
			for (i in listViews)
				listMessages.addView(i)

			findViewById<SwipeRefreshLayout>(R.id.swipe_refresh).isRefreshing = false
		}


	}

	override fun messagesGetFailure() {
		runOnUiThread {
			findViewById<SwipeRefreshLayout>(R.id.swipe_refresh).isRefreshing = false
		}
	}

	override fun onMessagePosted(msg: MessageInfo) {
		val v = layoutInflater.inflate(R.layout.message_layout, null)
		populateMessage(msg, v)
		runOnUiThread {
			findViewById<LinearLayout>(R.id.hotspot_messages_list_layout).addView(v)
		}
	}

}