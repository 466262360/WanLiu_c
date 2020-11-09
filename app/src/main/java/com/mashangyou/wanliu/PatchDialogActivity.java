package com.mashangyou.wanliu;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Created by Administrator on 2020/9/17.
 * Des:
 */
public class PatchDialogActivity extends AppCompatActivity {
    private String title, ultimateMessage;
    private static final String EXTRA_TITLE = "extra_title";
    private static final String EXTRA_ULTIMATE_MESSAGE = "extra_ultimate_message";
    public static Intent newIntent(Context context, String title, String ultimateMessage) {

        Intent intent = new Intent();
        intent.setClass(context, PatchDialogActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        intent.putExtra(EXTRA_TITLE, title);
        intent.putExtra(EXTRA_ULTIMATE_MESSAGE, ultimateMessage);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        overridePendingTransition(0, 0);
        super.onCreate(savedInstanceState);
        parseIntent(getIntent());
        if (ultimateMessage == null) {
            ultimateMessage = getString(R.string.error_message);
        }
        if (title == null) {
            title = getString(R.string.error_title);
        }
        ultimateSolution();

    }

    private void ultimateSolution() {
        new AlertDialog.Builder(this, R.style.MyAlertDialogStyle)
                .setTitle(title)
                .setMessage(ultimateMessage)
                .setCancelable(true)
                .setIcon(R.drawable.ic_error)
                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override public void onCancel(DialogInterface dialog) {
                        finish();
                    }
                })
                .setPositiveButton(getString(R.string.action_exit), new DialogInterface.OnClickListener() {
                    @Override public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish();
                    }
                }).show();
    }


    private void restart() {
        Intent intent = getBaseContext().getPackageManager().getLaunchIntentForPackage(getBaseContext().getPackageName());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        super.onDestroy();
    }


    @Override public void finish() {
        super.finish();
        overridePendingTransition(0, 0);
    }

    private void parseIntent(Intent intent) {
        title = intent.getStringExtra(EXTRA_TITLE);
        ultimateMessage = intent.getStringExtra(EXTRA_ULTIMATE_MESSAGE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
    }
}
