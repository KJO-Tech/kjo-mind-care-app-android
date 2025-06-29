package tech.kjo.kjo_mind_care

import android.app.Application
import com.cloudinary.android.MediaManager
import com.google.firebase.FirebaseApp
import dagger.hilt.android.HiltAndroidApp
import tech.kjo.kjo_mind_care.data.model.StaticBlogData
import tech.kjo.kjo_mind_care.utils.NotificationUtils

@HiltAndroidApp
class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        StaticBlogData.init(this)
        NotificationUtils.createNotificationChannel(this)
        initCloudinary()
    }

    private fun initCloudinary() {
        val config = HashMap<String, Any>()
        config["cloud_name"] = BuildConfig.CLOUDINARY_CLOUD_NAME
        config["api_key"] = BuildConfig.CLOUDINARY_API_KEY
        config["api_secret"] = BuildConfig.CLOUDINARY_API_SECRET
        config["secure"] = true
        MediaManager.init(this, config)
    }
}