package lass.govertime;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditarPerfil extends AppCompatActivity {
    private CircleImageView imgPerfil;
    private EditText edtNome;
    private Button btneditar;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private ProgressDialog mProgress;
    private Uri imgUri = null;
    private StorageReference mStorage;
    String opcao;

    private static final int IMAGEM_REQUEST = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_perfil);
        final ActionBar act = getSupportActionBar();
        act.setDisplayHomeAsUpEnabled(true);
        act.setTitle("Editar Perfil");
        opcao = getIntent().getExtras().getString("op");
        mAuth = FirebaseAuth.getInstance();
        mStorage = FirebaseStorage.getInstance().getReference().child("Imagem_User");
        mDatabase = FirebaseDatabase.getInstance().getReference().child("users");
        imgPerfil = (CircleImageView)findViewById(R.id.imgPerfil);
        edtNome = (EditText)findViewById(R.id.nomePerfil);
        btneditar = (Button)findViewById(R.id.btnEditar);

        mProgress = new ProgressDialog(this);
        btneditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editarPerfil();
            }
        });
        imgPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent CarregarIMG = new Intent();
                CarregarIMG.setAction(Intent.ACTION_GET_CONTENT);
                CarregarIMG.setType("image/*");
                startActivityForResult(CarregarIMG, IMAGEM_REQUEST);
            }
        });

    }
    @Override
    protected void onStart() {
        super.onStart();
        if (mAuth.getCurrentUser() == null){
            Intent inicio = new Intent(EditarPerfil.this, Login.class);
            startActivity(inicio);
        }
    }

    private void editarPerfil() {

        final String nome = edtNome.getText().toString().trim();
        final String user_id = mAuth.getCurrentUser().getUid();

        if (!TextUtils.isEmpty(nome) && imgUri != null){

            mProgress.setTitle("Editando perfil");
            mProgress.setMessage("Finalizando edição do perfil...");
            mProgress.show();

            StorageReference pastaIMG = mStorage.child(imgUri.getLastPathSegment());

            pastaIMG.putFile(imgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    String imgCel = taskSnapshot.getDownloadUrl().toString();
                    mDatabase.child(user_id).child("nome").setValue(nome);
                    mDatabase.child(user_id).child("imagem").setValue(imgCel);
                    if (opcao.equals("cadastro")) {
                        mDatabase.child(user_id).child("email").setValue(mAuth.getCurrentUser().getEmail());
                        mDatabase.child(user_id).child("uid").setValue(mAuth.getCurrentUser().getUid());
                        bemVindo();
                    }else{
                        alterado();
                    }
                    mProgress.dismiss();
                    Intent inicio = new Intent(EditarPerfil.this, MainActivity.class);
                    startActivity(inicio);
                    finish();
                }
            });


        }else if(!TextUtils.isEmpty(nome) && imgUri == null){
            mProgress.setTitle("Editando perfil");
            mProgress.setMessage("Finalizando edição do perfil...");
            mProgress.show();
            DatabaseReference database = FirebaseDatabase.getInstance().getReference("users").child(user_id);
            DatabaseReference myRef = database;
            myRef.child("nome").setValue(nome);
            if (opcao.equals("cadastro")) {
                myRef.child("imagem").setValue("default");
                myRef.child("email").setValue(mAuth.getCurrentUser().getEmail());
                myRef.child("uid").setValue(mAuth.getCurrentUser().getUid());
                bemVindo();
            }
            alterado();
            mProgress.dismiss();
            Intent inicio = new Intent(EditarPerfil.this, MainActivity.class);
            startActivity(inicio);
            finish();
        }else if(TextUtils.isEmpty(nome) && imgUri != null && opcao.equals("alterar")){
            mProgress.setTitle("Editando perfil");
            mProgress.setMessage("Finalizando edição do perfil...");
            mProgress.show();

            StorageReference pastaIMG = mStorage.child(imgUri.getLastPathSegment());

            pastaIMG.putFile(imgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    String imgCel = taskSnapshot.getDownloadUrl().toString();
                    mDatabase.child(user_id).child("imagem").setValue(imgCel);
                    alterado();
                    mProgress.dismiss();
                    Intent inicio = new Intent(EditarPerfil.this, MainActivity.class);
                    startActivity(inicio);
                    finish();
                }
            });
        }else{
            if(opcao.equals("cadastro")) {
                Toast.makeText(this, "Pelo menos o nome deve ser preenchido!", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this, "Nenhum dado foi alterado!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void alterado(){
        Toast.makeText(this, "Dados alterados!", Toast.LENGTH_SHORT).show();
    }

    private void bemVindo(){
        Toast.makeText(this, "Bem vindo!", Toast.LENGTH_SHORT).show();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGEM_REQUEST && resultCode == RESULT_OK){

            imgUri = data.getData();
            imgPerfil.setImageURI(imgUri);
        }
    }

    @Override
    public boolean onSupportNavigateUp(){
        startActivity(new Intent(EditarPerfil.this, MainActivity.class));
        finish();
        return true;
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(EditarPerfil.this, MainActivity.class));
        finish();
    }
}
