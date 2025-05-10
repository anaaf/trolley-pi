package com.example.trollyinterface.ui.theme;

import android.graphics.Typeface;
import android.text.TextPaint;
import android.text.style.MetricAffectingSpan;

public class Typography {
    public static final float BODY_LARGE_SIZE = 16f;
    public static final float BODY_LARGE_LINE_HEIGHT = 24f;
    public static final float BODY_LARGE_LETTER_SPACING = 0.5f;

    public static final float TITLE_LARGE_SIZE = 22f;
    public static final float TITLE_LARGE_LINE_HEIGHT = 28f;
    public static final float TITLE_LARGE_LETTER_SPACING = 0f;

    public static final float HEADLINE_MEDIUM_SIZE = 28f;
    public static final float HEADLINE_MEDIUM_LINE_HEIGHT = 36f;
    public static final float HEADLINE_MEDIUM_LETTER_SPACING = 0f;

    public static class BodyLargeSpan extends MetricAffectingSpan {
        @Override
        public void updateMeasureState(TextPaint textPaint) {
            textPaint.setTextSize(BODY_LARGE_SIZE);
            textPaint.setLetterSpacing(BODY_LARGE_LETTER_SPACING);
        }

        @Override
        public void updateDrawState(TextPaint textPaint) {
            textPaint.setTextSize(BODY_LARGE_SIZE);
            textPaint.setLetterSpacing(BODY_LARGE_LETTER_SPACING);
        }
    }

    public static class TitleLargeSpan extends MetricAffectingSpan {
        @Override
        public void updateMeasureState(TextPaint textPaint) {
            textPaint.setTextSize(TITLE_LARGE_SIZE);
            textPaint.setLetterSpacing(TITLE_LARGE_LETTER_SPACING);
        }

        @Override
        public void updateDrawState(TextPaint textPaint) {
            textPaint.setTextSize(TITLE_LARGE_SIZE);
            textPaint.setLetterSpacing(TITLE_LARGE_LETTER_SPACING);
        }
    }

    public static class HeadlineMediumSpan extends MetricAffectingSpan {
        @Override
        public void updateMeasureState(TextPaint textPaint) {
            textPaint.setTextSize(HEADLINE_MEDIUM_SIZE);
            textPaint.setLetterSpacing(HEADLINE_MEDIUM_LETTER_SPACING);
            textPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        }

        @Override
        public void updateDrawState(TextPaint textPaint) {
            textPaint.setTextSize(HEADLINE_MEDIUM_SIZE);
            textPaint.setLetterSpacing(HEADLINE_MEDIUM_LETTER_SPACING);
            textPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        }
    }
} 