package com.dimas.simapara;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import com.dimas.simapara.api.ApiClient;
import com.dimas.simapara.api.ApiInterface;
import com.dimas.simapara.model.Data;
import com.dimas.simapara.model.Region;
import com.dimas.simapara.model.UniqueCode;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewActivity extends AppCompatActivity {
    Button closeFilter;
    EditText inputSearch;
    RecyclerView recyclerView;
    FirebaseRecyclerOptions<Wisata> options;
    FirebaseRecyclerAdapter<Wisata,MyViewHolder>adapter;
    DatabaseReference Dataref;
    Spinner filter;
    private String dataFilter,codeUniqueCookie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);

        Dataref = FirebaseDatabase.getInstance().getReference().child("Wisata");

        inputSearch = findViewById(R.id.inputSearch);
        recyclerView = findViewById(R.id.recyclerView);
        filter = findViewById(R.id.filterSpinner);
        closeFilter = findViewById(R.id.closeFilter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setHasFixedSize(true);
        loadUniqueCode();
        loadData("");
        inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString()!=null){
                    loadData(editable.toString());
                }else{
                    loadData("");
                }
            }
        });

        List<String> data = new ArrayList<>();
        data.add(0,"Filter");
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(ViewActivity.this,
                android.R.layout.simple_spinner_dropdown_item, data);
        filter.setAdapter(adapter);
        filter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }
    public void loadUniqueCode() {
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<UniqueCode> call = apiService.getUniqueCode();

        call.enqueue(new Callback<UniqueCode>() {
            @Override
            public void onResponse(Call<UniqueCode> call, Response<UniqueCode> response) {
                String code = "MeP7c5ne" + response.body().getUniqueCode();
                loadProvinceList(code);
                codeUniqueCookie = code;
            }

            @Override
            public void onFailure(Call<UniqueCode> call, Throwable t) {

            }
        });
    }

    public void loadProvinceList(final String code) {

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        Call<Data> call = apiService.getProvinceList(code);

        call.enqueue(new Callback<Data>() {
            @Override
            public void onResponse(Call<Data> call, Response<Data> response) {
                final List<Region> daftarProvinsi = response.body().getData();

                List<String> data = new ArrayList<>();
                data.add(0,getString(R.string.filter));
                for (int i = 0; i < daftarProvinsi.size(); i++) {
                    data.add(daftarProvinsi.get(i).getName());
                }

                final ArrayAdapter<String> adapter = new ArrayAdapter<>(ViewActivity.this,
                        android.R.layout.simple_spinner_dropdown_item, data);
                filter.setAdapter(adapter);





                filter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if (!filter.getSelectedItem().toString().equals(getString(R.string.filter))) {
                            long idProv = daftarProvinsi.get(position - 1).getId();
                            dataFilter = daftarProvinsi.get(position - 1).getName();
                            LoadByFilter(dataFilter);
                        }

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                closeFilter.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        loadData("");
                        loadProvinceList(codeUniqueCookie);

                    }
                });
            }

            @Override
            public void onFailure(Call<Data> call, Throwable t) {

            }
        });
    }


    private void loadData(String data) {

            Query query = Dataref.orderByChild("WisataName").startAt(data).endAt(data+"\uf8ff");

        options = new FirebaseRecyclerOptions.Builder<Wisata>().setQuery(query,Wisata.class).build();
        adapter= new FirebaseRecyclerAdapter<Wisata, MyViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull MyViewHolder holder, final int position, @NonNull Wisata model) {
                    holder.textView.setText(model.getWisataName());
                    holder.textViewPropinsi.setText(model.getProvinsi());
                Picasso.get().load(model.getImageUrl()).into(holder.imageView);
                holder.v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(ViewActivity.this, DetailActivity.class);
                        intent.putExtra("WisataKey",getRef(position).getKey());
                        startActivity(intent);
                    }
                });

            }

            @NonNull
            @Override
            public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.single_view,parent,false);
                return new MyViewHolder(v);
            }
        };

        adapter.startListening();
        recyclerView.setAdapter(adapter);
    }

    private void LoadByFilter(String data) {
        Query query = Dataref.orderByChild("Provinsi").startAt(data).endAt(data+"\uf8ff");

        options = new FirebaseRecyclerOptions.Builder<Wisata>().setQuery(query,Wisata.class).build();
        adapter= new FirebaseRecyclerAdapter<Wisata, MyViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull MyViewHolder holder, final int position, @NonNull Wisata model) {
                holder.textView.setText(model.getWisataName());
                holder.textViewPropinsi.setText(model.getProvinsi());
                Picasso.get().load(model.getImageUrl()).into(holder.imageView);
                holder.v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(ViewActivity.this, DetailActivity.class);
                        intent.putExtra("WisataKey",getRef(position).getKey());
                        startActivity(intent);
                    }
                });

            }

            @NonNull
            @Override
            public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.single_view,parent,false);
                return new MyViewHolder(v);
            }
        };

        adapter.startListening();
        recyclerView.setAdapter(adapter);
    }
}