package tech.kjo.kjo_mind_care.usecase.user

import android.net.Uri
import tech.kjo.kjo_mind_care.data.enums.MediaType
import tech.kjo.kjo_mind_care.data.repository.IAuthRepository
import tech.kjo.kjo_mind_care.data.repository.IMediaUploadRepository
import javax.inject.Inject

class UpdateUserUseCase @Inject constructor(
    private val authRepository: IAuthRepository,
    private val mediaUploadRepository: IMediaUploadRepository
) {
    suspend operator fun invoke(name: String, photoUri: Uri?): Result<Unit> {
        return try {
            val user = authRepository.getCurrentUserDetails() ?: return Result.failure(Exception("User not found"))

            val newImageUrl: String? = if (photoUri != null && photoUri.toString() != user.profileImage) {
                // Si hay una nueva URI y es diferente a la actual, subirla.
                mediaUploadRepository.uploadMedia(photoUri, MediaType.IMAGE).getOrThrow()
            } else {
                // Si no, mantener la URI que llegó (puede ser la misma o nula si se eliminó)
                photoUri?.toString()
            }

            val updatedUser = user.copy(fullName = name, profileImage = newImageUrl)
            authRepository.updateUser(updatedUser)

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}