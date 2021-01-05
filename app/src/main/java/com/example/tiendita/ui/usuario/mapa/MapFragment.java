package com.example.tiendita.ui.usuario.mapa;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
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
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.tiendita.R;
import com.example.tiendita.datos.firebase.AccionesFireStorage;
import com.example.tiendita.datos.firebase.AccionesFirebaseRTDataBase;
import com.example.tiendita.datos.firebase.DowloadCallback;
import com.example.tiendita.datos.firebase.FirebaseCallback;
import com.example.tiendita.datos.modelos.SucursalModelo;
import com.example.tiendita.utilidades.Constantes;
import com.example.tiendita.utilidades.ImageManager;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.storage.FileDownloadTask;

import java.io.File;
import java.util.ArrayList;

public class MapFragment extends Fragment implements OnMapReadyCallback,
                                                    GoogleMap.OnMarkerClickListener,
                                                    FirebaseCallback<DataSnapshot>,
                                                    DowloadCallback<Task<FileDownloadTask.TaskSnapshot>>,
                                                    View.OnClickListener,
                                                    GoogleMap.OnMarkerDragListener
{

    private MapViewModel mapViewModel;
    private Location currentLocation;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private ArrayList<SucursalModelo> list;
    private SucursalModelo sucursalModelo;
    private LatLng latLng;
    private Boolean esNegocio;
    private TextView tvLatitud,tvLongitud;
    private Button bttnAceptar;
    private static final int REQUEST_CODE = 101;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mapViewModel =
                new ViewModelProvider(this).get(MapViewModel.class);
        View root = inflater.inflate(R.layout.fragment_map, container, false);

        Bundle data = this.getArguments();
        if (data != null) {
            initComps(root);
            esNegocio=data.getBoolean(Constantes.CONST_MAPA_TYPE);
        }else{
            esNegocio=false;
        }

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this.getActivity());
        fetchLastLocation();

        return root;
    }

    private void initComps(View root) {
        tvLatitud=root.findViewById(R.id.text_latitud);
        tvLongitud=root.findViewById(R.id.text_longitud);
        bttnAceptar=root.findViewById(R.id.bttn_aceptar_localizacion);
        bttnAceptar.setOnClickListener(this);
    }


    private void fetchLastLocation() {
        if (ActivityCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this.getActivity(),new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            return;
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if(location!=null){
                    currentLocation= location;
                    SupportMapFragment supportMapFragment=(SupportMapFragment)
                            getActivity().getSupportFragmentManager().findFragmentById(R.id.google_map);
                    supportMapFragment.getMapAsync(MapFragment.this::onMapReady);


                }
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        latLng= new LatLng(19.265172, -99.634658);
        googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
        if(esNegocio){
            negocioConfig(googleMap);
        }else{
            clientConfig(googleMap);
        }
    }
    private void negocioConfig(GoogleMap googleMap){
        Marker marker = googleMap.addMarker(new MarkerOptions().position(latLng).title("Localizacion").draggable(true));
        googleMap.setOnMarkerDragListener(this);
    }
    private void clientConfig(GoogleMap googleMap){
        list= new ArrayList<>();
        AccionesFirebaseRTDataBase.getNearSucursales(this);

        for (SucursalModelo sucursal: list) {
            LatLng latLng = new LatLng(sucursal.getLatitud()
                    ,sucursal.getLongitud());
            Marker marker = googleMap.addMarker(new MarkerOptions().position(latLng).title(sucursal.getNombre()));
            marker.setTag(sucursal);
        }

        googleMap.setOnMarkerClickListener(this);

    }



    @Override
    public void enInicio() {

    }



    @Override
    public void enExito(DataSnapshot respuesta) {
        for (DataSnapshot dataSnapshot : respuesta.getChildren()) {
            if(isInRange(Double.parseDouble(dataSnapshot.child(Constantes.CONST_SUCURSAL_LAT).getValue().toString()),
            Double.parseDouble(dataSnapshot.child(Constantes.CONST_SUCURSAL_LONG).getValue().toString()))) {
                SucursalModelo sucursalModelo = new SucursalModelo();
                sucursalModelo.setNombre(dataSnapshot.child(Constantes.CONST_SUCURSAL_NOMBRE).getValue().toString());
                sucursalModelo.setDireccion(dataSnapshot.child(Constantes.CONST_SUCURSAL_DIRECCION).getValue().toString());
                sucursalModelo.setHoraAper(dataSnapshot.child(Constantes.CONST_SUCURSAL_HORAAPER).getValue().toString());
                sucursalModelo.setHoraCierre(dataSnapshot.child(Constantes.CONST_SUCURSAL_HORACIERRE).getValue().toString());
                sucursalModelo.setSucursalID(dataSnapshot.child(Constantes.CONST_SUCURSAL_ID).getValue().toString());
                sucursalModelo.setNegocioID(dataSnapshot.child(Constantes.CONST_NEGOCIO_ID).getValue().toString());
                sucursalModelo.setLocalImg(dataSnapshot.child(Constantes.CONST_SUCURSAL_LOCALIMG).getValue().toString());
                sucursalModelo.setRemoteImg(dataSnapshot.child(Constantes.CONST_SUCURSAL_REMOTEIMG).getValue().toString());
                sucursalModelo.setLatitud(Double.parseDouble(dataSnapshot.child(Constantes.CONST_SUCURSAL_LAT).getValue().toString()));
                sucursalModelo.setLongitud(Double.parseDouble(dataSnapshot.child(Constantes.CONST_SUCURSAL_LONG).getValue().toString()));
                list.add(sucursalModelo);
            }

        }
    }

    private boolean isInRange(double lat, double lon) {
        double distancia= 6371*Math.acos(
                Math.sin(Math.toRadians(lat))
                *Math.sin(Math.toRadians(latLng.latitude))
                +Math.cos(Math.toRadians(lon-latLng.longitude))
                *Math.cos(Math.toRadians(lat))
                *Math.cos(Math.toRadians(latLng.latitude)));
        return distancia<1.0;
    }


    @Override
    public void enFallo(Exception excepcion) {
        Toast.makeText(this.getContext(), R.string.sin_sucursales, Toast.LENGTH_LONG).show();

    }






    @Override
    public void enInicioDesc() {

    }

    @Override
    public void enExitoDesc(Task<FileDownloadTask.TaskSnapshot> respuesta,File localFile) {
        sucursalModelo.setLocalImg(localFile.getAbsolutePath());
        showDialog(sucursalModelo);
    }

    @Override
    public void enFalloDesc(Exception excepcion) {
        Toast.makeText(this.getContext(), R.string.error_cargar_img,Toast.LENGTH_LONG).show();
        Log.d("Descargar imagen","Error al descargar imagen\n Causa: "+excepcion.getCause());
    }

    @Override
    public boolean onMarkerClick(final Marker marker) {
           sucursalModelo=(SucursalModelo)marker.getTag();
            File filePhoto= new File(sucursalModelo.getLocalImg());
            if(filePhoto.exists()) {
                showDialog(sucursalModelo);
            }else{
                AccionesFireStorage.downloadImg(sucursalModelo.getRemoteImg(),
                        this.getActivity(),
                        this.getContext(),
                        this,
                        sucursalModelo.getSucursalID(),
                        Constantes.UPDATE_LOCALIMG_SUCURSAL);
            }
        return false;
    }

    private void showDialog(final SucursalModelo sucursalModelo){
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.marker_layout,null);
        ((TextView) dialogView.findViewById(R.id.info_tiendita)).setText(sucursalModelo.getNombre()+"\nDireccion:"+sucursalModelo.getDireccion());
        ImageView imagen = dialogView.findViewById(R.id.foto_tiendita);
        ImageManager.loadImage(sucursalModelo.getLocalImg(),imagen,this.getContext());
        AlertDialog.Builder dialogo= new AlertDialog.Builder(getContext());
        dialogo.setTitle(R.string.header_tiendita);
        dialogo.setView(dialogView);
        dialogo.setPositiveButton(R.string.action_ir, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //redirect sucursal view
                /*
                    Bundle data = new Bundle();
                    data.putString("id",id);
                    NavHostFragment.findNavController(this)
                                    .navigate(R.id.action_nav_listar_to_nav_editar, data);
                */
            }
        });
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

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        tvLatitud.setText(marker.getPosition().latitude+"");
        tvLongitud.setText(marker.getPosition().longitude+"");
    }
}