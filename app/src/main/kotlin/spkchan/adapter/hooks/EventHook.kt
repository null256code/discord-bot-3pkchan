package spkchan.adapter.hooks

import discord4j.core.event.domain.Event

interface EventHook<T: Event> {
    fun handle(event: T)
}