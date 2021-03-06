package com.example.bmptransformtest;

import Utils.ByteConvertUtil;
import Utils.FileUtils;
import Utils.ToastUtils;
import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import steganography.Analysis;
import steganography.EncodingCallBack;
import steganography.Steganalysis;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.runtime.Permission;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author ethan
 */
public class MainActivity extends AppCompatActivity implements EncodingCallBack {

    private static final String TAG = "MainActivity";

    @BindView(R.id.raw_bmpImg)
    ImageView raw_bmpImg;
    @BindView(R.id.hide_bmpImg)
    ImageView hide_bmpImg;
    @BindView(R.id.merge_bmpImg)
    ImageView merge_bmpImg;
    @BindView(R.id.decode_bmpImg)
    ImageView decode_bmpImg;
    @BindView(R.id.choose_rawBmp_button)
    Button choose_rawBmp_button;
    @BindView(R.id.choose_hideBmp_button)
    Button choose_hideBmp_button;
    @BindView(R.id.merge_bmp_button)
    Button merge_bmp_button;
    @BindView(R.id.decode_hideBmp_button)
    Button decode_hideBmp_button;
    @BindView(R.id.setting_config_RadioGroup)
    RadioGroup radioGroup;
    @BindView(R.id.bmp_config_RadioGroup_YES)
    RadioButton bmp_config_RadioGroup_YES;
    @BindView(R.id.character_config_RadioGroup_YES)
    RadioButton character_config_RadioGroup_YES;
    @BindView(R.id.textView_hideCharacter)
    TextView textView_hideCharacter;
    @BindView(R.id.hide_key_edit)
    EditText hide_key_edit;
    @BindView(R.id.decode_hideTV)
    TextView decode_hideTV;
    @BindView(R.id.choose_merge_button)
    Button choose_merge_button;

    private Steganalysis steganalysis;
    private Analysis analysis;

    private static final int SELECT_PICTURE_RAW_BMP = 100;
    private static final int SELECT_PICTURE_HIDE_BMP = 101;
    private static final int SELECT_PICTURE_MERGE_BMP = 102;

    private Bitmap raw_bitmap;
    private Bitmap hide_bitmap;
    private Bitmap merge_bitmap;

    private Bitmap chooserMergeBitmap;

    private String rawBmpPath;
    private String hideBmpPath;
    private String newMergeBmpPath;

    private boolean encodeBmp = false;

    private boolean encodeText = false;

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

    private final String mergeBmpRootPath = Environment.getExternalStorageDirectory().getPath() + "/mergeBmp/" ;
    private final String decodeBmpRootPath = Environment.getExternalStorageDirectory().getPath() + "/decodeBmp/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        AndPermission.with(this)
                .runtime()
                .permission(new String[]{Permission.READ_EXTERNAL_STORAGE, Permission.WRITE_EXTERNAL_STORAGE,
                         Permission.READ_PHONE_STATE
                        , Manifest.permission.INTERNET,
                        Manifest.permission.ACCESS_WIFI_STATE,
                        Manifest.permission.CHANGE_WIFI_STATE,
                        Manifest.permission.ACCESS_NETWORK_STATE})
                .onGranted(permissions -> {
                            Log.i(TAG, "onGranted");
                        }).start();
        bmp_config_RadioGroup_YES.setChecked(true);
        textView_hideCharacter.setVisibility(View.GONE);
        hide_key_edit.setVisibility(View.GONE);

    }

    @OnClick(R.id.choose_rawBmp_button)
    public void ImageChooserRaw() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE_RAW_BMP);
    }

    @OnClick(R.id.choose_hideBmp_button)
    public void ImageChooserHide() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE_HIDE_BMP);
    }

    @OnClick(R.id.choose_merge_button)
    public void ImageChooserMerge() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE_MERGE_BMP);
    }

    @OnClick(R.id.decode_hideBmp_button)
    public void decode(){
        String text = "";
        int hideSize = 0;
//        if(!TextUtils.isEmpty(hide_key_edit.getText().toString())){
            //?????????????????????????????????
            if(encodeText && !encodeBmp && (character_config_RadioGroup_YES.isChecked())){
//                int byteSize = hide_key_edit.getText().toString().getBytes().length;
                Analysis analysis = new Analysis(newMergeBmpPath, 0);
                byte[] hideDataBytes = analysis.getBytes();
                Log.d(TAG, "hideDataBytes: " + ByteConvertUtil.byte2hex(hideDataBytes));
                if(hideDataBytes.length >0){
                    Log.d(TAG,"decodesize :" + hideDataBytes.length);
                    //?????????string????????????bmp????????????
                    String hideText = "";
                    try {
                        hideText = new String(hideDataBytes,"UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    Log.d(TAG,"hideText : " + hideText);
                    decode_hideTV.setText(hideText);
                }
                else{
                    Log.e(TAG,"decode error,size ==0 ");
                }
            }
//        }
        else if(!encodeText && encodeBmp && (bmp_config_RadioGroup_YES.isChecked())){
                //????????????????????????
//                try {
//                    FileInputStream fileInputStream = new FileInputStream(this.hideBmpPath);
//                     hideSize = fileInputStream.available();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
                Analysis analysis = new Analysis(newMergeBmpPath, 0);
                byte[] hideDataBytes = analysis.getBytes();
                Log.d(TAG,"decodesize***** :" + hideDataBytes.length);
                int type = analysis.getType();

                if(hideDataBytes.length >0){
                    if(type ==0){
                        //??????????????????
                        String hideText = "";
                        try {
                            hideText = new String(hideDataBytes,"UTF-8");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        Log.d(TAG,"hideText***** : " + hideText);
                        decode_hideTV.setText(hideText);
                    }
                    else if(type ==1){
                        //??????????????????
                        //?????????????????????bitmap
                        //???????????????????????????
                        Bitmap bitmapHideDecode = BitmapFactory.decodeByteArray(hideDataBytes,0,hideDataBytes.length);
                        String decodeFilePath = decodeBmpRootPath + simpleDateFormat.format(new Date()) + "/";
                        decode_bmpImg.setImageBitmap(bitmapHideDecode);
                        FileUtils.bytesToImageFile(hideDataBytes,decodeFilePath);
                    }
                }
                else{
                    Log.e(TAG,"decode error,size ==0");
                }
            }
        //?????????????????????????????????????????????????????????
        else if(!TextUtils.isEmpty(newMergeBmpPath)&&!encodeText && !encodeBmp){
                // ??????????????????????????????????????????????????????
                Analysis analysis = new Analysis(newMergeBmpPath, 0);
                byte[] hideDataBytes = analysis.getBytes();
                Log.d(TAG,"decodesize :" + hideDataBytes.length);
                int type = analysis.getType();
                if(hideDataBytes.length >0){

                    if(type ==0) {
                        //??????????????????
                        String hideText = "";
                        try {
                            hideText = new String(hideDataBytes, "UTF-8");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        Log.d(TAG, "hideText***** : " + hideText);
                        decode_hideTV.setText(hideText);
                    }
                    else if(type ==1){
                        //?????????????????????bitmap
                        //???????????????????????????
                        Bitmap bitmapHideDecode = BitmapFactory.decodeByteArray(hideDataBytes,0,hideDataBytes.length);
                        String decodeFilePath = decodeBmpRootPath + simpleDateFormat.format(new Date()) + "/";
                        decode_bmpImg.setImageBitmap(bitmapHideDecode);
                        FileUtils.bytesToImageFile(hideDataBytes,decodeFilePath);
                    }

                }
                else{
                    Log.e(TAG,"decode error,size ==0");
                }
        }
         else{
            Log.e(TAG,"decode error,????????????");
                ToastUtils.showToast(MainActivity.this,"????????????!");
        }
    }

    @OnClick(R.id.merge_bmp_button)
    public void encodeBmp(){
        Log.d(TAG,"encodeBmp");
        String hidePath = "";
        String rawPath = "";
        String text = "";
        if(filepathRaw != null){
            rawBmpPath = FileUtils.getFileAbsolutePath(this,filepathRaw);
        }
        if(filepathHide != null ){
            hideBmpPath = FileUtils.getFileAbsolutePath(this,filepathHide);
        }
        rawPath = rawBmpPath;
        if(bmp_config_RadioGroup_YES.isChecked()){
            if(TextUtils.isEmpty(hideBmpPath) || TextUtils.isEmpty(rawBmpPath)){
                Log.d(TAG,"??????????????????????????????????????????");
                ToastUtils.showToast(MainActivity.this,"??????????????????????????????????????????");
                encodeBmp = false;
                encodeText = false;
                return;
            }
//            hideBmpPath = FileUtils.getFileAbsolutePath(this,filepathHide);
            hidePath = hideBmpPath;
            Log.d(TAG,"hideBmpPath :" + hideBmpPath + ",rawBmpPath : " + rawBmpPath);
            encodeBmp = true;
            encodeText = false;
        }
        else if(character_config_RadioGroup_YES.isChecked() && !TextUtils.isEmpty(hide_key_edit.getText())){
            text = hide_key_edit.getText().toString();
            encodeBmp = false;
            encodeText = true;
        }
        Log.d(TAG,"encodeBmp:" + encodeBmp + ",encodeText:" + encodeText);
        steganalysis = new Steganalysis(hidePath,
                rawPath,text,this);
        //??????bmp???????????????????????????
        steganalysis.datasourceToBMP(mergeBmpRootPath + simpleDateFormat.format(new Date()) + "/");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private Uri filepathRaw;
    private Uri filepathHide;
    private Uri filePathMerge;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Image set to imageView
        if (requestCode == SELECT_PICTURE_RAW_BMP && resultCode == RESULT_OK && data != null && data.getData() != null) {

            filepathRaw = data.getData();
            Log.d(TAG,"filepathRaw : " + filepathRaw.getPath());
            try {
                raw_bitmap = BitmapFactory.decodeStream(
                        getContentResolver().openInputStream(
                                filepathRaw));
//                raw_bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filepathRaw);
                raw_bmpImg.setImageBitmap(raw_bitmap);
            } catch (IOException e) {
                Log.e(TAG, "Error : " + e);
            }
        }
        else if (requestCode == SELECT_PICTURE_HIDE_BMP && resultCode == RESULT_OK && data != null && data.getData() != null) {

            filepathHide = data.getData();
            Log.d(TAG,"filepathHide : " + filepathHide + " ,UriPath = " +filepathHide.getPath());
            try {
                hide_bitmap = BitmapFactory.decodeStream(
                        getContentResolver().openInputStream(
                                filepathHide));
//                hide_bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filepathHide);
                hide_bmpImg.setImageBitmap(hide_bitmap);
            } catch (IOException e) {
                Log.e(TAG, "Error : " + e);
            }
        }
        else if (requestCode == SELECT_PICTURE_MERGE_BMP && resultCode == RESULT_OK && data != null && data.getData() != null) {

            filePathMerge = data.getData();
            Log.d(TAG,"filePathMerge : " + filePathMerge + " ,UriPath = " +filePathMerge.getPath());
            try {
                chooserMergeBitmap = BitmapFactory.decodeStream(
                        getContentResolver().openInputStream(
                                filePathMerge));
//                hide_bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filepathHide);
                merge_bmpImg.setImageBitmap(chooserMergeBitmap);
                if(filePathMerge != null ){
                    newMergeBmpPath = FileUtils.getFileAbsolutePath(this,filePathMerge);
                }
                encodeBmp = false;
                encodeText = false;
            } catch (IOException e) {
                Log.e(TAG, "Error : " + e);
            }
        }

    }

    @Override
    protected void onStart() {
        super.onStart();

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                if(checkedId == R.id.bmp_config_RadioGroup_YES){
                    choose_hideBmp_button.setVisibility(View.VISIBLE);
                    textView_hideCharacter.setVisibility(View.GONE);
                    hide_key_edit.setVisibility(View.GONE);
                    decode_hideTV.setVisibility(View.GONE);
                    decode_bmpImg.setVisibility(View.VISIBLE);
                    hide_bmpImg.setVisibility(View.VISIBLE);
                }
                else if(checkedId == R.id.character_config_RadioGroup_YES){
                    choose_hideBmp_button.setVisibility(View.GONE);
                    textView_hideCharacter.setVisibility(View.VISIBLE);
                    hide_key_edit.setVisibility(View.VISIBLE);
                    decode_hideTV.setVisibility(View.VISIBLE);
                    decode_bmpImg.setVisibility(View.GONE);
                    hide_bmpImg.setVisibility(View.GONE);

                }
            }
        });
    }

    @Override
    public void onStartEncoding() {

    }

    @Override
    public void onCompleteEncoding(Bitmap bitmap, String newbmpFilePath) {
        Log.d(TAG,"newbmpFilePath : " + newbmpFilePath);
        newMergeBmpPath = newbmpFilePath;
        merge_bitmap = bitmap;
        if(bitmap != null){
            merge_bmpImg.setImageBitmap(bitmap);
        }
    }

}