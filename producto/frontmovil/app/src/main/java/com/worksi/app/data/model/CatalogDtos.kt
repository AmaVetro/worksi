package com.worksi.app.data.model

import com.squareup.moshi.Json

data class CatalogItemDto(
    val id: Long,
    val code: String,
    val name: String
)

data class CatalogListDto(
    @Json(name = "items") val items: List<CatalogItemDto> = emptyList()
)
