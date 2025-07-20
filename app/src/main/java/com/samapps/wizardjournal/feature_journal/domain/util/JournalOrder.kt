package com.samapps.wizardjournal.feature_journal.domain.util

sealed class JournalOrder(val orderType: OrderType) {
    class Date(orderType: OrderType) : JournalOrder(orderType)
    class ModifiedTimestamp(orderType: OrderType) : JournalOrder(orderType)
    class Title(orderType: OrderType) : JournalOrder(orderType)
    class Content(orderType: OrderType) : JournalOrder(orderType)
}