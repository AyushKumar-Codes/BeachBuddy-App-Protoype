package com.prototype.beach

data class BeachJson(
    var id: Int,
    var imageName: String,
    var name: String,
    var latitude: Double,
    var longitude: Double,
    var polygonCoordinates:List<List<Double>> = listOf()
)