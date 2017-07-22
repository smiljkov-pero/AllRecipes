package de.foodora.android.managers.remoteconfig

import com.google.gson.annotations.SerializedName

/**
 sample JSON:
 {
    "build_numbers": [
        {"from":527, "to": 528, "translation_key":"NEXTGEN_UPDATE", "alternative_message":"please update"},
        {"from":200, "to", 300, "translation_key":"NEXTGEN_UPDATE"}
        {"from":160, "to", 190, "alternative_message":"you must update"}
        {"from":100, "to", 150}
    ]
 }
 */
class ForceUpdate {

    @SerializedName("build_numbers") val forceUpdateBuildNumbers = ArrayList<VersionRange>()

    class VersionRange {
        @SerializedName("from") var from = 0
        @SerializedName("to") var to = 0
        @SerializedName("translation_key") var translationKey: String? = null
        @SerializedName("alternative_message") var alternativeMessage: String? = null
    }
}
