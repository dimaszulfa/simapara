package com.dimas.simapara;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class DetailActivity extends AppCompatActivity {

    private ImageView imageView;
    TextView textView;
    TextView Deskripsi,provinsi,kabupaten,kecamatan,kelurahan;
    Button btnDelete;

    DatabaseReference ref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        imageView = findViewById(R.id.image_single_DetailActivity);
        textView = findViewById(R.id.textView_single_DetailActivity);
        Deskripsi = findViewById(R.id.WisataDeskripsi);
        provinsi = findViewById(R.id.dataProvinsi);
        kabupaten = findViewById(R.id.dataKabupaten);
        kecamatan = findViewById(R.id.dataKecamatan);
        kelurahan = findViewById(R.id.dataKelurahan);

//        btnDelete = findViewById(R.id.btnDelete);
        ref = FirebaseDatabase.getInstance().getReference().child("Wisata");
        String WisataKey=getIntent().getStringExtra("WisataKey");
        ref.child(WisataKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String WisataName=snapshot.child("WisataName").getValue().toString();
                    String ImageUrl=snapshot.child("ImageUrl").getValue().toString();
                    String WisataDes=snapshot.child("WisataDes").getValue().toString();

                    String dataProvinsi=snapshot.child("Provinsi").getValue().toString();
                    String dataKabupaten=snapshot.child("Kabupaten").getValue().toString();
                    String dataKecamatan=snapshot.child("Kecamatan").getValue().toString();
                    String dataKelurahan=snapshot.child("Kelurahan").getValue().toString();
//                    dataProvinsi = upperCase(dataProvinsi);
//                    dataKabupaten = upperCase(dataKabupaten);
//                    dataKecamatan = upperCase(dataKecamatan);
//                    dataKelurahan = upperCase(dataKelurahan);
                     Picasso.get().load(ImageUrl).into(imageView);
                    textView.setText(WisataName);
                    Deskripsi.setText(WisataDes);
                    provinsi.setText(dataProvinsi);
                    kabupaten.setText(dataKabupaten);
                    kecamatan.setText(dataKecamatan);
                    kelurahan.setText(dataKelurahan);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




    }

    private String upperCase(String word) {
        word = word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase();
        return word;

    }
}