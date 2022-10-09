package com.example.m16marsgram.entity

interface MarsPhoto {
    val id: Int
    val sol: Int
    val camera: RoverCamera
    val imgSrc: String
    val earthDate: String
    val rover: Rover
}

interface RoverCamera {
    val id: Int
    val name: String
    val roverId: Int
    val fullName: String
}

interface Rover {
    val id: Int
    val name: String
    val landingDate: String
    val launchDate: String
    val status: String
}

interface MissionManifest {
    val name: String
    val landingDate: String
    val launchDate: String
    val status: String
    val maxSol: Int
    val maxDate: String
    val totalPhotos: Int
    val photos: List<SingleSolManifest>
}

interface SingleSolManifest {
    val sol: Int
    val earthDate: String
    val totalPhotos: Int
    val cameras: List<String>
}

interface AllRoversMissions {
    val roversMissionsList: List<RoverMissionManifest>
}

interface RoverMissionManifest {
    val cameras: List<RoverCamera>
    val id: Int
    val landingDate: String
    val launchDate: String
    val maxDate: String
    val maxSol: Int
    val name: String
    val status: String
    val totalPhotos: Int
}

//    "photo_manifest": {
//        "name": "Curiosity",
//        "landing_date": "2012-08-06",
//        "launch_date": "2011-11-26",
//        "status": "active",
//        "max_sol": 3566,
//        "max_date": "2022-08-18",
//        "total_photos": 591854,
//        "photos": [
//        {
//                "sol": 0,
//                "earth_date": "2012-08-06",
//                "total_photos": 3702,
//                "cameras": [
//                    "CHEMCAM",
//                    "FHAZ",
//                    "MARDI",
//                    "RHAZ"
//                ]
//            }]
//    }

//{
// "id":102693,
// "sol":1000,
// "camera":{"id":20,"name":"FHAZ","rover_id":5,"full_name":"Front Hazard Avoidance Camera"},
// "img_src":"http://mars.jpl.nasa.gov/msl-raw-images/proj/msl/redops/ods/surface/sol/01000/opgs/edr/fcam/FLB_486265257EDR_F0481570FHAZ00323M_.JPG",
// "earth_date":"2015-05-30",
// "rover":{"id":5,"name":"Curiosity","landing_date":"2012-08-06","launch_date":"2011-11-26","status":"active"}}


