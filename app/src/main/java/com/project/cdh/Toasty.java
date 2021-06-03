package com.project.cdh;
import android.content.Context;
import android.view.Gravity;

import com.muddzdev.styleabletoast.StyleableToast;

public class Toasty
{
    public static int SUCCESS = 0;
    public static int WARNING = 1;
    public static int ERROR = 2;
    public static int INFORMATION = 4;

    public static void showText(Context context, String message, int option, int length)
    {
        int white = context.getColor(R.color.white);
        int black = context.getColor(R.color.toast_color);
        int green = context.getColor(R.color.success_color);
        int yellow = context.getColor(R.color.warning_color);
        int red = context.getColor(R.color.error_color);
        int blue = context.getColor(R.color.info_color);

        if(option == SUCCESS)
        {
            new StyleableToast
                    .Builder(context)
                    .text(message)
                    .textSize(16)
                    .textColor(black)
                    .textBold()
                    .backgroundColor(white)
                    .solidBackground()
                    .stroke(1, green)
                    .cornerRadius(3)
                    .iconStart(R.drawable.ic_success)
                    .gravity(Gravity.BOTTOM)
                    .length(length)
                    .show();
        }
        else if(option == WARNING)
        {
            new StyleableToast
                    .Builder(context)
                    .text(message)
                    .textSize(16)
                    .textColor(black)
                    .textBold()
                    .backgroundColor(white)
                    .solidBackground()
                    .stroke(1, yellow)
                    .cornerRadius(3)
                    .iconStart(R.drawable.ic_warning)
                    .gravity(Gravity.BOTTOM)
                    .length(length)
                    .show();
        }
        else if(option == ERROR)
        {
            new StyleableToast
                    .Builder(context)
                    .text(message)
                    .textSize(16)
                    .textColor(black)
                    .textBold()
                    .backgroundColor(white)
                    .solidBackground()
                    .stroke(1, red)
                    .cornerRadius(3)
                    .iconStart(R.drawable.ic_error)
                    .gravity(Gravity.BOTTOM)
                    .length(length)
                    .show();
        }
        else if(option == INFORMATION)
        {
            new StyleableToast
                    .Builder(context)
                    .text(message)
                    .textSize(16)
                    .textColor(black)
                    .textBold()
                    .backgroundColor(white)
                    .solidBackground()
                    .stroke(1, blue)
                    .cornerRadius(3)
                    .iconStart(R.drawable.ic_info)
                    .gravity(Gravity.BOTTOM)
                    .length(length)
                    .show();
        }
    }
}