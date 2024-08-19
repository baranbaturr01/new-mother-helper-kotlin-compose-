package com.baranbatur.newmotherhelper.components

import android.app.Activity
import android.content.Context
import androidx.compose.runtime.Composable
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

object InterstitialAdManager {
    private var interstitialAd: InterstitialAd? = null
    private var isAdShowing: Boolean = false

    fun loadInterstitialAd(
        context: Context
    ) {
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(context,
            "ca-app-pub-9414421758468387/8218799859",
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitialAd = ad
                }

                override fun onAdFailedToLoad(p0: LoadAdError) {
                    interstitialAd = null
                }
            })
    }

    fun showInterstitialAd(context: Context) {
        if (interstitialAd != null && !isAdShowing) {
            isAdShowing = true
            interstitialAd?.show(context as Activity)
            interstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    isAdShowing = false
                    loadInterstitialAd(context) // Reklam gösterildikten sonra yeni bir reklam yükle
                }

                override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                    isAdShowing = false
                }

                override fun onAdShowedFullScreenContent() {
                    interstitialAd = null // Reklam gösterildiğinde nesneyi sıfırla
                }
            }
        } else if (!isAdShowing) {
            loadInterstitialAd(context) // Eğer reklam yoksa, yeni bir tane yükle
        }
    }
}