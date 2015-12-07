package com.example.violet.demo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.LayoutAnimationController;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends Activity {

    @Bind(R.id.btn)
    Button btn;
    @Bind(R.id.rl)
    LinearLayout rl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        AlphaAnimation aa = new AlphaAnimation(0,1);
        aa.setDuration(1000);
        LayoutAnimationController lac = new LayoutAnimationController(aa, 0.5f);
        rl.setLayoutAnimation(lac);
    }

    @OnClick({R.id.btn})
    public void click(View view) {
        switch (view.getId()) {
            case R.id.btn:
                //btn.startAnimation(AnimationUtils.loadAnimation(this, R.anim.btn));
//                RotateAnimation ra = new RotateAnimation(0, 360, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
//                ra.setDuration(2000);
//                btn.startAnimation(AnimationUtils.loadAnimation(this, R.anim.btn_rotate));
//                TranslateAnimation ta = new TranslateAnimation(TranslateAnimation.RELATIVE_TO_SELF, 0f,TranslateAnimation.RELATIVE_TO_SELF, 1f,
//                        TranslateAnimation.RELATIVE_TO_SELF, 0f, TranslateAnimation.RELATIVE_TO_SELF, 1f);
//                ta.setDuration(1000);
//                ta.setFillAfter(true);
//                ta.setFillEnabled(true);
//                btn.startAnimation(ta);
//                btn.startAnimation(AnimationUtils.loadAnimation(this, R.anim.btn_translate));
//                ScaleAnimation sa = new ScaleAnimation(0, 1, 0, 1, ScaleAnimation.RELATIVE_TO_SELF, 0.5f, ScaleAnimation.RELATIVE_TO_SELF, 0.5f);
//                sa.setDuration(2000);
//                btn.startAnimation(sa);
                // btn.startAnimation(AnimationUtils.loadAnimation(this, R.anim.btn_scale));


                break;
        }
    }

}
