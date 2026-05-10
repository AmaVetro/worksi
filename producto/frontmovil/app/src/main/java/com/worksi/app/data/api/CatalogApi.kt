package com.worksi.app.data.api

import com.worksi.app.data.model.CatalogListDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface CatalogApi {
    @GET("api/v1/catalogs/regions")
    suspend fun regions(): Response<CatalogListDto>

    @GET("api/v1/catalogs/regions/{region_id}/communes")
    suspend fun communes(@Path("region_id") regionId: Long): Response<CatalogListDto>

    @GET("api/v1/catalogs/sectors")
    suspend fun sectors(): Response<CatalogListDto>

    @GET("api/v1/catalogs/sectors/{sector_id}/skills")
    suspend fun skills(@Path("sector_id") sectorId: Long): Response<CatalogListDto>
}
