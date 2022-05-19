package com.sildian.apps.togetrail.common.baseViewModels

import com.sildian.apps.togetrail.common.baseDataRequests.DataRequest

/*************************************************************************************************
 * This file provides with classes aiming to hold data and expose its state within viewModels
 ************************************************************************************************/

/**Base for all dataHolders**/
abstract class DataHolder<T: Any?>(val data: T, val error: Throwable?)

/**Holds a list of data**/
class ListDataHolder<T: Any>(data: List<T>, error: Throwable? = null): DataHolder<List<T>>(data, error)

/**Holds a single data**/
class SingleDataHolder<T: Any?>(data: T, error: Throwable? = null): DataHolder<T>(data, error)

/**For data requests which doesn't return any data, this watches the success state**/
class DataRequestStateHolder(dataRequest: DataRequest, error: Throwable?) : DataHolder<DataRequest>(dataRequest, error)