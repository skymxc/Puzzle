package com.skymxc.demo.puzzle;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.skymxc.demo.puzzle.model.Puzzle;

import java.util.List;

/**
 * Created by sky-mxc
 */
public class PuzzleAdapter extends BaseAdapter {

    private List<Puzzle> puzzles;
    private Context mContext;
    private Puzzle emptyPuzzle;
    private boolean win;
    private GridView.LayoutParams lp;
    public void setWin(boolean win) {
        this.win = win;
    }

    public PuzzleAdapter(Context context , List<Puzzle> puzzles, Puzzle puzzle){
        this.mContext =context;
        this.puzzles = puzzles;
        this.emptyPuzzle = puzzle;
        lp = new GridView.LayoutParams(GridView.LayoutParams.MATCH_PARENT,GridView.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public int getCount() {
        return puzzles == null ? 0:puzzles.size();
    }

    @Override
    public Puzzle getItem(int i) {
        return puzzles.get(i);
    }

    @Override
    public long getItemId(int i) {
        return getItem(i).order;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ImageView image;
        if (view ==null){
            image = new ImageView(mContext);
            image.setAdjustViewBounds(true);
            image.setLayoutParams(lp);
        }else{
            image = (ImageView) view;
        }
        Puzzle puzzle = getItem(i);
        if (puzzle.order == emptyPuzzle.order && !win){
            image.setImageBitmap(null);
        }else {
            image.setImageBitmap(puzzle.bmp);
        }
        return image;
    }
}
