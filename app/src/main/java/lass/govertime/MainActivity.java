package lass.govertime;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;
import lass.govertime.Adapter.SearchAdapter;
import lass.govertime.Adapter.SearchAdapterNoticias;
import lass.govertime.Adapter.SearchAdapterPresidentes;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, SearchView.OnQueryTextListener {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser user;
    private String nome,email, img;
    private FragmentManager fragmentManager;

    private RecyclerView listaRancking;
    private RecyclerView listaNoticias;
    private RecyclerView listaPresidentes;
    private RecyclerView listaFavoritos;
    private DatabaseReference mDatabase;
    private DatabaseReference mDatabaseUser;
    private DatabaseReference mRancking;
    private DatabaseReference mPresidentes;
    private DatabaseReference mDatabaseNoticias;
    private DatabaseReference mSeguir;
    private boolean clickVotar = false;
    private boolean clickSeguir = false;
    private int base;
    ArrayList<String> nomeList;
    ArrayList<String> imgList;
    ArrayList<String> idList;
    SearchAdapter searchAdapter;
    MenuItem searchmenu;

    ArrayList<String> nomeListPres;
    ArrayList<String> imgListPres;
    ArrayList<String> idListPres;
    ArrayList<String> votoListPres;
    SearchAdapterPresidentes searchAdapterPresidentes;

    ArrayList<String> textoList;
    ArrayList<String> imgNList;
    ArrayList<String> img1NList;
    ArrayList<String> idNList;
    ArrayList<String> linkList;
    ArrayList<String> dataList;
    SearchAdapterNoticias searchAdapterNoticias;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DatabaseUtil.getDatabase();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setLayout(5);
        base = 5;
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Logado();
                } else {
                    Logar();
                }
            }
        };
    }


    private void setLayout(int op){
        Object classe = null;
        String id = null;

        if(op == 1){
            classe = new Ranking();
            id = "Ranking";

        }else if(op == 2){
            classe = new ListaPresidente();
            id = "Lista Presidente";

        }else if(op == 3){
            classe = new PerfilUsuario();
            id = "Meu Perfil";
        }else if(op == 4){
            classe = new Favoritos();
            id = "Favoritos";
        }else if(op == 5){
            classe = new NoticiasEleicao();
            id = "Noticias";
        }
        base = op;
        FrameLayout it = (FrameLayout)findViewById(R.id.container);
        it.removeAllViews();
        getSupportActionBar().setTitle(id);
        fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.container, (Fragment) classe, id );
        transaction.commitAllowingStateLoss();
    }



    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);

    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void Logado(){
        String uid = user.getUid();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("users").child(uid);
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                img = dataSnapshot.child("imagem").getValue().toString();
                nome = dataSnapshot.child("nome").getValue().toString();
                email = dataSnapshot.child("email").getValue().toString();
                setInfo();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void Logar(){
        NavigationView navi = (NavigationView)findViewById(R.id.nav_view);
        navi.setNavigationItemSelectedListener(this);
        Menu menu = navi.getMenu();


        MenuItem logout = menu.findItem(R.id.nav_logout);
        MenuItem login = menu.findItem(R.id.nav_login);

        logout.setVisible(false);
        login.setVisible(true);
    }

    private void setInfo(){
        NavigationView navi = (NavigationView)findViewById(R.id.nav_view);
        navi.setNavigationItemSelectedListener(this);
        View header = navi.getHeaderView(0);
        Menu menu = navi.getMenu();

        CircleImageView imga = (CircleImageView)header.findViewById(R.id.imageView);
        TextView nome2 = (TextView) header.findViewById(R.id.menuNome);
        TextView email2 = (TextView) header.findViewById(R.id.menuEmail);
        nome2.setText(nome.toString());
        email2.setText(email.toString());
        Picasso.with(header.getContext()).load(img).into(imga);


        MenuItem logout = menu.findItem(R.id.nav_logout);
        MenuItem login = menu.findItem(R.id.nav_login);
        MenuItem favoritos = menu.findItem(R.id.nav_favoritos);
        MenuItem perfil = menu.findItem(R.id.nav_perfil);

        favoritos.setVisible(true);
        perfil.setVisible(true);
        logout.setVisible(true);
        login.setVisible(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        SearchView mSearchView = (SearchView) menu.findItem(R.id.search)
                .getActionView();
        searchmenu = menu.findItem(R.id.search);
        //Define um texto de ajuda:
        mSearchView.setQueryHint("Pesquisar...");
        // exemplos de utilização:
        mSearchView.setOnQueryTextListener(this);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if(id == R.id.nav_login){
            startActivity(new Intent(this, Login.class));
            finish();
        }else if (id == R.id.nav_feed) {
            setLayout(5);
            pesquisa(1);
        }else if (id == R.id.nav_ranking) {
            setLayout(1);
            pesquisa(1);
        }else if (id == R.id.nav_all) {
            setLayout(2);
            pesquisa(1);
        }else if (id == R.id.nav_perfil) {
            setLayout(3);
            pesquisa(2);
        } else if (id == R.id.nav_favoritos) {
            setLayout(4);
            pesquisa(1);
        } else if (id == R.id.nav_termos) {
            cliqueTermos();
        } else if (id == R.id.nav_logout) {
            mAuth.getInstance().signOut();
            startActivity(new Intent(this, MainActivity.class));
            finish();

        } else if (id == R.id.nav_fechar) {
            finish();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void pesquisa(int op) {
        if (op == 1) {
            searchmenu.setVisible(true);
        } else {
            searchmenu.setVisible(false);
        }
    }

    private void cliqueTermos(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Termos de Uso");
        builder.setMessage("Salvador, 02 de Junho de 2018\n" +
                "1. ACEITAÇÃO\n" +
                "Este é um contrato firmado entre você, de agora em diante denominado como usuário, e a Connects. Este “Termo de Uso de Aplicativo” rege o uso de todos os aplicativos\n" +
                "disponibilizados gratuitamente pela Connects sejam para dispositivos móveis (Android, IOS, Windows Mobile),\n" +
                "servidores, computadores pessoais (desktops) ou serviços web. Se você não concordar com estes termos não\n" +
                "use este aplicativo.\n" +
                "Você reconhece que analisou e aceitou as condições de uso. Leia-as atentamente pois o uso deste aplicativo\n" +
                "significa que você aceitou todos os termos e concorda em cumpri-los. Se você, usuário, for menor de idade ou\n" +
                "declarado incapaz em quaisquer aspectos, precisará da permissão de seus pais ou responsáveis que também\n" +
                "deverão concordar com estes mesmos termos e condições.\n" +
                "2. LICENÇA LIMITADA\n" +
                "Você recebeu uma licença limitada, não transferível, não exclusiva, livre de royalties e revogável para baixar,\n" +
                "instalar, executar e utilizar este aplicativo em seu dispositivo. Você reconhece e concorda que a Connects\n" +
                "concede ao usuário uma licença exclusiva para uso e desta forma não lhe transfere os direitos sobre o produto.\n" +
                "O aplicativo deverá ser utilizado por você, usuário. A venda, transferência, modificação, engenharia reversa ou\n" +
                "distribuição bem como a cópia de textos, imagens ou quaisquer partes nele contido é expressamente proibida.\n" +
                "3. ALTERAÇÕES, MODIFICAÇÕES E RESCISÃO\n" +
                "A Connects reserva-se no direito de, a qualquer tempo, modificar estes termos seja incluindo, removendo ou\n" +
                "alterando quaisquer de suas cláusulas. Tais modificações terão efeito imediato. Após publicadas tais alterações,\n" +
                "ao continuar com o uso do aplicativo você terá aceitado e concordado em cumprir os termos modificados.\n" +
                "A Connects pode, de tempos em tempos, modificar ou descontinuar (temporária ou permanentemente) a\n" +
                "distribuição ou a atualização deste aplicativo.\n" +
                "A Connects não é obrigada a fornecer nenhum serviço de suporte para este aplicativo.\n" +
                "O usuário não poderá responsabilizar a Connects nem seus diretores, executivos, funcionários, afiliados, agentes,\n" +
                "contratados ou licenciadores por quaisquer modificações, suspensões ou descontinuidade do aplicativo.\n" +
                "CONSENTIMENTO PARA COLETA E USO DE DADOS\n" +
                "Você concorda que a Connects pode coletar e usar dados técnicos de seu dispositivo tais como especificações,\n" +
                "configurações, versões de sistema operacional, tipo de conexão à internet e afins.\n" +
                "ISENÇÃO DE GARANTIAS E LIMITAÇÕES DE RESPONSABILIDADE\n" +
                "Este aplicativo estará em contínuo desenvolvimento e pode conter erros e, por isso, o uso é fornecido \"no\n" +
                "estado em que se encontra\" e sob risco do usuário final. Na extensão máxima permitida pela legislação aplicável\n" +
                "a Connects e seus fornecedores isentam-se de quaisquer garantias e condições expressas ou implícitas incluindo,\n" +
                "sem limitação, garantias de comercialização, adequação a um propósito específico, titularidade e não violação no\n" +
                "que diz respeito ao aplicativo e qualquer um de seus componentes ou ainda à prestação ou não de serviços de\n" +
                "suporte. A Connects não garante que a operação deste aplicativo seja contínua e sem defeitos.\n" +
                "Exceto pelo estabelecido neste documento não há outras garantias, condições ou promessas aos aplicativos,\n" +
                "expressas ou implícitas, e todas essas garantias, condições e promessas podem ser excluídas de acordo com o\n" +
                "que é permitido por lei sem prejuízo à Connects e seus colaboradores.\n" +
                "I. A Connects não garante, declara ou assegura que o uso deste aplicativo será ininterrupto ou livre de erros\n" +
                "e você concorda que a Connects poderá remover por períodos indefinidos ou cancelar este aplicativo a qualquer\n" +
                "momento sem que você seja avisado.\n" +
                "II. A Connects não garante, declara nem assegura que este aplicativo esteja livre de perda, interrupção,\n" +
                "ataque, vírus, interferência, pirataria ou outra invasão de segurança e isenta-se de qualquer responsabilidade em\n" +
                "relação à essas questões. Você é responsável pelo backup do seu próprio dispositivo.\n" +
                "III. Em hipótese alguma a Connects, bem como seus diretores, executivos, funcionários, afiliadas, agentes,\n" +
                "contratados ou licenciadores responsabilizar-se-ão por perdas ou danos causados pelo uso do aplicativo.");

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
            }
        });

        AlertDialog  alerta = builder.create();
        alerta.show();
        Button positiveButton = alerta.getButton(AlertDialog.BUTTON_POSITIVE);
        LinearLayout parent = (LinearLayout) positiveButton.getParent();
        parent.setGravity(Gravity.CENTER_HORIZONTAL);
        View leftSpacer = parent.getChildAt(1);
        leftSpacer.setVisibility(View.GONE);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if(base == 5) {
            carregarNoticias(newText.toString());
        }else if (base == 1){
            carregarRanking(newText.toString());
        }else if (base == 2){
            carregarLista(newText.toString());
        }else if(base == 4){
            pesquisarUsuario(newText.toString());
        }
        return true;
    }

    private void carregarNoticias(final String string) {
        mDatabaseNoticias = FirebaseDatabase.getInstance().getReference().child("UltimasNoticias");

        textoList = new ArrayList<>();
        imgNList = new ArrayList<>();
        img1NList = new ArrayList<>();
        idNList = new ArrayList<>();
        linkList = new ArrayList<>();
        dataList = new ArrayList<>();

        listaNoticias = (RecyclerView)findViewById(R.id.listaNoticias);
        listaNoticias.setHasFixedSize(true);
        LinearLayoutManager ln = new LinearLayoutManager(getApplicationContext());
        listaNoticias.setLayoutManager(ln);


        mDatabaseNoticias.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                textoList.clear();
                imgNList.clear();
                img1NList.clear();
                idNList.clear();
                linkList.clear();
                dataList.clear();
                listaNoticias.removeAllViews();

                for (DataSnapshot snapshot:dataSnapshot.getChildren()){
                    String nome = snapshot.child("texto").getValue(String.class);
                    String img = snapshot.child("imagem").getValue(String.class);
                    String img1 = snapshot.child("imagem1").getValue(String.class);
                    String link = snapshot.child("link").getValue(String.class);
                    String data = snapshot.child("data").getValue(String.class);
                    String id = snapshot.getKey();

                    if (nome.toLowerCase().contains(string.toLowerCase())){

                        textoList.add(nome);
                        imgNList.add(img);
                        img1NList.add(img1);
                        idNList.add(id);
                        linkList.add(link);
                        dataList.add(data);
                    }

                }

                searchAdapterNoticias = new SearchAdapterNoticias(getApplicationContext(), textoList, imgNList,img1NList, idNList, linkList, dataList);
                listaNoticias.setAdapter(searchAdapterNoticias);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


    private void pesquisarUsuario(final String txt){
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference().child("Favorito");
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<String> idPresidentes = new ArrayList<>();

                for (DataSnapshot snapshot:dataSnapshot.getChildren()){

                    if(snapshot.hasChild(user.getUid())) {
                        idPresidentes.add(snapshot.getKey());
                    }

                }
                if(!idPresidentes.isEmpty()) {
                    carregarFavoritos(idPresidentes,txt);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void carregarFavoritos(final ArrayList idPresidentes, final String txt) {
        listaFavoritos = (RecyclerView) findViewById(R.id.listaFavoritos);
        listaFavoritos.setHasFixedSize(true);
        LinearLayoutManager ln = new LinearLayoutManager(getApplicationContext());
        listaFavoritos.setLayoutManager(ln);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Eleicao");
        nomeListPres = new ArrayList<>();
        imgListPres = new ArrayList<>();
        idListPres = new ArrayList<>();
        votoListPres = new ArrayList<>();

            mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    nomeListPres.clear();
                    imgListPres.clear();
                    idListPres.clear();
                    votoListPres.clear();
                    listaFavoritos.removeAllViews();

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String nome = snapshot.child("nome").getValue(String.class);
                        String img = snapshot.child("imagem").getValue(String.class);
                        String voto = snapshot.child("voto").getValue(String.class);
                        String id = snapshot.getKey();
                        for (int i = 0; i < idPresidentes.size(); i++) {
                            if(txt.toString().equals("")){
                                if (idPresidentes.get(i).toString().contains(id)) {
                                    nomeListPres.add(nome);
                                    imgListPres.add(img);
                                    idListPres.add(id);
                                    votoListPres.add(voto);
                                    break;
                                }

                            }else{
                                if (idPresidentes.get(i).toString().contains(id) && nome.toString().toLowerCase().contains(txt.toString().toLowerCase())){
                                    nomeListPres.add(nome);
                                    imgListPres.add(img);
                                    idListPres.add(id);
                                    votoListPres.add(voto);
                                    break;
                                }
                            }
                        }

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            searchAdapterPresidentes = new SearchAdapterPresidentes(getApplicationContext(), nomeListPres, imgListPres, idListPres, votoListPres);
            listaFavoritos.setAdapter(searchAdapterPresidentes);

    }

    private void carregarRanking(final String string) {
       DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("Eleicao");

        nomeListPres = new ArrayList<>();
        imgListPres = new ArrayList<>();
        idListPres = new ArrayList<>();
        votoListPres = new ArrayList<>();

        listaRancking = (RecyclerView)findViewById(R.id.listaRancking);
        listaRancking.setHasFixedSize(true);
        LinearLayoutManager ln = new LinearLayoutManager(getApplicationContext());
        listaRancking.setLayoutManager(ln);


        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                nomeListPres.clear();
                imgListPres.clear();
                idListPres.clear();
                votoListPres.clear();
                listaRancking.removeAllViews();

                int cont = 0;
                for (DataSnapshot snapshot:dataSnapshot.getChildren()){
                    String politico = snapshot.child("nome").getValue(String.class);
                    String img = snapshot.child("imagem").getValue(String.class);
                    String id = snapshot.getKey();
                    String voto = snapshot.child("voto").getValue(String.class);

                    if(!string.equals("")) {
                        if (politico != null) {
                            if (politico.toLowerCase().contains(string.toLowerCase())) {

                                nomeListPres.add(politico);
                                imgListPres.add(img);
                                idListPres.add(id);
                                votoListPres.add(voto);
                                cont++;

                            }
                        }
                    }else{
                        nomeListPres.add(politico);
                        imgListPres.add(img);
                        idListPres.add(id);
                        votoListPres.add(voto);
                        cont++;
                    }
                }
                String aux;
                int voto,voto2;
                for(int i = 0;i<cont;i++){
                        for(int j = i+1 ;j<cont;j++){
                            voto = Integer.parseInt(votoListPres.get(i));
                            voto2 = Integer.parseInt(votoListPres.get(j));
                            if(voto < voto2){
                                aux = nomeListPres.get(i);
                                nomeListPres.set(i,nomeListPres.get(j));
                                nomeListPres.set(j,aux);

                                aux = imgListPres.get(i);
                                imgListPres.set(i,imgListPres.get(j));
                                imgListPres.set(j,aux);

                                aux = idListPres.get(i);
                                idListPres.set(i,idListPres.get(j));
                                idListPres.set(j,aux);

                                aux = votoListPres.get(i);
                                votoListPres.set(i,votoListPres.get(j));
                                votoListPres.set(j,aux);


                            }
                        }
                    }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        searchAdapterPresidentes = new SearchAdapterPresidentes(getApplicationContext(), nomeListPres, imgListPres, idListPres, votoListPres);
        listaRancking.setAdapter(searchAdapterPresidentes);
    }

    private void carregarLista(final String txt3) {
        mPresidentes = FirebaseDatabase.getInstance().getReference().child("Presidentes");
        nomeList = new ArrayList<>();
        imgList = new ArrayList<>();
        idList = new ArrayList<>();

        listaPresidentes = (RecyclerView)findViewById(R.id.listaPresidentes);
        listaPresidentes.setHasFixedSize(true);
        listaPresidentes.setLayoutManager(new LinearLayoutManager(MainActivity.this));

        mPresidentes.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                nomeList.clear();
                imgList.clear();
                idList.clear();
                listaPresidentes.removeAllViews();

                for (DataSnapshot snapshot:dataSnapshot.getChildren()){
                    String nome = snapshot.child("nome").getValue(String.class);
                    String img = snapshot.child("imagem").getValue(String.class);
                    String id = snapshot.getKey();

                    if (nome.toLowerCase().contains(txt3.toLowerCase())){

                        nomeList.add(nome);
                        imgList.add(img);
                        idList.add(id);
                    }

                }

                searchAdapter = new SearchAdapter(getApplicationContext(), nomeList, imgList, idList);
                listaPresidentes.setAdapter(searchAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

}
