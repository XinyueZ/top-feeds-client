package com.topfeeds4j.sample.widgets;

import java.util.concurrent.ConcurrentHashMap;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import com.topfeeds4j.sample.R;


/**
 * A TextView that allows a custom font to be defined in a layout. The font must
 * be in the assets folder.
 * <a href="http://stackoverflow.com/questions/2376250/custom-fonts-and-xml-layouts-android">Stackoverflow</a>
 */
public class FontTextView extends TextView {
	public FontTextView(Context context) {
		super(context);
	}

	public FontTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initialize(context, attrs);
	}

	public FontTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initialize(context, attrs);
	}

	private void initialize(Context context, AttributeSet attrs) {
		String font;

		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.FontTextView);
		int fontIndex = a.getInt(R.styleable.FontTextView_font, -1);

		// defined in attrs.xml
		switch (fontIndex) {
		case 1:
			font = Fonts.BLACK;
			break;
		case 2:
			font = Fonts.BOLD;
			break;
		case 3:
			font = Fonts.DEMILIGHT;
			break;
		case 4:
			font =  Fonts.LIGHT;
			break;
		case 5:
			font = Fonts.MEDIUM;
			break;
		case 6:
			font = Fonts.REGULAR;
			break;
		case 7:
			font = Fonts.THIN;
			break;
		default:
			font = Fonts.REGULAR;
			break;
		}

		a.recycle();

		if (font != null) {
			setFont(font);
		}
	}

	public void setFont(String font) {
		if (!isInEditMode()) {
			Typeface tf = Fonts.getFont(getContext(), font);
			setTypeface(tf);
		}
	}

	/**
	 * A cache for Fonts. Works around a known memory leak in
	 * <code>Typeface.createFromAsset</code>.
	 * 
	 * <a href="http://code.google.com/p/android/issues/detail?id=9904">Google Code</a>
	 */
	public final static class Fonts {
		private static final ConcurrentHashMap<String, Typeface> sTypefaces = new ConcurrentHashMap<String, Typeface>();

		public static final String BLACK = "NotoSansCJKsc-Black.otf";
		public static final String BOLD = "NotoSansCJKsc-Bold.otf";
		public static final String DEMILIGHT = "NotoSansCJKsc-DemiLight.otf";
		public static final String LIGHT = "NotoSansCJKsc-Light.otf";
		public static final String MEDIUM = "NotoSansCJKsc-Medium.otf";
		public static final String REGULAR = "NotoSansCJKsc-Regular.otf";
		public static final String THIN = "NotoSansCJKsc-Thin.otf";

		public static Typeface getFont(Context context, String assetPath) {
			Typeface font = sTypefaces.get(assetPath);
			if (font == null) {
				font = Typeface.createFromAsset(context.getAssets(), "fonts/"
						+ assetPath);
				sTypefaces.put(assetPath, font);
			}
			return font;
		}

	}
}
