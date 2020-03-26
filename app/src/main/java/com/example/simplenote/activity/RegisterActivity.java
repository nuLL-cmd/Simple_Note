package com.example.simplenote.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.navigationdrawer.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

import com.example.simplenote.provider.UserProvider;

import de.hdodenhof.circleimageview.CircleImageView;

public class RegisterActivity extends AppCompatActivity {

    public static boolean status;
    private EditText edt_name_register;
    private EditText edt_email_register;
    private EditText edt_password_register;
    private EditText edt_user_register;
    private RadioButton rb_fem_register;
    private CircleImageView img_profile_register;
    private RadioButton rb_masc_register;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private boolean validate = true;
    private UserProvider userProvider;
    private String user_name;
    private String user_email;
    private String use_password;
    private String user_sex;
    private String use_user;
    private Uri uri;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        edt_email_register = findViewById(R.id.edt_email_register);
        edt_name_register = findViewById(R.id.edt_name_register);
        edt_password_register = findViewById(R.id.edt_password_register);
        edt_user_register = findViewById(R.id.edt_user_register);
        rb_fem_register  = findViewById(R.id.rb_fem_register);
        rb_masc_register = findViewById(R.id.rb_masc_register);
        img_profile_register = findViewById(R.id.img_profile_register);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
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
    private void validateFields(){
     user_name = edt_name_register.getText().toString();
     user_email = edt_email_register.getText().toString();
     use_password = edt_password_register.getText().toString();
     use_user = edt_user_register.getText().toString();

        if (rb_masc_register.isChecked())
            user_sex = rb_masc_register.getText().toString();
        else
            user_sex = rb_fem_register.getText().toString();

        if (user_name.trim().isEmpty() || user_email.trim().isEmpty() || use_password.trim().isEmpty() || use_user.trim().isEmpty()){
            Toast.makeText(this, "Campos nao pdoems er vazios!", Toast.LENGTH_SHORT).show();
            if (user_name.trim().isEmpty())
                edt_name_register.requestFocus();
            else if (use_password.trim().isEmpty())
                edt_password_register.requestFocus();
            else if (use_user.trim().isEmpty())
                edt_user_register.requestFocus();
            else
                edt_email_register.requestFocus();

            validate = false;
        }else{
            userProvider = new UserProvider(user_name, user_email, null, user_sex,null, use_user);
        }
    }
    public void registerMethod(View view){

        validateFields();
        if (validate){
            firebaseAuth.createUserWithEmailAndPassword(user_email, use_password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        userProvider.setUserUid(firebaseAuth.getCurrentUser().getUid());
                        savePickFirestore();
                    }
                }
            }).addOnFailureListener(this, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e("logx","ExceptionCreate: "+e.getMessage(),e);
                }
            });
        }
    }

    private void saveUserDatabase(UserProvider userProvider) {
        databaseReference = firebaseDatabase.getReference("users");
        databaseReference.child(userProvider.getUserUid()).setValue(userProvider)
                .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            nextActivityMain();
                            Toast.makeText(RegisterActivity.this, "Sucesso!",Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("logx","ExceptionSaveUser: "+e.getMessage(),e);
            }
        });
    }

    public void clearEdits(View view){
        edt_user_register.setText(null);
        edt_name_register.setText(null);
        edt_password_register.setText(null);
        edt_email_register.setText(null);
        rb_fem_register.setChecked(true);

    }

    public void selectPickUser(View view){
        View dialog = getLayoutInflater().inflate(R.layout.layout_source_image,null);
        ImageButton btn_gallery_layout_source_image = dialog.findViewById(R.id.btn_gallery_layout_source_image);
        ImageButton btn_camera_layout_source_image = dialog.findViewById(R.id. btn_camera_layout_source_image);
        final AlertDialog source = new AlertDialog.Builder(RegisterActivity.this).create();
        source.setView(dialog);
        source.show();
        btn_gallery_layout_source_image.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 0);
                source.dismiss();
            }
        });
        btn_camera_layout_source_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (intent.resolveActivity(getPackageManager())!= null){
                    startActivityForResult(intent, 1);
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == Activity.RESULT_OK) {
            uri = data.getData();
            //Picasso.get().load(uri.toString()).into(img_profile_register);
             Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),uri);
                img_profile_register.setImageDrawable(new BitmapDrawable(bitmap));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private void savePickFirestore(){
        storageReference = firebaseStorage.getReference("images/"+userProvider.getUserUid());
        storageReference.putFile(uri).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()){
                    storageReference.getDownloadUrl().addOnSuccessListener(RegisterActivity.this, new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            userProvider.setUserUrlPhoto(uri.toString());
                            saveUserDatabase(userProvider);
                        }
                    });
                }
            }
        }).addOnFailureListener(RegisterActivity.this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }
    private void nextActivityMain(){
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (!MainActivity.status && firebaseUser!= null){
            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
            intent.putExtra("uid",firebaseUser.getUid());
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (!LoginActivity.status){
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();        }
    }


}
