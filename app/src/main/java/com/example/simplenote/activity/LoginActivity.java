package com.example.simplenote.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.navigationdrawer.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    public static boolean status;
    private FirebaseAuth firebaseAuth;
    private EditText edt_email_login;
    private EditText edt_password_login;
    private String user_email;
    private String user_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        edt_email_login = findViewById(R.id.edt_email_login);
        edt_password_login = findViewById(R.id.edt_password_login);

        firebaseAuth = FirebaseAuth.getInstance();
    }

    public void loginMethod(View view) {
        user_email = edt_email_login.getText().toString();
        user_password = edt_password_login.getText().toString();

        if (user_email.trim().isEmpty() || user_password.trim().isEmpty()) {
            Toast.makeText(this, "Campos nao podem ser vazios", Toast.LENGTH_SHORT).show();
            if (user_email.trim().isEmpty())
                edt_email_login.requestFocus();
            else
                edt_password_login.requestFocus();
        } else {
            firebaseAuth.signInWithEmailAndPassword(user_email, user_password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(LoginActivity.this, "Sucesso!!", Toast.LENGTH_SHORT).show();
                                nextActivityMain();
                            }
                        }
                    }).addOnFailureListener(this, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.i("logx", "ExceptiionLogin: " + e.getMessage(), e);
                }
            });
        }
    }

    private void nextActivityMain() {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (!MainActivity.status && firebaseUser != null) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.putExtra("uid", firebaseUser.getUid());
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        nextActivityMain();
        status = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        status = false;
    }

    public void nextActivityRegister(View view) {
        if (!RegisterActivity.status) {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
            finish();
        }
    }

    public void recoveryMethod(View view) {
        View dialog = getLayoutInflater().inflate(R.layout.layout_recovery, null);
        final EditText edt_email_layout_recovery = dialog.findViewById(R.id.edt_email_layout_recovery);
        Button btn_send_layout_recovery = dialog.findViewById(R.id.btn_send_layout_recovery);

        final AlertDialog recovery = new AlertDialog.Builder(LoginActivity.this).create();
        recovery.setView(dialog);
        recovery.show();

        btn_send_layout_recovery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email_recovery;
                if (edt_email_layout_recovery.getText().toString().trim().isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Insira um email valido para a redefinição de senha!", Toast.LENGTH_SHORT).show();
                    edt_email_layout_recovery.requestFocus();
                } else {
                    email_recovery = edt_email_layout_recovery.getText().toString();
                    FirebaseAuth.getInstance().sendPasswordResetEmail(email_recovery)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {

                                        AlertDialog.Builder alerta = new AlertDialog.Builder(LoginActivity.this);
                                        alerta.setTitle("Sucesso!");
                                        alerta.setMessage("\nUm email de redefinição foi enviado para o endereço " + email_recovery + ".\nVerifique sua lixeira e / ou caixa de spam!");
                                        alerta.setPositiveButton("Entendi", null);
                                        alerta.show();
                                        recovery.dismiss();

                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e("logx","ExceptionSendEmail: "+e.getMessage(),e);
                            Toast.makeText(LoginActivity.this, "Porfavor verifique o email digitado!", Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            }
        });
    }


}
