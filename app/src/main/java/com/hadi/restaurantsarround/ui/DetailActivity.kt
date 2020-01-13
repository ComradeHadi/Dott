package com.hadi.restaurantsarround.ui

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.hadi.restaurantsarround.BaseApplication
import com.hadi.restaurantsarround.R
import com.hadi.restaurantsarround.model.VenueDetailResponse
import com.hadi.restaurantsarround.network.FourSquareAPI
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.activity_detail.toolbar
import kotlinx.android.synthetic.main.toolbar.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import javax.inject.Inject

class DetailActivity : AppCompatActivity() {

    @Inject
    lateinit var retrofit: Retrofit

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        (application as BaseApplication).getApiComponent().injectToDetail(this)

        configureToolbar()

        textViewPlace.text = intent.getStringExtra("name")
        val id = intent.getStringExtra("venueId")
        getData(id)

    }

    //MARK: make network call to server
    private fun getData(venueId: String) {
        val apiVersion = "20130815"
        val apiClient = retrofit.create<FourSquareAPI>(FourSquareAPI::class.java)
        apiClient.getVenueDetail(venueId, apiVersion, getString(R.string.foursquare_client_id), getString(R.string.foursquare_client_secret))
            ?.enqueue(object : Callback<VenueDetailResponse?> {
                override fun onFailure(call: Call<VenueDetailResponse?>, t: Throwable) {
                    Toast.makeText(this@DetailActivity, t.localizedMessage, Toast.LENGTH_LONG)
                        .show()
                }

                override fun onResponse(
                    call: Call<VenueDetailResponse?>,
                    response: Response<VenueDetailResponse?>
                ) {
                    val venueBody = response.body()
                    val venue = venueBody?.response
                    if (200 == venueBody?.meta?.code) {
                        Log.e("DETAILS", venueBody.meta.toString())
                        Log.e("DETAILS", venue.toString())
                        textViewAddress.text = venue?.venue?.location?.address
                    }
                }

            })
    }


    //MARK: set up toolbar and add title to page
    private fun configureToolbar() {
        setSupportActionBar(toolbar as Toolbar?)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        textViewTitle.text = "DETAIL"
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
