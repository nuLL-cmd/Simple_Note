package com.example.simplenote.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.navigationdrawer.R;
import com.example.simplenote.adapter.CommitmentAdapter;
import com.example.simplenote.provider.CommitmentProvider;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ActiveFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ActiveFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private CommitmentProvider commitmentProvider;
    private List<CommitmentProvider> commitentProviderList;
    private RecyclerView recycler_active;
    private CommitmentAdapter commitmentAdapter;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    public ActiveFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PresentesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ActiveFragment newInstance(String param1, String param2) {
        ActiveFragment fragment = new ActiveFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("commitments");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_active, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        recycler_active = view.findViewById(R.id.recycler_active);
        createRecycler();
        getDatabase();
        onItemClick();
        onItemLongClick();
        super.onViewCreated(view, savedInstanceState);

    }

    private void createRecycler() {
        recycler_active.hasFixedSize();
        recycler_active.setLayoutManager(new LinearLayoutManager(getContext()));
        commitentProviderList = new ArrayList<>();
        commitmentAdapter = new CommitmentAdapter(commitentProviderList);
        recycler_active.setAdapter(commitmentAdapter);

    }

    private void getDatabase() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                commitentProviderList.clear();
                for (DataSnapshot docs : dataSnapshot.getChildren()) {
                    commitmentProvider = docs.getValue(CommitmentProvider.class);
                    if (commitmentProvider.getStatus().equals("Ativo"))
                        commitentProviderList.add(commitmentProvider);
                }

                commitmentAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void onItemClick(){
        commitmentAdapter.setOnItemClickListener(new CommitmentAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                final CommitmentProvider commitmentProvider = commitentProviderList.get(position);
                AlertDialog.Builder alerta = new AlertDialog.Builder(getContext());
                alerta.setTitle("Dados do compromisso");
                alerta.setMessage("Titulo: "+commitmentProvider.getTitle()+"\n--------------------------\nDescrição: "+commitmentProvider.getDescription()
                +"\n--------------------------\nData: "+commitmentProvider.getDate());
                alerta.setPositiveButton("Ok",null);
                alerta.show();
                commitmentAdapter.notifyItemChanged(position);
            }
        });
    }

    private void onItemLongClick(){
        commitmentAdapter.setOnItemLongClickListener(new CommitmentAdapter.OnItemLongClickListener() {
            @Override
            public void onItemClick(final int position) {
                final CommitmentProvider commitmentProvider = commitentProviderList.get(position);
                AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                alert.setTitle("Alteração de dados.");
                alert.setMessage("Autorizar compromisso para inativo?");
                alert.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        commitmentProvider.setStatus("Inativo");
                        databaseReference.child(commitmentProvider.getCommitUid())
                                .updateChildren(commitmentProvider.toMap())
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){
                                            Toast.makeText(getContext(),"Sucesso!",Toast.LENGTH_SHORT).show();

                                        }
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.i("logx","ExceptionUpdateNote: "+e.getMessage(),e);
                            }
                        });
                    }
                });
                alert.setNegativeButton("Não",null);
                alert.show();
                commitmentAdapter.notifyItemChanged(position);
            }
        });


    }
}
