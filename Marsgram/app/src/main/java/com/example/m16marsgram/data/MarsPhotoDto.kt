package com.example.m16marsgram.data

import com.example.m16marsgram.entity.*
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ResponseResultDto(
    @Json(name = "photos") val photos: List<MarsPhotoDto>
)

@JsonClass(generateAdapter = true)
data class ResponseManifestMissionDto(
    @Json(name = "photo_manifest") val photo_manifest: MissionManifestDto
)

@JsonClass(generateAdapter = true)
data class MarsPhotoDto(
    @Json(name = "id") override val id: Int,
    @Json(name = "sol") override val sol: Int,
    @Json(name = "camera") override val camera: RoverCameraDto,
    @Json(name = "img_src") override val imgSrc: String,
    @Json(name = "earth_date") override val earthDate: String,
    @Json(name = "rover") override val rover: RoverDto
): MarsPhoto

@JsonClass(generateAdapter = true)
data class RoverCameraDto(
    @Json(name = "id") override val id: Int,
    @Json(name = "name") override val name: String,
    @Json(name = "rover_id") override val roverId: Int,
    @Json(name = "full_name") override val fullName: String
): RoverCamera

@JsonClass(generateAdapter = true)
data class RoverDto(
    @Json(name = "id") override val id: Int,
    @Json(name = "name") override val name: String,
    @Json(name = "landing_date") override val landingDate: String,
    @Json(name = "launch_date") override val launchDate: String,
    @Json(name = "status") override val status: String
): Rover

@JsonClass(generateAdapter = true)
data class MissionManifestDto(
    @Json(name = "name") override val name: String,
    @Json(name = "landing_date") override val landingDate: String,
    @Json(name = "launch_date") override val launchDate: String,
    @Json(name = "status") override val status: String,
    @Json(name = "max_sol") override val maxSol: Int,
    @Json(name = "max_date") override val maxDate: String,
    @Json(name = "total_photos") override val totalPhotos: Int,
    @Json(name = "photos") override val photos: List<SingleSolManifestDto>,
    ): MissionManifest

@JsonClass(generateAdapter = true)
data class SingleSolManifestDto(
    @Json(name = "sol") override val sol: Int,
    @Json(name = "earth_date") override val earthDate: String,
    @Json(name = "total_photos") override val totalPhotos: Int,
    @Json(name = "cameras") override val cameras: List<String>
): SingleSolManifest

@JsonClass(generateAdapter = true)
data class AllRoversMissionsDto (
    @Json(name = "rovers") override val roversMissionsList: List<RoverMissionManifestDto>
): AllRoversMissions

@JsonClass(generateAdapter = true)
data class RoverMissionManifestDto(
    @Json(name = "cameras") override val cameras: List<RoverCameraDto>,
    @Json(name = "id") override val id: Int,
    @Json(name = "landing_date") override val landingDate: String,
    @Json(name = "launch_date") override val launchDate: String,
    @Json(name = "max_date") override val maxDate: String,
    @Json(name = "max_sol") override val maxSol: Int,
    @Json(name = "name") override val name: String,
    @Json(name = "status") override val status: String,
    @Json(name = "total_photos") override val totalPhotos: Int
): RoverMissionManifest

//{
// "id":102693,
// "sol":1000,
// "camera":{"id":20,"name":"FHAZ","rover_id":5,"full_name":"Front Hazard Avoidance Camera"},
// "img_src":"http://mars.jpl.nasa.gov/msl-raw-images/proj/msl/redops/ods/surface/sol/01000/opgs/edr/fcam/FLB_486265257EDR_F0481570FHAZ00323M_.JPG",
// "earth_date":"2015-05-30",
// "rover":{"id":5,"name":"Curiosity","landing_date":"2012-08-06","launch_date":"2011-11-26","status":"active"}}

//    "photo_manifest": {
//        "name": "Curiosity",
//        "landing_date": "2012-08-06",
//        "launch_date": "2011-11-26",
//        "status": "active",
//        "max_sol": 3566,
//        "max_date": "2022-08-18",
//        "total_photos": 591854,
//        "photos": []
//    }