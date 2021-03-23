package com.browser.mini;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    public WebView webPage;
    EditText searchBar;
    Button BtnOK, BtnBack, BtnNext;
    ProgressBar loadBar;
    public boolean finishFlag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        webPage = (WebView)findViewById(R.id.webpage);
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

        BtnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = searchBar.getText().toString();
                if(url.indexOf("https://") == -1){
                    url = "https://" + url;
                }
                webPage.loadUrl(url);
            }
        });

        BtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(webPage.canGoBack()){
                    webPage.goBack();
                }
            }
        });

        BtnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(webPage.canGoForward()){
                    webPage.goForward();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        if(webPage.canGoBack()){
            webPage.goBack();
            return;
        }
        else if(finishFlag==false){
            Toast.makeText(getBaseContext(),"뒤로가기 키를 한번 더 누르면 종료됩니다.",Toast.LENGTH_SHORT).show();
            finishFlag=true;
        }
        else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("경고");
            builder.setMessage("정말로 앱을 종료하시겠습니까?");
            builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finishFlag=false;
                }
            }); //취소
            builder.setPositiveButton("종료", new DialogInterface.OnClickListener() {
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