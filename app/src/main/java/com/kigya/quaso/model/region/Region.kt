package com.kigya.quaso.model.region

/**
 * Represents a region.
 */
enum class Region(val nameCut: String, val nameFull: String) {
    Europe("EU", "Europe"),
    Asia("AS", "Asia"),
    Africa("AF", "Africa"),
    NorthAmerica("NA", "North America"),
    SouthAmerica("SA", "South America"),
    Oceania("OC", "Oceania"),
    World("WO", "World")
}