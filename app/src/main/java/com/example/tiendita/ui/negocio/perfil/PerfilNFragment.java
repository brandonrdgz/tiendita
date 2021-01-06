package com.example.tiendita.ui.negocio.perfil;

import androidx.core.content.FileProvider;
import androidx.lifecycle.ViewModelProvider;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tiendita.R;
import com.example.tiendita.datos.firebase.AccionesFireStorage;
import com.example.tiendita.datos.firebase.AccionesFirebaseAuth;
import com.example.tiendita.datos.firebase.AccionesFirebaseRTDataBase;
import com.example.tiendita.datos.firebase.DownloadCallback;
import com.example.tiendita.datos.firebase.FirebaseCallback;
import com.example.tiendita.datos.firebase.UploadCallback;
import com.example.tiendita.datos.modelos.NegocioModelo;
import com.example.tiendita.datos.modelos.UsuarioModelo;
import com.example.tiendita.ui.usuario.perfil.PerfilUViewModel;
import com.example.tiendita.utilidades.Constantes;
import com.example.tiendita.utilidades.ImageManager;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class PerfilNFragment extends Fragment implements View.OnClickListener,
        FirebaseCallback<DataSnapshot>,
        DownloadCallback,
        UploadCallback<Task<Uri>>  {

    private PerfilNViewModel mViewModel;
    private TextView tvNombre,tvApellido,tvPassword,tvCorreo,tvNombreNegocio;
    private Button bttnEdit,bttnSave,bttnDiscard;
    private ImageView ivNegocio;
    private NegocioModelo current;
    public static final int REQUEST_TAKE_PHOTO=1;
    public static  String currentPath;
    public static Uri photoUri;
    private boolean imgHasChange;

    public static PerfilNFragment newInstance() {
        return new PerfilNFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mViewModel =
                new ViewModelProvider(this).get(PerfilNViewModel.class);
        View root = inflater.inflate(R.layout.fragment_perfiln, container, false);
        initComps(root);
        return root;
    }

    private void initComps(View root) {
        tvNombre = root.findViewById(R.id.tf_perfil_nombre_negocio);
        tvNombreNegocio = root.findViewById(R.id.tf_perfil_nombre_negocio);
        tvApellido = root.findViewById(R.id.tf_perfil_apellido_negocio);
        tvPassword = root.findViewById(R.id.tf_perfil_password_negocio);
        tvCorreo = root.findViewById(R.id.tf_perfil_correo_negocio);
        bttnSave = root.findViewById(R.id.bttn_save_negocio);
        bttnEdit = root.findViewById(R.id.bttn_edit_negocio);
        bttnDiscard = root.findViewById(R.id.bttn_discad_negocio);
        ivNegocio=root.findViewById(R.id.iv_perfil_negocio);

        tvCorreo.setEnabled(false);
        tvNombreNegocio.setEnabled(false);
        tvPassword.setEnabled(false);
        tvNombre.setEnabled(false);
        tvApellido.setEnabled(false);
        ivNegocio.setEnabled(false);
        bttnDiscard.setVisibility(View.GONE);
        bttnSave.setVisibility(View.GONE);

        bttnEdit.setOnClickListener(this);
        bttnSave.setOnClickListener(this);
        bttnDiscard.setOnClickListener(this);
        ivNegocio.setOnClickListener(this);

        imgHasChange=false;

        AccionesFirebaseRTDataBase.getNegocio(AccionesFirebaseAuth.getUID(),
                this);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(PerfilNViewModel.class);
        // TODO: Use the ViewModel
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bttn_save_negocio:
                onSave();
                break;
            case R.id.bttn_edit_negocio:
                onEdit();
                break;
            case R.id.bttn_discad_negocio:
                onDiscard();
                break;
            case R.id.iv_perfil_negocio:
                takePhoto();
                break;

        }

    }
    //metodos  onClick
    private void onSave(){
        current.setNombre(tvNombre.getText().toString());
        current.setContrasenia(tvPassword.getText().toString());
        current.setApellido(tvApellido.getText().toString());
        current.setCorreo(tvCorreo.getText().toString());
        if(imgHasChange){
            //si se cambio la imagen de usuario se actualiza la referencia remota
            AccionesFireStorage.updateImage(AccionesFirebaseAuth.getUID(),
                    current.getRemoteImg(),
                    current.getLocalImg(),
                    this.getContext(),
                    this);
        }else{
            //si no se cambio la imagen se actualizan solo los datos de realtime
            AccionesFirebaseRTDataBase.updateNegocio(current,this);
        }
    }
    private void onEdit(){
        bttnDiscard.setVisibility(View.VISIBLE);
        bttnSave.setVisibility(View.VISIBLE);
        bttnEdit.setVisibility(View.GONE);
        tvCorreo.setEnabled(true);
        tvPassword.setEnabled(true);
        tvNombre.setEnabled(true);
        tvApellido.setEnabled(true);
        tvNombreNegocio.setEnabled(true);
        ivNegocio.setEnabled(true);

    }
    private void onDiscard(){
        tvCorreo.setEnabled(false);
        tvPassword.setEnabled(false);
        tvNombre.setEnabled(false);
        tvApellido.setEnabled(false);
        tvNombreNegocio.setEnabled(false);
        ivNegocio.setEnabled(false);
        bttnDiscard.setVisibility(View.GONE);
        bttnSave.setVisibility(View.GONE);
        bttnEdit.setVisibility(View.VISIBLE);
        showData();
    }
    //metodos para tomar foto
    public  void takePhoto() {
        Intent tomaFoto = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (tomaFoto.resolveActivity(this.getActivity().getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();

            } catch (IOException ex) {
                Toast.makeText(this.getContext(), R.string.no_img, Toast.LENGTH_LONG).show();
            }
            if (photoFile != null) {
                photoUri = FileProvider.getUriForFile(this.getContext(), "com.example.tiendita", photoFile);
                tomaFoto.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(tomaFoto, REQUEST_TAKE_PHOTO);
            }
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode==REQUEST_TAKE_PHOTO && resultCode== Activity.RESULT_OK){
            current.setLocalImg(currentPath);
            ImageManager.loadImage(currentPath,ivNegocio,this.getContext());
            imgHasChange=true;
        }
    }

    public File createImageFile()throws IOException {
        String timeStamp=new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFile="JPEG_"+timeStamp+"_";
        File storageDir=this.getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File ima=File.createTempFile(imageFile,".jpg",storageDir);
        currentPath=ima.getAbsolutePath();
        return ima;
    }

    //metodo para desplegar los datos
    private void showData() {
        ImageManager.loadImage(current.getLocalImg(),ivNegocio,this.getContext());
        tvCorreo.setText(current.getCorreo());
        tvPassword.setText(current.getContrasenia());
        tvNombre.setText(current.getNombre());
        tvApellido.setText(current.getApellido());
        tvNombreNegocio.setText(current.getNombreNegocio());
    }

    //metodos de callback
    //carga y guardado de datos
    @Override
    public void enInicio() {

    }

    @Override
    public void enExito(DataSnapshot respuesta, int accion) {
        switch (accion){
            case AccionesFirebaseRTDataBase.GET_NEGOCIO_ACCTION:
                HashMap cliente = (HashMap) respuesta.getValue();
                current = new NegocioModelo();
                current.setId(cliente.get(Constantes.CONST_BASE_ID).toString());
                current.setNombre(cliente.get(Constantes.CONST_BASE_NOMBRE).toString());
                current.setApellido(cliente.get(Constantes.CONST_BASE_APELLIDO).toString());
                current.setContrasenia(cliente.get(Constantes.CONST_BASE_CONTRASENIA).toString());
                current.setLocalImg(cliente.get(Constantes.CONST_BASE_LOCALIMG).toString());
                current.setRemoteImg(cliente.get(Constantes.CONST_BASE_REMOTEIMG).toString());
                current.setNombreNegocio(cliente.get(Constantes.CONST_NEGOCIO_NOMBRE).toString());

                File filePhoto = new File(current.getLocalImg());
                if (filePhoto.exists()) {
                    showData();
                } else {
                    AccionesFireStorage.downloadImg(current.getRemoteImg(),
                            this.getActivity(),
                            this.getContext(),
                            this,
                            current.getId(),
                            Constantes.UPDATE_LOCALIMG_CLIENTE);
                }
                break;
            case AccionesFirebaseRTDataBase.UPDATE_NEGOCIO_ACCTION:
                //si se guardaron datos del cliente
                Toast.makeText(this.getContext(), R.string.datos_actualizados, Toast.LENGTH_LONG).show();
                tvCorreo.setEnabled(false);
                tvPassword.setEnabled(false);
                tvNombre.setEnabled(false);
                tvApellido.setEnabled(false);
                ivNegocio.setEnabled(false);
                tvNombreNegocio.setEnabled(false);
                bttnDiscard.setVisibility(View.GONE);
                bttnSave.setVisibility(View.GONE);
                bttnEdit.setVisibility(View.VISIBLE);
                break;
        }

    }



    @Override
    public void enFallo(Exception excepcion) {
        Toast.makeText(this.getContext(), R.string.error_datos, Toast.LENGTH_LONG).show();
    }
    //descarga de imagen remota
    @Override
    public void enInicioDesc() {

    }

    @Override
    public void enExitoDesc(Object respuesta, File localFile) {
        //se guarda la nueva direccion local
        //y se muestran los datos
        current.setLocalImg(localFile.getAbsolutePath());
        showData();
    }

    @Override
    public void enFalloDesc(Exception excepcion) {
        Toast.makeText(this.getContext(), R.string.error_cargar_img,Toast.LENGTH_LONG).show();
        Log.d("Descargar imagen","Error al descargar imagen\n Causa: "+excepcion.getCause());
    }
    //carga nueva imagen a firestore

    @Override
    public void enInicioCar() {
        Toast.makeText(this.getContext(), R.string.actualizando_img,Toast.LENGTH_LONG).show();
    }

    @Override
    public void enExitoCar(Task<Uri> respuesta) {
        //se actualiza imagen remota y se actualiza el usuario
        List<String> segments=respuesta.getResult().getPathSegments();
        current.setRemoteImg( segments.get(segments.size()-1));
        AccionesFirebaseRTDataBase.updateNegocio(current,this);

    }

    @Override
    public void enFalloCar(Exception excepcion) {
        Toast.makeText(this.getContext(), R.string.error_actualiza_img,Toast.LENGTH_LONG).show();
    }

}