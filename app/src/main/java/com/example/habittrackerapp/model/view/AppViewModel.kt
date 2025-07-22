package com.example.habittrackerapp.model.view

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.habittrackerapp.model.data.AppUIState
import com.example.habittrackerapp.model.data.User
import com.example.habittrackerapp.util.FirebaseUtil
import com.example.habittrackerapp.util.FirestoreUtil
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AppViewModel : ViewModel() {

    // App UI state
    private val _uiState = MutableStateFlow(AppUIState())
    val uiState: StateFlow<AppUIState> = _uiState.asStateFlow()

    private val db = FirebaseFirestore.getInstance()
    private val _currentPartnership = MutableLiveData<String>()
    private val _rules = MutableLiveData<String>()
    private val _limits = MutableLiveData<String>()
    private val _ideas = MutableLiveData<String>()
    private val _notes = MutableLiveData<String>()
    private val _activeNotesTab = MutableLiveData<String>("rules")
    private val _notesTabList = listOf<String>("RULES", "LIMITS", "IDEAS", "NOTES")

    val currentPartnership: LiveData<String> get() = _currentPartnership
    val rules: LiveData<String> get() = _rules
    val limits: LiveData<String> get() = _limits
    val ideas: LiveData<String> get() = _ideas
    val notes: LiveData<String> get() = _notes
    val activeNotesTab: LiveData<String> get() = _activeNotesTab
    val notesTabList: List<String> get() = _notesTabList

    private var rulesListener: ListenerRegistration? = null
    private var limitsListener: ListenerRegistration? = null
    private var ideasListener: ListenerRegistration? = null
    private var notesListener: ListenerRegistration? = null

    private val userId: String = FirebaseAuth.getInstance().currentUser?.uid.toString()

    init {
        if (FirebaseUtil.isLoggedIn()) {
            _uiState.value = _uiState.value.copy(loggedIn = true)
            getUserData(
                onDataReceived = {
                    _uiState.value = _uiState.value.copy(user = it)
                    _currentPartnership.value = _uiState.value.user?.currentPartnership!!
                    fetchRules()
                    fetchLimits()
                    fetchIdeas()
                    fetchNotes()
                }
            )
        } else {
            _uiState.value = _uiState.value.copy(loggedIn = false)
            
            // Clear user data
            _uiState.value = _uiState.value.copy(user = null)
            
            // Clear notes
            _rules.value = ""
            _limits.value = ""
            _ideas.value = ""
            _notes.value = ""
        }        
    }

    fun getUserData(
        onDataReceived: (User) -> Unit
    ) {
        FirestoreUtil.getUserData(
            onDataReceived = {
                onDataReceived(it)
            }
        )
    }

    fun updateUserData(field: String, value: Any) {
        // Update user data with the field and value provided
        when (field) {
            "userName" -> _uiState.value.user?.userName = value as String
            "currentPartnership" -> _uiState.value.user?.currentPartnership = value as String
            "currentPartner" -> _uiState.value.user?.currentPartner = value as String
            else -> Log.e("AppViewModel", "Invalid field: $field")
        }

        FirestoreUtil.updateUserData(field, value)
    }

    fun getUserName(): String {
        return _uiState.value.user?.userName ?: ""
    }

    fun fetchRules() {
        rulesListener?.remove() // Remove any existing listener to prevent duplicates

        rulesListener = db
            .collection("partnership")
            .document(_currentPartnership.value.toString())
            .collection("notes")
            .document("rules")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("AppViewModel", "Error fetching rules", error)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    _rules.value = snapshot.getString("data") ?: ""
                }
            }
    }

    fun fetchLimits() {
        limitsListener?.remove() // Remove any existing listener to prevent duplicates

        limitsListener = db
            .collection("partnership")
            .document(_currentPartnership.value.toString())
            .collection("notes")
            .document("limits")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("AppViewModel", "Error fetching limits", error)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    _limits.value = snapshot.getString("data") ?: ""
                }
            }
    }

    fun fetchIdeas() {
        ideasListener?.remove() // Remove any existing listener to prevent duplicates

        ideasListener = db
            .collection("partnership")
            .document(_currentPartnership.value.toString())
            .collection("notes")
            .document("ideas")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("AppViewModel", "Error fetching ideas", error)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    _ideas.value = snapshot.getString("data") ?: ""
                }
            }
    }

    fun fetchNotes() {
        notesListener?.remove() // Remove any existing listener to prevent duplicates

        notesListener = db
            .collection("partnership")
            .document(_currentPartnership.value.toString())
            .collection("notes")
            .document("notes")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("AppViewModel", "Error fetching notes", error)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    _notes.value = snapshot.getString("data") ?: ""
                }
            }
    }

    fun setActiveTab(tab: String) {
        _activeNotesTab.value = tab
    }

    fun updateNotes(content: String, onClose: () -> Unit) {
        // Convert data to a map
        val data = hashMapOf(
            "data" to content,
            "updatedBy" to Firebase.auth.currentUser?.uid,
            "timestamp" to System.currentTimeMillis()
        )

        db.collection("partnership")
            .document(_currentPartnership.value.toString())
            .collection("notes")
            .document(_activeNotesTab.value.toString())
            .set(data)
            .addOnSuccessListener { onClose() }
            .addOnFailureListener { /* Handle error */ }
    }
}