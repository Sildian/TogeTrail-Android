package com.sildian.apps.togetrail.common.network

import com.sildian.apps.togetrail.common.utils.nextAlphaString
import com.sildian.apps.togetrail.common.utils.nextEmailAddressString
import com.sildian.apps.togetrail.common.utils.nextString
import com.sildian.apps.togetrail.common.utils.nextUrlString
import kotlin.random.Random

fun Random.nextUser(
    id: String = nextString(),
    name: String = nextAlphaString(),
    emailAddress: String = nextEmailAddressString(),
    photoUrl: String? = nextUrlString(),
): User =
    User(
        id = id,
        name = name,
        emailAddress = emailAddress,
        photoUrl = photoUrl,
    )