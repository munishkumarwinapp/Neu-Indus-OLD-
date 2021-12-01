package com.winapp.util;

import java.lang.reflect.Method;

import android.content.Context;
import android.graphics.Rect;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

public class KeyboardlessEditText extends EditText {
	private static final Method mShowSoftInputOnFocus = ReflectionUtils.getMethod(EditText.class, "setShowSoftInputOnFocus", boolean.class);

	private OnClickListener mOnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			setCursorVisible(true);
		}
	};

	private OnLongClickListener mOnLongClickListener = new OnLongClickListener() {
		@Override
		public boolean onLongClick(View v) {
			setCursorVisible(true);
			return false;
		}
	};

	public KeyboardlessEditText(Context context) {
		super(context);
		initialize();
	}

	public KeyboardlessEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		initialize();
	}

	public KeyboardlessEditText(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		initialize();
	}

	private void initialize() {
		synchronized (this) {
			setInputType(getInputType()
					| InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
			setFocusableInTouchMode(true);
		}

		// Needed to show cursor when user interacts with EditText so that the
		// edit operations
		// still work. Without the cursor, the edit operations won't appear.
		setOnClickListener(mOnClickListener);
		setOnLongClickListener(mOnLongClickListener);

		// setShowSoftInputOnFocus(false); // This is a hidden method in
		// TextView.
		reflexSetShowSoftInputOnFocus(false); // Workaround.

		// Ensure that cursor is at the end of the input box when initialized.
		// Without this, the
		// cursor may be at index 0 when there is text added via layout XML.
		setSelection(getText().length());
	}

	@Override
	protected void onFocusChanged(boolean focused, int direction,Rect previouslyFocusedRect) {
		super.onFocusChanged(focused, direction, previouslyFocusedRect);
		hideKeyboard();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		final boolean ret = super.onTouchEvent(event);
		// Must be done after super.onTouchEvent()
		hideKeyboard();
		return ret;
	}

	private void hideKeyboard() {
		final InputMethodManager imm = ((InputMethodManager) getContext()
				.getSystemService(Context.INPUT_METHOD_SERVICE));
		if (imm != null && imm.isActive(this)) {
			imm.hideSoftInputFromWindow(getApplicationWindowToken(), 0);
		}
	}

	private void reflexSetShowSoftInputOnFocus(boolean show) {
		if (mShowSoftInputOnFocus != null) {
			ReflectionUtils.invokeMethod(mShowSoftInputOnFocus, this, show);
		} else {
			// Use fallback method. Not tested.
			hideKeyboard();
		}
	}
}
