package com.zmovie.app.view;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.zmovie.app.R;

public class DescMovieDialog extends Dialog {


    private final TextView clickDesc;

    public DescMovieDialog(@NonNull Context context, int themeResId) {
        super(context, R.style.Dialog_Fullscreens);
        setContentView(R.layout.desc_mov_layout);

        clickDesc = findViewById(R.id.click_desc);


    }

    public void setDescData(String mvdescTx, String posterUrl) {

        clickDesc.setText(mvdescTx);

    }
}
