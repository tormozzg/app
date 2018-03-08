package com.tormozzg.app.model.technical

import java.sql.Timestamp
import javax.persistence.Column
import javax.persistence.PrePersist
import javax.persistence.PreUpdate

interface CreateTimestamp {
    var created: Timestamp
        @Column(name = "created", updatable = false, insertable = true)
        get
}

interface UpdateTimestamp : CreateTimestamp {
    var updated: Timestamp
        @Column(name = "updated", updatable = true, insertable = false)
        get
}


open class TimestampListener {

    @PrePersist
    fun onCreate(entity: Any) {
        (entity as? CreateTimestamp)?.created = Timestamp(System.currentTimeMillis())
    }

    @PreUpdate
    fun onUpdate(entity: Any) {
        (entity as? UpdateTimestamp)?.updated = Timestamp((System.currentTimeMillis()))
    }
}