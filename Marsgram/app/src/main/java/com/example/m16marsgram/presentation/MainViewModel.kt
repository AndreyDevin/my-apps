package com.example.m16marsgram.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.m16marsgram.data.MarsPhotoDto
import com.example.m16marsgram.domain.GetMissionsInfo
import com.example.m16marsgram.domain.PagingSourceUseCase
import com.example.m16marsgram.entity.RoverName
import com.example.m16marsgram.entity.UIEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val DEFAULT_CAMERA = "ALL"

@HiltViewModel
class MainViewModel @Inject constructor (
    private val getMissionsInfo: GetMissionsInfo,
    private val pagingSourceUseCase: PagingSourceUseCase
): ViewModel(), UIEntity {
    override var currentRover: RoverName? = null
    override var currentSol = 1
    override var currentCamera = DEFAULT_CAMERA
    var dialogMenuIsActive = true

    private val _maxSolForMission = MutableStateFlow<Int?>(null)
    val maxSolForMission = _maxSolForMission.asStateFlow()

    private val _camerasToCurrentSol = MutableStateFlow(listOf(DEFAULT_CAMERA))
    val camerasToCurrentSol = _camerasToCurrentSol.asStateFlow()

    private val _camerasToCurrentSolIsEnabled = MutableStateFlow(false)
    val camerasToCurrentSolIsEnabled = _camerasToCurrentSolIsEnabled.asStateFlow()

    fun setRover(selectedRover: RoverName) {
        currentRover = selectedRover
        currentSol = 1
    }

    fun getUpdateSolAndCamParam() {
        viewModelScope.launch {
            val manifestMissionsList = getMissionsInfo.getMissionsInfo()
            val manifestMissionInfo = manifestMissionsList.roversMissionsList.first { roverMission ->
                roverMission.name == currentRover!!.roverName.replaceFirstChar { it.uppercase() }
            }
            _maxSolForMission.value = manifestMissionInfo.maxSol

            val camerasList = mutableListOf(DEFAULT_CAMERA)
            manifestMissionInfo.cameras.forEach { camerasList.add(it.name) }
            _camerasToCurrentSol.value = camerasList
            _camerasToCurrentSolIsEnabled.value = true
        }
    }

    fun getPhotoFromRepo(): Flow<PagingData<MarsPhotoDto>> {

        pagingSourceUseCase.currentRover = currentRover!!
        pagingSourceUseCase.currentSol = currentSol
        pagingSourceUseCase.currentCamera = currentCamera

        return Pager(
            config = PagingConfig(
                pageSize = 9,
                initialLoadSize = 20,
                maxSize = 50
            ),
            pagingSourceFactory = { pagingSourceUseCase }
        ).flow.cachedIn(viewModelScope)
    }
}