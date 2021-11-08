package com.hdz.freegamer.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hdz.freegamer.R;
import com.hdz.freegamer.models.User;
import com.hdz.freegamer.providers.AuthProvider;
import com.hdz.freegamer.providers.UsersProvider;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;

public class RegisterActivity extends AppCompatActivity {

    //creacion de objetos
    CircleImageView mCircleImageViewBack;//para volver a la vista anterior
    TextInputEditText mTextInputUsername;
    TextInputEditText mTextInputEmail;
    TextInputEditText mTextInputPassword;
    TextInputEditText mTextInputConfirmPassword;
    Button mButtonRegister;
    AuthProvider mAuthProvider;
    UsersProvider mUsersProvider;
    AlertDialog mDialog;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //instancia
        mCircleImageViewBack = findViewById(R.id.circleImageBack);
        mTextInputEmail = findViewById(R.id.textInputEmail);
        mTextInputUsername = findViewById(R.id.textInputUsername);
        mTextInputPassword = findViewById(R.id.textInputPassword);
        mTextInputConfirmPassword = findViewById(R.id.textInputConfirmPassword);
        mButtonRegister = findViewById(R.id.btnRegister);

        mAuthProvider = new AuthProvider();
        mUsersProvider = new UsersProvider();
        mDialog =  new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Espere un momento")
                .setCancelable(false).build();

        mButtonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                register();
            }
        });

        mCircleImageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void register() {
        String username = mTextInputUsername.getText().toString();
        String email = mTextInputEmail.getText().toString();
        String password  = mTextInputPassword.getText().toString();
        String confirmPassword = mTextInputConfirmPassword.getText().toString();

        //metodo isEmpty pregunta si lo que inserto el usuario en el campo username esta vacio
        // ! si no esta vacio, si ha insertado algo
        if (!username.isEmpty() && !email.isEmpty() && !password.isEmpty() && !confirmPassword.isEmpty()) {
            if (isEmailValid(email)) {
                if (password.equals(confirmPassword)) {
                    if (password.length() >= 6) {
                        createUser(username, email, password);
                    }
                    else {
                        //Toast para mostrar mensaje de validaciòn
                        Toast.makeText(this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(this, "Las contraseña no coinciden", Toast.LENGTH_SHORT).show();
                }
            }
            else {
                Toast.makeText(this, "Insertaste todos los campos pero el correo no es valido", Toast.LENGTH_LONG).show();
            }
        }
        else {
            Toast.makeText(this, "Para continuar inserta todos los campos", Toast.LENGTH_SHORT).show();
        }
    }

    private void createUser( final String username, final String email, final String password) {
        mDialog.show();
        mAuthProvider.register(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    String id = mAuthProvider.getUid();//obtiene la sesion actual del uusario, siempre cuando este logueado
                    User user = new User();
                    user.setId(id);
                    user.setEmail(email);
                    user.setUsername(username);

                    mUsersProvider.create(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            mDialog.dismiss();
                            //valida si es exitosa
                            if(task.isSuccessful()){
                                Intent intent =  new Intent( RegisterActivity.this, HomeActivity.class);
                                //para limpiar el historial de pantallas en la que ha navegado el usuario
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }else{
                                Toast.makeText(RegisterActivity.this, "El usuario no se pudo almacenar en la base de datos ", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                else {
                    mDialog.dismiss();
                    Toast.makeText(RegisterActivity.this, "No se pudo registrar el usuario", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /*
     * VERIFICAR QUE SEA UN EMAIL VALIDO
     */
    public boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

}