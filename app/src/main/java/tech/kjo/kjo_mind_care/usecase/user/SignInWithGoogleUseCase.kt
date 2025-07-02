package tech.kjo.kjo_mind_care.usecase.user

import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import tech.kjo.kjo_mind_care.data.repository.IAuthRepository
import tech.kjo.kjo_mind_care.utils.Resource
import javax.inject.Inject

class SignInWithGoogleUseCase @Inject constructor(
    private val repository: IAuthRepository
) {
    suspend operator fun invoke(accountToken: GoogleSignInAccount): Resource<Boolean> {
        return repository.signInWithGoogle(accountToken)
    }
}