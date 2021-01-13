package com.example.tiendita.ui.mapa;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.tiendita.R;
import com.example.tiendita.datos.firebase.AccionesFireStorage;
import com.example.tiendita.datos.firebase.AccionesFirebaseRTDataBase;
import com.example.tiendita.datos.firebase.DownloadCallback;
import com.example.tiendita.datos.firebase.FirebaseCallback;
import com.example.tiendita.datos.modelos.SucursalModelo;
import com.example.tiendita.utilidades.Constantes;
import com.example.tiendita.utilidades.Dialogo;
import com.example.tiendita.utilidades.ImageManager;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.storage.FileDownloadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class MapFragment extends Fragment implements OnMapReadyCallback,
   GoogleMap.OnMarkerClickListener,
   GoogleMap.OnMyLocationButtonClickListener,
   FirebaseCallback<DataSnapshot>,
   DownloadCallback<Task<FileDownloadTask.TaskSnapshot>>,
   View.OnClickListener,
   GoogleMap.OnMarkerDragListener,
   DialogInterface.OnClickListener {

    private ArrayList<SucursalModelo> list;
    private SucursalModelo sucursalModelo;
    private GoogleMap googleMap;
    private LatLng latLng;
    private Boolean esNegocio;
    private TextView tvLatitud, tvLongitud;
    private Button bttnAceptar;
    private static final int REQUEST_CODE = 101;
    private AlertDialog alertDialog;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_map, container, false);

        Bundle data = this.getArguments();
        if (data != null) {

            esNegocio = data.getBoolean(Constantes.CONST_NEGOCIO_TYPE);
            initComps(root);
        }
        else {
            esNegocio = false;
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.google_map);
        mapFragment.getMapAsync(this);

        return root;
    }

    private void initComps(View root) {
        tvLatitud = root.findViewById(R.id.text_latitud);
        tvLongitud = root.findViewById(R.id.text_longitud);
        bttnAceptar = root.findViewById(R.id.bttn_aceptar_localizacion);
        if (esNegocio) {
            bttnAceptar.setOnClickListener(this);
        }
        else {
            tvLatitud.setVisibility(View.GONE);
            tvLongitud.setVisibility(View.GONE);
            bttnAceptar.setVisibility(View.GONE);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        this.googleMap.setOnMyLocationButtonClickListener(this);
        habilitarLocalizacionDispositivo();
    }

    private void habilitarLocalizacionDispositivo() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            if (this.googleMap != null) {
                this.googleMap.setMyLocationEnabled(true);

                LocationManager locationManager = (LocationManager) this.getActivity()
                   .getSystemService(Context.LOCATION_SERVICE);
                Criteria criteria = new Criteria();
                Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));

                if (location != null) {
                    this.latLng = new LatLng(location.getLatitude(), location.getLongitude());
                }
                else {
                    Snackbar.make(this.getView(), "Active su ubicación", Snackbar.LENGTH_LONG).show();
                    this.latLng = new LatLng(Math.random(), Math.random());
                }

                this.googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12));

                if (esNegocio) {
                    negocioConfig(googleMap);
                }
                else{
                    clientConfig(googleMap);
                }
            }
        }
        else {
            ActivityCompat.requestPermissions(this.getActivity(), new String[]{
               Manifest.permission.ACCESS_FINE_LOCATION
            }, REQUEST_CODE);
        }
    }

    @Override
    public boolean onMyLocationButtonClick() {
        this.latLng = this.googleMap.getCameraPosition().target;
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode != REQUEST_CODE) {
            return;
        }

        habilitarLocalizacionDispositivo();
    }

    private void negocioConfig(GoogleMap googleMap){
        Marker marker = googleMap.addMarker(new MarkerOptions().position(latLng).title("Localizacion").draggable(true));
        googleMap.setOnMarkerDragListener(this);
    }

    private void clientConfig(GoogleMap googleMap){
        this.list= new ArrayList<>();
        this.googleMap=googleMap;
        AccionesFirebaseRTDataBase.getNearSucursales(this);
    }

    @Override
    public void enInicio() {
        alertDialog = Dialogo.dialogoProceso(getContext(), R.string.msj_cargando_tiendas);
        Dialogo.muestraDialogoProceso(alertDialog);
    }

    @Override
    public void enExito(DataSnapshot respuesta, int accion) {
        for (DataSnapshot dataSnapshot : respuesta.getChildren()) {
            for(DataSnapshot datas:dataSnapshot.getChildren()){
            HashMap<String, String> data = (HashMap<String, String>) datas.getValue();
            String lat = String.valueOf(data.get(Constantes.CONST_SUCURSAL_LAT));
            String lon = String.valueOf(data.get(Constantes.CONST_SUCURSAL_LONG));
            if(isInRange(Double.parseDouble(lat), Double.parseDouble(lon))) {
                SucursalModelo sucursalModelo = new SucursalModelo();
                sucursalModelo.setNombre(data.get(Constantes.CONST_SUCURSAL_NOMBRE));
                sucursalModelo.setDireccion(data.get(Constantes.CONST_SUCURSAL_DIRECCION));
                sucursalModelo.setHoraAper(data.get(Constantes.CONST_SUCURSAL_HORAAPER));
                sucursalModelo.setHoraCierre(data.get(Constantes.CONST_SUCURSAL_HORACIERRE));
                sucursalModelo.setSucursalID(data.get(Constantes.CONST_SUCURSAL_ID));
                sucursalModelo.setNegocioID(data.get(Constantes.CONST_NEGOCIO_ID));
                sucursalModelo.setRemoteImg(data.get(Constantes.CONST_BASE_REMOTEIMG));
                sucursalModelo.setLatitud(Double.parseDouble(lat));
                sucursalModelo.setLongitud(Double.parseDouble(lon));
                list.add(sucursalModelo);
            }


            }
        }

        for (SucursalModelo sucursal: list) {
            LatLng latLng = new LatLng(sucursal.getLatitud()
                    ,sucursal.getLongitud());
            Marker marker = this.googleMap.addMarker(new MarkerOptions().position(latLng).title(sucursal.getNombre()));
            marker.setTag(sucursal);
        }

        Dialogo.ocultaDialogoProceso(alertDialog);
        this.googleMap.setOnMarkerClickListener(this);
    }

    private boolean isInRange(double lat, double lon) {
        double distancia= 6371*Math.acos(
                Math.sin(Math.toRadians(lat))
                *Math.sin(Math.toRadians(latLng.latitude))
                +Math.cos(Math.toRadians(lon-latLng.longitude))
                *Math.cos(Math.toRadians(lat))
                *Math.cos(Math.toRadians(latLng.latitude)));
        return distancia<20.0;
    }

    @Override
    public void enFallo(Exception excepcion) {
        Dialogo.ocultaDialogoProceso(alertDialog);
        Toast.makeText(this.getContext(), R.string.sin_sucursales, Toast.LENGTH_LONG).show();
    }

    @Override
    public void enInicioDesc() {
        alertDialog = Dialogo.dialogoProceso(getContext(), R.string.msj_descargando_datos_tienda);
        Dialogo.muestraDialogoProceso(alertDialog);
    }

    @Override
    public void enExitoDesc(Task<FileDownloadTask.TaskSnapshot> respuesta,File localFile) {
        Dialogo.ocultaDialogoProceso(alertDialog);
        showDialog(sucursalModelo);
    }

    @Override
    public void enFalloDesc(Exception excepcion) {
        Dialogo.ocultaDialogoProceso(alertDialog);
        Toast.makeText(this.getContext(), R.string.error_cargar_img,Toast.LENGTH_LONG).show();
        Log.d("Descargar imagen","Error al descargar imagen\n Causa: "+excepcion.getCause());
    }

    @Override
    public boolean onMarkerClick(final Marker marker) {
           sucursalModelo=(SucursalModelo)marker.getTag();
        String localRef=AccionesFirebaseRTDataBase.getLocalImgRef(sucursalModelo.getSucursalID(),getContext());
        if(localRef!=null) {
            File filePhoto = new File(localRef);
            if (filePhoto.exists()) {
                showDialog(sucursalModelo);
            } else {
                AccionesFireStorage.downloadImg(sucursalModelo.getRemoteImg(),
                        this.getActivity(),
                        this.getContext(),
                        this,
                        sucursalModelo.getSucursalID());
            }
        }else{
            AccionesFireStorage.downloadImg(sucursalModelo.getRemoteImg(),
                    this.getActivity(),
                    this.getContext(),
                    this,
                    sucursalModelo.getSucursalID());
        }
        return false;
    }

    private void showDialog(final SucursalModelo sucursalModelo){
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.marker_layout,null);
        ((TextView) dialogView.findViewById(R.id.info_tiendita)).setText(sucursalModelo.getNombre()+"\nDireccion:"+sucursalModelo.getDireccion()+"\nHorario:"+sucursalModelo.getHoraAper()+"-"+sucursalModelo.getHoraCierre());
        ImageView imagen = dialogView.findViewById(R.id.foto_tiendita);
        String localRef=AccionesFirebaseRTDataBase.getLocalImgRef(sucursalModelo.getSucursalID(),getContext());
        ImageManager.loadImage(localRef,imagen,this.getContext());
        AlertDialog.Builder dialogo= new AlertDialog.Builder(getContext());
        dialogo.setTitle(R.string.header_tiendita);
        dialogo.setView(dialogView);
        dialogo.setPositiveButton(R.string.action_ir, this);
        dialogo.show();
    }

    @Override
    public void onClick(View v) {
        //redirect creacion de sucursal view
               /*
                    Bundle data = new Bundle();
                    data.putString(Constantes.CONST_SUCURSAL_LAT,tvLatitud.getText().toString());
                    data.putString(Constantes.CONST_SUCURSAL_LONG,tvLongitud.getText().toString());
                    NavHostFragment.findNavController(this)
                                    .navigate(R.id.action_nav_listar_to_nav_editar, data);
                                                    */
    }

    @Override
    public void onMarkerDragStart(Marker marker) {
        tvLatitud.setVisibility(View.GONE);
        tvLongitud.setVisibility(View.GONE);
        bttnAceptar.setVisibility(View.GONE);

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        tvLatitud.setVisibility(View.VISIBLE);
        tvLongitud.setVisibility(View.VISIBLE);
        bttnAceptar.setVisibility(View.VISIBLE);

        tvLatitud.setText(marker.getPosition().latitude+"");
        tvLongitud.setText(marker.getPosition().longitude+"");
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        Bundle data = new Bundle();
        data.putBoolean(Constantes.CONST_EDICION_TYPE,false);
        data.putString(Constantes.CONST_SUCURSAL_ID,sucursalModelo.getSucursalID());
        data.putParcelable(Constantes.LLAVE_SUCURSAL, sucursalModelo);

        NavHostFragment.findNavController(this)
                .navigate(R.id.action_nav_mapu_to_nav_detalle_sucursalu, data);
    }
}