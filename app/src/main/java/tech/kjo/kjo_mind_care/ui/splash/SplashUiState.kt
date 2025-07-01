package tech.kjo.kjo_mind_care.ui.splash

sealed class SplashUiState {
    object Loading : SplashUiState()
    object Authenticated : SplashUiState()
    object Unauthenticated : SplashUiState()
    object NetworkError : SplashUiState()
    object GeneralError : SplashUiState()
}