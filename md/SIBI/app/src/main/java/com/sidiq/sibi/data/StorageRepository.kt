package com.sidiq.sibi.data

import com.sidiq.sibi.data.source.StorageService
import com.sidiq.sibi.data.wrapper.UploadResult
import com.sidiq.sibi.domain.model.Word
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StorageRepository @Inject constructor(
    private val storageService: StorageService) {

    @ExperimentalCoroutinesApi
    suspend fun uploadContrib(word: Word): Flow<UploadResult> =
        storageService.uploadContrib(word)


}