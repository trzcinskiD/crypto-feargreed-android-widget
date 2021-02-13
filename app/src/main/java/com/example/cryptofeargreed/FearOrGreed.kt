package com.example.cryptofeargreed

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.widget.RemoteViews
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.util.Log
import androidx.core.content.ContextCompat
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET

/**
 * Implementation of App Widget functionality.
 */

class FearOrGreed : AppWidgetProvider() {
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

internal fun updateAppWidget(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetId: Int
) {
    fun getColor(indexMeaning: String) = when (indexMeaning) {
        "Extreme Greed" -> ContextCompat.getColor(context, R.color.extreme_greed)
        "Greed" -> ContextCompat.getColor(context, R.color.greed)
        "Neutral" -> ContextCompat.getColor(context, R.color.neutral)
        "Fear" -> ContextCompat.getColor(context, R.color.fear)
        "Extreme Fear" -> ContextCompat.getColor(context, R.color.extreme_fear)
        else -> {
            ContextCompat.getColor(context, R.color.white)
        }
    }
/* Creates an instance of the UserService using a simple Retrofit builder using Moshi
     * as a JSON converter, this will append the endpoints set on the UserService interface
     * (for example '/api', '/api?results=2') with the base URL set here, resulting on the
     * full URL that will be called: 'https://randomuser.me/api' */
    val service = Retrofit.Builder()
        .baseUrl("https://api.alternative.me/")
        .addConverterFactory(MoshiConverterFactory.create())
        .build()
        .create(UserService::class.java)

    /* Calls the endpoint set on getUsers (/api) from UserService using enqueue method
     * that creates a new worker thread to make the HTTP call */
    service.getIndex().enqueue(object : Callback<CryptoIndexData> {

        /* The HTTP call failed. This method is run on the main thread */
        override fun onFailure(call: Call<CryptoIndexData>, t: Throwable) {
            Log.d("TAG_", "An error happened!")
            t.printStackTrace()
        }

        /* The HTTP call was successful, we should still check status code and response body
         * on a production app. This method is run on the main thread */
        override fun onResponse(call: Call<CryptoIndexData>, response: Response<CryptoIndexData>) {
            /* This will print the response of the network call to the Logcat */
            Log.d("TAG_", response.body().toString())
            val payload = response.body()!!
            val indexValue = payload.`data`[0].value
            val indexMeaning = payload.`data`[0].value_classification
            Log.d("TAG_Result", indexValue + indexMeaning)

            // Construct the RemoteViews object
            val views = RemoteViews(context.packageName, R.layout.fear_or_greed)
            views.setTextViewText(R.id.appwidget_id_indexValue, indexValue)
            views.setTextViewText(R.id.appwidget_id_indexMeaning, indexMeaning)
            views.setInt(
                R.id.container_id,
                "setBackgroundColor",
                getColor(indexMeaning)
            )

            // Instruct the widget manager to update the widget
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    })
}

/* Kotlin data/model classes that map the JSON response, we could also add Moshi
* annotations to help the compiler with the mappings on a production app */
data class CryptoIndexData(
    val `data`: List<Data>,
    val metadata: Metadata,
    val name: String
) {
    data class Data(
        val time_until_update: String,
        val timestamp: String,
        val value: String,
        val value_classification: String
    )

    data class Metadata(
        val error: Any
    )
}

/* Retrofit service that maps the different endpoints on the API, you'd create one
 * method per endpoint, and use the @Path, @Query and other annotations to customize
 * these at runtime */
interface UserService {
    @GET("/fng")
    fun getIndex(): Call<CryptoIndexData>
}