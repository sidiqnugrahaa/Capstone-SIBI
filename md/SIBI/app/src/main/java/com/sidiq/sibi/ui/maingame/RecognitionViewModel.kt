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

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sidiq.sibi.data.LeaderboardRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.tensorflow.lite.support.label.Category
import javax.inject.Inject

data class Recognition(val label:String, val confidence:Float, val bitmap: Bitmap) {

    override fun toString():String{
        return "$label / $probabilityString"
    }
    val probabilityString = String.format("%.1f%%", confidence * 100.0f)

    companion object {
        fun Category.toLabel(bitmap: Bitmap) : Recognition =
            Recognition(label, score, bitmap)
    }

}

class RecognitionListViewModel @Inject constructor(
    repository: LeaderboardRepository) : ViewModel() {

    private val _toast = MutableLiveData<String?>()
    val toast : LiveData<String?> get() = _toast

    private val _spinner = MutableLiveData(false)
    val spinner : LiveData<Boolean> get() = _spinner

    private val _recognition = MutableLiveData<Recognition>()
    val recognition: LiveData<Recognition> = _recognition

    fun updateData(recognition: Recognition){
        _recognition.postValue(recognition)
    }



    private fun launchDataLoad(block: suspend () -> Unit): Job {
        return viewModelScope.launch {
            try { _spinner.value = true; block() }
            catch (e: Throwable){ _toast.value = e.message }
            finally { _spinner.value = false }
        }
    }



}

