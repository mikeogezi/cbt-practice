package com.makerloom.common.activity;

import android.support.annotation.LayoutRes;
import android.view.Menu;
import android.view.MenuItem;

import com.makerloom.ujcbt.R;

/**
 * Created by michael on 2/25/18.
 */

public class MyFullToolbarActivity extends MyAppCompatActivity {
    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);
        setupToolbar();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.full_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }
}
