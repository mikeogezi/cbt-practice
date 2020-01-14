package com.makerloom.common.activity;

import androidx.annotation.LayoutRes;
import android.view.Menu;
import android.view.MenuItem;

/**
 * Created by michael on 2/28/18.
 */

public class MyBackToolbarActivity extends MyAppCompatActivity {
    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);
        setupToolbar();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return true;
    }
}
