package kang.customlayout;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.zip.Inflater;

public class MainActivity extends AppCompatActivity {

    private TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       /* KLayout kLayout = new KLayout(this);
        TextView textView = new TextView(this);
        textView.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_bright));
        textView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        textView.setText("KLayout");
        kLayout.addView(textView);
        setContentView(kLayout);*/

        setContentView(R.layout.activity_main);
        KLayout kLayout = (KLayout)findViewById(R.id.klayout);
        kLayout.setHeaderView(LayoutInflater.from(this).inflate(R.layout.header_new,null,false));
        tv = (TextView) findViewById(R.id.tv);
        tv.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                Log.d("TextView", "tv is touched");
                return true;
            }
        });
    }
}
