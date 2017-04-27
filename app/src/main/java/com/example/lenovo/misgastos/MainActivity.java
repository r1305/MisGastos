package com.example.lenovo.misgastos;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.example.lenovo.misgastos.Utils.SessionManager;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


    SessionManager session;

    /****************NAVIGATION*************/
    DrawerLayout dl;
    Toolbar toolbar;
    NavigationView nav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /***************NAVIGATION*********************/
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        nav = (NavigationView) findViewById(R.id.navigation);
        dl = (DrawerLayout) findViewById(R.id.drawer_layout);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        toolbar.animate();
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dl.openDrawer(GravityCompat.START);
                if (dl.isDrawerOpen(GravityCompat.START)) {
                    dl.closeDrawers();
                }
            }
        });
        toolbar.setTitleTextColor(getResources().getColor(R.color.drawer));
        toolbar.setSubtitleTextColor(getResources().getColor(R.color.drawer));
        toolbar.setNavigationIcon(R.drawable.gastos_logo);

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, dl, toolbar, R.string.openDrawer,
                R.string.closeDrawer) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };
        dl.setScrimColor(getResources().getColor(R.color.drawer));

        dl.setDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        nav.setNavigationItemSelectedListener(this);
        session = new SessionManager(this);

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        Fragment newFragment;
        newFragment = new ListaFragment().newInstance();

        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack
        transaction.replace(R.id.content, newFragment);
        transaction.addToBackStack(null);

        // Commit the transaction
        transaction.commit();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        Fragment newFragment;
        switch (item.getItemId()) {
            case R.id.navigation_home:
                    /*updateNavigationBarState(R.id.navigation_home);*/
                newFragment = new ListaFragment().newInstance();

                // Replace whatever is in the fragment_container view with this fragment,
                // and add the transaction to the back stack
                transaction.replace(R.id.content, newFragment);
                transaction.addToBackStack(null);

                // Commit the transaction
                transaction.commit();
                dl.closeDrawers();
                return true;
            case R.id.navigation_dashboard:
                    /*updateNavigationBarState(R.id.navigation_dashboard);*/
                newFragment = new DashBoardFragment().newInstance();

                // Replace whatever is in the fragment_container view with this fragment,
                // and add the transaction to the back stack
                transaction.replace(R.id.content, newFragment);
                transaction.addToBackStack(null);

                // Commit the transaction
                transaction.commit();
                dl.closeDrawers();
                return true;
            case R.id.navigation_gastos:
                    /*updateNavigationBarState(R.id.navigation_notifications);*/
                newFragment = new RegisterFragment().newInstance();
                // Replace whatever is in the fragment_container view with this fragment,
                // and add the transaction to the back stack
                transaction.replace(R.id.content, newFragment);
                transaction.addToBackStack(null);

                // Commit the transaction
                transaction.commit();
                dl.closeDrawers();
                return true;
            case R.id.navigation_ingresos:
                    /*updateNavigationBarState(R.id.navigation_web_view);*/
                newFragment = new RegistrarIngresosFragment().newInstance();
                // Replace whatever is in the fragment_container view with this fragment,
                // and add the transaction to the back stack
                transaction.replace(R.id.content, newFragment);
                transaction.addToBackStack(null);

                // Commit the transaction
                transaction.commit();
                dl.closeDrawers();
                return true;
        }
        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}
