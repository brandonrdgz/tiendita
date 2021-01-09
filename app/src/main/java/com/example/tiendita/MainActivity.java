package com.example.tiendita;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.tiendita.datos.firebase.AccionesFirebaseAuth;
import com.example.tiendita.utilidades.Constantes;
import com.google.android.material.navigation.NavigationView;

import androidx.navigation.NavController;
import androidx.navigation.NavGraph;
import androidx.navigation.NavInflater;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private boolean esNegocio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        esNegocio = getIntent().getBooleanExtra(Constantes.CONST_NEGOCIO_TYPE, false);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        if(esNegocio) {
            navigationView.getMenu().removeGroup(R.id.drawer_usuario);
            mAppBarConfiguration = new AppBarConfiguration.Builder(
                    R.id.nav_homen, R.id.nav_perfiln, R.id.nav_pedidosn,R.id.nav_pedidon, R.id.nav_infon,
                    R.id.nav_sucursales,R.id.nav_detalle_sucursaln,R.id.nav_editar_productos,R.id.nav_mapn)
                    .setDrawerLayout(drawer)
                    .build();
            NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
            NavInflater inflater= navHostFragment.getNavController().getNavInflater();
            NavGraph graph= inflater.inflate(R.navigation.mobile_navigation);
            graph.setStartDestination(R.id.nav_homen);
            navHostFragment.getNavController().setGraph(graph);
            NavController navController = navHostFragment.getNavController();
            NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
            NavigationUI.setupWithNavController(navigationView, navController);
        }else{
            navigationView.getMenu().removeGroup(R.id.drawer_negocio);
            mAppBarConfiguration = new AppBarConfiguration.Builder(
                    R.id.nav_homeu, R.id.nav_perfilu, R.id.nav_pedidosu, R.id.nav_infou,R.id.nav_editpedido,
                    R.id.nav_pedidou,R.id.nav_detalle_sucursalu,R.id.nav_mapu)
                    .setDrawerLayout(drawer)
                    .build();
            NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
            NavInflater inflater= navHostFragment.getNavController().getNavInflater();
            NavGraph graph= inflater.inflate(R.navigation.mobile_navigation);
            graph.setStartDestination(R.id.nav_homeu);
            navHostFragment.getNavController().setGraph(graph);
            NavController navController = navHostFragment.getNavController();
            NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
            NavigationUI.setupWithNavController(navigationView, navController);
        }

    }
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_logout: {
                AccionesFirebaseAuth.cerrarSesion(this);
            }
            break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}