package com.sildian.apps.togetrail.common.utils

fun <T: Any> Iterable<T>.split(maxSize: Int): List<List<T>> {
    val subLists: MutableList<List<T>> = mutableListOf()
    val remainingItemsInOriginList = this.toMutableList()
    while (remainingItemsInOriginList.isNotEmpty()) {
        val itemsCountToPush: Int = minOf(maxSize, remainingItemsInOriginList.size)
        subLists.add(remainingItemsInOriginList.take(itemsCountToPush))
        remainingItemsInOriginList.subList(fromIndex = 0, toIndex = itemsCountToPush).clear()
    }
    return subLists
}