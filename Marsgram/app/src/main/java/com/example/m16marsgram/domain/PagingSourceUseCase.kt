package com.example.m16marsgram.domain

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.m16marsgram.data.MarsPhotoDto
import com.example.m16marsgram.data.NasaRepository
import com.example.m16marsgram.data.ResponseResultDto
import com.example.m16marsgram.entity.RoverName
import com.example.m16marsgram.entity.UIEntity
import javax.inject.Inject

class PagingSourceUseCase @Inject constructor(
    private val nasaRepository: NasaRepository
) : PagingSource<Int, MarsPhotoDto>(), UIEntity {

    override var currentRover: RoverName? = null
    override var currentSol: Int = 1
    override lateinit var currentCamera: String

    override fun getRefreshKey(state: PagingState<Int, MarsPhotoDto>): Int {
        return state.anchorPosition ?: FIRST_PAGE
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, MarsPhotoDto> {
        val page = params.key ?: FIRST_PAGE

        if (currentCamera == "ALL") {
            return kotlin.runCatching {
                getAllCamerasPhotosFromRepo(page)
            }.fold(
                onSuccess = {
                    LoadResult.Page(
                        data = it.photos,
                        prevKey = null,
                        nextKey = if (it.photos.isEmpty()) null else page + 1
                    )
                },
                onFailure = { LoadResult.Error(it) }
            )
        }

        return kotlin.runCatching {
            getCurrentCameraPhotosFromRepo(page)
        }.fold(
            onSuccess = {
                LoadResult.Page(
                    data = it.photos,
                    prevKey = null,
                    nextKey = if (it.photos.isEmpty()) null else page + 1
                )
            },
            onFailure = { LoadResult.Error(it) }
        )
    }

    private suspend fun getAllCamerasPhotosFromRepo(page: Int): ResponseResultDto {
        return nasaRepository.photosFromAllCamerasApi.getPhotos(
            rover = currentRover!!.roverName,
            sol = currentSol,
            page = page
        )
    }

    private suspend fun getCurrentCameraPhotosFromRepo(page: Int): ResponseResultDto {
        return nasaRepository.photosFromDifferentCamerasApi.getPhotos(
            rover = currentRover!!.roverName,
            sol = currentSol,
            page = page,
            camera = currentCamera
        )
    }

    companion object {
        private const val FIRST_PAGE = 1
    }
}