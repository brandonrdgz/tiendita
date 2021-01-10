package com.example.tiendita.ui.home;

import android.app.Dialog;
import android.media.Image;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.example.tiendita.R;
import com.example.tiendita.datos.firebase.AccionesFirebaseRTDataBase;
import com.example.tiendita.utilidades.Constantes;
import static android.content.DialogInterface.BUTTON_NEGATIVE;
import static android.content.DialogInterface.BUTTON_POSITIVE;


public class HomeFragment extends Fragment implements DialogInterface.OnClickListener, View.OnClickListener{

    private HomeViewModel homeViewModel;
    private boolean esNegocio;
    private ViewFlipper vf;
    private ImageButton imgBttnLeft, imgBttnRight, imgBttnMiddle, imgBttnBottom;
    private TextView tv_cabecera_uno, tv_cabecera_dos;
    private int ImagesUsuario[] = new int[]{R.drawable.img_banner_uno, R.drawable.img_banner_dos, R.drawable.img_banner_tres};
    private int ImagesNegocio[] = new int[]{R.drawable.img_banner_cuatro, R.drawable.img_banner_dos, R.drawable.img_banner_cinco};
    private int Images[] = new int[3];

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        Bundle data = this.getArguments();
        asignarComponentes(root);
        if (data != null) {
            esNegocio = data.getBoolean(Constantes.CONST_NEGOCIO_TYPE);
        } else {
            esNegocio = false;
        }
        asignarRecursos(esNegocio);

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

    private void asignarComponentes(View root) {
        vf = root.findViewById(R.id.viewFlipper_banner);
        imgBttnLeft = root.findViewById(R.id.imgBttnLeft);
        imgBttnRight = root.findViewById(R.id.imgBttnRight);
        imgBttnMiddle = root.findViewById(R.id.imgBttnMiddle);
        imgBttnBottom = root.findViewById(R.id.imgBttnBottom);
        imgBttnLeft.setOnClickListener(this::onClick);
        imgBttnRight.setOnClickListener(this::onClick);
        imgBttnMiddle.setOnClickListener(this::onClick);
        imgBttnBottom.setOnClickListener(this::onClick);
        tv_cabecera_uno = root.findViewById(R.id.tv_cabecera_uno);
        tv_cabecera_dos = root.findViewById(R.id.tv_cabecera_dos);
    }

    private void asignarRecursos(boolean esNegocio) {
        if (esNegocio) {
            Images = ImagesNegocio;
            imgBttnLeft.setImageResource(R.drawable.img_profile_bttn);
            imgBttnRight.setImageResource(R.drawable.img_orders_bttn);
            imgBttnMiddle.setImageResource(R.drawable.img_businness_bttn);
            tv_cabecera_uno.setText("Mis productos más vendidos");
            tv_cabecera_dos.setText("Mis sucursales");
        } else {
            Images = ImagesUsuario;
            imgBttnLeft.setImageResource(R.drawable.img_stores_bttn);
            imgBttnRight.setImageResource(R.drawable.img_orders_bttn);
            imgBttnMiddle.setImageResource(R.drawable.img_profile_bttn);
            tv_cabecera_uno.setText("Productos más populares");
            tv_cabecera_dos.setText("Tiendas cercanas");
        }
        //ViewFlipper Banner superior
        for (int image : Images) {
            flipperImages(image);
        }
    }

    private void flipperImages(int image) {
        ImageView imageView = new ImageView(getContext());
        imageView.setBackgroundResource(image);
        vf.addView(imageView);
        vf.setFlipInterval(4000);
        vf.setAutoStart(true);

        vf.setInAnimation(getContext(), android.R.anim.slide_in_left);
        vf.setOutAnimation(getContext(), android.R.anim.slide_out_right);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imgBttnLeft:
                if (esNegocio) {
                    NavHostFragment.findNavController(this)
                            .navigate(R.id.action_nav_homen_to_nav_perfiln);
                  } else {
                    NavHostFragment.findNavController(this)
                            .navigate(R.id.action_nav_homeu_to_nav_mapu);
                }
                break;
            case R.id.imgBttnRight:
                if (esNegocio) {
                    NavHostFragment.findNavController(this)
                            .navigate(R.id.action_nav_homen_to_nav_pedidosn);
                   } else {
                    NavHostFragment.findNavController(this)
                            .navigate(R.id.action_nav_homeu_to_nav_pedidosu);
                     }
                break;
            case R.id.imgBttnMiddle:
                if (esNegocio) {
                    NavHostFragment.findNavController(this)
                            .navigate(R.id.action_nav_homen_to_nav_sucursales);
                     } else {
                    NavHostFragment.findNavController(this)
                            .navigate(R.id.action_nav_homeu_to_nav_perfilu);
                   }
                break;
            case R.id.imgBttnBottom:
                if (esNegocio) {
                    NavHostFragment.findNavController(this)
                            .navigate(R.id.action_nav_homen_to_nav_info);
                } else {
                    NavHostFragment.findNavController(this)
                            .navigate(R.id.action_nav_homeu_to_nav_info);
                } break;
        }
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
