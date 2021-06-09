/*
 * Copyright (C) 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sidiq.sibi.ui.maingame

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.sidiq.sibi.data.LeaderboardRepository
import com.sidiq.sibi.data.wrapper.Result
import com.sidiq.sibi.domain.model.History
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.tensorflow.lite.task.vision.detector.Detection
import javax.inject.Inject

data class Recognition(val label:String, val confidence:Float) {

    override fun toString():String{
        return "$label / $probabilityString"
    }
    private val probabilityString = String.format("%.1f%%", confidence * 100.0f)

    companion object {

        fun Detection.toRecognition() : Recognition{
            return try {
                val label = categories.apply {
                    sortByDescending { it.score }
                }[0]
                Recognition(label.label, label.score)
            }catch (exception: Exception){
                Recognition("", 0f)
            }
        }

    }

}

@HiltViewModel
class GameViewModel @Inject constructor(
    private val repository: LeaderboardRepository) : ViewModel() {

    private val _toast = MutableLiveData<String?>()
    val toast : LiveData<String?> get() = _toast

    private val _spinner = MutableLiveData(false)
    val spinner : LiveData<Boolean> get() = _spinner

    private val _recognition = MutableLiveData<Recognition>()
    val recognition: LiveData<Recognition> = _recognition

    private val _result = MutableLiveData<Result<Void>>()
    val result : LiveData<Result<Void>> get() = _result

    fun updateData(recognition: Recognition){
        _recognition.postValue(recognition)
    }

    @ExperimentalCoroutinesApi
    fun insertHistory(score: Int, type: String, userId : String){
        launchDataLoad {
            val timestamp = Timestamp.now()
            val history = History(timestamp = timestamp, type = type, score = score.toLong())
            _result.postValue(repository.insertHistory(userId, history))
        }
    }

    private fun launchDataLoad(block: suspend () -> Unit): Job {
        return viewModelScope.launch {
            try { _spinner.value = true; block() }
            catch (e: Throwable){ _toast.value = e.message }
            finally { _spinner.value = false }
        }
    }



}

