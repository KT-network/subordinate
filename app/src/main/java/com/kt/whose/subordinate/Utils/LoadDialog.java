package com.kt.whose.subordinate.Utils;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.kt.whose.subordinate.R;


public class LoadDialog {

    private AlertDialog alertDialog;
    View view;
    TextView msg;
    Context mContext;

    public LoadDialog(Context context){
        alertDialog = new AlertDialog.Builder(context).create();
        mContext=context;
        view = LinearLayout.inflate(context, R.layout.dialog_load,null);
        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        msg = view.findViewById(R.id.msg);

        alertDialog.setCancelable(false);

    }

    public void setMsg(String msg1){
        msg.setText(msg1);

    }

    public void show(){
        alertDialog.setView(view);
        alertDialog.show();
    }

    public void showToast(String msg){
        view.post(new Runnable() {
            @Override
            public void run() {
                Tool.Toast(mContext,msg);
            }
        });
    }

    public void close(){
//        showToast(msg);
        alertDialog.dismiss();

    }


}
