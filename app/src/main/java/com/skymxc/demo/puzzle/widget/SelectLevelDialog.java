package com.skymxc.demo.puzzle.widget;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.skymxc.demo.puzzle.R;
import com.skymxc.demo.puzzle.model.Image;

/**
 * Created by sky-mxc
 * 自定义 Dialog
 */
public class SelectLevelDialog extends AlertDialog {

    private ImageView back;
    private Button btEasy;
    private Button btNormal;
    private Button btHard;
    private Button btCrazy;
    private View.OnClickListener clickListener;
    private Image image;


    /**
     * 设置四个等级按钮的监听
     * @param listener 监听
     */
   public void setOnButtonClickListener(View.OnClickListener listener){
       this.clickListener= listener;
   }

    public void setImage(Image image) {
        this.image = image;
        switch (image.level){
            case 4:
            case 3:
                btCrazy.setEnabled(true);
            case 2:
                btHard.setEnabled(true);
            case 1:
                btNormal.setEnabled(true);
            case 0:
                btEasy.setEnabled(true);
        }
    }

    public Image getImage() {
        return image;
    }

    public SelectLevelDialog(@NonNull Context context) {
        super(context,R.style.SelectLevelDialog);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_select_level);
        back = (ImageView) findViewById(R.id.back);
        btEasy= (Button) findViewById(R.id.easy);
        btNormal = (Button) findViewById(R.id.normal);
        btHard = (Button) findViewById(R.id.hard);
        btCrazy = (Button) findViewById(R.id.crazy);
        back.setClickable(true);

        btNormal.setOnClickListener(clickListener);
        btEasy.setOnClickListener(clickListener);
        btHard.setOnClickListener(clickListener);
        btCrazy.setOnClickListener(clickListener);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancel();// 取消
            }
        });


    }

}
