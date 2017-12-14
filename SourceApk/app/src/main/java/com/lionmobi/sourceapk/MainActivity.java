package com.lionmobi.sourceapk;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*TextView content = new TextView(this);
        content.setText("damp good!!");*/
        setContentView(R.layout.activity_main);
        ((TextView)findViewById(R.id.text)).setText(getResources().getString(R.string.damon_good));
    }
}
