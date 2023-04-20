package com.sildian.apps.togetrail.repositories.database.hiker.main

import com.sildian.apps.togetrail.common.network.DatabaseException
import com.sildian.apps.togetrail.common.network.databaseOperation
import com.sildian.apps.togetrail.repositories.database.entities.hiker.Hiker
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class HikerRepositoryImpl @Inject constructor(
    private val databaseService: HikerDatabaseService,
) : HikerRepository {

    override suspend fun getHiker(id: String): Hiker =
        databaseOperation {
            databaseService
                .getHiker(id = id)
                .get()
                .await()
                .toObject(Hiker::class.java)
                ?: throw DatabaseException.UnknownException()
        }

    override suspend fun getHikers(ids: List<String>): List<Hiker> =
        databaseOperation {
            databaseService
                .getHikers(ids = ids)
                .get()
                .await()
                .toObjects(Hiker::class.java)
        }

    override suspend fun getHikersWithNameContainingText(
        text: String,
        hikerNameToExclude: String,
    ): List<Hiker> =
        databaseOperation {
            databaseService
                .getHikersWithNameContainingText(text = text, hikerNameToExclude = hikerNameToExclude)
                .get()
                .await()
                .toObjects(Hiker::class.java)
        }

    override suspend fun addOrUpdateHiker(hiker: Hiker) {
        databaseOperation {
            databaseService
                .addOrUpdateHiker(hiker = hiker)
                ?.await()
                ?: throw DatabaseException.UnknownException()
        }
    }

    //TODO In the usecase, don't forget to delete all Hiker related data
    override suspend fun deleteHiker(hikerId: String) {
        databaseOperation {
            databaseService
                .deleteHiker(hikerId = hikerId)
                .await()
        }
    }
}