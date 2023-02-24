package com.example.domain

import kotlinx.serialization.Serializable

@Serializable
data class PeerState(
    val uid: String,
    val name: String,
    val enabledMicrophone: Boolean,
    val enabledHeadset: Boolean,
)
