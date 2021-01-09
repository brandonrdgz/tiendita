package com.example.tiendita.ui.home;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.tiendita.R;
import com.example.tiendita.datos.firebase.AccionesFirebaseRTDataBase;
import com.example.tiendita.utilidades.Constantes;

import static android.content.DialogInterface.BUTTON_NEGATIVE;
import static android.content.DialogInterface.BUTTON_POSITIVE;

public class HomeFragment extends Fragment implements DialogInterface.OnClickListener {

    private HomeViewModel homeViewModel;
    private boolean esNegocio;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        Bundle data = this.getArguments();
        if (data != null) {
            esNegocio=data.getBoolean(Constantes.CONST_NEGOCIO_TYPE);
        }else{
            esNegocio=false;
        }
        final TextView textView = root.findViewById(R.id.text_home);
        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }

        });
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                android.app.AlertDialog.Builder dialogo= new android.app.AlertDialog.Builder(getContext());
                dialogo.setTitle(R.string.header_salir);
                dialogo.setMessage(R.string.message_salir);
                dialogo.setPositiveButton(R.string.action_salir, HomeFragment.this);
                dialogo.setNegativeButton(R.string.action_cancelar,HomeFragment.this);
                dialogo.show();
            }
        };

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);

        return root;
    }


    @Override
    public void onClick(DialogInterface dialog, int which) {
        dialog.dismiss();
        switch (which) {
            case BUTTON_POSITIVE:
                getActivity().finishAffinity();
                break;
            case BUTTON_NEGATIVE:
                break;
        }

    }


}