package com.intake.intakevisor.analyse.util

import com.intake.intakevisor.analyse.FoodRegion
import com.intake.intakevisor.analyse.widget.TransparentOverlayView

class RegionRenderer(private val overlayView: TransparentOverlayView) {

    private val detectedRegions = mutableListOf<FoodRegion>()
    private val selectedRegions = mutableListOf<FoodRegion>()

    fun setRegions(regions: List<FoodRegion>) {
        detectedRegions.clear()
        detectedRegions.addAll(regions)
        selectedRegions.clear()
        updateOverlay()
    }

    fun toggleRegionSelection(region: FoodRegion) {
        if (selectedRegions.contains(region)) {
            selectedRegions.remove(region)
        } else {
            selectedRegions.add(region)
        }
        updateOverlay()
    }

    fun clearRegions() {
        detectedRegions.clear()
        selectedRegions.clear()
        updateOverlay()
    }

    private fun updateOverlay() {
        // Update regions in the overlay
        val regions = detectedRegions.map { region ->
            region to selectedRegions.contains(region) // Pair each region with its selected status
        }
        overlayView.setRegions(regions)
    }

    fun hasSelectedRegions(): Boolean = selectedRegions.isNotEmpty()

    fun getSelectedRegions(): List<FoodRegion> = selectedRegions.toList()
}
