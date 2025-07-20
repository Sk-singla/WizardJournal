package com.samapps.wizardjournal.feature_journal.domain.util

sealed class OrderType {
    object Ascending : OrderType()
    object Descending : OrderType()
}