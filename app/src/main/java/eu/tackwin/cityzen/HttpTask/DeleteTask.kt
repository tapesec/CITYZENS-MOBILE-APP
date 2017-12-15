package eu.tackwin.cityzen.HttpTask

import android.os.AsyncTask
import android.util.Log
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.StringBuilder
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.nio.charset.Charset
import javax.net.ssl.HttpsURLConnection

/**
 * Created by tackw on 15/12/2017.
 */
class DeleteTask(
	private val listener: DeleteTaskListener? = null,
	private val headers: Map<String, String>? = null
) : AsyncTask<JSONObject, Int, ByteArray>() {


	override fun doInBackground(vararg args: JSONObject?): ByteArray {
		val arg = if (args.isNotEmpty()) (args[0] as JSONObject) else JSONObject()
		if (!arg.has("url")) return ByteArray(0)

		val u = arg["url"] as String
		arg.remove("url")

		val body = if (args.size > 1) args[1] as JSONObject else JSONObject()

		return sendDelete(u, arg, body)
	}

	private fun sendDelete(baseUrl: String, params: JSONObject, body: JSONObject): ByteArray {
		var connection: HttpURLConnection? = null

		try {

			var fullUrl = baseUrl

			if (params.length() != 0) {
				val getData = StringBuilder()
				getData.append('?')

				var first = true
				for (it in params.keys()) {
					if (first)
						first = false
					else
						getData.append('&')

					getData.append(URLEncoder.encode(it as String, "UTF-8"))
					getData.append("=")
					getData.append(URLEncoder.encode(params[it] as String, "UTF-8"))
				}
				fullUrl += getData.toString()
			}

			val url = URL(baseUrl)
			connection = url.openConnection() as HttpURLConnection


			if (headers != null){
				for (it in headers.asIterable()){
					connection.setRequestProperty(
							URLEncoder.encode(it.key, "UTF-8"),
							URLEncoder.encode(it.value, "UTF-8")
					)
				}
			}
			connection.setRequestProperty("content-type", "application/json")

			setRequestMethod(connection, "DELETE")
			connection.doOutput = true

			var output = StringBuilder()

			for (it in body.keys()) {
				if (output.isNotEmpty()) output.append('&')
				output.append(URLEncoder.encode(it as String, "UTF-8"))
				output.append('=')

				val value = body[it]
				when (value){
					is Int -> output.append(value)
					is Long -> output.append(value)
					is Float -> output.append(value)
					is Double -> output.append(value)
					is String -> output.append(value)
					is Boolean -> output.append(value)
					is JSONObject -> output.append(value.toString(0).replace("[\n]".toRegex(), ""))
				}
			}

			val bodyData = body.toString(0).toByteArray()
			Log.i("b", bodyData.toString(Charset.defaultCharset()))
			connection.setRequestProperty("Content-Length", bodyData.size.toString())

			connection.outputStream.write(bodyData)
			connection.outputStream.flush()
			connection.outputStream.close()

			connection.connect()

			BufferedReader(InputStreamReader(connection.inputStream)).use {
				val response = it.readText().toByteArray()
				listener?.deleteComplete()
				return response
			}

		} catch (e: Throwable){
			Log.e("post", e.message)
			Log.e("url", baseUrl)
			Log.e("params", params.toString(4))
			Log.e("body", body.toString(4))

			if (connection != null){
				Log.e("response", connection.responseCode.toString())
				Log.e("response", connection.responseMessage.toString())

				BufferedReader(InputStreamReader(connection.errorStream)).use {
					val string = it.readText()
					val response = string.toByteArray()
					listener?.deleteFailure()
					Log.e("response body", string)
					return response
				}
			}

			listener?.deleteFailure()

			return ByteArray(0)
		}
	}


	private fun setRequestMethod(c: HttpURLConnection, value: String) {
		try {
			val target: Any
			if (c is HttpsURLConnection) {
				val delegate = HttpsURLConnection::class.java!!.getDeclaredField("delegate")
				delegate.isAccessible = true
				target = delegate.get(c)
			} else {
				target = c
			}
			val f = HttpURLConnection::class.java.getDeclaredField("method")
			f.isAccessible = true
			f.set(target, value)
		} catch (ex: IllegalAccessException) {
			throw AssertionError(ex)
		} catch (ex: NoSuchFieldException) {
			throw AssertionError(ex)
		}
	}

}