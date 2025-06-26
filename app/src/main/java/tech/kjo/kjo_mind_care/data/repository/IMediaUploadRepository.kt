package tech.kjo.kjo_mind_care.data.repository

import android.net.Uri
import tech.kjo.kjo_mind_care.data.enums.MediaType
import java.io.File

interface IMediaUploadRepository {
    suspend fun uploadMedia(fileUri: Uri, mediaType: MediaType): Result<String>
}