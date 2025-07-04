package tech.kjo.kjo_mind_care.data.repository.impl

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.suspendCancellableCoroutine
import tech.kjo.kjo_mind_care.data.model.Emergency
import tech.kjo.kjo_mind_care.data.repository.IEmergencyResourceRepository
import javax.inject.Inject
import kotlin.coroutines.resumeWithException

class EmergencyResourceRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) : IEmergencyResourceRepository {

    override fun fetchResources(): Flow<List<Emergency>> = flow {
        val list = suspendCancellableCoroutine<List<Emergency>> { cont ->
            firestore.collection("emergency")
                .get()
                .addOnSuccessListener { snap ->
                    val datos = snap.documents.mapNotNull { it.toObject(Emergency::class.java) }
                    cont.resume(datos) {}
                }
                .addOnFailureListener { e ->
                    cont.resumeWithException(e)
                }
        }
        emit(list)
    }.flowOn(Dispatchers.IO)
}
