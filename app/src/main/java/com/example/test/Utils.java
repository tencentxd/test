package com.example.test;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.graphics.fonts.Font;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.security.SecureRandom;
import java.text.DecimalFormat;


public class Utils
{
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static SecureRandom random = new SecureRandom();
    public static final String TAG = "LoginActivity";
    public static final String SERVER_URL = "https://gamerscasino.000webhostapp.com/login.php";  // Replace with your actual server URL

    public  static  int clr18= Color.parseColor("#ff181818");
    public  static  int clr20= Color.parseColor("#FF202020");
    public  static  int clr30= Color.parseColor("#FF303030");
    public  static  int clrAccent= Color.parseColor("#ff9800");
    public static void showMsg(Context context,String msg)
    {
    Toast.makeText(context,msg,1).show();
    }
    public static void roundBG(View view, int Clr , int radius ,int elevation)
    {
        GradientDrawable gd = new GradientDrawable();
        gd.setColor(Clr);
        gd.setCornerRadius((float) radius);
        view.setBackground(gd);
        view.setElevation((float) elevation);
    }
    public static void longProg(final Context context) {
        final ProgressDialog prog = new ProgressDialog(context);
        prog.setTitle("Loading...");
        prog.setMessage("Please Wait...");
        prog.show();
        prog.setCancelable(false);
        prog.setCanceledOnTouchOutside(false);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (prog != null && prog.isShowing()) {
                    prog.dismiss();
                }
            }
        }, 3000); // Dismiss after 3 seconds (3000 milliseconds)
    }
    public static String generateUserId() {
        StringBuilder sb = new StringBuilder(10);
        for (int i = 0; i < 10; i++) {
            sb.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }
        return sb.toString();
    }
    public static String setDoubleToString(double amnt){
        DecimalFormat currencyFormat = new DecimalFormat("0.00");
        return currencyFormat.format(amnt).toString();

    }
    public static Typeface setFont(Context context, String fontType){

        Typeface font = Typeface.createFromAsset(context.getAssets(),"/fonts/"+fontType+".ttf");
        return font;
    }

}
