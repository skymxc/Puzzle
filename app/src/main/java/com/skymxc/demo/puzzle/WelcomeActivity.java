package com.skymxc.demo.puzzle;

import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.skymxc.demo.puzzle.utils.FileUtil;
import com.skymxc.demo.puzzle.utils.T;

/**
 * Created by sky-mxc
 * 准备操作：
 *  将图片复制到 SD卡中
 */
public class WelcomeActivity extends AppCompatActivity {
    private ImageView image;
    private boolean prepared;   //初始准备完成标识
    private String permission ="android.permission.WRITE_EXTERNAL_STORAGE";  //6.0以后 需要用户同意


    /**
     * //准备工作执行完后跳转
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        image = (ImageView) findViewById(R.id.image);
        //加载动画
        Animation animation = AnimationUtils.loadAnimation(this,R.anim.anim_scale);
        //开始动画
        image.startAnimation(animation);
        //延时跳转2秒 至少两秒钟后跳转
        image.postDelayed(new Runnable() {
            @Override
            public void run() {
                toMainActivity();
            }
        },2000);
        //判断系统版本 >= 6.0
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            //检查权限  查看是否有写入的权限
            int result = PermissionChecker.checkSelfPermission(this,permission);
            if (result != PermissionChecker.PERMISSION_GRANTED){
                //不同意 就请求权限   100requestCode
                requestPermissions(new String[]{permission},100);
            }else{
                //同意
                initData();
            }
        }else{
            initData();
        }



    }

    /**
     * 请求权限的结果的回调
     * @param requestCode
     * @param permissions 请求的权限
     * @param grantResults 权限的结果
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100){
            if (grantResults[0] == PermissionChecker.PERMISSION_GRANTED){
                initData();
            }else{
                new AlertDialog.Builder(WelcomeActivity.this).setMessage("图片，音频访问权限是必须的，请授权。")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        })
                        .setOnCancelListener(new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialogInterface) {
                                finish();
                            }
                        }).show();
            }
        }
    }

    /**
     * 初始化数据
     */
    private void initData() {
        //图片的复制操作
        new Thread(new Runnable() {
            @Override
            public void run() {
               if (FileUtil .initData(WelcomeActivity.this)){
                   toMainActivity();
               }else{

                   runOnUiThread(new Runnable() {
                       @Override
                       public void run() {
                           T.show("数据初始化失败");
                       }
                   });
                   finish();
               }

            }
        }).start();
    }

    /**
     * 跳转到主activity
     * 动画和 准备工作必须都达到目的才能跳转
     */
    private void toMainActivity() {
        if (prepared){
            MainActivity.startActivity(WelcomeActivity.this);
        }else{
            prepared=true;
        }
    }
}
