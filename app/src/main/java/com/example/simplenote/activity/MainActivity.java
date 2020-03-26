package com.example.simplenote.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapCircleThumbnail;
import com.example.navigationdrawer.R;
import com.example.simplenote.fragments.MainFragment;

import com.example.simplenote.fragments.UserFragment;
import com.example.simplenote.provider.UserProvider;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawerLayout;
    private NavigationView nav_view;
    static boolean status;
    private String uid;
    private FirebaseDatabase firebaseDatabase;
    private UserProvider userProvider;
    private DatabaseReference databaseReference;
    private TextView txt_name_user_nav;
    private TextView txt_email_user_nav;
    private ImageView img_background_nav;
    private CircleImageView img_profile_nav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Componentes para ativar o navigation bar:
        //toolbar = instanciar como de costume
        Toolbar toolbar = findViewById(R.id.toolbar_main);
        //adicionar o suporte para a toolbar instanciada
        setSupportActionBar(toolbar);
        //instanciar o componente drawerLayout do tipo DrawerLayout
        drawerLayout = findViewById(R.id.drawer_layout);
        //instanciar o componente  nav_view do tipo NavigationView
        nav_view = findViewById(R.id.nav_view);
        //Para chamar e instanciar elementos de um navigation drawer é necessario atribuir a instancia a uma view e atraves desta view instanciar os componentes de design
        View view = nav_view.getHeaderView(0);
        //componentes de design da navigationdrawer, note que esta usando o findViewById atraves do objeto view do tipo View
        txt_email_user_nav = view.findViewById(R.id.txt_email_user_nav);
        txt_name_user_nav = view.findViewById(R.id.txt_name_user_nav);
        img_profile_nav = view.findViewById(R.id.img_profile_nav);
        img_background_nav = view.findViewById(R.id.img_background_nav);

        //Uasa um objeto toggle do tipo ActionBarDrawerToggle como uma instancia passando os parametro contexto, um draweerLayout, toolbar,
        //uma descrição para a operação de abertura do drawer e outra para fechamento,

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        //adiciona atraves do meetodo addDrawerListener do objeto drawerLayout o toggle criado acima
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        //metodo que recebe como parametro o contexto
        nav_view.setNavigationItemSelectedListener(this);

        firebaseDatabase = FirebaseDatabase.getInstance();
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new MainFragment())
                    .commit();

        }

        getData();
    }

    @Override
    public void onBackPressed() {

        if (getFragmentManager().getBackStackEntryCount() > 0 || drawerLayout.isDrawerOpen(GravityCompat.START)) {
            getFragmentManager().popBackStack();
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
           // this.finishAffinity();
        }
    }

    //O menu drawer usa o metodo onNavigationItemSelected e com um switch no objeto menuItem.getItemId() obtem se o menu que esta sendo pressionado e determina as funções atraves do case:
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.item_user:
                if (!UserFragment.status) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, new UserFragment())
                            .addToBackStack("Fragment").commit();
                }
                break;
            case R.id.item_logout:
                AlertDialog.Builder alerta = new AlertDialog.Builder(this);
                alerta.setMessage("Fazer logout no aplciativo?");
                alerta.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseAuth.getInstance().signOut();
                        if (!LoginActivity.status) {
                            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                });
                alerta.show();
                break;
            case R.id.item_home:
                getSupportFragmentManager().popBackStack();
               /* getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new MainFragment())
                       .commit();*/
                break;
            case R.id.item_new:
                androidx.appcompat.app.AlertDialog newNotes = new androidx.appcompat.app.AlertDialog.Builder(MainActivity.this).create();
                View dialog = getLayoutInflater().inflate(R.layout.layout_notes, null);
                newNotes.setView(dialog);
                newNotes.show();
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        status = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        status = false;
    }


    public void getData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            uid = bundle.getString("uid");
            getDataDatabase(uid);
        }
    }

    private void getDataDatabase(String uid) {
        databaseReference = firebaseDatabase.getReference("users").child(uid);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userProvider = dataSnapshot.getValue(UserProvider.class);
                txt_email_user_nav.setText(userProvider.getUserEmail());
                txt_name_user_nav.setText(userProvider.getUserName());

                Picasso.get().load(userProvider.getUserUrlPhoto()).into(img_profile_nav);
                Picasso.get().load(userProvider.getUserUrlPhoto()).into(img_background_nav);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

}
