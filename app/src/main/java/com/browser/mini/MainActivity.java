package com.browser.mini;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {

    private SharedPreferences sharpref;
    private SharedPreferences.Editor srprefEditor;

    private ActionBarDrawerToggle drawerToggle;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private LinearLayout buttonLayout;
    public WebView webPage;
    public LinearLayout linear;
    public EditText searchBar;
    private Button BtnOK, BtnBack, BtnNext, BtnBookMark;
    private ProgressBar loadBar;
    public boolean finishFlag = false;

    private String setjavatogglestr = "setJavatoggle";

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(drawerToggle.onOptionsItemSelected(item)){
            return false;
        }
        return super.onOptionsItemSelected(item);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Intent intent = new Intent();

        sharpref = PreferenceManager.getDefaultSharedPreferences(this);
        srprefEditor = sharpref.edit();

        webPage = (WebView)findViewById(R.id.webpage);
        linear = (LinearLayout)findViewById(R.id.Linear);
        searchBar = (EditText)findViewById(R.id.searchbar);
        BtnBack = (Button)findViewById(R.id.btnBack);
        BtnOK = (Button)findViewById(R.id.btnOk);
        BtnNext = (Button)findViewById(R.id.btnNext);
        loadBar = (ProgressBar)findViewById(R.id.loadbar);


        BtnBookMark = (Button)findViewById(R.id.btnBookmark);

        drawerLayout = findViewById(R.id.main_drawer);
        drawerToggle = new ActionBarDrawerToggle(this,drawerLayout,R.string.drawer_open, R.string.drawer_close);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        drawerToggle.syncState();

        WebSettings set = webPage.getSettings();
        set.setJavaScriptEnabled(sharpref.getBoolean(setjavatogglestr, false));
        set.setBuiltInZoomControls(true);
        set.setDisplayZoomControls(false);
        webPage.setWebViewClient(new WebViewClient());//웹뷰 활성화
        webPage.setFocusable(false);
        webPage.setFocusableInTouchMode(false);
        webPage.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                loadBar.setProgress(newProgress);
                if(newProgress == loadBar.getMax()){
                    loadBar.setVisibility(View.GONE);
                } else {
                    loadBar.setVisibility(View.VISIBLE);
                    clearcookies(getApplicationContext());
                    if(!webPage.canGoBack()){
                        BtnBack.setVisibility(View.GONE);
                    }
                }
            }
        });
        webPage.loadUrl("https://duckduckgo.com");



        set.setMixedContentMode(WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        cookieManager.setAcceptThirdPartyCookies(webPage, false);


        BtnOK.setOnClickListener(v -> {
            searchurl();
            hidekeyboard();
        });

        BtnBack.setOnClickListener(v -> {
            if(webPage.canGoBack()){
                webPage.goBack();
            }
            hidekeyboard();
        });

        BtnNext.setOnClickListener(v -> {
            if(webPage.canGoForward()){
                webPage.goForward();
            }
            hidekeyboard();
        });

        navigationView = findViewById(R.id.main_drawer_view);
        navigationView.setNavigationItemSelectedListener(item -> {
            if(item.getItemId() == R.id.menu_bookmark){
                String link = searchBar.getText().toString();
                intent.putExtra("golink",link);
                intent.putExtra("goname",webPage.getTitle());
                intent.setAction("com.broswer.BOOKMARK_VIEW");
                startActivityForResult(intent, 10);
                hidekeyboard();
            } else if(item.getItemId() == R.id.javascript_check){
                if(set.getJavaScriptEnabled() == true){
                    item.setChecked(false);
                    set.setJavaScriptEnabled(false);
                    srprefEditor.putBoolean(setjavatogglestr, false);
                    srprefEditor.apply();
                } else if(set.getJavaScriptEnabled() == false){
                    item.setChecked(true);
                    set.setJavaScriptEnabled(true);
                    srprefEditor.putBoolean(setjavatogglestr,true);
                    srprefEditor.apply();
                }
            }
            if(drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START);
            }
            return false;
        });

        BtnBookMark.setOnClickListener(v -> {
            String link = searchBar.getText().toString();
            intent.putExtra("golink",link);
            intent.putExtra("goname",webPage.getTitle());
            intent.setAction("com.broswer.BOOKMARK_VIEW");
            startActivityForResult(intent, 10);
            hidekeyboard();
        });

       /* linear.setOnTouchListener((v, event) -> {
            //hidekeyboard();
            return false;
        });*/

        searchBar.setOnEditorActionListener((v, actionId, event) -> {
            switch (actionId){
                case EditorInfo.IME_ACTION_SEARCH:
                    searchurl();
                    hidekeyboard();
                    break;
                default:
                    return false;
            }
            return true;
        });

        swipeRefreshLayout = findViewById(R.id.refresh_layout);

        swipeRefreshLayout.setOnRefreshListener(() -> {
            webPage.reload();
        });

        webPage.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if(scrollY == 0){
                    swipeRefreshLayout.setEnabled(true);
                } else {
                    swipeRefreshLayout.setEnabled(false);
                }
                String tmp = String.valueOf(scrollY);
                //Log.e("스크롤 변화 테스트", tmp);
            }
        });

        //스크롤 올리는 도중 새로고침 방지
      /* swipeRefreshLayout.getViewTreeObserver().addOnScrollChangedListener(() -> {
           swipeRefreshLayout.setEnabled(webPage.getScrollY() == 0);
       });*/

        swipeRefreshLayout.setColorSchemeResources(
                android.R.color.holo_blue_bright
        );

        webPage.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                searchBar.setText(webPage.getUrl());

                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    public void hidekeyboard(){
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(searchBar.getWindowToken(),0);
    }

    public void searchurl(){
        String url = searchBar.getText().toString();
        String searchurl = "https://duckduckgo.com/html?q=";
        String result = "";
        if(!url.contains("https://") && !url.contains("http://")){
            result = searchurl + url;
        }
        if(url.contains(".com") || url.contains(".co")){
            result = "https://" + url;
        }
        webPage.loadUrl(result);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_ENTER:
                searchurl();
                hidekeyboard();
                return true;
            default:
                return super.onKeyDown(keyCode, event);
        }
    }

    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
            return;
        }
        else if(webPage.canGoBack()){
            webPage.goBack();
            return;
        }
        else if(finishFlag==false){
            String toastcontent = getString(R.string.toastquit); //string.xml 에서 스트링 가져오기
            Toast.makeText(getBaseContext(),toastcontent,Toast.LENGTH_SHORT).show();
            finishFlag=true;
        }
        else {
            String warnpopupname = getString(R.string.warnpopname);
            String warnpopupcontent = getString(R.string.warnpopcontent);
            String warnpopupok = getString(R.string.warnpopok);
            String warnpopupno = getString(R.string.warnpopno);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(warnpopupname);
            builder.setMessage(warnpopupcontent);
            builder.setNegativeButton(warnpopupno, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finishFlag=false;
                }
            }); //취소
            builder.setPositiveButton(warnpopupok, new DialogInterface.OnClickListener() {
                //종료시 앱을 죽인다.
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    MainActivity.super.onBackPressed();
                }
            });
            builder.show();
        }
    }

    @Override
    protected void onDestroy() {
        webPage.stopLoading();
        webPage.clearHistory();
        webPage.clearFormData();
        ViewGroup webParent = (ViewGroup)webPage.getParent();
        if(webParent != null){
            webParent.removeView(webPage);
        }
        webPage.destroy();
        clearcookies(getApplicationContext());
        android.os.Process.killProcess(android.os.Process.myPid());
        super.onDestroy();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        buttonLayout = (LinearLayout)findViewById(R.id.buttonlayout);
        if(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE){
            buttonLayout.setVisibility(View.GONE);
        } else {
            buttonLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case 10:
                switch (resultCode){
                    case RESULT_OK:
                        String link = data.getStringExtra("getlink");
                        //Log.e("링크 테스트",link);
                        EditText Searchbar = (EditText)findViewById(R.id.searchbar);
                        Searchbar.setText(link);
                        webPage.loadUrl(Searchbar.getText().toString());
                        break;
                }
                break;
        }
    }

    public static void clearcookies(Context context){
        CookieManager.getInstance().removeAllCookies(null);
        CookieManager.getInstance().removeSessionCookies(null);
        CookieManager.getInstance().flush();
    }



}