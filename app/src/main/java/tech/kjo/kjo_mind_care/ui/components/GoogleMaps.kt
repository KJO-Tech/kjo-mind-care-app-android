package tech.kjo.kjo_mind_care.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng

import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState


@Composable
fun GoogleMaps(){
//    val limaPeru = LatLng(-12.046374,-77.042793)
//    val cameraPositionState = rememberCameraPositionState {
//        position = CameraPosition.fromLatLngZoom(limaPeru, 10f)
//    }
//    GoogleMap(
//        modifier = Modifier.fillMaxSize(),
//        cameraPositionState = cameraPositionState
//    ) {
//        Marker(
//            state = MarkerState(position = limaPeru),
//            title = "Singapore",
//            snippet = "Marker in Singapore"
//        )
//    }
    Image(
        painter = rememberAsyncImagePainter(
            ImageRequest.Builder(LocalContext.current)
                .data("https://motor.elpais.com/wp-content/uploads/2022/01/google-maps-22.jpg")
                .build()
        ),
        contentDescription = "Mapa de recursos de emergencia",
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.Crop
    )

}