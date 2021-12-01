package com.winapp.sot;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.slidingmenu.lib.SlidingMenu;
import com.splunk.mint.Mint;
import com.winapp.SFA.R;
import com.winapp.fwms.LandingActivity;
import com.winapp.helper.Constants;

import static com.winapp.sot.SOTSummaryWebService.context;

public class QuickTransferHeader extends SherlockFragmentActivity implements
        SlideMenuFragment.MenuClickInterFace,Constants {
    SlidingMenu menu;
    ImageButton searchIcon, custsearchIcon, printer,save;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setIcon(R.drawable.ic_menu);
        Mint.initAndStartSession(QuickTransferHeader.this, "29088aa0");
        setContentView(R.layout.activity_quick_transfer_header);

        ActionBar ab = getSupportActionBar();
        ab.setHomeButtonEnabled(true);
        View customNav = LayoutInflater.from(this).inflate(
                R.layout.slidemenu_actionbar_title, null);
        TextView txt = (TextView) customNav.findViewById(R.id.pageTitle);
        txt.setText("Quick Transfer");
        searchIcon = (ImageButton) customNav.findViewById(R.id.search_img);
        printer = (ImageButton) customNav.findViewById(R.id.printer);
        custsearchIcon = (ImageButton) customNav
                .findViewById(R.id.custcode_img);
        save = (ImageButton)customNav.findViewById(R.id.save);
        searchIcon.setVisibility(View.GONE);
        save.setVisibility(View.GONE);
        SOTDatabase.init(context);


        ab.setCustomView(customNav);
        ab.setDisplayShowCustomEnabled(true);

        ab.setDisplayHomeAsUpEnabled(false);
        menu = new SlidingMenu(this);
        menu.setMode(SlidingMenu.LEFT);
        menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        menu.setShadowWidthRes(R.dimen.shadow_width);
        menu.setBehindOffsetRes(R.dimen.slidingmenu_width);
        menu.setFadeDegree(0.35f);
        menu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
        menu.setMenu(R.layout.slidemenufragment);
        menu.setSlidingEnabled(false);

        custsearchIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(QuickTransferHeader.this,QuickTransferAddProduct.class);
                startActivity(intent);
                finish();

            }
        });
    }

    @Override
    public void onListItemClick(String item) {
        menu.toggle();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        menu.toggle();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(QuickTransferHeader.this,LandingActivity.class);
        startActivity(intent);
        finish();
    }
}
