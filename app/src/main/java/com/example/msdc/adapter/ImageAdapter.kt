package com.example.msdc.adapter

import android.widget.ImageView
import android.widget.Toast
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso

object ImageAdapter {
    private const val POSTER_IMAGE_LOGO_URL = "https://image.tmdb.org/t/p/w185"
    private const val PROFILE_IMAGE_BASE_URL = "https://image.tmdb.org/t/p/w185"
    private const val POSTER_IMAGE_BASE_URL = "https://image.tmdb.org/t/p/w500"
    private const val POSTER_HD_BASE_URL = "https://image.tmdb.org/t/p/w780"
    private const val BACKDROP_IMAGE_BASE_URL = "https://image.tmdb.org/t/p/original"

    @JvmStatic
    fun setPosterURL(imageView: ImageView, path: String?) {
        try {
            imageView.alpha = 0f
            Picasso.get().load(POSTER_IMAGE_BASE_URL + path).noFade()
                .into(imageView, object : Callback {
                    override fun onSuccess() {
                        imageView.animate().setDuration(500).alpha(1f).start()
                    }

                    override fun onError(e: Exception) {
                        Toast.makeText(
                            imageView.context, e.message + " cause : "
                                    + e.cause, Toast.LENGTH_SHORT
                        ).show()
                    }
                })
        } catch (ignored: Exception) {
        }
    }

    @JvmStatic
    fun setPosterLogoURL(imageView: ImageView, path: String?) {
        try {
            imageView.alpha = 0f
            Picasso.get().load(POSTER_IMAGE_LOGO_URL + path).noFade()
                .into(imageView, object : Callback {
                    override fun onSuccess() {
                        imageView.animate().setDuration(500).alpha(1f).start()
                    }

                    override fun onError(e: Exception) {
                        Toast.makeText(
                            imageView.context, e.message + " cause : "
                                    + e.cause, Toast.LENGTH_SHORT
                        ).show()
                    }
                })
        } catch (ignored: Exception) {
        }
    }

    fun setProfileLogoURL(imageView: ImageView, path: String?) {
        try {
            imageView.alpha = 0f
            Picasso.get().load(PROFILE_IMAGE_BASE_URL + path).noFade()
                .into(imageView, object : Callback {
                    override fun onSuccess() {
                        imageView.animate().setDuration(500).alpha(1f).start()
                    }

                    override fun onError(e: Exception) {
                        Toast.makeText(
                            imageView.context, e.message + " cause : "
                                    + e.cause, Toast.LENGTH_SHORT
                        ).show()
                    }
                })
        } catch (ignored: Exception) {
        }
    }

    fun setPosterHD(imageView: ImageView, path: String?) {
        try {
            imageView.alpha = 0f
            Picasso.get().load(POSTER_HD_BASE_URL + path).noFade()
                .into(imageView, object : Callback {
                    override fun onSuccess() {
                        imageView.animate().setDuration(500).alpha(1f).start()
                    }

                    override fun onError(e: Exception) {
                        Toast.makeText(
                            imageView.context, e.message + " cause : "
                                    + e.cause, Toast.LENGTH_SHORT
                        ).show()
                    }
                })
        } catch (ignored: Exception) {
        }
    }

    @JvmStatic
    fun setBackdropURL(imageView: ImageView, path: String?) {
        try {
            imageView.alpha = 0f
            Picasso.get().load(BACKDROP_IMAGE_BASE_URL + path).noFade()
                .into(imageView, object : Callback {
                    override fun onSuccess() {
                        imageView.animate().setDuration(500).alpha(1f).start()
                    }

                    override fun onError(e: Exception) {
                        Toast.makeText(
                            imageView.context, e.message + " cause : "
                                    + e.cause, Toast.LENGTH_SHORT
                        ).show()
                    }
                })
        } catch (ignored: Exception) {
        }
    }
}