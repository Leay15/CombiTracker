package com.combitracker;

import android.net.Uri;
import android.support.v4.app.Fragment;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.combitracker.Fragments.AgregarCombi;
import com.combitracker.Fragments.AgregarRuta;
import com.combitracker.Fragments.addElement;
import com.combitracker.Fragments.addRuta;
import com.combitracker.Objetos.Combi;
import com.combitracker.Objetos.cooki;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        addElement.OnFragmentInteractionListener,
        addRuta.OnFragmentInteractionListener,
        AgregarCombi.OnFragmentInteractionListener,
        AgregarRuta.OnFragmentInteractionListener{


    private Menu menuNavigation;
    //Referencias de firebase


    private cooki sesion;
    private Fragment fragment=null;


    private boolean op;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Toolbar toolbar = findViewById(R.id.toolbar);


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        menuNavigation = navigationView.getMenu();



        fragment=null;
        fragment= new addElement();
        abrirFragment(fragment);


    }



    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();



        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        fragment=null;


        if(item.getTitle().toString().equalsIgnoreCase("Rutas")){
            fragment= new addRuta();

        }else{
            fragment= new addElement();

        }


        abrirFragment(fragment);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void cerrarFragmrnt() {

        FragmentTransaction ft = this.getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.contenedor,new addElement());
        ft.commit();
    }

    @Override
    public void agregarCombi() {

        FragmentTransaction ft = this.getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.contenedor,new AgregarCombi()).addToBackStack(addElement.class.getName());
        ft.commit();
    }

    @Override
    public void modificarCombi(Combi aux) {
        fragment=new AgregarCombi();
        Bundle datos= new Bundle();
        datos.putString("number",aux.getNumero());
        datos.putString("user",aux.getUsuario());
        datos.putString("pass",aux.getContraseña());
        datos.putString("route",aux.getRutaAsignada());
        FragmentTransaction ft = this.getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.contenedor,fragment).addToBackStack(addElement.class.getName());
        ft.commit();
        fragment.setArguments(datos);
    }


    @Override
    public void agregarRuta() {

        FragmentTransaction ft = this.getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.contenedor,new AgregarRuta()).addToBackStack(addRuta.class.getName());
        ft.commit();
    }

    public  void abrirFragment(Fragment fragment){


        if(fragment!=null){
            FragmentTransaction ft = this.getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.contenedor,fragment);
            ft.commit();
        }

    }
}
