package com.zmovie.app.view;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.zmovie.app.R;

public class DescDialog extends Dialog {


    private final TextView clickDesc;
    private final ImageView screenCut;

    public DescDialog(@NonNull Context context, int themeResId) {
        super(context, R.style.Dialog_Fullscreens);
        setContentView(R.layout.desc_layout);

        screenCut = findViewById(R.id.screen_cut);
        clickDesc = findViewById(R.id.click_desc);


    }

    public void setDescData(String mvdescTx, String posterUrl) {

        Glide.with(getContext()).load(posterUrl).into(screenCut);
        clickDesc.setText(mvdescTx);

    }
}
