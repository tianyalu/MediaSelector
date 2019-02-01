package com.sty.media.selector;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.permissions.RxPermissions;
import com.luck.picture.lib.tools.PictureFileUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * Created by tian on 2019/1/31.
 */

public class PhotoFragmentActivity extends AppCompatActivity {

    private PhotoFragment fragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_fragment);
        //在部分低端手机,调用单独拍照时内存不足时会导致activity被回收,所以不重复创建fragment
        if (savedInstanceState == null) {
            //添加显示第一个fragment
            fragment = new PhotoFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fl_tab_content, fragment, PictureConfig.FC_TAG).show(fragment).commit();

        } else {
            fragment = (PhotoFragment) getSupportFragmentManager()
                    .findFragmentByTag(PictureConfig.FC_TAG);
        }

        //清空图片缓存,包括裁剪,压缩后的图片 注意:必须在上传完成后调用,必须要获得权限
        RxPermissions permissions = new RxPermissions(this);
        permissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE).subscribe(new Observer<Boolean>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Boolean aBoolean) {
                if (aBoolean) {
                    PictureFileUtils.deleteCacheDirFile(PhotoFragmentActivity.this);
                } else {
                    Toast.makeText(PhotoFragmentActivity.this, "读取内存卡权限被拒绝", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }

}
