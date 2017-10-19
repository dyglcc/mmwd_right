package com.adhoc.utils;

import android.content.Context;
import android.widget.Toast;

public class Toaster {

	public static void toast(Context context, String string) {

        Toast.makeText(context,string,Toast.LENGTH_LONG).show();

	}


}
