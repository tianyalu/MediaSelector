package com.sty.media.selector;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by tian on 2019/1/31.
 */

public class RouterActivity extends AppCompatActivity implements View.OnClickListener{

    @BindView(R.id.btn_activity)
    Button btnActivity;
    @BindView(R.id.btn_fragment)
    Button btnFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_router);
        ButterKnife.bind(this);
        setListeners();
    }

    private void setListeners(){
        btnActivity.setOnClickListener(this);
        btnFragment.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.btn_activity:
                intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_fragment:
                intent = new Intent(this, PhotoFragmentActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }
}
