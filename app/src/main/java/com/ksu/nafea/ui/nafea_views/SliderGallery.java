package com.ksu.nafea.ui.nafea_views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.ksu.nafea.R;
import com.ksu.nafea.utilities.NafeaUtil;

import java.util.ArrayList;


public class SliderGallery extends LinearLayout
{
    public static final int DEFAULT_EDGES_THICKNESS = 10;
    public static final int DEFAULT_EDGES_ALPHA = 250;
    public static final int DEFAULT_BACKGROUND_ALPHA = 150;

    private int sideMargins, tailsMargins;
    private int iconWidth, iconHeight;
    private int iconForm;
    private int iconCornersRadius, iconEdgesThickness;
    private int backgroundAlpha, edgesAlpha;
    private int textSize = 16;


    private ArrayList<TextImage> icon;

    private TextImage selectedElement = null;

    public SliderGallery(Context context)
    {
        super(context);
        init(null, 0);
    }

    public SliderGallery(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init(attrs, 0);
    }

    public SliderGallery(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle)
    {
        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.SliderGallery, defStyle, 0);
        icon = new ArrayList<TextImage>();

        sideMargins = a.getInteger(R.styleable.SliderGallery_margins_side, 20);
        tailsMargins = a.getInteger(R.styleable.SliderGallery_margins_tails, 5);

        iconWidth = a.getInteger(R.styleable.SliderGallery_icon_width, 150);
        iconHeight = a.getInteger(R.styleable.SliderGallery_icon_height, 100);

        iconForm = a.getInteger(R.styleable.SliderGallery_icon_form, 0);

        iconCornersRadius = a.getInteger(R.styleable.SliderGallery_icon_corners_radius, 20);
        iconEdgesThickness = a.getInteger(R.styleable.SliderGallery_icon_edges_thickness, DEFAULT_EDGES_THICKNESS);

        backgroundAlpha = a.getInteger(R.styleable.SliderGallery_icon_background_alpha, DEFAULT_BACKGROUND_ALPHA);
        edgesAlpha = a.getInteger(R.styleable.SliderGallery_icon_edges_alpha, DEFAULT_EDGES_ALPHA);

        a.recycle();
    }


    private RelativeLayout.LayoutParams setElementMargins(RelativeLayout.LayoutParams layoutParams)
    {
        int zeroMargin = 0;

        if(icon.isEmpty())
        {
            layoutParams.setMargins(tailsMargins, zeroMargin, tailsMargins, zeroMargin);
            return layoutParams;
        }


        TextImage lastIcon = icon.get(icon.size() - 1);
        LinearLayout.LayoutParams lastIconLayout = (LinearLayout.LayoutParams) lastIcon.getLayoutParams();


        if(icon.size() == 1)
        {
            lastIconLayout.setMargins(tailsMargins, zeroMargin, zeroMargin, zeroMargin);
            layoutParams.setMargins(sideMargins, zeroMargin, tailsMargins, zeroMargin);
        }
        else
        {
            lastIconLayout.setMargins(sideMargins, zeroMargin, zeroMargin, zeroMargin);
            layoutParams.setMargins(sideMargins, zeroMargin, tailsMargins, zeroMargin);
        }


        lastIcon.setLayoutParams(lastIconLayout);
        return layoutParams;
    }

    private  TextImage createTextImage(Integer iconID, String text, int width, int height, int form, int cornersRadius, int edgesThickness, int backgroundAlpha, int edgesAlpha)
    {
        final float dp = getResources().getDisplayMetrics().density;

        TextImage textImage = new TextImage(getContext());

        textImage.setIconID(iconID);
        textImage.setText(text);
        textImage.setTextSize(textSize);
        textImage.setForm(form);

        width = (int) (width * dp);
        height = (int) (height * dp);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(width, height);
        layoutParams = setElementMargins(layoutParams);
        textImage.setLayoutParams(layoutParams);


        int randomColor = NafeaUtil.getRangedRandomColor(100, 200, backgroundAlpha);//NafeaUtil.getRandomColor(backgroundAlpha);
        textImage.setBackgroundColor(randomColor);
        textImage.setCornersRadius(cornersRadius);
        textImage.setCornersThickness(edgesThickness);
        textImage.setCornersColor(NafeaUtil.changeColorAlpha(randomColor, edgesAlpha));

        return textImage;
    }


    public void clearSelection()
    {
        if(selectedElement != null)
        {
            int normalColor = NafeaUtil.changeColorAlpha(selectedElement.getBackgroundColor(), edgesAlpha);
            selectedElement.setCornersColor(normalColor);

            int previousColor = NafeaUtil.changeColorAlpha(selectedElement.getBackgroundColor(), backgroundAlpha);
            selectedElement.setBackgroundColor(previousColor);

            selectedElement.setCornersThickness(iconEdgesThickness);

            selectedElement = null;
        }
    }

    public void selectElement(TextImage textImage)
    {
        if(selectedElement != null)
        {
            int normalColor = NafeaUtil.changeColorAlpha(selectedElement.getBackgroundColor(), edgesAlpha);
            selectedElement.setCornersColor(normalColor);

            int previousColor = NafeaUtil.changeColorAlpha(selectedElement.getBackgroundColor(), backgroundAlpha);
            selectedElement.setBackgroundColor(previousColor);

            selectedElement.setCornersThickness(iconEdgesThickness);
        }

        int highlightColor = Color.argb(220, 250, 250, 0);
        textImage.setCornersColor(highlightColor);

        int selectColor = NafeaUtil.changeColorAlpha(textImage.getBackgroundColor(), backgroundAlpha + 50);
        textImage.setBackgroundColor(selectColor);

        textImage.setCornersThickness(iconEdgesThickness + 10);

        selectedElement = textImage;
    }

    public boolean isSelectedElement()
    {
        return selectedElement != null;
    }

    public TextImage getSelectedElement()
    {
        return selectedElement;
    }

    public void setElementOnClickListener(OnClickListener listener)
    {
        for(int i = 0; i < icon.size(); i++)
        {
            icon.get(i).setOnClickListener(listener);
        }
    }


    public int length()
    {
        return icon.size();
    }

    public boolean insert(IconData data)
    {
        TextImage textImage = createTextImage(data.getIconID(), data.getText(), iconWidth, iconHeight, iconForm, iconCornersRadius, iconEdgesThickness, backgroundAlpha, edgesAlpha);
        addView(textImage);

        return this.icon.add(textImage);
    }

    public boolean insert(ArrayList<IconData> datas)
    {
        for(int i = 0; i < datas.size(); i++)
            if(!insert(datas.get(i)))
                return false;

        return true;
    }


    public boolean remove(TextImage icon)
    {
        if(this.icon.isEmpty())
            return true;

        //removeView(icon);
        return this.icon.remove(icon);
    }

    public void removeAll()
    {
        removeAllViews();
        for(int i = 0; i < icon.size(); i++)
        {
            remove(icon.get(i));
        }
    }



    //--------------------------------[Setters & Getters]--------------------------------


    public int getSideMargins() {
        return sideMargins;
    }

    public void setSideMargins(int sideMargins) {
        this.sideMargins = sideMargins;
    }

    public int getTailsMargins() {
        return tailsMargins;
    }

    public void setTailsMargins(int tailsMargins) {
        this.tailsMargins = tailsMargins;
    }

    public int getIconWidth() {
        return iconWidth;
    }

    public void setIconWidth(int iconWidth) {
        this.iconWidth = iconWidth;
    }

    public int getIconHeight() {
        return iconHeight;
    }

    public void setIconHeight(int iconHeight) {
        this.iconHeight = iconHeight;
    }

    public int getIconForm() {
        return iconForm;
    }

    public void setIconForm(int iconForm) {
        this.iconForm = iconForm;
    }

    public int getIconCornersRadius() {
        return iconCornersRadius;
    }

    public void setIconCornersRadius(int iconCornersRadius) {
        this.iconCornersRadius = iconCornersRadius;
    }

    public int getIconEdgesThickness() {
        return iconEdgesThickness;
    }

    public void setIconEdgesThickness(int iconEdgesThickness) {
        this.iconEdgesThickness = iconEdgesThickness;
    }

    public int getBackgroundAlpha() {
        return backgroundAlpha;
    }

    public void setBackgroundAlpha(int backgroundAlpha) {
        this.backgroundAlpha = backgroundAlpha;
    }

    public int getEdgesAlpha() {
        return edgesAlpha;
    }

    public void setEdgesAlpha(int edgesAlpha) {
        this.edgesAlpha = edgesAlpha;
    }

    public int getTextSize() {
        return textSize;
    }

    public void setTextSize(int textSize) {
        this.textSize = textSize;
    }
}
