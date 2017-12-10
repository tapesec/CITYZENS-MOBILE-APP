package eu.tackwin.cityzen.HttpTask

import android.os.AsyncTask
import android.util.Log
import org.json.JSONObject
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.InputStreamReader
import java.lang.StringBuilder
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import javax.net.ssl.HttpsURLConnection

/**
 * Created by tackw on 05/12/2017.
 */
class PostTask(
		private val postListener: PostListener,
		private val headers: Map<String, String>? = null
): AsyncTask<JSONObject, Int, ByteArray>() {

	override fun doInBackground(vararg args: JSONObject?): ByteArray {
		if (args.size < 2) return ByteArray(0)

		val arg = args[0] as JSONObject

		if (!arg.has("url")) return ByteArray(0)

		val u = arg["url"] as String
		arg.remove("url")

		val body = args[1] as JSONObject

		return sendPost(u, arg, body)
	}

	private fun sendPost(baseUrl: String, params: JSONObject, body: JSONObject): ByteArray {
		var connection: HttpURLConnection? = null

		try {

			var fullUrl = baseUrl

			if (params.length() != 0){
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

				Log.d("POST", fullUrl)
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

			connection.requestMethod = "POST"

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
					is JSONObject -> output.append(value.toString(0).replace("[\n]".toRegex(), ""))
				}
			}

			val bodyData = body.toString(0).toByteArray()
			Log.i("data", output.toString())
			connection.setRequestProperty("Content-Length", bodyData.size.toString())

			connection.outputStream.write(bodyData)
			connection.outputStream.flush()
			connection.outputStream.close()


			connection.connect()

			BufferedReader(InputStreamReader(connection.inputStream)).use {
				val response = it.readText().toByteArray()
				postListener.postComplete(response)
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
					postListener.postFailure(
							connection?.responseCode ?: 0,
							connection?.responseMessage ?: "",
							response
					)
					Log.e("response body", string)
					return response
				}
			}

			postListener.postFailure(
				connection?.responseCode ?: 0,
				connection?.responseMessage ?: ""
			)

			return ByteArray(0)
		}
	}

}