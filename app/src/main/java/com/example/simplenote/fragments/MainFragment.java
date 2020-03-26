package com.example.simplenote.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.beardedhen.androidbootstrap.BootstrapEditText;
import com.example.navigationdrawer.R;

import com.example.simplenote.adapter.AdapterFragment;
import com.example.simplenote.helper.MaskEditText;
import com.example.simplenote.provider.CommitmentProvider;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainFragment extends Fragment {
    public static boolean status;

    private TabLayout tab_notes;
    private ViewPager view_notes;
    private AdapterFragment adapterFragment;
    private FloatingActionButton fb_add;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private BootstrapEditText edt_title_commitment;
    private BootstrapEditText edt_description_commitment;
    private BootstrapEditText edt_date_commitment;
    private Button btn_save_commitment;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
        .child("commitments");
        return inflater.inflate(R.layout.frag_main, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        status = true;
    }

    @Override
    public void onResume() {
        super.onResume();
        status = true;
    }

    @Override
    public void onStop() {
        super.onStop();
        status = false;
    }

    public void onClick(View view) {

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tab_notes = view.findViewById(R.id.tab_notes);
        view_notes = view.findViewById(R.id.view_notes);
        fb_add = view.findViewById(R.id.fb_add);

        tab_notes.addTab(tab_notes.newTab().setIcon(R.drawable.ic_list_icon));
        tab_notes.addTab(tab_notes.newTab().setIcon(R.drawable.ic_cancel_black));
        tab_notes.setTabGravity(TabLayout.GRAVITY_FILL);

        adapterFragment = new AdapterFragment(getChildFragmentManager(), tab_notes.getTabCount());
        view_notes.setAdapter(adapterFragment);
        view_notes.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tab_notes));


        tab_notes.addOnTabSelectedListener(new TabLayout.BaseOnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                view_notes.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        fb_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog newNotes = new AlertDialog.Builder(getContext()).create();
                View dialog = getLayoutInflater().inflate(R.layout.layout_notes, null);
                edt_title_commitment = dialog.findViewById(R.id.edt_title_commiment);
                edt_description_commitment = dialog.findViewById(R.id.edt_desription_commitment);
                edt_date_commitment = dialog.findViewById(R.id.edt_date_commitment);
                btn_save_commitment = dialog.findViewById(R.id.btn_save_commitment);
                edt_date_commitment.addTextChangedListener(MaskEditText.mask(edt_date_commitment,MaskEditText.FORMAT_DATE));
                btn_save_commitment.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View view){
                        newNote(newNotes);
                    }
                });
                newNotes.setView(dialog);
                newNotes.show();
            }
        });
    }

    public void newNote(final AlertDialog alert){
        CommitmentProvider commit;
        String title;
        String description;
        String date;
        title = edt_title_commitment.getText().toString();
        description = edt_description_commitment.getText().toString();
        date = edt_date_commitment.getText().toString();

        if (title.trim().isEmpty() || description.trim().isEmpty() || date.trim().isEmpty()){
            Toast.makeText(getContext(), "Necessario o preenchimento de todos os valores!", Toast.LENGTH_SHORT)
                    .show();
            if (title.trim().isEmpty())
                edt_title_commitment.requestFocus();
            else if (description.trim().isEmpty())
                edt_description_commitment.requestFocus();
            else edt_date_commitment.requestFocus();

            return;
        }else{
            commit = new CommitmentProvider(title, description,date,"Ativo",
                    databaseReference.push().getKey());
        }
        databaseReference.child(commit.getCommitUid()).setValue(commit)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(getContext(), "Sucesso!!", Toast.LENGTH_SHORT).show();
                        alert.dismiss();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i("logx","ExceptionCommitmentSave: "+e.getMessage(),e);
            }
        });


    }
}
