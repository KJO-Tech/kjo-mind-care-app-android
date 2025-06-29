package tech.kjo.kjo_mind_care.data.repository.impl

import android.content.Context
import android.net.Uri
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.suspendCancellableCoroutine
import tech.kjo.kjo_mind_care.data.enums.MediaType
import tech.kjo.kjo_mind_care.data.repository.IMediaUploadRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

@Singleton
class MediaUploadRepository @Inject constructor(
    @ApplicationContext private val context: Context
) : IMediaUploadRepository {

    override suspend fun uploadMedia(
        fileUri: Uri,
        mediaType: MediaType
    ): Result<String> {

        return suspendCancellableCoroutine { continuation ->
            val resourceType = when (mediaType) {
                MediaType.IMAGE -> "image"
                MediaType.VIDEO -> "video"
            }

            val folderName = if (mediaType == MediaType.IMAGE) "blog/images" else "blog/videos"

            MediaManager.get().upload(fileUri)
                .option("resource_type", resourceType)
                .option("folder", folderName)
                .callback(object : UploadCallback {
                    override fun onStart(requestId: String) {}
                    override fun onProgress(requestId: String, bytes: Long, totalBytes: Long) {}

                    override fun onSuccess(requestId: String, resultData: Map<*, *>?) {
                        val url = resultData?.get("url") as? String
                        if (url != null) {
                            if (continuation.isActive) {
                                continuation.resume(Result.success(url))
                            }
                        } else {
                            if (continuation.isActive) {
                                continuation.resume(Result.failure(Exception("URL de Cloudinary no encontrada.")))
                            }
                        }
                    }

                    override fun onError(requestId: String?, error: ErrorInfo?) {
                        if (continuation.isActive) {
                            continuation.resume(Result.failure(Exception(error?.description ?: "Error desconocido al subir a Cloudinary.")))
                        }
                    }

                    override fun onReschedule(requestId: String, error: ErrorInfo?) {}
                })
                .dispatch()

            continuation.invokeOnCancellation {
                println("Cloudinary upload coroutine was cancelled.")
            }
        }
    }
}