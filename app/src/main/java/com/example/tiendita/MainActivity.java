package com.example.tiendita;

import android.os.Bundle;
import android.view.Menu;

import com.example.tiendita.utilidades.Constantes;
import com.google.android.material.navigation.NavigationView;

import androidx.navigation.NavController;
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
            navigationView.inflateMenu(R.menu.negocio_drawer);
            mAppBarConfiguration = new AppBarConfiguration.Builder(
                    R.id.nav_homen, R.id.nav_perfiln, R.id.nav_perdidosn,R.id.nav_pedidon)
                    .setDrawerLayout(drawer)
                    .build();
            NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
            NavController navController = navHostFragment.getNavController();
            navController.getGraph().setStartDestination(R.id.nav_homen);
            NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
            NavigationUI.setupWithNavController(navigationView, navController);
        }else{
            navigationView.inflateMenu(R.menu.usuario_drawer);
            mAppBarConfiguration = new AppBarConfiguration.Builder(
                    R.id.nav_homeu, R.id.nav_perfilu, R.id.nav_perdidosu,R.id.nav_pedidou,R.id.nav_mapu)
                    .setDrawerLayout(drawer)
                    .build();
            NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
            NavController navController = navHostFragment.getNavController();
            navController.getGraph().setStartDestination(R.id.nav_homeu);
            NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
            NavigationUI.setupWithNavController(navigationView, navController);
        }

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