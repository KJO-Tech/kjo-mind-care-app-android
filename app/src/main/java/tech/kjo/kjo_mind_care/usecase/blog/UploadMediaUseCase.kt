package tech.kjo.kjo_mind_care.usecase.blog

import android.net.Uri
import tech.kjo.kjo_mind_care.data.enums.MediaType
import tech.kjo.kjo_mind_care.data.repository.IMediaUploadRepository
import javax.inject.Inject

class UploadMediaUseCase @Inject constructor(
    private val mediaUploadRepository: IMediaUploadRepository
) {
    suspend operator fun invoke(fileUri: Uri, mediaType: MediaType): Result<String> {
        return mediaUploadRepository.uploadMedia(fileUri, mediaType)
    }
}