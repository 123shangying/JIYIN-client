package com.shangying.JiYin.function;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;

import com.shangying.JiYin.R;

import java.io.File;
/**
 * @author shangying
 */
public class Upimages extends Activity {
    private final String newName = "image.jpg";
    //private String uploadFile="/mnt/sdcard/DCIM/Camera/Icon-Small.png";
    private final String actionUrl = "";
    private String path;
    private Uri uri;
    private final int REQ_CODE_CAMERA = 1;
    private final int REQ_CODE_PICTURE = 0;
    private String imgurl;
    private File tempfile;

    public Upimages() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upimages);

        Button bpic = (Button) findViewById(R.id.share_send2);
        bpic.setOnClickListener(v -> {
        });

        Button button = (Button) findViewById(R.id.share_send1);
        button.setOnClickListener(v -> {

        });
    }




}