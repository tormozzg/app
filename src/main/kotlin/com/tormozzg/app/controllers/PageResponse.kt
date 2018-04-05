package com.tormozzg.app.controllers

import org.springframework.data.domain.Page

class PageResponse(val page: Int,
                   val pageSize: Int,
                   val items: Page<*>) {
    val totalPages: Int = items.totalPages
    val count: Int = items.numberOfElements
}