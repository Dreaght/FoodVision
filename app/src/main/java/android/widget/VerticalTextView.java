package android.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.TypedValue;

import com.intake.intakevisor.R;

public class VerticalTextView extends androidx.appcompat.widget.AppCompatTextView {

    private int _width, _height;
    private final Rect _bounds = new Rect();
    private int _rotationAngle = 90; // Default angle is 90 degrees

    public VerticalTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs);
    }

    public VerticalTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public VerticalTextView(Context context) {
        super(context);
    }

    private void init(AttributeSet attrs) {
        if (attrs != null) {
            TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.VerticalTextView);
            _rotationAngle = typedArray.getInt(R.styleable.VerticalTextView_rotationAngle, 90); // Default is 90
            typedArray.recycle(); // Always recycle the TypedArray
        }
    }

    public void setRotationAngle(int angle) {
        _rotationAngle = angle;
        requestLayout(); // Recalculate layout if angle changes
        invalidate(); // Redraw the view
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        // Adjust dimensions based on rotation
        _height = getMeasuredWidth();
        _width = getMeasuredHeight();
        setMeasuredDimension(_width, _height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.save();

        // Rotate canvas by the specified angle
        switch (_rotationAngle) {
            case 90:
                canvas.translate(_width, 0);
                canvas.rotate(90);
                break;
            case 180:
                canvas.translate(_width, _height);
                canvas.rotate(180);
                break;
            case 270:
                canvas.translate(0, _height);
                canvas.rotate(270);
                break;
            default: // For other angles
                canvas.rotate(_rotationAngle, _width / 2f, _height / 2f);
                break;
        }

        TextPaint paint = getPaint();
        paint.setColor(getTextColors().getDefaultColor());

        String text = text();
        paint.getTextBounds(text, 0, text.length(), _bounds);

        // Draw text centered
        canvas.drawText(text, getCompoundPaddingLeft(), (_bounds.height() + _width) / 2f, paint);

        canvas.restore();
    }

    private String text() {
        return super.getText().toString();
    }
}
