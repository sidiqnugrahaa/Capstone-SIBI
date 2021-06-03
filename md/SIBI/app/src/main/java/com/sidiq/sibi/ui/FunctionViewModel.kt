package com.sidiq.sibi.ui

import androidx.lifecycle.*
import com.sidiq.sibi.data.FunctionRepository
import com.sidiq.sibi.data.wrapper.Resource
import com.sidiq.sibi.domain.model.Word
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FunctionViewModel @Inject constructor(private val repository: FunctionRepository) : ViewModel() {

    lateinit var word : LiveData<Resource<Word>>

    fun getRandomWord(){
        viewModelScope.launch {
            word = repository.getRandomWord().asLiveData()
        }
    }

}