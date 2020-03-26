package com.example.simplenote.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.navigationdrawer.R;
import com.example.simplenote.provider.UserProvider;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserFragment extends Fragment {
    public static boolean status;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private EditText edt_name_profile;
    private EditText edt_email_profile;
    private EditText edt_user_profile;
    private EditText edt_password_profile;
    private RadioButton rb_fem_profile;
    private RadioButton rb_masc_profile;
    private CircleImageView img_profile_profile;
    private UserProvider userProvider;
    private ImageButton btn_active_pass_profile;
    private ImageButton getBtn_active_pass_profile2;
    private Button btn_save_profile;
    private FirebaseAuth firebaseAuth;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDataDatabase();
        return inflater.inflate(R.layout.frag_user, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        edt_email_profile = view.findViewById(R.id.edt_email_profile);
        edt_name_profile = view.findViewById(R.id.edt_name_profile);
        edt_user_profile = view.findViewById(R.id.edt_user_profile);
        edt_password_profile = view.findViewById(R.id.edt_password_profile);
        rb_fem_profile = view.findViewById(R.id.rb_fem_profile);
        rb_masc_profile = view.findViewById(R.id.rb_masc_profile);
        img_profile_profile = view.findViewById(R.id.img_profile_profile);
        btn_active_pass_profile = view.findViewById(R.id.btn_active_pass_profile);
        btn_save_profile = view.findViewById(R.id.btn_save_profile);
        getBtn_active_pass_profile2 = view.findViewById(R.id.btn_active_pass_profile2);

        btn_active_pass_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edt_password_profile.setEnabled(true);
                edt_password_profile.setText(null);
                edt_password_profile.requestFocus();
                btn_save_profile.setEnabled(true);
                btn_active_pass_profile.setEnabled(false);
                ;
            }
        });
        btn_save_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveUpdateData();
            }
        });

        getBtn_active_pass_profile2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edt_name_profile.setEnabled(true);
                edt_user_profile.setEnabled(true);
                rb_fem_profile.setEnabled(true);
                rb_masc_profile.setEnabled(true);
                btn_save_profile.setEnabled(true);
                getBtn_active_pass_profile2.setEnabled(false);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        status = true;
    }

    @Override
    public void onStop() {
        super.onStop();
        status = false;
    }

    private void getDataDatabase() {
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userProvider = dataSnapshot.getValue(UserProvider.class);
                edt_name_profile.setText(userProvider.getUserName());
                edt_email_profile.setText(userProvider.getUserEmail());
                edt_user_profile.setText(userProvider.getUserUser());
                edt_password_profile.setText(null);
                if (userProvider.getUserSex().equals("Feminino"))
                    rb_fem_profile.setChecked(true);
                else
                    rb_masc_profile.setChecked(true);

                Picasso.get().load(userProvider.getUserUrlPhoto()).into(img_profile_profile);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void saveUpdateData() {
        UserProvider userUpdate;
        String user_user = edt_user_profile.getText().toString();
        String user_sex;
        String user_name = edt_name_profile.getText().toString();
        String user_password = edt_password_profile.getText().toString();
        if (user_user.trim().isEmpty() || user_name.trim().isEmpty())
            Toast.makeText(getContext(), "Campos não podem ser vazios", Toast.LENGTH_SHORT).show();
        else {
            if (rb_masc_profile.isChecked())
                user_sex = "Masculino";
            else
                user_sex = "Feminino";

            userUpdate = new UserProvider(user_name, userProvider.getUserEmail(), FirebaseAuth.getInstance().getCurrentUser().getUid(),
                    user_sex, userProvider.getUserUrlPhoto(), user_user);

            if (edt_password_profile.isEnabled() && edt_password_profile.getText().toString().trim().isEmpty()){
                Toast.makeText(getContext(), "Você escolheu att sua senha \n Digite uma senha para continuar", Toast.LENGTH_SHORT).show();
                return;
            }

            attData(userUpdate,user_password);


        }
    }

    private void attData(UserProvider userUpdate, final String user_password) {
        databaseReference.updateChildren(userUpdate.toMap()).addOnCompleteListener(getActivity(), new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    if (!user_password.trim().isEmpty()){
                        firebaseAuth = FirebaseAuth.getInstance();
                        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                        firebaseUser.updatePassword(user_password).addOnCompleteListener(getActivity(), new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(getContext(), "Dados atualizdos com sucesso!", Toast.LENGTH_SHORT).show();
                                getActivity().getSupportFragmentManager().popBackStack();;
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e("logx","ExceptionUpdatePass: "+e.getMessage(), e);
                            }
                        });

                    }else{
                        Toast.makeText(getContext(), "Dados atualizdos com sucesso!", Toast.LENGTH_SHORT).show();
                        getActivity().getSupportFragmentManager().popBackStack();;
                    }
                }
            }
        });
    }

}
