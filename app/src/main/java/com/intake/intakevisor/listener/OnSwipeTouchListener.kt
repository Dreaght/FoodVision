import android.content.Context
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener

open class OnSwipeTouchListener(context: Context?) : View.OnTouchListener {
    private val gestureDetector: GestureDetector = GestureDetector(context, GestureListener())

    override fun onTouch(v: View?, event: MotionEvent): Boolean {
//        Log.d("Swipe", "onTouch called")
        return gestureDetector.onTouchEvent(event)
    }

    private inner class GestureListener : SimpleOnGestureListener() {
        private val SWIPE_THRESHOLD = 100
        private val SWIPE_VELOCITY_THRESHOLD = 100

        override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
            Log.d("Swipe", "onSingleTapConfirmed called")
            return super.onSingleTapConfirmed(e)
        }

        override fun onLongPress(e: MotionEvent) {
            Log.d("Swipe", "onLongPress called")
            super.onLongPress(e)
        }

        override fun onScroll(
            e1: MotionEvent?,
            e2: MotionEvent,
            distanceX: Float,
            distanceY: Float
        ): Boolean {
            Log.d("Swipe", "onScroll called")
            return super.onScroll(e1, e2, distanceX, distanceY)
        }

        override fun onFling(
            e1: MotionEvent?,
            e2: MotionEvent,
            velocityX: Float,
            velocityY: Float
        ): Boolean {

            Log.d("Swipe", "onFling called")

            var result = false
            try {
                val diffX = e2?.x?.minus(e1?.x ?: 0f)
                val diffY = e2?.y?.minus(e1?.y ?: 0f)

                if (Math.abs(diffX ?: 0f) > Math.abs(diffY ?: 0f)) { // Horizontal swipe
                    if (Math.abs(diffX ?: 0f) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffX ?: 0f > 0) {
                            onSwipeRight()  // Right swipe
                        } else {
                            onSwipeLeft()   // Left swipe
                        }
                        result = true
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return result
        }

        override fun onDown(e: MotionEvent): Boolean {
            return true
        }
    }

    open fun onSwipeLeft() {}
    open fun onSwipeRight() {}
}
