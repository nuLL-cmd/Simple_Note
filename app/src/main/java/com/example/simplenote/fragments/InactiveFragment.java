package com.example.simplenote.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.navigationdrawer.R;
import com.example.simplenote.adapter.CommitmentAdapter;
import com.example.simplenote.provider.CommitmentProvider;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link InactiveFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InactiveFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private List<CommitmentProvider> commitentProviderList;
    private CommitmentProvider commitmentProvider;
    private RecyclerView recycler_inactive;
    private CommitmentAdapter commitmentAdapter;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public InactiveFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Passados.
     */
    // TODO: Rename and change types and number of parameters
    public static InactiveFragment newInstance(String param1, String param2) {
        InactiveFragment fragment = new InactiveFragment();
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
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("commitments");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_inactive, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recycler_inactive = view.findViewById(R.id.recycler_inactive);
        createRecycler();
        getDatabase();
        onItemClick();
    }

    private void getDatabase() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                commitentProviderList.clear();
                for (DataSnapshot docs : dataSnapshot.getChildren()) {
                    commitmentProvider = docs.getValue(CommitmentProvider.class);
                    if (commitmentProvider.getStatus().equals("Inativo"))
                        commitentProviderList.add(commitmentProvider);
                }
                commitmentAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void createRecycler() {
        recycler_inactive.hasFixedSize();
        recycler_inactive.setLayoutManager(new LinearLayoutManager(getContext()));
        commitentProviderList = new ArrayList<>();
        commitmentAdapter = new CommitmentAdapter(commitentProviderList);
        recycler_inactive.setAdapter(commitmentAdapter);
    }

    private void onItemClick() {
        commitmentAdapter.setOnItemClickListener(new CommitmentAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                final CommitmentProvider commitmentProvider = commitentProviderList.get(position);
                AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                alert.setTitle("Alteração de dados.");
                alert.setMessage("Titulo: " + commitmentProvider.getTitle() + "\n--------------------------\nDescrição: " + commitmentProvider.getDescription()
                        + "\n--------------------------\nData: " + commitmentProvider.getDate());
                alert.setPositiveButton("Excluir", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        databaseReference.child(commitmentProvider.getCommitUid())
                                .removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(getContext(), "Excluido!", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.i("logx", "ExceptionDeleteNotes: " + e.getMessage(), e);
                            }
                        });
                    }
                });
                alert.setNegativeButton("Mudar para ativo", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        commitmentProvider.setStatus("Ativo");
                       databaseReference.child(commitmentProvider.getCommitUid())
                               .updateChildren(commitmentProvider.toMap()).addOnSuccessListener(new OnSuccessListener<Void>() {
                           @Override
                           public void onSuccess(Void aVoid) {
                               Toast.makeText(getContext(), "Alterado!", Toast.LENGTH_SHORT).show();
                           }
                       }).addOnFailureListener(new OnFailureListener() {
                           @Override
                           public void onFailure(@NonNull Exception e) {
                               Log.i("logx","ExceptionUpdateStatus: "+e.getMessage(),e);
                           }
                       });
                    }
                });
                alert.show();
                commitmentAdapter.notifyItemChanged(position);

            }
        });

    }
}
