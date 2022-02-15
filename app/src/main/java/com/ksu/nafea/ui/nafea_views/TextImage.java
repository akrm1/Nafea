package com.ksu.nafea.ui.nafea_views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ksu.nafea.R;


public class TextImage extends RelativeLayout
{
    public static final int POSITION_CENTER = 0;
    public static final int POSITION_TOP = 1;
    public static final int POSITION_TOPRIGHT = 2;
    public static final int POSITION_RIGHT = 3;
    public static final int POSITION_BOTTOMRIGHT = 4;
    public static final int POSITION_BOTTOM = 5;
    public static final int POSITION_BOTTOMLEFT = 6;
    public static final int POSITION_LEFT = 7;
    public static final int POSITION_TOPLEFT = 8;


    public static final int DEFAULT_TEXT_COLOR = Color.BLACK;
    public static final int DEFAULT_TEXT_SIZE = 18;
    public static final int DEFAULT_TEXT_MARGIN = 0;
    public static final int DEFAULT_TEXT_POSITION = POSITION_CENTER;
    public static final int DEFAULT_TEXTAREA_COLOR = Color.argb(150, 0,0,50);
    public static final int DEFAULT_TEXTAREA_COVERAGE = 50;


    private String text;
    private int textColor, textSize, textPosition;
    private int textTopMargin, textRightMargin, textBottomMargin, textLeftMargin;
    private int textAreaPosition, textAreaColor, textAreaCoverage;
    private int cornersRadius, cornersThickness, cornersColor;
    private int backgroundColor;
    private Drawable imgDrawable = null;

    private Integer iconID = 0;

    private TextView textView = null;
    private ImageView coverView = null;
    private ImageView imageView = null;

    private int form = 0;

    public static final int FULL_FORM = 0;
    public static final int TEXT_FORM = 1;
    public static final int IMAGE_FORM = 2;


    public TextImage(Context context)
    {
        super(context);
        init(null, 0);
    }

    public TextImage(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init(attrs, 0);
    }

    public TextImage(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle)
    {
        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.TextImage, defStyle, 0);

        this.form = a.getInteger(R.styleable.TextImage_form, 0);

        text = a.getString(R.styleable.TextImage_text);
        textColor = a.getColor(R.styleable.TextImage_textColor, DEFAULT_TEXT_COLOR);
        textPosition = a.getInteger(R.styleable.TextImage_textPosition, DEFAULT_TEXT_POSITION);

        int pxSize = a.getDimensionPixelSize(R.styleable.TextImage_textSize, DEFAULT_TEXT_SIZE);//a.getInteger(R.styleable.TextImage_textSize, DEFAULT_TEXT_SIZE);
        textSize = a.hasValue(R.styleable.TextImage_textSize) ? (int) (pxSize / getResources().getDisplayMetrics().scaledDensity) : DEFAULT_TEXT_SIZE;

        textTopMargin = a.getInteger(R.styleable.TextImage_textTopMargin, DEFAULT_TEXT_MARGIN);
        textRightMargin = a.getInteger(R.styleable.TextImage_textRightMargin, DEFAULT_TEXT_MARGIN);
        textBottomMargin = a.getInteger(R.styleable.TextImage_textBottomMargin, DEFAULT_TEXT_MARGIN);
        textLeftMargin = a.getInteger(R.styleable.TextImage_textLeftMargin, DEFAULT_TEXT_MARGIN);

        textAreaPosition = a.getInteger(R.styleable.TextImage_textAreaPosition, POSITION_BOTTOM);
        textAreaColor = a.getColor(R.styleable.TextImage_textAreaColor, DEFAULT_TEXTAREA_COLOR);
        textAreaCoverage = a.getInteger(R.styleable.TextImage_textAreaCoverage, DEFAULT_TEXTAREA_COVERAGE);

        cornersRadius = a.getInteger(R.styleable.TextImage_corners_radius, 0);
        cornersThickness = a.getInteger(R.styleable.TextImage_corners_thickness, 0);
        cornersColor = a.getColor(R.styleable.TextImage_corners_color, Color.BLACK);

        backgroundColor = a.getColor(R.styleable.TextImage_background_color, Color.TRANSPARENT);

        imgDrawable = a.getDrawable(R.styleable.TextImage_android_drawable);

        setBackground(new GradientDrawable());
        a.recycle();
    }

    private void setViewPosition(View view, int position)
    {
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view.getLayoutParams();

        switch (position)
        {
            case POSITION_CENTER:
                layoutParams.addRule(CENTER_IN_PARENT);
                break;
            case POSITION_TOP:
                layoutParams.addRule(ALIGN_PARENT_TOP);
                layoutParams.addRule(CENTER_HORIZONTAL);
                break;
            case POSITION_TOPRIGHT:
                layoutParams.addRule(ALIGN_PARENT_TOP);
                layoutParams.addRule(ALIGN_PARENT_RIGHT);
                break;
            case POSITION_RIGHT:
                layoutParams.addRule(ALIGN_PARENT_RIGHT);
                layoutParams.addRule(CENTER_VERTICAL);
                break;
            case POSITION_BOTTOMRIGHT:
                layoutParams.addRule(ALIGN_PARENT_RIGHT);
                layoutParams.addRule(ALIGN_PARENT_BOTTOM);
                break;
            case POSITION_BOTTOM:
                layoutParams.addRule(CENTER_HORIZONTAL);
                layoutParams.addRule(ALIGN_PARENT_BOTTOM);
                break;
            case POSITION_BOTTOMLEFT:
                layoutParams.addRule(ALIGN_PARENT_LEFT);
                layoutParams.addRule(ALIGN_PARENT_BOTTOM);
                break;
            case POSITION_LEFT:
                layoutParams.addRule(ALIGN_PARENT_LEFT);
                layoutParams.addRule(CENTER_VERTICAL);
                break;
            case POSITION_TOPLEFT:
                layoutParams.addRule(ALIGN_PARENT_LEFT);
                layoutParams.addRule(ALIGN_PARENT_TOP);
                break;
        }

        view.setLayoutParams(layoutParams);
    }

    private TextView createTextView()
    {
        textView = new TextView(getContext());

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(textLeftMargin, textTopMargin, textRightMargin, textBottomMargin);
        textView.setLayoutParams(layoutParams);


        setText(text);
        setTextSize(textSize);
        setTextColor(textColor);
        setViewPosition(textView, textPosition);

        return textView;
    }

    private ImageView createCoverView()
    {
        final float dp = getResources().getDisplayMetrics().density;

        coverView = new ImageView(getContext());

        int fill = (int) (textAreaCoverage * dp);
        RelativeLayout.LayoutParams layoutParams;

        if( (textAreaPosition == POSITION_TOP) || (textAreaPosition == POSITION_BOTTOM) )
            layoutParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, fill);
        else
            layoutParams = new RelativeLayout.LayoutParams(fill, LayoutParams.MATCH_PARENT);

        coverView.setLayoutParams(layoutParams);

        GradientDrawable drawable = new GradientDrawable();
        coverView.setBackground(drawable);

        setViewPosition(coverView, textAreaPosition);
        setTextAreaColor(textAreaColor);

        return coverView;
    }

    private ImageView createImageView()
    {
        final float dp = getResources().getDisplayMetrics().density;

        imageView = new ImageView(getContext());

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        imageView.setLayoutParams(layoutParams);

        setImage(imgDrawable);

        return imageView;
    }


    private void onCreateTextImage()
    {
        removeAllViews();
        imageView = null;
        textView = null;
        coverView = null;

        switch (form)
        {
            case FULL_FORM:
                addView(createImageView());
                addView(createCoverView());
                addView(createTextView());

                break;
            case TEXT_FORM:
                addView(createTextView());
                break;
            case IMAGE_FORM:
                addView(createImageView());
                break;
        }

        setBackgroundColor(backgroundColor);
        setCornersRadius(cornersRadius);
        setCornersThickness(cornersThickness);
        setCornersColor(cornersColor);
    }

    @Override
    protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();

        onCreateTextImage();
    }


    //--------------------[Setter & Getters]--------------------

    public String getText() {
        if(textView != null)
            return textView.getText().toString();
        else
            return "";
    }

    public void setText(String text) {
        this.text = text;
        if(textView != null)
            textView.setText(text);
    }

    public int getTextColor() {
        if(textView != null)
            return  textView.getCurrentTextColor();
        else
            return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
        if(textView != null)
            textView.setTextColor(textColor);
    }

    public int getTextSize() {
        if(textView != null)
            return  (int) textView.getTextSize();
        else
            return textSize;
    }

    public void setTextSize(int textSize) {
        this.textSize = textSize;
        if(textView != null)
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
    }

    public int getTextTopMargin() {
        if(textView != null)
            return  ((LayoutParams) textView.getLayoutParams()).topMargin;
        else
            return textTopMargin;
    }

    public void setTextTopMargin(int textTopMargin) {
        this.textTopMargin = textTopMargin;
        if(textView != null)
            ((LayoutParams) textView.getLayoutParams()).setMargins(textLeftMargin, textTopMargin, textRightMargin, textBottomMargin);
    }

    public int getTextRightMargin() {
        if(textView != null)
            return  ((LayoutParams) textView.getLayoutParams()).rightMargin;
        else
            return textRightMargin;
    }

    public void setTextRightMargin(int textRightMargin) {
        this.textRightMargin = textRightMargin;
        if(textView != null)
            ((LayoutParams) textView.getLayoutParams()).setMargins(textLeftMargin, textTopMargin, textRightMargin, textBottomMargin);
    }

    public int getTextBottomMargin() {
        if(textView != null)
            return  ((LayoutParams) textView.getLayoutParams()).bottomMargin;
        else
            return textBottomMargin;
    }

    public void setTextBottomMargin(int textBottomMargin) {
        this.textBottomMargin = textBottomMargin;
        if(textView != null)
            ((LayoutParams) textView.getLayoutParams()).setMargins(textLeftMargin, textTopMargin, textRightMargin, textBottomMargin);
    }

    public int getTextLeftMargin() {
        if(textView != null)
            return  ((LayoutParams) textView.getLayoutParams()).leftMargin;
        else
            return textLeftMargin;
    }

    public void setTextLeftMargin(int textLeftMargin) {
        this.textLeftMargin = textLeftMargin;
        if(textView != null)
            ((LayoutParams) textView.getLayoutParams()).setMargins(textLeftMargin, textTopMargin, textRightMargin, textBottomMargin);
    }

    public int getTextPosition() {
        return textPosition;
    }

    public void setTextPosition(int textPosition) {
        this.textPosition = textPosition;
        if(textView != null)
            setViewPosition(textView, textPosition);
    }

    public int getTextAreaPosition() {
        return textAreaPosition;
    }

    public void setTextAreaPosition(int textAreaPosition) {
        this.textAreaPosition = textAreaPosition;
        if(coverView != null)
            setViewPosition(coverView, textAreaPosition);
    }

    public int getTextAreaColor() {
            return textAreaColor;
    }

    public void setTextAreaColor(int textAreaColor) {
        this.textAreaColor = textAreaColor;

        if(coverView != null)
        {
            GradientDrawable drawable = (GradientDrawable) coverView.getBackground();
            drawable.setColor(textAreaColor);
            coverView.setBackground(drawable);
        }
    }

    public int getTextAreaCoverage() {
        return textAreaCoverage;
    }

    public void setTextAreaCoverage(int textAreaCoverage)
    {
        this.textAreaCoverage = textAreaCoverage;

        if(coverView != null)
        {
            final float dp = getResources().getDisplayMetrics().density;
            int fill = (int) (textAreaCoverage * dp);
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) coverView.getLayoutParams();

            if( (textAreaPosition == POSITION_TOP) || (textAreaPosition == POSITION_BOTTOM) )
            {
                layoutParams.width = LayoutParams.MATCH_PARENT;
                layoutParams.height = fill;

            }
            else
            {
                layoutParams.width = fill;
                layoutParams.height = LayoutParams.MATCH_PARENT;
            }

            coverView.setLayoutParams(layoutParams);
        }
    }

    public Drawable getImage() {
        return imgDrawable;
    }

    public void setImage(Drawable imgDrawable) {
        this.imgDrawable = imgDrawable;
        imageView.setImageDrawable(imgDrawable);
    }


    public int getCornerRadius() {
        return cornersRadius;
    }

    public void setCornersRadius(int cornersRadius) {
        this.cornersRadius = cornersRadius;

        GradientDrawable parentDrawable = (GradientDrawable) getBackground();
        if(parentDrawable != null)
        {
            parentDrawable.setCornerRadius(cornersRadius);
            setBackground(parentDrawable);
        }

        if(coverView != null)
        {
            GradientDrawable coverDrawable = (GradientDrawable) coverView.getBackground();
            float corners[] = {0, 0, 0, 0, cornersRadius, cornersRadius, cornersRadius, cornersRadius};
            coverDrawable.setCornerRadii(corners);
            coverView.setBackground(coverDrawable);
        }
    }


    public int getCornersThickness() {
        return cornersThickness;
    }

    public void setCornersThickness(int cornersThickness) {
        this.cornersThickness = cornersThickness;

        GradientDrawable parentDrawable = (GradientDrawable) getBackground();
        if(parentDrawable != null)
        {
            parentDrawable.setStroke(cornersThickness, cornersColor);
            setBackground(parentDrawable);
        }
    }


    public int getCornersColor() {
        return cornersColor;
    }

    public void setCornersColor(int cornersColor) {
        this.cornersColor = cornersColor;

        GradientDrawable parentDrawable = (GradientDrawable) getBackground();
        if(parentDrawable != null)
        {
            parentDrawable.setStroke(cornersThickness, cornersColor);
            setBackground(parentDrawable);
        }
    }


    public int getBackgroundColor()
    {
        return backgroundColor;
    }

    @Override
    public void setBackgroundColor(int backgroundColor)
    {
        this.backgroundColor = backgroundColor;

        GradientDrawable parentDrawable = (GradientDrawable) getBackground();
        if(parentDrawable != null)
        {
            parentDrawable.setColor(backgroundColor);
            setBackground(parentDrawable);
        }
    }


    public int getForm()
    {
        return form;
    }

    public void setForm(int form)
    {
        this.form = form;
    }


    public Integer getIconID()
    {
        return iconID;
    }

    public void setIconID(Integer iconID)
    {
        this.iconID = iconID;
    }

}
