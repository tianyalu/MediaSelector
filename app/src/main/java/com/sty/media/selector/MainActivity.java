package com.sty.media.selector;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.permissions.RxPermissions;
import com.luck.picture.lib.tools.PictureFileUtils;
import com.sty.media.selector.adapter.GridImageAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,
        RadioGroup.OnCheckedChangeListener, CompoundButton.OnCheckedChangeListener {
    @BindView(R.id.iv_left_back)
    ImageView ivLeftBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_right)
    TextView tvRight;
    @BindView(R.id.rl_title)
    RelativeLayout rlTitle;
    @BindView(R.id.rcv_recycler_view)
    RecyclerView rcvRecyclerView;
    @BindView(R.id.iv_minus)
    ImageView ivMinus;
    @BindView(R.id.tv_select_num)
    TextView tvSelectNum;
    @BindView(R.id.iv_plus)
    ImageView ivPlus;
    @BindView(R.id.rb_default_style)
    RadioButton rbDefaultStyle;
    @BindView(R.id.rb_white_style)
    RadioButton rbWhiteStyle;
    @BindView(R.id.rb_num_style)
    RadioButton rbNumStyle;
    @BindView(R.id.rb_sina_style)
    RadioButton rbSinaStyle;
    @BindView(R.id.rgb_style)
    RadioGroup rgbStyle;
    @BindView(R.id.cb_voice)
    CheckBox cbVoice;
    @BindView(R.id.rb_all)
    RadioButton rbAll;
    @BindView(R.id.rb_image)
    RadioButton rbImage;
    @BindView(R.id.rb_video)
    RadioButton rbVideo;
    @BindView(R.id.rb_audio)
    RadioButton rbAudio;
    @BindView(R.id.rgb_photo_mode)
    RadioGroup rgbPhotoMode;
    @BindView(R.id.cb_mode)
    CheckBox cbMode;
    @BindView(R.id.cb_choose_mode)
    CheckBox cbChooseMode;
    @BindView(R.id.cb_is_camera)
    CheckBox cbIsCamera;
    @BindView(R.id.cb_is_gif)
    CheckBox cbIsGif;
    @BindView(R.id.cb_preview_img)
    CheckBox cbPreviewImg;
    @BindView(R.id.cb_preview_video)
    CheckBox cbPreviewVideo;
    @BindView(R.id.cb_preview_audio)
    CheckBox cbPreviewAudio;
    @BindView(R.id.cb_compress_img)
    CheckBox cbCompressImg;
    @BindView(R.id.cb_crop_img)
    CheckBox cbCropImg;
    @BindView(R.id.rb_crop_default)
    RadioButton rbCropDefault;
    @BindView(R.id.rb_crop_1to1)
    RadioButton rbCrop1to1;
    @BindView(R.id.rb_crop_3to4)
    RadioButton rbCrop3to4;
    @BindView(R.id.rb_crop_3to2)
    RadioButton rbCrop3to2;
    @BindView(R.id.rb_crop_16to9)
    RadioButton rbCrop16to9;
    @BindView(R.id.rgb_crop)
    RadioGroup rgbCrop;
    @BindView(R.id.cb_crop_circular)
    CheckBox cbCropCircular;
    @BindView(R.id.cb_show_crop_grid)
    CheckBox cbShowCropGrid;
    @BindView(R.id.cb_show_crop_frame)
    CheckBox cbShowCropFrame;
    @BindView(R.id.cb_crop_style)
    CheckBox cbCropStyle;
    @BindView(R.id.cb_hide)
    CheckBox cbHide;

    private static final String TAG = MainActivity.class.getSimpleName();
    private List<LocalMedia> selectList = new ArrayList<>();
    private GridImageAdapter adapter;
    private int maxSelectNum = 9;
    private int aspectRatioX, aspectRatioY;
    private int themeId;
    private int chooseMode = PictureMimeType.ofAll();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        themeId = R.style.picture_default_style;

        rgbCrop.setOnCheckedChangeListener(this);
        rgbStyle.setOnCheckedChangeListener(this);
        rgbPhotoMode.setOnCheckedChangeListener(this);
        ivLeftBack.setOnClickListener(this);
        ivMinus.setOnClickListener(this);
        ivPlus.setOnClickListener(this);
        cbCropCircular.setOnCheckedChangeListener(this);
        cbCropImg.setOnCheckedChangeListener(this);
        cbCompressImg.setOnCheckedChangeListener(this);
        FullyGridLayoutManager manager = new FullyGridLayoutManager(MainActivity.this, 4, GridLayoutManager.VERTICAL, false);
        rcvRecyclerView.setLayoutManager(manager);
        adapter = new GridImageAdapter(MainActivity.this, onAddPicClickListener);
        adapter.setList(selectList);
        adapter.setSelectMax(maxSelectNum);
        rcvRecyclerView.setAdapter(adapter);
        adapter.setmOnItemClickListener(new GridImageAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                if (selectList.size() > 0) {
                    LocalMedia media = selectList.get(position);
                    String pictureType = media.getPictureType();
                    int mediaType = PictureMimeType.pictureToVideo(pictureType);
                    switch (mediaType) {
                        case 1:
                            //预览图片,可自定义长按保存路径
                            PictureSelector.create(MainActivity.this).themeStyle(themeId).openExternalPreview(position, selectList);
                            break;
                        case 2:
                            //预览视频
                            PictureSelector.create(MainActivity.this).externalPictureVideo(media.getPath());
                            break;
                        case 3:
                            //预览音频
                            PictureSelector.create(MainActivity.this).externalPictureAudio(media.getPath());
                            break;
                    }
                }
            }
        });

        // 清空图片缓存,包括裁剪,压缩后的图片 注意:必须要在上传完成之后调用,必须要获取权限
        RxPermissions permissions = new RxPermissions(this);
        permissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE).subscribe(new Observer<Boolean>() {

            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Boolean aBoolean) {
                if (aBoolean) {
                    PictureFileUtils.deleteCacheDirFile(MainActivity.this);
                } else {
                    Toast.makeText(MainActivity.this, "读取内存卡权限被拒绝", Toast.LENGTH_SHORT).show();
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

    private GridImageAdapter.OnAddPicClickListener onAddPicClickListener = new GridImageAdapter.OnAddPicClickListener() {
        @Override
        public void onAddPicClick() {
            boolean mode = cbMode.isChecked();
            if (mode) {
                //进入相册 以下是例子: 不需要的api可以不写
                PictureSelector.create(MainActivity.this)
                        .openGallery(chooseMode) //全部:PictureMimeType.ofAll(), 图片:.ofImage(), 视频:.ofVideo(), 音频:.ofVideo()
                        .theme(themeId) //主题样式设置 具体参考 values/styles 用法: R.style.picture.white.style
                        .maxSelectNum(maxSelectNum) //最大图片选择数量
                        .minSelectNum(1) //最小选择数量
                        .imageSpanCount(4) //每行显示个数
                        .selectionMode(cbChooseMode.isChecked() ? PictureConfig.MULTIPLE : PictureConfig.SINGLE) //多选/单选
                        .previewImage(cbPreviewImg.isChecked()) //是否可预览图片
                        .previewVideo(cbPreviewVideo.isChecked()) //是否可预览视频
                        .enablePreviewAudio(cbPreviewAudio.isChecked()) //是否可播放音频
                        .isCamera(cbIsCamera.isChecked()) //是否显示拍照按钮
                        .isZoomAnim(true) //图片列表点击 缩放效果 默认true
                        //.imageFormat(PictureMimeType.PNG) //拍照保存图片格式后缀,默认jpeg
                        //.setOutputCameraPath("/CustomPath") //自定义拍照保存路径
                        .enableCrop(cbCropImg.isChecked()) //是否裁剪
                        .compress(cbCompressImg.isChecked()) //是否压缩
                        .synOrAsy(true) //同步true或异步false 压缩默认同步
                        //.compressSavePath(getPath()) //压缩图片保存路径
                        //.sizeMultiplier(0.5f) //glide 加载图片大小 0~1之间 如设置.glideOverride()无效
                        .glideOverride(160, 160) //glide 加载宽高, 越小图片月流畅,但会影响列表图片浏览的清晰度
                        .withAspectRatio(aspectRatioX, aspectRatioY) //裁剪比例 如 16:9 3:2 1:1 可自定义
                        .hideBottomControls(cbHide.isChecked() ? false : true) //是否显示uCrop工具栏,默认不显示
                        .isGif(cbIsGif.isChecked()) //是否显示gif图片
                        .freeStyleCropEnabled(true) //裁剪框是否可拖拽
                        .circleDimmedLayer(cbCropCircular.isChecked()) //是否显示圆形裁剪
                        .showCropFrame(cbShowCropFrame.isChecked()) //是否显示裁剪矩形边框 圆形裁剪时建议设成false
                        .showCropGrid(cbShowCropGrid.isChecked()) //是否显示裁剪矩形网格 圆形裁剪时建议设为false
                        .openClickSound(cbVoice.isChecked()) //是否开启点击声音
                        .selectionMedia(selectList) //是否传入已选图片
                        //.isDragFrame(false) //是否可拖动裁剪框(固定)
                        //.videoMaxSecond(15) //显示最大15s时长的视频/音频
                        //.videoMinSecond(10) //显示最小10s时长的视频/音频
                        //.previewEggs(false) //预览图片时 是否增强左右滑动图片体验(图片滑动一半即可看到上一张是否选中)
                        //.cropCompressQuality(90) //裁剪压缩质量 默认100
                        .minimumCompressSize(100) //小于100kb的图片不压缩
                        //.cropWH(4, 3) //裁剪宽高比, 设置如果大于图片本身宽高则无效
                        //.rotateEnabled(true) //裁剪是否可旋转图片
                        //.scaleEnabled(true) //裁剪是否可放大缩小图片
                        //.videoQuality(1) //设置视频录制质量 0 or 1
                        //.recordVideoSecond(60) //录制视频秒数,默认60s
                        .forResult(PictureConfig.CHOOSE_REQUEST); //结果回调onActivityResult code

            } else {
                //单独拍照
                PictureSelector.create(MainActivity.this)
                        .openCamera(chooseMode) //单独拍照,也可以录像或者音频 看传入的类型是图片or视频
                        .theme(themeId) //主题样式设置 具体参考values/styles
                        .maxSelectNum(maxSelectNum) //最大图片选择数量
                        .minSelectNum(1) //最小选择数量
                        .selectionMode(cbChooseMode.isChecked() ? PictureConfig.MULTIPLE : PictureConfig.SINGLE) //多选 or 单选
                        .previewImage(cbPreviewImg.isChecked()) //是否可预览图片
                        .previewVideo(cbPreviewVideo.isChecked()) //是否可预览视频
                        .enablePreviewAudio(cbPreviewAudio.isChecked()) //是否可播放音频
                        .isCamera(cbIsCamera.isChecked()) //是否可预览视频
                        .enableCrop(cbCropImg.isChecked()) //是否裁剪
                        .compress(cbCompressImg.isChecked()) //是否压缩
                        .glideOverride(160, 160) //glide加载宽高,越小图片列表越流畅,单会影响列表图片浏览的清晰度
                        .withAspectRatio(aspectRatioX, aspectRatioY) //裁剪比例 如16:9 3:2 3:4 1:1 可自定义
                        .hideBottomControls(cbHide.isChecked() ? false : true) //是否显示uCrop工具栏,默认不显示
                        .isGif(cbIsGif.isChecked()) //是否显示GIF图
                        .freeStyleCropEnabled(cbCropStyle.isChecked()) //裁剪框是否可拖拽
                        .circleDimmedLayer(cbCropCircular.isChecked()) //是否圆形裁剪
                        .showCropFrame(cbShowCropFrame.isChecked()) //是否显示裁剪矩形边框 圆形裁剪时建议设成false
                        .showCropGrid(cbShowCropGrid.isChecked()) //是否显示裁剪矩形网格 圆形裁剪时建议设为false
                        .openClickSound(cbVoice.isChecked()) //是否开启点击声音
                        .selectionMedia(selectList) //是否传入已选图片
                        .previewEggs(false) //预览图片时 是否增强左右滑动图片体验(图片滑一半即可看到上一张是否选中)
                        //.cropCompressQuality(90) //裁剪压缩质量 默认为100
                        .minimumCompressSize(100) //小于100kb的图片不被压缩
                        //.cropWH(4:3) //裁剪宽高比, 设置如果大于图片本身宽高则无效
                        //.rotateEnabled(true) //裁剪是否可旋转图片
                        //.scaleEnabled(true) //裁剪是否可放大缩小图片
                        //.videoQuality(1) //视频录制质量
                        //.videoMinSecond(10) //显示最小10s时长的视频/音频
                        //.videoMaxSecond(15) //显示最大15s时长的视频/音频
                        .forResult(PictureConfig.CHOOSE_REQUEST); //结果回调onActivityResult code
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK) {
            switch (requestCode) {
                case PictureConfig.CHOOSE_REQUEST:
                    //图片选择结果回调
                    selectList = PictureSelector.obtainMultipleResult(data);
                    //例如LocalMedia 里面返回三种path
                    //1.media.getPath() --原图path
                    //2.media.getCutPath() --裁剪后path,需要判断media.isCut()是否为true
                    //3.media.getCompressPath() --压缩后path,需要判断media.isCompressed()是否为true
                    //如果裁剪并压缩了,以压缩路径为准,因为是先裁剪后压缩的
                    for(LocalMedia media : selectList){
                        Log.i("图片-----> ", media.getPath());
                    }
                    adapter.setList(selectList);
                    adapter.notifyDataSetChanged();
                    break;
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_left_back:
                finish();
                break;
            case R.id.iv_minus:
                if(maxSelectNum > 1){
                    maxSelectNum --;
                }
                tvSelectNum.setText(maxSelectNum + "");
                adapter.setSelectMax(maxSelectNum);
                break;
            case R.id.iv_plus:
                maxSelectNum ++;
                tvSelectNum.setText(maxSelectNum + "");
                adapter.setSelectMax(maxSelectNum);
                break;
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
        switch (checkedId) {
            case R.id.rb_all:
                chooseMode = PictureMimeType.ofAll();
                cbPreviewImg.setChecked(true);
                cbPreviewVideo.setChecked(true);
                cbIsGif.setChecked(false);
                cbPreviewVideo.setChecked(true);
                cbPreviewImg.setChecked(true);
                cbPreviewVideo.setVisibility(View.VISIBLE);
                cbPreviewImg.setVisibility(View.VISIBLE);
                cbCompressImg.setVisibility(View.VISIBLE);
                cbCropImg.setVisibility(View.VISIBLE);
                cbIsGif.setVisibility(View.VISIBLE);
                cbPreviewAudio.setVisibility(View.GONE);
                break;
            case R.id.rb_image:
                chooseMode = PictureMimeType.ofImage();
                cbPreviewImg.setChecked(true);
                cbPreviewVideo.setChecked(false);
                cbIsGif.setChecked(false);
                cbPreviewImg.setVisibility(View.VISIBLE);
                cbPreviewVideo.setVisibility(View.GONE);
                cbPreviewAudio.setVisibility(View.GONE);
                cbCompressImg.setVisibility(View.VISIBLE);
                cbCropImg.setVisibility(View.VISIBLE);
                cbIsGif.setVisibility(View.VISIBLE);
                break;
            case R.id.rb_video:
                chooseMode = PictureMimeType.ofVideo();
                cbPreviewImg.setChecked(false);
                cbPreviewVideo.setChecked(true);
                cbIsGif.setChecked(false);
                cbPreviewImg.setVisibility(View.GONE);
                cbPreviewVideo.setVisibility(View.VISIBLE);
                cbIsGif.setVisibility(View.GONE);
                cbPreviewAudio.setVisibility(View.GONE);
                cbCompressImg.setVisibility(View.GONE);
                cbCropImg.setVisibility(View.GONE);
                break;
            case R.id.rb_audio:
                chooseMode = PictureMimeType.ofAudio();
                cbPreviewAudio.setVisibility(View.VISIBLE);
                break;
            case R.id.rb_crop_default:
                aspectRatioX = 0;
                aspectRatioY = 0;
                break;
            case R.id.rb_crop_1to1:
                aspectRatioX = 1;
                aspectRatioY = 1;
                break;
            case R.id.rb_crop_3to4:
                aspectRatioX = 3;
                aspectRatioY = 4;
                break;
            case R.id.rb_crop_3to2:
                aspectRatioX = 3;
                aspectRatioY = 2;
                break;
            case R.id.rb_crop_16to9:
                aspectRatioX = 16;
                aspectRatioY = 9;
                break;
            case R.id.rb_default_style:
                themeId = R.style.picture_default_style;
                break;
            case R.id.rb_white_style:
                themeId = R.style.picture_white_style;
                break;
            case R.id.rb_num_style:
                themeId = R.style.picture_QQ_style;
                break;
            case R.id.rb_sina_style:
                themeId = R.style.picture_sina_style;
                break;
            default:
                break;
        }
    }

    private int x = 0, y = 0;

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()){
            case R.id.cb_crop_img:
                rgbCrop.setVisibility(isChecked ? View.VISIBLE : View.GONE);
                cbHide.setVisibility(isChecked ? View.VISIBLE : View.GONE);
                cbCropCircular.setVisibility(isChecked ? View.VISIBLE : View.GONE);
                cbCropStyle.setVisibility(isChecked ? View.VISIBLE : View.GONE);
                cbShowCropFrame.setVisibility(isChecked ? View.VISIBLE : View.GONE);
                cbShowCropGrid.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            case R.id.cb_crop_circular:
                if (isChecked) {
                    x = aspectRatioX;
                    y = aspectRatioY;
                    aspectRatioX = 1;
                    aspectRatioY = 1;
                } else {
                    aspectRatioX = x;
                    aspectRatioY = y;
                }
                rgbCrop.setVisibility(isChecked ? View.GONE : View.VISIBLE);
                if (isChecked) {
                    cbShowCropFrame.setChecked(false);
                    cbShowCropGrid.setChecked(false);
                } else {
                    cbShowCropFrame.setChecked(true);
                    cbShowCropGrid.setChecked(true);
                }
                break;
            default:
                break;
        }
    }

    private String getPath() {
        String path = Environment.getExternalStorageDirectory() + "/compressed/image/";
        File file = new File(path);
        if (file.mkdirs()) {
            return path;
        }
        return path;
    }
}
