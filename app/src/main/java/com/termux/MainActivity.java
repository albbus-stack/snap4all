package com.termux;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.termux.app.TermuxActivity;
import com.termux.app.TermuxService;

import java.io.File;

public class MainActivity extends Activity {

    public static MainActivity activity;
    public AlertDialog alertDialog;
    public Button btnConsole;
    private Intent info_intent;
    public static Button btnNodeRed;
    public static Button btnDashBoard;
    public static TextView text_welcome;
    public Button btnInfo;
    private Intent console;
    private Intent node_red;
    public static boolean enableNodeRed = false;
    public static int ActualActivity = 1;
    // ActualActivity = 1 means that Main Activity is being used so onResume has to
    // show it back.
    // ActualActivity = 2 means that TermuxConsole is being used so onResume has to
    // show it back.

    // metodo che permette di abilitare i bottoni quando parte node-red
    public void enableButtons() {
        Log.d("termux", "enable buttons is being called");

        MainActivity.enableNodeRed = true;

        if (btnNodeRed != null && !btnNodeRed.isEnabled()) {
            Log.d("termux", "entered in If because btnNode.enable is false and MainActivity isnt null");
            try {
                Log.d("termux", "setting Node-red.enable to tre");
                btnNodeRed.setEnabled(true);
                Log.d("termux", "setting Node-red.enable to tre2");

            } catch (Exception e) {
                Log.d("termux", "exception from enable btn-red:" + e.getMessage());
            }
            try {
                Log.d("termux", "refreshing  Node-red");
                btnNodeRed.refreshDrawableState();

                Log.d("termux", "refreshing  Node-red2");
            } catch (Exception e) {
                Log.d("termux", "exception from enable btn-red:" + e.getMessage());
            }

        }
        if (btnDashBoard != null && !btnDashBoard.isEnabled()) {
            Log.d("termux", "entered in If because btnNode.enable is false and MainActivity isnt null");

            try {
                Log.d("termux", "setting Node-red.enable to tre");
                btnDashBoard.setEnabled(true);

            } catch (Exception e) {
                Log.d("termux", "exception from enable btn-dasboard:" + e.getMessage());
            }
            try {
                Log.d("termux", "refreshing  Node-dashobard");

                MainActivity.btnDashBoard.refreshDrawableState();
            } catch (Exception e) {
                Log.d("termux", "exception from enable btn-dashboard:" + e.getMessage());
            }

        }
        Log.d("termux", "end of method enable buttons");
        // if(!btnConsole.isEnabled())btnConsole.setEnabled(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // permette all'api_method enablebuttons di abilitare i bottoni node-red e
        // dashboard
        activity = this;
        if (enableNodeRed) {
            this.enableButtons();
            if (!btnConsole.isEnabled())
                btnConsole.setEnabled(true);
        }

        // if Termux has been installed and console is off turn it on.
        if (TermuxActivity.installed && !btnConsole.isEnabled())
            btnConsole.setEnabled(true);
    }

    @Override
    public void onBackPressed() {
        // bisogna ricordare che l'ultimo Activity visibile era MainActivity
        ActualActivity = 1;
        // minimize App serve per far finta di chiudere l'app dopo il BackPressed della
        // schermata principale.
        minimizeApp();
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        activity = this;
        node_red = new Intent(Intent.ACTION_VIEW, Uri.parse("http://localhost:1880"));
        btnNodeRed = (Button) findViewById(R.id.btn_node_red);

        btnNodeRed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(node_red);
            }
        });
        btnNodeRed.setEnabled(false);
        btnDashBoard = (Button) findViewById(R.id.btn_dashboard);
        btnDashBoard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://localhost:1880/ui/")));
            }
        });
        btnDashBoard.setEnabled(false);
        btnConsole = (Button) findViewById(R.id.btn_console);
        btnConsole.setEnabled(false);
        text_welcome = (TextView) findViewById(R.id.text_welcome);
        text_welcome.setPadding(0, getStatusBarHeight(), 0, 0);

        btnConsole.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActualActivity = 2;
                moveToConsole();

            }
        });

        info_intent = new Intent(this, InfoActivity.class);

        btnInfo = (Button) findViewById(R.id.btn_info);
        btnInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(info_intent);
            }
        });

        File rebooted = new File(TermuxService.HOME_PATH + "/rebooted");
        if (!rebooted.exists()) {
            btnNodeRed.setVisibility(View.INVISIBLE);
            btnDashBoard.setVisibility(View.INVISIBLE);
            alertDialog = new AlertDialog.Builder(this).create();
            alertDialog.setTitle("Information");
            alertDialog.setMessage(
                    "To finalize the installation of Snap4all the device must be restarted.\n\nOnce restarted, the new features installed will be shown in this menu.\n\nWithout restarting you can use the Termux Console or read the Information.\n\n-> If the Termux Console is disabled please check your internet connection, then close and reopen the app before restart.");
            try {
                alertDialog.show();
            } catch (WindowManager.BadTokenException e) {
                // Activity already dismissed - ignore.
            }
        }

    }

    public void moveToConsole() {
        super.onBackPressed();
    }

    public void minimizeApp() {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
    }

    @Override
    protected void onDestroy() {
        TermuxActivity.first_activity_options = false;
        if (alertDialog != null) {
            alertDialog.dismiss();
            alertDialog = null;
        }
        super.onDestroy();
    }

    @Override
    public void onStop() {
        if (alertDialog != null) {
            alertDialog.dismiss();
            alertDialog = null;
        }
        super.onStop();
    }
}
