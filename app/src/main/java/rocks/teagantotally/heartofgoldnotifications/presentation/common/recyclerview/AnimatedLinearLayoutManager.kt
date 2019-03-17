package rocks.teagantotally.heartofgoldnotifications.presentation.common.recyclerview

import android.content.Context
import android.support.v7.widget.LinearLayoutManager

class AnimatedLinearLayoutManager(
    context: Context,
    orientation: Int,
    reverseLayout: Boolean
) : LinearLayoutManager(
    context,
    orientation,
    reverseLayout
) {
    override fun supportsPredictiveItemAnimations(): Boolean = true
}