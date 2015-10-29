package com.mixiaoxiao.actionmenu;

import java.util.ArrayList;
import java.util.Collections;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.TextView;

/**
 * Show a action-menubar like the menus(copy,select all, paste) for UILabel in IOS
 * 显示类似于IOS中UILabel的菜单（复制、全选、粘贴）的菜单条
 * 
 * @author Mixiaoxiao 2015-10-28
 */
public class ActionMenu {

	public interface ActionMenuListener {
		public void onAction(String action, int which);
	}

	private final Activity activity;
	private final float density;

	/** The anchor to show popupwindow **/
	private final View anchor;
	/** Normal state color of ActionItem **/
	private final int itemNormalColor = 0xff20252a;
	/** Pressed state color of ActionItem **/
	private final int itemPressedColor = 0xff50555a;
	/** TextColor of ActionItem **/
	private final int itemTextColor = 0xfff0f0f0;
	/** TextSize of ActionItem **/
	private final int itemTextSize = 14;// sp
	/** Height of Arrow(black triangle) **/
	private final int arrowSize = 8;// arrow's height in dp
	/** Radius of ActionItem **/
	private final int itemRadius;
	/** Margin of ActionItem **/
	private final int itemMargin;
	private final int itemHorizontalPadding;
	private final int itemVerticalPadding;
	private ArrayList<String> actionItems = new ArrayList<String>();
	private ActionMenuListener ActionMenuListener;
	private PopupWindow popupWindow;

	private ActionMenu(Activity activity, View anchor) {
		super();
		this.activity = activity;
		this.anchor = anchor;
		this.density = activity.getResources().getDisplayMetrics().density;
		this.itemRadius = (int) (8 * density);
		this.itemMargin = (int) (0.5 * density);
		this.itemHorizontalPadding = (int) (14 * density);
		this.itemVerticalPadding = (int) (8 * density);
	}

	public static ActionMenu build(Activity activity, View anchor) {
		return new ActionMenu(activity, anchor);
	}

	public ActionMenu addActions(String... actions) {
		Collections.addAll(actionItems, actions);
		return this;
	}

	public ActionMenu addAction(String action) {
		if (action != null) {
			actionItems.add(action);
		}
		return this;
	}

	public ActionMenu setListener(ActionMenuListener ActionMenuListener) {
		this.ActionMenuListener = ActionMenuListener;
		return this;
	}

	public void show() {
		if (actionItems.size() == 0) {
			return;
		}
		LinearLayout actionsContainer = new LinearLayout(activity);
		actionsContainer.setOrientation(LinearLayout.HORIZONTAL);
		if (this.actionItems.size() == 1) {
			actionsContainer.addView(makeActionView(actionItems.get(0), 0,
					Type.SINGLE));
		} else {
			for (int i = 0; i < this.actionItems.size(); i++) {
				Type type = Type.CENTER;
				if (i == 0) {
					type = Type.LEFT;
				} else if (i == actionItems.size() - 1) {
					type = Type.RIGHT;
				}
				actionsContainer.addView(makeActionView(actionItems.get(i), i,
						type));
			}
		}
		LinearLayout rootLayout = new LinearLayout(activity);
		rootLayout.setOrientation(LinearLayout.VERTICAL);
		final int WRAP_CONTENT = LinearLayout.LayoutParams.WRAP_CONTENT;
		rootLayout.addView(actionsContainer, WRAP_CONTENT, WRAP_CONTENT);
		ArrowView arrowView = new ArrowView(activity, itemNormalColor);
		rootLayout.addView(arrowView);
		popupWindow = new PopupWindow(rootLayout);
		popupWindow.setFocusable(true);
		rootLayout.measure(WRAP_CONTENT, WRAP_CONTENT);
		popupWindow.setAnimationStyle(R.style.PopupAnimationStyle);
		popupWindow.setOutsideTouchable(true);
		popupWindow.setBackgroundDrawable(new ColorDrawable());
		int width = rootLayout.getMeasuredWidth();
		int height = rootLayout.getMeasuredHeight();
		final int decorWidth = activity.getWindow().getDecorView().getWidth();
		width = Math.min(width, decorWidth);
		popupWindow.setWidth(width);
		popupWindow.setHeight(height);
		LinearLayout.LayoutParams params = (LayoutParams) arrowView
				.getLayoutParams();
		int[] lll = new int[2];
		anchor.getLocationOnScreen(lll);
		// 这里必须要用getLocationOnScreen而不能是getLocationInWindow(lll) 原因是：
		// 比如在Dialog的window会比activity的window小，但是popupwindow能显示在dialog的window的外侧
		// anyone would translate this comment to English??
		final int anchorLeft = lll[0];
		final int anchorWidth = anchor.getWidth();
		final int anchorRight = decorWidth - anchorLeft - anchorWidth;
		log("decorWidth->" + decorWidth + " anchorLeft->" + anchorLeft
				+ " anchorRight->" + anchorRight + " anchorWidth->"
				+ anchorWidth + " width->" + width);
		if (anchorLeft + anchorWidth / 2f < width / 2) {// anchor的左边不足以让popup居中显示
			params.gravity = Gravity.LEFT;
			params.leftMargin = (int) (anchorLeft + anchorWidth / 2f - arrowView
					.getMeasuredWidth() / 2f);
		} else if (anchorRight + anchorWidth / 2f < width / 2) {// anchor的右边不足以让popup居中显示
			params.gravity = Gravity.RIGHT;
			params.rightMargin = (int) (anchorRight + anchorWidth / 2f - arrowView
					.getMeasuredWidth() / 2f);
		} else {
			params.gravity = Gravity.CENTER_HORIZONTAL;
		}
		// 坑爹的showAsDropDown不一定是dropdown!!!在窗口（如dialog）的靠底部的位置的anchor弹出showAsDropDown是在anchor的上方的。。
		// int xoff = (int) (-(width - anchorWidth) / 2f);
		// int yoff = -anchor.getHeight() - height;
		// popupWindow.showAsDropDown(anchor, xoff ,yoff);
		int windowLocation[] = new int[2];
		anchor.getLocationInWindow(windowLocation);
		// popupWindow show的location坐标是基于window的，但是popupWindow可以显示在此窗口外侧（如弹出的dialog窗口），以上大量的计算是基于screen的，用来修正arrowView的位置
		popupWindow.showAtLocation(anchor, Gravity.NO_GRAVITY,
				(int) (windowLocation[0] - (width - anchorWidth) / 2f),
				(int) (windowLocation[1] - height));
	}

	static void log(String msg) {
		// Log.d("FUCK", msg);
	}

	private enum Type {
		LEFT, CENTER, RIGHT, SINGLE;
	}

	private View.OnClickListener onClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			if (popupWindow != null) {
				popupWindow.dismiss();
			}
			if (ActionMenuListener != null) {
				ActionMenuListener.onAction(
						((TextView) v).getText().toString(), v.getId());
			}
		}
	};

	private TextView makeActionView(String action, int which, Type type) {
		TextView textView = new ActionTextView(activity, action, which, type);
		if (this.ActionMenuListener != null) {
			textView.setOnClickListener(onClickListener);
		}

		return textView;
	}

	/**
	 * The "Arrow" of the menu just draw a black triangle
	 * 
	 * @author Mixiaoxiao
	 * 
	 */
	private class ArrowView extends View {

		private final int width;
		private final int height;
		private Path path = new Path();
		private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

		public ArrowView(Context context, int arrowColor) {
			super(context);
			this.paint.setColor(arrowColor);
			final int arrowheight = (int) (ActionMenu.this.arrowSize * context
					.getResources().getDisplayMetrics().density);
			final int halfWidth = (int) (arrowheight / 1.2f);
			path.reset();
			path.moveTo(0, 0);
			path.lineTo(halfWidth * 2, 0);
			path.lineTo(halfWidth, arrowheight);
			path.close();
			width = halfWidth * 2;
			height = arrowheight;
		}

		@Override
		protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
			setMeasuredDimension(width, height);
		}

		@Override
		protected void onDraw(Canvas canvas) {
			canvas.drawPath(path, paint);
		}
	}

	/**
	 * The ActionItem's TextView.
	 * 每一项的TextView
	 * @author Mixiaoxiao
	 * 
	 */
	private class ActionTextView extends TextView {

		/**
		 * Because the "View.PRESSED_ENABLED_STATE_SET" is "protected" in
		 * View.class, I must make this class...
		 * 因为View.PRESSED_ENABLED_STATE_SET是protected的，所以要专门写个类继承于TextView，实在蛋疼
		 * 
		 * @param context
		 * @param action
		 * @param which
		 *            also is the TextView's ID
		 * @param type
		 */
		public ActionTextView(Context context, String action, int which,
				Type type) {
			super(context);
			setText(action);
			setId(which);
			setTextColor(itemTextColor);
			setTextSize(itemTextSize);
			setSingleLine(true);
			setText(action);
			setPadding(itemHorizontalPadding, itemVerticalPadding,
					itemHorizontalPadding, itemVerticalPadding);
			initType(type);
		}

		@SuppressWarnings("deprecation")
		@SuppressLint("NewApi")
		private void initType(Type type) {

			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.WRAP_CONTENT,
					LinearLayout.LayoutParams.WRAP_CONTENT);
			float[] radii = new float[8];
			switch (type) {
			case LEFT:
				radii[0] = radii[1] = radii[6] = radii[7] = itemRadius;
				break;
			case CENTER:
				params.leftMargin = itemMargin;
				break;
			case RIGHT:
				radii[2] = radii[3] = radii[4] = radii[5] = itemRadius;
				params.leftMargin = itemMargin;
				break;
			case SINGLE:
				setFocusable(true);
				setFocusableInTouchMode(true);
				setEllipsize(TextUtils.TruncateAt.MARQUEE);
				break;
			default:
				break;
			}
			setLayoutParams(params);

			StateListDrawable drawable = new StateListDrawable();
			if (Build.VERSION.SDK_INT >= 11) {
				drawable.setExitFadeDuration(100);
			}
			GradientDrawable normalDrawable = new GradientDrawable();
			GradientDrawable pressedDrawable = new GradientDrawable();
			if (type == Type.SINGLE) {
				normalDrawable.setCornerRadius(itemRadius);
				pressedDrawable.setCornerRadius(itemRadius);
			} else {
				normalDrawable.setCornerRadii(radii);
				pressedDrawable.setCornerRadii(radii);
			}
			normalDrawable.setColor(itemNormalColor);
			pressedDrawable.setColor(itemPressedColor);
			drawable.addState(View.PRESSED_ENABLED_STATE_SET, pressedDrawable);
			drawable.addState(View.EMPTY_STATE_SET, normalDrawable);
			setBackgroundDrawable(drawable);
		}

	}
}
