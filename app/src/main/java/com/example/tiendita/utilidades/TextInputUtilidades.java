package com.example.tiendita.utilidades;

import android.text.InputFilter;

import com.google.android.material.textfield.TextInputLayout;

public class TextInputUtilidades {
   public static void soloMayusculasTextInputLayout(TextInputLayout textInputLayout) {
      InputFilter[] inputFiltersAnteriores = textInputLayout.getEditText().getFilters();
      InputFilter[] inputFiltersNuevos = new InputFilter[inputFiltersAnteriores.length + 1];

      System.arraycopy(inputFiltersAnteriores, 0, inputFiltersNuevos, 0, inputFiltersAnteriores.length);
      inputFiltersNuevos[inputFiltersNuevos.length - 1] = new InputFilter.AllCaps();

      textInputLayout.getEditText().setFilters(inputFiltersNuevos);
   }
}
