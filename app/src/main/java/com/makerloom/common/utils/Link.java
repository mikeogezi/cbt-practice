package com.makerloom.common.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

/**
 * Created by michael on 3/13/18.
 */

public class Link {
    public static void visit (String url, Context context) {
        try {
            final Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            context.startActivity(intent);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
