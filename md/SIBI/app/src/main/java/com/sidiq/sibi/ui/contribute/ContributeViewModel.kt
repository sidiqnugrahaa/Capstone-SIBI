package com.sidiq.sibi.ui.contribute

import androidx.lifecycle.*
import com.google.firebase.Timestamp
import com.sidiq.sibi.data.StorageRepository
import com.sidiq.sibi.data.wrapper.Result
import com.sidiq.sibi.data.wrapper.UploadResult
import com.sidiq.sibi.domain.model.History
import com.sidiq.sibi.domain.model.Word
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ContributeViewModel @Inject constructor(private val repository: StorageRepository) : ViewModel(){

    lateinit var result : LiveData<UploadResult>

    @ExperimentalCoroutinesApi
    fun insertContrib(word: Word){
        viewModelScope.launch {
            result = repository.uploadContrib(word).asLiveData()
        }
    }

}