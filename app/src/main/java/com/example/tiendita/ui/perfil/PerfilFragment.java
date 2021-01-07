package com.example.tiendita.ui.perfil;

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

public class PerfilFragment extends Fragment implements View.OnClickListener,
        FirebaseCallback<DataSnapshot>,
        DownloadCallback,
        UploadCallback<Task<Uri>>  {

    private PerfilViewModel mViewModel;
    private TextView tvNombre,tvApellido,tvPassword,tvCorreo,tvNombreNegocio;
    private Button bttnEdit,bttnSave,bttnDiscard;
    private ImageView ivImagen,ivNombreNegocioIcon;
    private NegocioModelo currentN;
    private UsuarioModelo currentU;
    private boolean esNegocio;
    public static final int REQUEST_TAKE_PHOTO=1;
    public static  String currentPath;
    public static Uri photoUri;
    private boolean imgHasChange;

    public static PerfilFragment newInstance() {
        return new PerfilFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mViewModel =
                new ViewModelProvider(this).get(PerfilViewModel.class);
        View root = inflater.inflate(R.layout.fragment_perfil, container, false);
        Bundle data = this.getArguments();
        if (data != null) {
            initComps(root);
            esNegocio=data.getBoolean(Constantes.CONST_NEGOCIO_TYPE);
        }else{
            esNegocio=false;
        }
        return root;
    }

    private void initComps(View root) {

        tvNombre = root.findViewById(R.id.tf_perfil_nombre);
        tvNombreNegocio = root.findViewById(R.id.tf_perfil_nombre_negocio);
        tvApellido = root.findViewById(R.id.tf_perfil_apellido);
        tvPassword = root.findViewById(R.id.tf_perfil_password);
        tvCorreo = root.findViewById(R.id.tf_perfil_correo);
        bttnSave = root.findViewById(R.id.bttn_save_perfil);
        bttnEdit = root.findViewById(R.id.bttn_edit_perfil);
        bttnDiscard = root.findViewById(R.id.bttn_discad_perfil);
        ivImagen =root.findViewById(R.id.iv_perfil);
        ivNombreNegocioIcon=root.findViewById(R.id.iv_perfil_nombre_negocio_icon);

        if(esNegocio){
            tvNombreNegocio.setEnabled(false);
        }else{
            tvNombreNegocio.setVisibility(View.GONE);
            ivNombreNegocioIcon.setVisibility(View.GONE);

        }
        tvCorreo.setEnabled(false);
        tvPassword.setEnabled(false);
        tvNombre.setEnabled(false);
        tvApellido.setEnabled(false);
        ivImagen.setEnabled(false);
        bttnDiscard.setVisibility(View.GONE);
        bttnSave.setVisibility(View.GONE);

        bttnEdit.setOnClickListener(this);
        bttnSave.setOnClickListener(this);
        bttnDiscard.setOnClickListener(this);
        ivImagen.setOnClickListener(this);

        imgHasChange=false;
        if(esNegocio) {
            AccionesFirebaseRTDataBase.getNegocio(AccionesFirebaseAuth.getUID(),
                    this);
        }else {
            AccionesFirebaseRTDataBase.getUser(AccionesFirebaseAuth.getUID(),
                    this);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(PerfilViewModel.class);
        // TODO: Use the ViewModel
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bttn_save_perfil:
                onSave();
                break;
            case R.id.bttn_edit_perfil:
                onEdit();
                break;
            case R.id.bttn_discad_perfil:
                onDiscard();
                break;
            case R.id.iv_perfil:
                takePhoto();
                break;

        }

    }
    //metodos  onClick
    private void onSave(){
        if(esNegocio) {
            if(imgHasChange||
                    !tvNombre.getText().toString().equals(currentN.getNombre())||
                    !tvNombreNegocio.getText().toString().equals(currentN.getNombreNegocio())||
                    !tvPassword.getText().toString().equals(currentN.getContrasenia())||
                    !tvApellido.getText().toString().equals(currentN.getApellido())) {

                currentN.setNombre(tvNombre.getText().toString());
                currentN.setNombreNegocio(tvNombreNegocio.getText().toString());
                if (!currentN.getContrasenia().equals(tvPassword.getText().toString())) {
                    currentN.setContrasenia(tvPassword.getText().toString());
                    AccionesFirebaseAuth.actualizaContrasenia(currentN.getContrasenia());
                }
                currentN.setApellido(tvApellido.getText().toString());
                if (imgHasChange) {

                    //si se cambio la imagen de usuario se actualiza la referencia remota
                    if (currentN.getRemoteImg() != null) {
                        AccionesFirebaseRTDataBase.updateLocalImgRef(currentN.getId(), currentPath, this.getContext());
                        AccionesFireStorage.updateImage(AccionesFirebaseAuth.getUID(),
                                currentN.getRemoteImg(),
                                currentPath,
                                this.getContext(),
                                this);
                    } else {
                        AccionesFirebaseRTDataBase.insertLocalImgRef(currentN.getId(), currentPath, this.getContext());
                        AccionesFireStorage.loadImage(AccionesFirebaseAuth.getUID(),
                                currentPath,
                                this.getContext(),
                                this);
                    }

                } else {
                    //si no se cambio la imagen se actualizan solo los datos de realtime
                    AccionesFirebaseRTDataBase.updateNegocio(currentN, this);
                }
            }else{
                onDiscard();
            }
        }else {
            if (imgHasChange ||
                    !tvNombre.getText().toString().equals(currentU.getNombre()) ||
                    !tvPassword.getText().toString().equals(currentU.getContrasenia()) ||
                    !tvApellido.getText().toString().equals(currentU.getApellido())) {

                currentU.setNombre(tvNombre.getText().toString());
                if (!currentU.getContrasenia().equals(tvPassword.getText().toString())) {
                    currentU.setContrasenia(tvPassword.getText().toString());
                    AccionesFirebaseAuth.actualizaContrasenia(currentU.getContrasenia());
                }
                currentU.setApellido(tvApellido.getText().toString());
                if (imgHasChange) {
                    //si se cambio la imagen de usuario se actualiza la referencia remota
                    if (currentU.getRemoteImg() != null) {
                        AccionesFirebaseRTDataBase.updateLocalImgRef(currentU.getId(), currentPath, this.getContext());
                        AccionesFireStorage.updateImage(AccionesFirebaseAuth.getUID(),
                                currentU.getRemoteImg(),
                                currentPath,
                                this.getContext(),
                                this);
                    } else {
                        AccionesFirebaseRTDataBase.insertLocalImgRef(currentU.getId(), currentPath, this.getContext());
                        AccionesFireStorage.loadImage(AccionesFirebaseAuth.getUID(),
                                currentPath,
                                this.getContext(),
                                this);
                    }

                } else {
                    //si no se cambio la imagen se actualizan solo los datos de realtime
                    AccionesFirebaseRTDataBase.updateUser(currentU, this);
                }
        }else{
            onDiscard();
        }
        }
    }
    private void onEdit(){
        bttnDiscard.setVisibility(View.VISIBLE);
        bttnSave.setVisibility(View.VISIBLE);
        bttnEdit.setVisibility(View.GONE);
        tvPassword.setEnabled(true);
        tvNombre.setEnabled(true);
        tvApellido.setEnabled(true);
        if(esNegocio) {
            tvNombreNegocio.setEnabled(true);
        }
        ivImagen.setEnabled(true);

    }
    private void onDiscard(){
        tvPassword.setEnabled(false);
        tvNombre.setEnabled(false);
        tvApellido.setEnabled(false);
        if(esNegocio) {
            tvNombreNegocio.setEnabled(false);
        }
        ivImagen.setEnabled(false);
        bttnDiscard.setVisibility(View.GONE);
        bttnSave.setVisibility(View.GONE);
        bttnEdit.setVisibility(View.VISIBLE);
        imgHasChange=false;
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
            ImageManager.loadImage(currentPath, ivImagen,this.getContext());
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
        if(esNegocio) {
            if(currentN.getRemoteImg()!=null) {
                String localRef = AccionesFirebaseRTDataBase.getLocalImgRef(currentN.getId(), getContext());
                ImageManager.loadImage(localRef, ivImagen, this.getContext());
            }
            tvCorreo.setText(currentN.getCorreo());
            tvPassword.setText(currentN.getContrasenia());
            tvNombre.setText(currentN.getNombre());
            tvApellido.setText(currentN.getApellido());
            tvNombreNegocio.setText(currentN.getNombreNegocio());
        }else{
            if(currentU.getRemoteImg()!=null) {
                String localRef = AccionesFirebaseRTDataBase.getLocalImgRef(currentU.getId(), getContext());
                ImageManager.loadImage(localRef, ivImagen, this.getContext());
            }
            tvCorreo.setText(currentU.getCorreo());
            tvPassword.setText(currentU.getContrasenia());
            tvNombre.setText(currentU.getNombre());
            tvApellido.setText(currentU.getApellido());

        }
    }

    //metodos de callback
    //carga y guardado de datos
    @Override
    public void enInicio() {

    }

    @Override
    public void enExito(DataSnapshot respuesta, int accion) {

        switch (accion){
            case AccionesFirebaseRTDataBase.GET_USER_ACCTION: {
                HashMap cliente = (HashMap) respuesta.getValue();
                currentU = new UsuarioModelo();
                currentU.setId(cliente.get(Constantes.CONST_BASE_ID).toString());
                currentU.setNombre(cliente.get(Constantes.CONST_BASE_NOMBRE).toString());
                currentU.setApellido(cliente.get(Constantes.CONST_BASE_APELLIDO).toString());
                currentU.setContrasenia(cliente.get(Constantes.CONST_BASE_CONTRASENIA).toString());
                currentU.setCorreo(cliente.get(Constantes.CONST_BASE_CORREO).toString());
                if(cliente.get(Constantes.CONST_BASE_REMOTEIMG)!=null) {
                    currentU.setRemoteImg(cliente.get(Constantes.CONST_BASE_REMOTEIMG).toString());

                    String localRef = AccionesFirebaseRTDataBase.getLocalImgRef(currentU.getId(), getContext());
                    if (localRef != null) {
                        File filePhoto = new File(localRef);
                        if (filePhoto.exists()) {
                            showData();
                        } else {
                            AccionesFireStorage.downloadImg(currentU.getRemoteImg(),
                                    this.getActivity(),
                                    this.getContext(),
                                    this,
                                    currentU.getId());
                        }
                    } else {
                        AccionesFireStorage.downloadImg(currentU.getRemoteImg(),
                                this.getActivity(),
                                this.getContext(),
                                this,
                                currentU.getId());

                    }
                }else{
                    showData();

                }
            }
            break;
            case AccionesFirebaseRTDataBase.GET_NEGOCIO_ACCTION: {
                HashMap cliente = (HashMap) respuesta.getValue();
                    currentN = new NegocioModelo();
                    currentN.setId(cliente.get(Constantes.CONST_BASE_ID).toString());
                    currentN.setNombre(cliente.get(Constantes.CONST_BASE_NOMBRE).toString());
                    currentN.setApellido(cliente.get(Constantes.CONST_BASE_APELLIDO).toString());
                    currentN.setContrasenia(cliente.get(Constantes.CONST_BASE_CONTRASENIA).toString());
                    currentN.setNombreNegocio(cliente.get(Constantes.CONST_NEGOCIO_NOMBRE).toString());
                currentN.setCorreo(cliente.get(Constantes.CONST_BASE_CORREO).toString());
                if(cliente.get(Constantes.CONST_BASE_REMOTEIMG)!=null) {
                    currentN.setRemoteImg(cliente.get(Constantes.CONST_BASE_REMOTEIMG).toString());
                    String localRef = AccionesFirebaseRTDataBase.getLocalImgRef(currentN.getId(), getContext());
                    if(localRef!=null) {
                        File filePhoto = new File(localRef);
                        if (filePhoto.exists()) {
                            showData();
                        } else {
                            AccionesFireStorage.downloadImg(currentN.getRemoteImg(),
                                    this.getActivity(),
                                    this.getContext(),
                                    this,
                                    currentN.getId());
                        }
                    } else {
                        AccionesFireStorage.downloadImg(currentN.getRemoteImg(),
                                this.getActivity(),
                                this.getContext(),
                                this,
                                currentN.getId());
                }
                }else{
                showData();
            }
            }
                break;
            case AccionesFirebaseRTDataBase.UPDATE_USER_ACCTION: {
                //si se guardaron datos del cliente
                Toast.makeText(this.getContext(), R.string.datos_actualizados, Toast.LENGTH_LONG).show();
                tvPassword.setEnabled(false);
                tvNombre.setEnabled(false);
                tvApellido.setEnabled(false);
                ivImagen.setEnabled(false);
                bttnDiscard.setVisibility(View.GONE);
                bttnSave.setVisibility(View.GONE);
                bttnEdit.setVisibility(View.VISIBLE);
                imgHasChange=false;
            }
            break;
            case AccionesFirebaseRTDataBase.UPDATE_NEGOCIO_ACCTION: {
                //si se guardaron datos del cliente
                Toast.makeText(this.getContext(), R.string.datos_actualizados, Toast.LENGTH_LONG).show();
                tvPassword.setEnabled(false);
                tvNombre.setEnabled(false);
                tvApellido.setEnabled(false);
                ivImagen.setEnabled(false);
                tvNombreNegocio.setEnabled(false);
                bttnDiscard.setVisibility(View.GONE);
                bttnSave.setVisibility(View.GONE);
                bttnEdit.setVisibility(View.VISIBLE);
                imgHasChange=false;
            }
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
        List<String> segments = respuesta.getResult().getPathSegments();
        if(esNegocio) {
            currentN.setRemoteImg(segments.get(segments.size() - 1));
            AccionesFirebaseRTDataBase.updateNegocio(currentN, this);
        }else {

            currentU.setRemoteImg(segments.get(segments.size() - 1));
            AccionesFirebaseRTDataBase.updateUser(currentU, this);

        }

    }

    @Override
    public void enFalloCar(Exception excepcion) {
        Toast.makeText(this.getContext(), R.string.error_actualiza_img,Toast.LENGTH_LONG).show();
    }

}