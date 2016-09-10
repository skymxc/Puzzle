package com.skymxc.demo.puzzle.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.skymxc.demo.puzzle.model.Puzzle;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sky-mxc
 * 图片工具类
 */
public class ImageUtil {
    /**
     * 分割图片 打乱， 并返回不显示的那一张
     * @param puzzles 块集合
     * @param colunm 列
     * @param path 图片路径
     * @return
     */
    public static Puzzle loadPuzzles(List<Puzzle> puzzles, int colunm, String path) {
        List<Puzzle> puzzleList =  new ArrayList<>();
        if (splitImage(puzzleList, colunm, path)) return null;

        Puzzle empty = puzzleList.get(puzzleList.size()-1);//最后一项为空

        List<Integer> indexs = getUpsetIndex(colunm);   //获取到一个打乱顺序的下标的集合
        if (indexs.size()!=puzzleList.size()){
            return  null;
        }
        for (int i=0;i<indexs.size();i++){
            int index = indexs.get(i);  //取出打乱顺序的下标
             //按照乱序的下标取出 图块
            Puzzle p = puzzleList.get(index);
            p.position=i;   //设置所在的位置
            puzzles.add(p);

        }

        return  empty;
    }


    /**
     * 获取到打乱顺序的集合
     * @param colunm
     * @return
     */
    private static List<Integer> getUpsetIndex(int colunm){
        List<Integer> nums = new ArrayList<>();
        //得到有序的数列
        for (int i =0;i<colunm*colunm;i++){
            nums.add(new Integer(i));
        }
        //随机从 有序数列 读取
        List<Integer> upsetIndex = new ArrayList<>();
        for (int i=0;i<colunm * colunm ;i++){
            int index = (int) (Math.random()*nums.size());  //得到最大值在有序长度 之内的整数
             upsetIndex.add(nums.remove(index)); //移除掉取出的数,并添加到 无须数列
        }
        //随机序列并不一定能够还原 不能还原必须重新生成
        int sum = getSum(upsetIndex);   //得到总和
        int line = colunm - upsetIndex.indexOf(new Integer(upsetIndex.size()-1))/colunm;   //所在的下标 /列数  得到行数
        //不可还原的重新生成
        if (!canReply(sum,line,colunm)){
            upsetIndex= getUpsetIndex(colunm);//重新生成数列
        }
        return  upsetIndex;
    }

    /**
     * 判断是否可以还原
     * @param sum 总和
     * @param line 行数
     * @param colunm 列数
     * @return
     */
    private static boolean canReply(int sum, int line, int colunm) {
        if (colunm%2==1 && sum%2 ==0){
            return  true;   //列宽是奇数 总和是偶数的可以还原
        }else if (colunm%2==0 && sum%2 == 1){
            //列宽是偶数，行数是偶数  或者 行数是偶数 都可以
            if ((line%2 ==0 && sum %2 ==1) || line%2 ==0){
                return  true;

            }
        }
        return  false;
    }

    private static int getSum(List<Integer> upserIndex){
        //赋值序列
        List<Integer> nums = new ArrayList<>(upserIndex);
        nums.remove(new Integer(nums.size()-1));    //移除掉为空的项
        int sum =0;
        for (int i=0;i<nums.size()-1;i++){
            int current = nums.get(i);//拿到当前数
            int count =0;//记录后面大于当前数 的数量
            for(int j=i+1;j<nums.size();j++){
                if (current>nums.get(j)){
                    count++;
                }
            }
            sum+=count;
        }
        return  sum;
    }

    /**
     * 拆分图片
     * @param puzzles 存放分割后的图片块
     * @param colunm 列数
     * @param path 图片路径
     * @return 是否成功
     */
    private static boolean splitImage(List<Puzzle> puzzles, int colunm, String path) {
        //加载图片
        Bitmap bmp = BitmapFactory.decodeFile(path);
        if (bmp ==null){
            return true;
        }
        int size = bmp.getWidth()/colunm;   //尺寸  用图片宽度 /列数 得到每块的尺寸
        //将图片分割
        for (int i=0;i<colunm ;i++){
            for (int j=0;j<colunm;j++){
                Puzzle puzzle =new Puzzle();
                //创建图片的方式
                puzzle.bmp=Bitmap.createBitmap(bmp,j*size,i*size,size,size);
                puzzle.order=j+i*colunm;    //正确顺序
                puzzles.add(puzzle);
            }
        }
        Log.e("Tag","============splitImage==========Size:"+puzzles.size());
        return false;
    }
}
