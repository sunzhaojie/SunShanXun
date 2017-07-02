package com.example.sunshanxun.main;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.sunshanxun.view.SXProgressDialog;


/**
 * Created by SunZJ on 16/10/2.
 */
public class BaseAty extends AppCompatActivity {

    private SXProgressDialog SXProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public  void showProgress(String msg) {
        if (SXProgressDialog != null) {
            SXProgressDialog.cancel();
        }
        SXProgressDialog = new SXProgressDialog(this, msg);
        SXProgressDialog.show();
    }

    public  void cancelProgress() {
        if (SXProgressDialog != null) {
            SXProgressDialog.cancel();
        }
    }
}
