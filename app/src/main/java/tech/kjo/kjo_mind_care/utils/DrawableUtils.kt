package tech.kjo.kjo_mind_care.utils

import android.content.Context
import androidx.annotation.DrawableRes
import tech.kjo.kjo_mind_care.R

@DrawableRes
fun Context.loadDrawableResByName(resName: String): Int {
    return resources.getIdentifier(resName, "drawable", packageName).let {
        if (it != 0) it else R.drawable.ic_kjo
    }
}