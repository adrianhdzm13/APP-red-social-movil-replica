package com.hdz.freegamer.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hdz.freegamer.R;
import com.hdz.freegamer.models.User;
import com.hdz.freegamer.providers.AuthProvider;
import com.hdz.freegamer.providers.UsersProvider;

import java.util.HashMap;
import java.util.Map;

import dmax.dialog.SpotsDialog;

public class MainActivity extends AppCompatActivity {

    //interactuar con las vistas
    TextView mTextViewRegister;
    TextInputEditText mTextInputEmail;
    TextInputEditText mTextInputPassword;
    Button mButtonLogin;
    AuthProvider mAuthProvider;
    SignInButton mButtonGoogle;
    private GoogleSignInClient mGoogleSignInClient;
    private final  int REQUEST_CODE_GOOGLE = 1;
   UsersProvider mUsersProvider;
   AlertDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mTextViewRegister = findViewById(R.id.textViewRegister);
        mTextInputEmail = findViewById(R.id.textInputEmail);
        mButtonGoogle = findViewById(R.id.btnLoginGoogle);
        mTextInputPassword = findViewById(R.id.textInputPassword);
        mButtonLogin = findViewById(R.id.btnLogin);


        mAuthProvider =  new AuthProvider();
        mDialog =  new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Espere un momento")
                .setCancelable(false).build();

        //Creacion de un objeto con todas las configuraciones necesarias para el inicio de sesion
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mUsersProvider = new UsersProvider();

        mButtonGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signInGoogle();//ejecuta el metodo al oprimir el boton de google
            }
        });

        mButtonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            //clic en el boton login
            public void onClick(View view) {
                login();
            }
        });




        mTextViewRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }
    //metodo para vereficar el inicio de sesion
    private void signInGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, REQUEST_CODE_GOOGLE);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == REQUEST_CODE_GOOGLE) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w("ERROR", "Google sign in failed", e);
                // ...
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        mDialog.show();
        mAuthProvider.googleLogin(acct).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            String id = mAuthProvider.getUid();
                            checkUserExist(id);

                        } else {
                            mDialog.dismiss();//para que deje de mostrarse
                            // If sign in fails, display a message to the user.
                            Log.w("ERROR", "signInWithCredential:failure", task.getException());
                            Toast.makeText(MainActivity.this, "No se pudo iniciar sesion con google", Toast.LENGTH_SHORT).show();
                        }


                    }
                });
    }

    //metodo que hace la consulta  ala DB, apuntando a la coleccion que vamos a consultar
    //para que el usuario inicie sesion con google
    private void checkUserExist( final String id) {
        mUsersProvider.getUser(id).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {//metodo que trae el documento de LA COLECCION, TIENE LA INFORMACION
                if(documentSnapshot.exists()){ //verifica si el documento que estoy apuntando existe
                    mDialog.dismiss();
                    Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                    startActivity(intent);
                }else {
                    String email = mAuthProvider.getEmail();//devuelve el email del usuario que ya tiene iniciada unsesion
                    User user = new User();
                    user.setEmail(email);
                    user.setId(id);
                    mUsersProvider.create(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            mDialog.dismiss();
                            if(task.isSuccessful()){
                                Intent intent = new Intent(MainActivity.this, CompleteProfileActivity.class);
                                startActivity(intent);
                            }
                            else{
                                Toast.makeText(MainActivity.this, "No se pudo almacenar la información del usuario", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }

    //METODO PARA MOSTRAR LOS VALORES EN LA CONSOLA
    private void login() {
        String email = mTextInputEmail.getText().toString();//captura el texto de email
        String password = mTextInputPassword.getText().toString();// contraseña
        //para el espere por favor
        mDialog.show();
        mAuthProvider.login(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                mDialog.dismiss();//oculta el mensaje
                //si fue exitoso, el login nos lleva al a la ventana HomeActivity
                if(task.isSuccessful()){
                    Intent intent =  new Intent(MainActivity.this, HomeActivity.class);
                    startActivity(intent);
                }else {
                    Toast.makeText(MainActivity.this, "El email o la contraseña que ingresaste no son correctas", Toast.LENGTH_LONG).show();
                }
            }
        });
        Log.d("CAMPO", "email: " + email); //para imprimir un mensaje en consola
        Log.d("CAMPO", "password: " + password);
    }
}
