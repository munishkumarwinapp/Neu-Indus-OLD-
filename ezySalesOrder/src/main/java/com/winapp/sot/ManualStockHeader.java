package com.winapp.sot;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.slidingmenu.lib.SlidingMenu;
import com.splunk.mint.Mint;
import com.winapp.SFA.R;
import com.winapp.fwms.LandingActivity;
import com.winapp.helper.Constants;

import static com.winapp.sot.SOTSummaryWebService.context;

public class ManualStockHeader extends SherlockFragmentActivity implements
        SlideMenuFragment.MenuClickInterFace,Constants {
    SlidingMenu menu;
    ImageButton searchIcon, custsearchIcon, printer;
    ListView listview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setIcon(R.drawable.ic_menu);
        Mint.initAndStartSession(ManualStockHeader.this, "29088aa0");
        setContentView(R.layout.activity_manual_stock_header);

        ActionBar ab = getSupportActionBar();
        ab.setHomeButtonEnabled(true);
        View customNav = LayoutInflater.from(this).inflate(
                R.layout.slidemenu_actionbar_title, null);
        TextView txt = (TextView) customNav.findViewById(R.id.pageTitle);
        txt.setText("Manual Stock");
        searchIcon = (ImageButton) customNav.findViewById(R.id.search_img);
        printer = (ImageButton) customNav.findViewById(R.id.printer);
        custsearchIcon = (ImageButton) customNav
                .findViewById(R.id.custcode_img);
        searchIcon.setVisibility(View.GONE);
        SOTDatabase.init(context);
        SOTDatabase.deleteManualStock();

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

        listview =(ListView)findViewById(R.id.saleO_listView1);

//        registerForContextMenu(listview);

        custsearchIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ManualStockHeader.this,ManualStockSummary.class);
                SalesOrderSetGet.setCon_no("");
                startActivity(intent);
                finish();

//                Intent i = new Intent(ManualStockHeader.this,
//                        ManualAddStockTake.class);
//                i.putExtra("stckTakeNo","1");
//                startActivity(i);
            }
        });

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        menu.add(0, v.getId(), 0, getResources().getString(R.string.edit_stock));
    }

    @Override
    public boolean onContextItemSelected(android.view.MenuItem item) {

        AdapterView.AdapterContextMenuInfo adapterInfo = (AdapterView.AdapterContextMenuInfo) item
                .getMenuInfo();

        if (item.getTitle() == getResources().getString(R.string.edit_stock)) {
            Intent i = new Intent(ManualStockHeader.this,
                    ManualAddStockTake.class);
            i.putExtra("stckTakeNo","25");
            startActivity(i);
        }

        return true;
    }

    @Override
    public void onListItemClick(String item) {
        menu.toggle();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ManualStockHeader.this,LandingActivity.class);
        startActivity(intent);
        finish();
    }
}
