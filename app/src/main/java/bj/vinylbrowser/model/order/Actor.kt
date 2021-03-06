package bj.vinylbrowser.model.order

import com.google.gson.annotations.SerializedName

/**
 * Created by Josh Laird on 19/05/2017.
 */
data class Actor(val username: String, @SerializedName("resource_url") val resourceUrl: String)