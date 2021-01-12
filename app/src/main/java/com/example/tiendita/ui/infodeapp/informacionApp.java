package com.example.tiendita.ui.infodeapp;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.tiendita.R;

public class informacionApp extends Fragment {

    private InformacionAppViewModel mViewModel;

    WebView miVisorWeb;
    private String url = "https://www.google.com.mx";

    public static informacionApp newInstance() {
        return new informacionApp();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_informacion_app, container, false);
        asignarWebView(root);
        return root;
    }

    private void asignarWebView(View root){
        // Definimos el webView
        miVisorWeb=(WebView)root.findViewById(R.id.webview);
        miVisorWeb.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        // Cargamos la web
        miVisorWeb.loadUrl(url);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(InformacionAppViewModel.class);
        // TODO: Use the ViewModel
    }

}