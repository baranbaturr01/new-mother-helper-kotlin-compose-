package com.baranbatur.newmotherhelper.components

import android.content.Context
import android.util.Log
import android.view.ViewGroup
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError

@Composable
fun BannerAdView(context: Context) {
    AndroidView(
        factory = {
            AdView(context).apply {
                setAdSize(AdSize.BANNER)
                adUnitId =
                    "ca-app-pub-9414421758468387/1672437334" // Gerçek test reklam birimi ID'si
                loadAd(AdRequest.Builder().build())

                // AdListener ekleyerek yükleme ve hata durumlarını kontrol edin
                adListener = object : AdListener() {
                    override fun onAdLoaded() {
                        Log.d("AdMob", "Ad loaded successfully.")
                    }

                    override fun onAdFailedToLoad(p0: LoadAdError) {
                        Log.d("LANANANANANSKJ BURADAKDKAJDSLALDŞ", "Ad failed to load: ${p0.message}")
                    }

                    override fun onAdOpened() {
                        Log.d("AdMob", "Ad opened.")
                    }

                    override fun onAdClicked() {
                        Log.d("AdMob", "Ad clicked.")
                    }

                    override fun onAdClosed() {
                        Log.d("AdMob", "Ad closed.")
                    }
                }

                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            }
        },
        modifier = Modifier
    )
}
