package com.skymxc.demo.puzzle;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.skymxc.demo.puzzle.db.DBUtil;
import com.skymxc.demo.puzzle.model.Puzzle;
import com.skymxc.demo.puzzle.utils.ImageUtil;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by sky-mxc
 */
public class GameActicity  extends AppCompatActivity implements View.OnClickListener{

    private ImageView back;
    private TextView tvStep;
    private ToggleButton tbStart;
    private GridView gvGame;
    private ImageView preview;
    private List<Puzzle> puzzles;
    private Puzzle emptyPuzzle; //不显示的那块图片
    private String path;        // 图的路径
    private int level;          //当前等级
    private int colunm;         //等级对应的列数
    private int id;             //当前图片的id
    private PuzzleAdapter adapter;
    private int step ;      //几步
    private boolean started;//是否开始
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        path = getIntent().getStringExtra("path");
        level = getIntent().getIntExtra("level",0);
        colunm = level+3;   //最小三列
        id = getIntent().getIntExtra("id",-1);

        back = (ImageView) findViewById(R.id.back);
        tvStep = (TextView) findViewById(R.id.step);
        tbStart = (ToggleButton) findViewById(R.id.toggle);
        gvGame = (GridView) findViewById(R.id.grid);
        preview = (ImageView) findViewById(R.id.image);
        puzzles = new LinkedList<>();   //操作居多
        preview.setImageURI(Uri.parse("file://"+path));
        preview.setClickable(true); //拦截点击事件
        emptyPuzzle = ImageUtil.loadPuzzles(puzzles,colunm,path);   //拆分图片加载
        Log.e("Tag","=======================puzzleCount:"+puzzles.size());
        gvGame.setNumColumns(colunm);   //设置列数
        adapter= new PuzzleAdapter(this,puzzles,emptyPuzzle);
        gvGame.setAdapter(adapter);
        tbStart.setOnCheckedChangeListener(onCheckedList);
        back.setOnClickListener(this);
        gvGame.setOnItemClickListener(click);
    }
  private AdapterView.OnItemClickListener click= new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Puzzle p = puzzles.get(i);//拿到当前单击
            Log.e("Tag","======= 当前点击项position:"+p.position+"==============emptyPosition:"+emptyPuzzle.position+"============size:"+puzzles.size());
                //判断点击项是否在空格的周围
                if(p.position+1==emptyPuzzle.position
                        || p.position -1 ==emptyPuzzle.position
                        ||p.position+colunm==emptyPuzzle.position
                        ||p.position-colunm == emptyPuzzle.position){
                    //可以移动
                    move(i);
                    step++;
                    tvStep.setText(String.format("移动步数：%d",step));
                    if (isWin()){
                        gvGame.setVisibility(View.GONE);
                        started =false;
                        adapter.setWin(true);
                        tbStart.setEnabled(false);
                        preview.setImageResource(R.drawable.select_win);
                        preview.setAnimation(AnimationUtils.loadAnimation(GameActicity.this,R.anim.anim_scale));

                        preview.setVisibility(View.VISIBLE);
                        preview.setOnClickListener(GameActicity.this);

                        setResult(RESULT_OK);
                        DBUtil.updatePuzzle(id,level+1);
                    }
                    adapter.notifyDataSetChanged();
                }
        }
    };

    /**
     * 移动
     * @param i
     */
    private void move(int i) {
        Puzzle p1;
        Puzzle p2;
        //调换集合中的位置
        if (i>emptyPuzzle.position){
             p1 = puzzles.remove(i);//当前
             p2 = puzzles.remove(emptyPuzzle.position);
            puzzles.add(emptyPuzzle.position,p1);
            puzzles.add(i,p2);
        }else{
            p2 = puzzles.remove(emptyPuzzle.position);
            p1 = puzzles.remove(i);//当前

            puzzles.add(i,p2);
            puzzles.add(emptyPuzzle.position,p1);
        }
        //更换下标位置
        int tmp =  p1.position;
         p1.position = p2.position;
        p2.position =tmp;
    }

    /**
     * 判断是否已经赢了
     * @return
     */
    private boolean isWin(){
        for (Puzzle p:puzzles){
            if (p.order!=p.position) return false;
        }
        return true;
    }



    /**
     * 开关
     */
  private CompoundButton.OnCheckedChangeListener onCheckedList= new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            if (b){
                started=true;
                preview.setVisibility(View.GONE);
                gvGame.setVisibility(View.VISIBLE);
            }else{
                preview.setVisibility(View.VISIBLE);
                gvGame.setVisibility(View.GONE);
            }
            Log.e("Tag",View.VISIBLE+":显示=="+View.GONE+":不显示，==="+View.INVISIBLE+":隐藏==gvGame Visible:"+gvGame.getVisibility()+",,,,gvItem:"+gvGame.getCount());
        }
    };
    /**
     * 跳转到游戏
     * @param mainActivity  context
     * @param id    游戏id
     * @param path  游戏图片路径
     * @param level 游戏等级
     */
    public static void startActivityForResult(MainActivity mainActivity, int id, String path, int level,int requestCode) {
        Intent in = new Intent(mainActivity,GameActicity.class);
        in.putExtra("id",id);
        in.putExtra("path",path);
        in.putExtra("level",level);
        mainActivity.startActivityForResult(in,requestCode);

    }



    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.back:
            case R.id.image:
                onBackPressed();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (started){
            new AlertDialog.Builder(this)
                    .setMessage("确定放弃游戏吗?")
                    .setNegativeButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            GameActicity.super.onBackPressed();
                        }
                    })
                    .setPositiveButton("取消",null)
                    .show();
        }else {
            super.onBackPressed();
        }
    }
}
