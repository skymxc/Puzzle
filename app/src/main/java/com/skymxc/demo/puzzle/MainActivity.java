package com.skymxc.demo.puzzle;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.skymxc.demo.puzzle.db.DBUtil;
import com.skymxc.demo.puzzle.model.Image;
import com.skymxc.demo.puzzle.utils.T;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private TextView title;
    private ImageView addPicks;
    private GridView gvImage;
    private List<Image> images;
    private ImageAdapter adapter ;
    //拍照图片的存储位置
    private  File takePictureFile;
    //裁剪的图片
    private File cropFile;

    //请求码
    private static final int REQUEST_PICTURE=1;         //相册
    private static final int REQUEST_CAPTURE=2;         //相机
    private static final int REQUEST_PERMISSION=3;      //相机权限
    private static final int REQUEST_CUT=4;             //剪裁

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        addPicks = (ImageView) findViewById(R.id.image);
        gvImage = (GridView) findViewById(R.id.image_list);
        //加载数据库的数据
        images = DBUtil.loadImages();
        adapter = new ImageAdapter();
        gvImage.setAdapter(adapter);
        addPicks.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.image:
               new  AlertDialog.Builder(MainActivity.this).setItems(new String[]{"相机", "相册"}, new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialogInterface, int i) {
                       switch (i){
                           case 1:
                              Intent in = new Intent(Intent.ACTION_PICK);
                               in.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                               startActivityForResult(in,REQUEST_PICTURE);
                               break;
                           case 0:
                               takePicture();
                               break;
                       }
                   }
               }).show();
                break;
        }

    }

    /**
     * 请求拍照
     */
    private void takePicture() {

        //相机权限
        String  permission ="android.permission.CAMERA";

        //检查版本
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            //6.0以上
            //检查权限
            if (PermissionChecker.PERMISSION_GRANTED!=PermissionChecker.checkSelfPermission(this,permission)){
                //没有权限 去请求
                requestPermissions(new String[]{permission},REQUEST_PERMISSION);
            }else{
                picture();
            }
        }else{
            picture();
        }
    }

    /**
     * 请求结果
     * @param requestCode 请求码
     * @param permissions 请求权限
     * @param grantResults 请求结果
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode== REQUEST_PERMISSION && grantResults[0] ==PermissionChecker.PERMISSION_GRANTED){
            picture();
        }else{
            T.show("无法访问相机，没有权限");
        }
    }

    /**
     * 去拍照
     */
    public void picture(){
        takePictureFile = new File(Environment.getExternalStorageDirectory(),"puzzle/picture/"+ SystemClock.uptimeMillis()+".jpg");
        if (createFile(takePictureFile)) {
            Intent in = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            //设置图片都存储位置
            in.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(takePictureFile));
            //图片的格式
            in.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
            startActivityForResult(in, REQUEST_CAPTURE);
        }else{
            T.show("无法创建照片文件");
        }
    }

    /**
     * 创建文件
     * @param f 文件
     * @return 是否成功
     */
    private boolean createFile(File f){
        try {
            if (f.exists()){
                f.delete();
            }else{
                if (!f.getParentFile().exists()){
                    f.getParentFile().mkdirs();//创建父级文件夹
                }
            }
            return f.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("Tag","========requestCode============"+requestCode);
        if (resultCode == RESULT_OK){
            switch (requestCode){
                case REQUEST_CAPTURE:  //相机
                      crop(Uri.fromFile(takePictureFile));
                    break;
                case REQUEST_PICTURE:
                    Log.e("Tag","========REQUEST_PICTURE========="+data);
                    if (data!=null){
                        crop(data.getData());
                    }
                    break;
                case REQUEST_CUT:           //裁剪
                    DBUtil.saveImage(new Image(cropFile.getPath(),0,1));
                    images.clear();
                    images.addAll(DBUtil.loadImages());
                    adapter.notifyDataSetChanged();
                    if (takePictureFile != null && takePictureFile.exists()){
                        takePictureFile.delete();
                        takePictureFile =null;
                    }
                    break;
            }
        }
    }

    /**
     * 调用裁剪
     * @param uri
     */
    private void crop(Uri uri){
        cropFile = new File(Environment.getExternalStorageDirectory(),"puzzle/gameimage/"+SystemClock.uptimeMillis()+".jpg");
        if (createFile(cropFile)){
            Intent in = new Intent("com.android.camera.action.CROP");
            in.setDataAndType(uri,"image/*");
            in.putExtra("crop","true");
            in.putExtra("aspectX",1);
            in.putExtra("aspectY",1);
            in.putExtra("outputX",500);
            in.putExtra("outputY",500);
            in.putExtra(MediaStore.EXTRA_OUTPUT,Uri.fromFile(cropFile));
            in.putExtra("return-data",false);
            in.putExtra("outputFormat",Bitmap.CompressFormat.JPEG.toString());
            in.putExtra("noFaceDetection",true);
            startActivityForResult(in,REQUEST_CUT);
        }else{
            T.show("无法创建剪裁文件");
        }

    }

    class  ImageAdapter extends BaseAdapter{


        @Override
        public int getCount() {
            return images!=null?images.size():0;
        }

        @Override
        public Image getItem(int i) {
            return images.get(i);
        }

        @Override
        public long getItemId(int i) {
            return getItem(i).id;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder holder = null;
            if (view==null){
                view = getLayoutInflater().inflate(R.layout.layout_item,null);
                holder =new ViewHolder(view);
                view.setTag(holder);
            }else{
                holder = (ViewHolder) view.getTag();
            }
            Image image = getItem(i);
            holder.level.setRating(image.level);
            holder.image.setImageURI(Uri.parse("file://"+image.path));
            return view;
        }
    }

    class ViewHolder{
        RatingBar level;
        ImageView image;

        public ViewHolder(View v){
            image = (ImageView) v.findViewById(R.id.image);
            level = (RatingBar) v.findViewById(R.id.level);
        }
    }

    /**
     *
     * @param context
     */
    public static void startActivity(Context context) {
        Intent in = new Intent(context,MainActivity.class);
        context.startActivity(in);
    }
}
