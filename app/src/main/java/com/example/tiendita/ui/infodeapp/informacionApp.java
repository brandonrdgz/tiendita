package com.example.tiendita.ui.infodeapp;

import androidx.cardview.widget.CardView;
import androidx.lifecycle.ViewModelProvider;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.transition.AutoTransition;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.VideoView;

import com.example.tiendita.R;

import static javax.xml.datatype.DatatypeConstants.DURATION;

public class informacionApp extends Fragment {

    private InformacionAppViewModel mViewModel;

    WebView miVisorWeb;
    CardView cardView, cardViewWeb;
    ImageButton button, buttonWeb;
    LinearLayout linearLayoutDetails, linearLayoutDetailsWeb;
    private String url = "https://www.google.com.mx";

    public static informacionApp newInstance() {
        return new informacionApp();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_informacion_app, container, false);
        asignarWebView(root);
        Uri uri = Uri.parse("android.resource://com.example.tiendita/"+R.raw.video); //Declare your url here.

        VideoView mVideoView  = (VideoView) root.findViewById(R.id.videoview);
        mVideoView.setVideoURI(uri);
        cardView = root.findViewById(R.id.card);
        cardViewWeb = root.findViewById(R.id.cardWeb);
        linearLayoutDetails = root.findViewById(R.id.linearLayoutDetails);
        linearLayoutDetailsWeb = root.findViewById(R.id.linearLayoutDetailsWeb);
        button = root.findViewById(R.id.imageViewExpand);
        buttonWeb = root.findViewById(R.id.imageViewExpandWeb);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Transition transition = new AutoTransition();
                transition.setDuration(1);
                TransitionManager.beginDelayedTransition(cardView, transition);
                linearLayoutDetails.setVisibility(linearLayoutDetails.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
                if(linearLayoutDetails.getVisibility() == View.GONE){
                    mVideoView.pause();
                    button.setImageResource(R.drawable.ic_baseline_keyboard_arrow_down_24);
                }else{
                    mVideoView.setMediaController(new MediaController(getContext()));
                    mVideoView.start();
                    button.setImageResource(R.drawable.ic_baseline_keyboard_arrow_up_24);
                }
            }
        });
        buttonWeb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Transition transition = new AutoTransition();
                transition.setDuration(1);
                TransitionManager.beginDelayedTransition(cardViewWeb, transition);
                linearLayoutDetailsWeb.setVisibility(linearLayoutDetailsWeb.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
                if(linearLayoutDetailsWeb.getVisibility() == View.GONE){
                    buttonWeb.setImageResource(R.drawable.ic_baseline_keyboard_arrow_down_24);
                }else{
                    buttonWeb.setImageResource(R.drawable.ic_baseline_keyboard_arrow_up_24);
                }
            }
        });
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