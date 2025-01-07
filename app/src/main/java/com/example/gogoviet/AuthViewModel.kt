package com.example.gogoviet

import android.net.Uri
import android.os.Message
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class AuthViewModel : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()


    private val _authState = MutableLiveData<AuthState>()
    val authState: LiveData<AuthState> = _authState

    private val _userInfo = MutableLiveData<UserInfo>()
    val userInfo: LiveData<UserInfo> = _userInfo




    init {
        checkAuthStatus()
    }

    fun checkAuthStatus() {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            _authState.value = AuthState.Unauthenticated
        } else {
            _authState.value = AuthState.Authenticated
            fetchUserInfo(currentUser.uid) // Fetch user info on authentication
        }
    }

    fun login(email: String, password: String) {

        if(email.isEmpty() || password.isEmpty()){
            _authState.value = AuthState.Error("Email or password must not be empty!")
            return
        }

        _authState.value = AuthState.Loading
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener{task->
                if (task.isSuccessful){
                    _authState.value = AuthState.Authenticated
                    auth.currentUser?.let { fetchUserInfo(it.uid)}
                }else{
                    _authState.value = AuthState.Error(task.exception?.message?:"Something went wrong")
            }
            }
    }

    // Fetch user information from Firestore
    fun fetchUserInfo(uid: String) {
        firestore.collection("users").document(uid).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val data = document.toObject(UserInfo::class.java)
                    _userInfo.value = data ?: UserInfo()
                }
            }
            .addOnFailureListener { e ->
                _authState.value = AuthState.Error(e.message ?: "Failed to fetch user info")
            }
    }

    // Save user information to Firestore
    private fun saveUserInfo(uid: String, userInfo: UserInfo) {
        firestore.collection("users").document(uid).set(userInfo)
            .addOnSuccessListener {
                _authState.value = AuthState.Authenticated
                _userInfo.value = userInfo
            }
            .addOnFailureListener { e ->
                _authState.value = AuthState.Error(e.message ?: "Failed to save user info")
            }
    }

    fun signup(email: String, password: String, name: String) {

        if(email.isEmpty() || password.isEmpty()){
            _authState.value = AuthState.Error("Email or password must not be empty!")
            return
        }

        _authState.value = AuthState.Loading
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener{task->
                if (task.isSuccessful){
                    val uid = auth.currentUser?.uid ?: return@addOnCompleteListener
                    val userInfo = UserInfo(
                        uid = uid,
                        name = name,
                        email = email,
                        password = password, // Hash this in production
                        address = "",
                        gender = "",
                        photoUrl = "", // Default or empty
                        postIdLiked = emptyList(),
                        postId = emptyList()
                    )
                    saveUserInfo(uid, userInfo)

                }else{
                    _authState.value = AuthState.Error(task.exception?.message?:"Something went wrong")
                }
            }
    }

    fun resetState() {
        if (_authState.value is AuthState.Error) {
            _authState.value = AuthState.Unauthenticated // Reset to a neutral state
        }
    }

    fun updateUserInfo(updatedFields: Map<String, Any>, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        val uid = auth.currentUser?.uid ?: return
        firestore.collection("users").document(uid).update(updatedFields)
            .addOnSuccessListener {
                fetchUserInfo(uid) // Refresh userInfo after successful update
                onSuccess()
            }
            .addOnFailureListener { exception ->
                onFailure(exception.message ?: "Failed to update user info")
            }
    }

    fun uploadProfilePicture(uri: Uri, onSuccess: (String) -> Unit, onFailure: (String) -> Unit) {
        val uid = auth.currentUser?.uid ?: return
        val storageRef = FirebaseStorage.getInstance().reference.child("profile_pictures/$uid.jpg")

        storageRef.putFile(uri)
            .addOnSuccessListener {
                storageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                    // Update Firestore with the new photo URL
                    updateUserInfo(mapOf("photoUrl" to downloadUri.toString()), {
                        _userInfo.value = _userInfo.value?.copy(photoUrl = downloadUri.toString())
                        onSuccess(downloadUri.toString())
                    }, { error ->
                        onFailure(error)
                    })
                }
            }
            .addOnFailureListener { exception ->
                onFailure(exception.message ?: "Failed to upload profile picture")
            }
    }


    fun signout(){
        auth.signOut()
        _authState.value = AuthState.Unauthenticated
    }
}



sealed class AuthState {
    object Authenticated : AuthState()
    object Unauthenticated : AuthState()
    object Loading : AuthState()
    data class Error(val message: String) : AuthState()
}

data class UserInfo(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val address: String = "",
    val gender: String = "",
    val postIdLiked: List<String> = emptyList(),
    val postId: List<String> = emptyList(),
    val photoUrl: String = ""
)