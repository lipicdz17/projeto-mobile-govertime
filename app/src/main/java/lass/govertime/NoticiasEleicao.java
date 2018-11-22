package lass.govertime;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;

public class NoticiasEleicao extends Fragment {

    private RecyclerView listaNoticias;
    private DatabaseReference mDatabaseNoticias;
    private Button btnCarregar;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private SwipeRefreshLayout RefreshNoticias;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private static final int TOTAL_ITENS_NOTICIAS = 10;
    private int countPage = 1;
    String uid;
    private ProgressDialog progressDialog;
    //private static String URL= "https://noticias.uol.com.br/politica/eleicoes/ultimas/";
    private static String URL1= "https://www1.folha.uol.com.br/poder/eleicoes-2018";

    //private boolean isLoading = true;
    //private int pastVisibleItens, visibleItensCount, totalItensCount, previous_total = 0;
    //private int view_threshold = 10;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_noticias_eleicao, container, false);
        mDatabaseNoticias = FirebaseDatabase.getInstance().getReference().child("UltimasNoticias");
        mDatabaseNoticias.keepSynced(true);
        btnCarregar = (Button)view.findViewById(R.id.CarregaNoticias);
        listaNoticias = (RecyclerView)view.findViewById(R.id.listaNoticias);
        final LinearLayoutManager ln = new LinearLayoutManager(getActivity());
        ln.setReverseLayout(true);
        listaNoticias.setLayoutManager(ln);
        listaNoticias.addItemDecoration(new DividerItemDecoration(getActivity(),LinearLayoutManager.VERTICAL));
        RefreshNoticias = (SwipeRefreshLayout)view.findViewById(R.id.refresh);

        btnCarregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mDatabaseNoticias.removeValue();
                new NoticiasEleicao.CarregarNoticiasHoje().execute();
            }
        });
        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if(user!= null) {
                    uid = user.getUid();
                    if (uid.contains("GVs4sUaDYwXpAHzFHP7DzoEtsNu2") || uid.contains("ybI4wGa1K1RZZF7JQrYHQXwlSFz1")|| uid.contains("ElEuqbdoQmg6r9HrmsRVW8DNz1m1")) {
                        btnCarregar.setVisibility(View.VISIBLE);
                    }
                }
            }
        };
        carregar();

        RefreshNoticias.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                countPage++;

                carregar();
            }
        });
        return view;

    }



    @Override
    public void onStart() {
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

    public void carregar(){
        mDatabaseNoticias = FirebaseDatabase.getInstance().getReference().child("UltimasNoticias");
        mDatabaseNoticias.keepSynced(true);
        Query query = mDatabaseNoticias.limitToFirst(countPage * TOTAL_ITENS_NOTICIAS);
        FirebaseRecyclerAdapter<Pessoa, NoticiasViewHolder> firebaseRecyclerAdapter
                = new FirebaseRecyclerAdapter<Pessoa, NoticiasEleicao.NoticiasViewHolder>(
                Pessoa.class,
                R.layout.item_noticias,
                NoticiasEleicao.NoticiasViewHolder.class,
                query
        ) {
            @Override
            protected void populateViewHolder(NoticiasEleicao.NoticiasViewHolder viewHolder, Pessoa model, int position) {


                viewHolder.setImagem(getActivity(), model.getImagem());
                viewHolder.setImagem1(getActivity(), model.getImagem1());
                viewHolder.setTexto(model.getTexto());
                viewHolder.setData(model.getData());

                final String chave_noticia = model.getLink();
                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent telaPres = new Intent(getActivity(), TelaNoticia.class);
                        telaPres.putExtra("linkNoticia", chave_noticia);
                        startActivity(telaPres);
                        getActivity().finish();

                    }
                });

            }
        };

        listaNoticias.setAdapter(firebaseRecyclerAdapter);
        RefreshNoticias.setRefreshing(false);

    }

    public static class NoticiasViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public NoticiasViewHolder(final View itemView) {
            super(itemView);
            mView = itemView;

        }
        public void setImagem(Context context, String imagem){

            ImageView imagemNoticias = (ImageView) mView.findViewById(R.id.imgNoticias);
            if (imagemNoticias.equals("default")){
                imagemNoticias.setVisibility(View.GONE);
            }else {
                Picasso.with(context).load(imagem).into(imagemNoticias);
            }

        }
        public void setImagem1(Context context, String imagem1){

            ImageView imagemNoticias = (ImageView) mView.findViewById(R.id.imgNoticias1);
            if (imagemNoticias.equals("default")){
                imagemNoticias.setVisibility(View.GONE);
            }else {
                Picasso.with(context).load(imagem1).into(imagemNoticias);
            }

        }

        public void setTexto(String texto){

            TextView textView = (TextView)mView.findViewById(R.id.textoNoticias);
            textView.setText(texto);

        }

        public void setData(String data){
            TextView textView = (TextView)mView.findViewById(R.id.dataNoticias);
            textView.setText(data);

        }

    }

    private class CarregarNoticiasHoje extends AsyncTask<Void, Void, Void> {
        Context context;
        DatabaseReference mDatabaseNoticias;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            mDatabaseNoticias = FirebaseDatabase.getInstance().getReference().child("UltimasNoticias");

            progressDialog = new ProgressDialog(getContext());
            progressDialog.setTitle("Carregando Noticias...");
            progressDialog.setMessage("Aguarde enquanto as noticias estão sendo carregadas...");
            progressDialog.setIndeterminate(false);
            progressDialog.show();



        }


        @Override
        protected Void doInBackground(Void... voids) {

            try {
                Document doc = Jsoup.connect(URL1).get();//.timeout(30*1000)

                // Elements img= doc.select("img[class=\"r h-opacity90 transition-050\"][src~=(?i)\\.(png|jpe?g|gif)]");
                //  Elements texto= doc.select("article[class] span[class=\"h-opacity60 transition-050\"]");
                //  Elements link= doc.select("a[class=\"opacity-group\"]");
                //  Elements data= doc.select("article[class] time");

                Elements link = doc.select("ol[class=\"u-list-unstyled\"] div[class=\"c-headline__media-wrapper\"] a");
                Elements texto = doc.select("ol[class=\"u-list-unstyled\"] div[class=\"c-headline__wrapper\"] a h2[class=\"c-headline__title\"]");
                Elements imagem = doc.select("ol[class=\"u-list-unstyled\"] div[class=\"c-headline__media-wrapper\"] a img");
                Elements imagem1 = doc.select("ol[class=\"u-list-unstyled\"] div[class=\"c-headline__media-wrapper\"] a img");
                Elements data = doc.select("ol[class=\"u-list-unstyled\"] div[class=\"c-headline__wrapper\"] a time[class=\"c-headline__dateline\"]");


                for (int i=0; i<link.size();i++) {

                    final String linkNoticias = link.get(i).attr("href");
                    final String textoNoticias = texto.get(i).text();
                    final String imagemNoticias = imagem.get(i).attr("src");
                    final String imagemNoticias1 = imagem1.get(i).attr("data-src");
                    final String dataNoticias = data.get(i).text();

                    // final String imagemNoticias = img.get(i).attr("src");
                    // final String textoNoticias = texto.get(i).text();
                    // final String linkNoticias = link.get(i).attr("href");
                    // final String dataNoticias = data.get(i).attr("datetime");

                    //Log.d("teste", ": " + img.get(j).attr("src"));
                    //Log.d("teste", ": " + link.get(j).attr("href"));
                    //Log.d("teste", ": " + texto.get(j).text());

                    final DatabaseReference enviarNoticias = mDatabaseNoticias.push();
                    mDatabaseNoticias.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            enviarNoticias.child("link").setValue(linkNoticias).toString();
                            enviarNoticias.child("texto").setValue(textoNoticias).toString();
                            if(imagemNoticias.equals("data:image/gif;base64,R0lGODlhAQABAIAAAAAAAP///yH5BAEAAAAALAAAAAABAAEAAAIBRAA7")){
                                enviarNoticias.child("imagem").setValue("default").toString();
                            }else {
                                enviarNoticias.child("imagem").setValue(imagemNoticias).toString();
                            }

                            if(imagemNoticias1.isEmpty()){
                                enviarNoticias.child("imagem1").setValue("default").toString();
                            }else{
                                enviarNoticias.child("imagem1").setValue(imagemNoticias1).toString();
                            }

                            enviarNoticias.child("data").setValue(dataNoticias).toString();

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }





            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);


            progressDialog.dismiss();
        }

    }
 }
