package com.browser.mini;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
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
    public WebView webPage;
    public LinearLayout linear;
    public EditText searchBar;
    Button BtnOK, BtnBack, BtnNext;
    ProgressBar loadBar;
    public boolean finishFlag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        webPage = (WebView)findViewById(R.id.webpage);
        linear = (LinearLayout)findViewById(R.id.Linear);
        searchBar = (EditText)findViewById(R.id.searchbar);
        BtnBack = (Button)findViewById(R.id.btnBack);
        BtnOK = (Button)findViewById(R.id.btnOk);
        BtnNext = (Button)findViewById(R.id.btnNext);
        loadBar = (ProgressBar)findViewById(R.id.loadbar);

        WebSettings set = webPage.getSettings();
        set.setJavaScriptEnabled(true);
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
                }
            }
        });
        webPage.loadUrl("https://google.com");

        set.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
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

    }

    public void hidekeyboard(){
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(searchBar.getWindowToken(),0);
    }

    public void searchurl(){
        String url = searchBar.getText().toString();
        if(url.indexOf("https://") == -1||url.indexOf("http://")==-1){
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
                    android.os.Process.killProcess(android.os.Process.myPid());
                }
            });
            builder.show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        webPage.stopLoading();
        webPage.clearHistory();
        webPage.clearFormData();
        ViewGroup webParent = (ViewGroup)webPage.getParent();
        if(webParent != null){
            webParent.removeView(webPage);
        }
        webPage.destroy();
        clearcookies(getApplicationContext());

    }

    public static void clearcookies(Context context){
        CookieManager.getInstance().removeAllCookies(null);
        CookieManager.getInstance().removeSessionCookies(null);
        CookieManager.getInstance().flush();
    }



}