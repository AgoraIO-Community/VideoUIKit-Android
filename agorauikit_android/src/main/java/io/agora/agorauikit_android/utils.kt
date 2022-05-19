package io.agora.agorauikit_android

import android.content.Context
import android.util.TypedValue

/**
 * Converts DP unit to Px unit
 *
 * @param context Activity Context
 * @param DP The DP value
 * @return Px Value
 */
internal fun DPToPx(context: Context, DP: Int): Int {
    val res = context.resources
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DP.toFloat(), res.displayMetrics).toInt()
}
