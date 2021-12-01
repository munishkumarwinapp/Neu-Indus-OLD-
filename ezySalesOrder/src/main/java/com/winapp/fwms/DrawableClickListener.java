package com.winapp.fwms;

import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.TextView;

public abstract class DrawableClickListener implements OnTouchListener {

	public static final int DRAWABLE_INDEX_LEFT = 0;
	public static final int DRAWABLE_INDEX_TOP = 1;	
	public static final int DRAWABLE_INDEX_RIGHT = 2;
	public static final int DRAWABLE_INDEX_BOTTOM = 3;
	public static final int DEFAULT_FUZZ = 10;

	private final int fuzz;
	private Drawable drawable = null;
	public DrawableClickListener(final TextView view, final int drawableIndex) {
		this(view, drawableIndex, DrawableClickListener.DEFAULT_FUZZ);
	}

	public DrawableClickListener(final TextView view, final int drawableIndex,
			final int fuzz) {
		super();
		this.fuzz = fuzz;
		final Drawable[] drawables = view.getCompoundDrawables();
		if (drawables != null && drawables.length == 4) {
			this.drawable = drawables[drawableIndex];
		}
	}

	@Override
	public boolean onTouch(final View v, final MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN && drawable != null) {
			final int x = (int) event.getX();
			final int y = (int) event.getY();
			final Rect bounds = drawable.getBounds();
			if (this.isClickOnDrawable(x, y, v, bounds, this.fuzz)) {
				return this.onDrawableClick();
			}
		}
		return false;
	}

	public abstract boolean isClickOnDrawable(final int x, final int y,
			final View view, final Rect drawableBounds, final int fuzz);

	public abstract boolean onDrawableClick();

	public static abstract class LeftDrawableClickListener extends
			DrawableClickListener {

		public LeftDrawableClickListener(final TextView view) {
			super(view, DrawableClickListener.DRAWABLE_INDEX_LEFT);
		}

		public LeftDrawableClickListener(final TextView view, final int fuzz) {
			super(view, DrawableClickListener.DRAWABLE_INDEX_LEFT, fuzz);
		}

		/* PUBLIC METHODS */
		@Override
		public boolean isClickOnDrawable(final int x, final int y,
				final View view, final Rect drawableBounds, final int fuzz) {
			if (x >= (view.getPaddingLeft() - fuzz)) {
				if (x <= (view.getPaddingLeft() + drawableBounds.width() + fuzz)) {
					if (y >= (view.getPaddingTop() - fuzz)) {
						if (y <= (view.getHeight() - view.getPaddingBottom() + fuzz)) {
							return true;
						}
					}
				}
			}
			return false;
		}

	}

	public static abstract class TopDrawableClickListener extends
			DrawableClickListener {

		public TopDrawableClickListener(final TextView view) {
			super(view, DrawableClickListener.DRAWABLE_INDEX_TOP);
		}

		public TopDrawableClickListener(final TextView view, final int fuzz) {
			super(view, DrawableClickListener.DRAWABLE_INDEX_TOP, fuzz);
		}

		/* PUBLIC METHODS */
		@Override
		public boolean isClickOnDrawable(final int x, final int y,
				final View view, final Rect drawableBounds, final int fuzz) {
			if (x >= (view.getPaddingLeft() - fuzz)) {
				if (x <= (view.getWidth() - view.getPaddingRight() + fuzz)) {
					if (y >= (view.getPaddingTop() - fuzz)) {
						if (y <= (view.getPaddingTop()
								+ drawableBounds.height() + fuzz)) {
							return true;
						}
					}
				}
			}
			return false;
		}

	}

	public static abstract class RightDrawableClickListener extends
			DrawableClickListener {

		public RightDrawableClickListener(final TextView view) {
			super(view, DrawableClickListener.DRAWABLE_INDEX_RIGHT);
		}

		public RightDrawableClickListener(final TextView view, final int fuzz) {
			super(view, DrawableClickListener.DRAWABLE_INDEX_RIGHT, fuzz);
		}

		@Override
		public boolean isClickOnDrawable(final int x, final int y,
				final View view, final Rect drawableBounds, final int fuzz) {
			if (x >= (view.getWidth() - view.getPaddingRight()
					- drawableBounds.width() - fuzz)) {
				if (x <= (view.getWidth() - view.getPaddingRight() + fuzz)) {
					if (y >= (view.getPaddingTop() - fuzz)) {
						if (y <= (view.getHeight() - view.getPaddingBottom() + fuzz)) {
							return true;
						}
					}
				}
			}
			return false;
		}

	}

	public static abstract class BottomDrawableClickListener extends
			DrawableClickListener {

		public BottomDrawableClickListener(final TextView view) {
			super(view, DrawableClickListener.DRAWABLE_INDEX_BOTTOM);
		}

		public BottomDrawableClickListener(final TextView view, final int fuzz) {
			super(view, DrawableClickListener.DRAWABLE_INDEX_BOTTOM, fuzz);
		}

		
		@Override
		public boolean isClickOnDrawable(final int x, final int y,
				final View view, final Rect drawableBounds, final int fuzz) {
			if (x >= (view.getPaddingLeft() - fuzz)) {
				if (x <= (view.getWidth() - view.getPaddingRight() + fuzz)) {
					if (y >= (view.getHeight() - view.getPaddingBottom()
							- drawableBounds.height() - fuzz)) {
						if (y <= (view.getHeight() - view.getPaddingBottom() + fuzz)) {
							return true;
						}
					}
				}
			}
			return false;
		}

	}

}