package com.sncfi.payment_system.dto

import org.springframework.data.domain.Page

data class PageMeta(
    val page: Int,
    val size: Int,
    val totalElements: Long,
    val totalPages: Int,
    val first: Boolean,
    val last: Boolean
)

data class PageResponse<T>(
    val data: List<T>,
    val meta: PageMeta
)

/** Maps a Spring Data Page<Entity> straight to PageResponse<Dto> in one call. */
fun <T : Any, R> Page<T>.toPageResponse(mapper: (T) -> R): PageResponse<R> =
    PageResponse(
        data = content.map(mapper),
        meta = PageMeta(
            page = number,
            size = size,
            totalElements = totalElements,
            totalPages = totalPages,
            first = isFirst,
            last = isLast
        )
    )