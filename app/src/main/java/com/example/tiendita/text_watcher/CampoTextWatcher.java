package com.example.tiendita.text_watcher;

import android.app.Activity;
import android.text.Editable;
import android.text.TextWatcher;

import com.example.tiendita.utilidades.Validaciones;
import com.google.android.material.textfield.TextInputLayout;

public class CampoTextWatcher implements TextWatcher {
   private Activity activity;
   private TextInputLayout textInputLayout;

   public CampoTextWatcher(Activity activity, TextInputLayout textInputLayout) {
      this.activity = activity;
      this.textInputLayout = textInputLayout;
   }

   @Override
   public void beforeTextChanged(CharSequence s, int start, int count, int after) {

   }

   @Override
   public void onTextChanged(CharSequence s, int start, int before, int count) {

   }

   @Override
   public void afterTextChanged(Editable s) {
      Validaciones.validaCampo(activity, textInputLayout);
   }
}
