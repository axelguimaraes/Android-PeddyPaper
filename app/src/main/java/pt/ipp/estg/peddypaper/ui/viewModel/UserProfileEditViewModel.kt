package pt.ipp.estg.peddypaper.ui.viewModel

import android.app.Application
import android.content.ContentValues.TAG
import android.util.Log
import androidx.core.util.PatternsCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.launch
import pt.ipp.estg.peddypaper.data.remote.firebase.Player

class UserProfileEditViewModel(aplication: Application) : AndroidViewModel(aplication){

    private val db: FirebaseFirestore
    private val collectionUsers: String

    private val _auth: FirebaseAuth

    private var _currentUser: MutableLiveData<Player?>
    private var _newUser = MutableLiveData<Player?>()

    init {
        db = Firebase.firestore
        collectionUsers = "PLAYERS"

        _auth = Firebase.auth

        _currentUser = MutableLiveData()
        loadCurrentUser()
    }

    val currentUser: LiveData<Player?> = _currentUser

    private fun loadCurrentUser(){
        _auth.currentUser?.let { user ->
            db.collection(collectionUsers)
                .document(user.uid)
                .get()
                .addOnSuccessListener { document ->
                if (document != null) {
                    Log.i(TAG, "Document data: ${document.data}")
                    val player = document.toObject(Player::class.java)
                    _currentUser.value = player
                    Log.i(TAG, "User data: ${_currentUser.value}")
                }
            }
                .addOnFailureListener {
                    Log.e(TAG, "Error getting user.")
                }
        }
    }

    fun isValidName(name: String): Boolean {
        if(name.isEmpty()) return true
        val regex = "^[a-zA-Z]+(([',. -][a-zA-Z ])?[a-zA-Z]*)*\$".toRegex()
        return regex.matches(name)
    }

    fun isValidEmail(email: String): Boolean {
        if(email.isEmpty()) return true
        return PatternsCompat.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun isValidPassword(password: String): Boolean {
        if(password.isEmpty()) return true
        val regex = "^(?=.*[A-Z])(?=.*\\d).{6,}\$".toRegex()
        return regex.matches(password)
    }

    fun isValidPasswordConfirm(password: String, passwordConfirm: String): Boolean {
        if(passwordConfirm.isEmpty()) return true
        return password == passwordConfirm
    }

    fun updateUser(name: String, email: String, password: String){

        _newUser.postValue(
            _currentUser.value?.copy(
                name = name,
                /*
                email isn't updated because it's updated before the user verifies the new email
                 */
            )
        )

        viewModelScope.launch {
            // Update user in database
            val document = db.collection(collectionUsers).document(_auth.currentUser?.uid.toString())
            document.set(_newUser.value!!)

            // Update user in firebase auth
            val profileUpdate = userProfileChangeRequest {
                displayName = name
            }
            _auth.currentUser?.updateProfile(profileUpdate)
                ?.addOnSuccessListener { Log.d(TAG, "User profile updated.") }
                ?.addOnFailureListener { e -> Log.e(TAG, "Error updating user profile.", e) }

            // Update user email in firebase auth
            if(_auth.currentUser?.isEmailVerified == true){
                if(email != _auth.currentUser?.email){
                    _auth.currentUser?.verifyBeforeUpdateEmail(email)
                        ?.addOnSuccessListener { Log.d(TAG, "User email address updated.") }
                        ?.addOnFailureListener { e -> Log.e(TAG, "Error updating user email.", e) }
                }
            }

            if(password.isNotEmpty()){
                Log.d(TAG, "new password: $password")
                _auth.currentUser?.updatePassword(password)
                    ?.addOnSuccessListener { Log.d(TAG, "User password updated.") }
                    ?.addOnFailureListener { e -> Log.e(TAG, "Error updating user password.", e) }

                // Reauthenticate user
                try {
                    val credential = _auth.currentUser?.email?.let {
                        EmailAuthProvider
                            .getCredential(it, password)
                    }
                    if (credential != null) {
                        _auth.currentUser?.reauthenticate(credential)
                            ?.addOnSuccessListener { Log.d(TAG, "User re-authentication successfully") }
                            ?.addOnFailureListener { e -> Log.e(TAG, "Error re-authentication user.", e) }
                    }
                }
                catch (e: Exception){
                    Log.e(TAG, "Error reauthenticating user.", e)
                    _auth.signOut()
                }
            }
        }

    }

    fun doneToUpdateUser(
        name: String,
        email: String,

        passwordCurrent: String?,
        passwordNew: String?,
        passwordConfirm: String?
    ): Boolean {
        // Log.d(TAG, "name: $name")
        val isNameValid = isValidName(name)
        // Log.d(TAG, "isNameValid: $isNameValid")

        // Log.d(TAG, "email: $email")
        val isEmailValid = isValidEmail(email)
        // Log.d(TAG, "isEmailValid: $isEmailValid")

        var isValidPassword = true
        if (!passwordCurrent.isNullOrEmpty() && !passwordNew.isNullOrEmpty() && !passwordConfirm.isNullOrEmpty()) {
            val isValidCurrentPassword = true //validate current password in firebase
            val isValidNewPassword = isValidPassword(passwordNew)
            val isValidNewPasswordConfirm = isValidPassword(passwordConfirm)
            val isValidPasswordConfirm = isValidPasswordConfirm(passwordNew, passwordConfirm)

            isValidPassword = isValidCurrentPassword && isValidNewPassword && isValidNewPasswordConfirm && isValidPasswordConfirm
        }
        // Log.d(TAG, "isValidPassword: $isValidPassword")

        return isNameValid && isEmailValid && isValidPassword
    }
}