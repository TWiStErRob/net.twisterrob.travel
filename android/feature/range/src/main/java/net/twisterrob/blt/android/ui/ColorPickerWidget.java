package net.twisterrob.blt.android.ui;

import android.view.View;
import android.view.View.OnClickListener;

import androidx.annotation.ColorInt;

import net.twisterrob.android.utils.tools.DialogTools.PopupCallbacks;
import net.twisterrob.android.utils.tools.DialogToolsColor;

public class ColorPickerWidget {
	private final View display;
	private int value;
	private OnValueChangeListener onValueChangeListener;

	public interface OnValueChangeListener {

		/**
		 * Called upon a change of the current value.
		 *
		 * @param picker The color picker associated with this listener.
		 * @param oldVal The previous value.
		 * @param newVal The new value.
		 */
		void onValueChange(ColorPickerWidget picker, @ColorInt int oldVal, @ColorInt int newVal);
	}

	public ColorPickerWidget(View view) {
		this.display = view;
		this.display.setOnClickListener(new OnClickListener() {
			@Override public void onClick(View v) {
				DialogToolsColor.pickColor(v.getContext(), value, new PopupCallbacks<Integer>() {
					@Override public void finished(Integer value) {
						if (value != null) {
							setValueInternal(value, true);
						}
					}
				}).show();
			}
		});
	}
	private void updateDisplay() {
		display.setBackgroundColor(value);
	}

	/**
	 * Sets the listener to be notified on change of the current value.
	 *
	 * @param onValueChangedListener The listener.
	 */
	public void setOnValueChangedListener(OnValueChangeListener onValueChangedListener) {
		this.onValueChangeListener = onValueChangedListener;
	}

	public @ColorInt int getValue() {
		return value;
	}
	public void setValue(@ColorInt int value) {
		setValueInternal(value, false);
	}
	private void setValueInternal(@ColorInt int value, boolean notify) {
		if (this.value == value) {
			return;
		}
		this.value = value;
		updateDisplay();
		if (notify && onValueChangeListener != null) {
			onValueChangeListener.onValueChange(this, this.value, value);
		}
	}
}
