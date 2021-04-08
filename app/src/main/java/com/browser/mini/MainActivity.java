package com.browser.mini;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
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

public class MainActivity extends AppCompatActivity {
    private SwipeRefreshLayout swipeRefreshLayout;
    private LinearLayout buttonLayout;
    public WebView webPage;
    public LinearLayout linear;
    public EditText searchBar;
    private Button BtnOK, BtnBack, BtnNext, BtnBookMark;
    private ProgressBar loadBar;
    public boolean finishFlag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Intent intent = new Intent();

        webPage = (WebView)findViewById(R.id.webpage);
        linear = (LinearLayout)findViewById(R.id.Linear);
        searchBar = (EditText)findViewById(R.id.searchbar);
        BtnBack = (Button)findViewById(R.id.btnBack);
        BtnOK = (Button)findViewById(R.id.btnOk);
        BtnNext = (Button)findViewById(R.id.btnNext);
        loadBar = (ProgressBar)findViewById(R.id.loadbar);

        BtnBookMark = (Button)findViewById(R.id.btnBookmark);

        WebSettings set = webPage.getSettings();
        set.setJavaScriptEnabled(false);
        set.setBuiltInZoomControls(true);
        webPage.setWebViewClient(new WebViewClient());//웹뷰 활성화
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
                }
            }
        });
        webPage.loadUrl("https://google.com");


        set.setMixedContentMode(WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE);
        set.setJavaScriptEnabled(false);
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

        BtnBookMark.setOnClickListener(v -> {
            String link = searchBar.getText().toString();
            intent.putExtra("golink",link);
            intent.setAction("com.broswer.BOOKMARK_VIEW");
            startActivityForResult(intent, 10);
            hidekeyboard();
        });

        linear.setOnTouchListener((v, event) -> {
            hidekeyboard();
            return false;
        });

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
        if(url.indexOf("https://") == -1 && url.indexOf("http://")==-1){
            url = "https://" + url;
        }
        webPage.loadUrl(url);
    }

    @Override
    public void onBackPressed() {
        if(webPage.canGoBack()){
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
                        Log.e("링크 테스트",link);
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