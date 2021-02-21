package com.dimas.simapara;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

//api
import com.dimas.simapara.api.ApiClient;
import com.dimas.simapara.api.ApiInterface;
import com.dimas.simapara.model.Data;
import com.dimas.simapara.model.Region;
import com.dimas.simapara.model.UniqueCode;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddActivity extends AppCompatActivity {
    private ImageView imageViewAdd;
    private EditText inputImageName;
    private EditText inputImageDes;
    private TextView textViewProgress;
    private ProgressBar progressBar;
    private Button btnUpload;
    private int REQUEST_CODE_IMAGE = 101;

    Uri imageUri;
    boolean isImageAdded=false;

    DatabaseReference Dataref;
    StorageReference  StorageRef;

    // spinner
    private Spinner spinnerProv;
    private Spinner spinnerKab;
    private Spinner spinnerKec;
    private Spinner spinnerKel;

    private String spinnerProvData;
    private String spinnerKabData;
    private String spinnerKecData;
    private String spinnerKelData;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);



        imageViewAdd=findViewById(R.id.imageViewAdd);
        inputImageName=findViewById(R.id.inputImageName);
        inputImageDes=findViewById(R.id.inputImageDes);
        textViewProgress=findViewById(R.id.textViewProgress);
        progressBar=findViewById(R.id.progressBar);
        btnUpload=findViewById(R.id.btnUpload);
        spinnerProv = findViewById(R.id.spinner_prov);
        spinnerKab = findViewById(R.id.spinner_kab);
        spinnerKec = findViewById(R.id.spinner_kec);
        spinnerKel = findViewById(R.id.spinner_kel);
        loadUniqueCode();
        textViewProgress.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);

        Dataref = FirebaseDatabase.getInstance().getReference().child("Wisata");
        StorageRef = FirebaseStorage.getInstance().getReference().child("WisataImage");


        imageViewAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent=new Intent();
//                intent.setType("image/*");
//                intent.setAction(Intent.ACTION_GET_CONTENT);
//                startActivityForResult(intent,REQUEST_CODE_IMAGE);

                CropImage.startPickImageActivity(AddActivity.this);

            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String imageName = inputImageName.getText().toString();
                final String imageDes = inputImageDes.getText().toString();
                final String Provinsi = spinnerProvData;
                final String Kabupaten = spinnerKabData;
                final String Kecamatan = spinnerKecData;
                final String Kelurahan = spinnerKelData;

                if (isImageAdded!=false){
                    if(imageName!=null && imageDes !=null ) {
                        uploadImage(imageName, imageDes, spinnerProvData, spinnerKabData, spinnerKecData, spinnerKelData);
                    }
                }
            }
        });

    }

    private void uploadImage(final String imageName, final String imageDes,final String spinnerProv,final String spinnerKab,final String spinnerKec,final String spinnerKel) {
        textViewProgress.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        String name = inputImageName.getText().toString();
        String Des = inputImageDes.getText().toString();

         if (name.isEmpty()) {
            showError(inputImageName, "Nama Tempat Wisata Harus Diisi!");
        } else if (Des.isEmpty()) {
            showError(inputImageDes, "Deskripsi tidak boleh kosong!");
        }else {
            final String key = Dataref.push().getKey();
            StorageRef.child(key + ".jpg").putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    StorageRef.child(key + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            HashMap hashMap = new HashMap();
                            hashMap.put("WisataName", imageName);
                            hashMap.put("WisataDes", imageDes);
                            hashMap.put("Provinsi", spinnerProv);
                            hashMap.put("Kabupaten", spinnerKab);
                            hashMap.put("Kecamatan", spinnerKec);
                            hashMap.put("Kelurahan", spinnerKel);

                            hashMap.put("ImageUrl", uri.toString());

                            Dataref.child(key).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(AddActivity.this, "Data Sucessfully Uploaded!", Toast.LENGTH_SHORT).show();

                                }
                            });
                        }
                    });
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (taskSnapshot.getBytesTransferred() * 100) / taskSnapshot.getTotalByteCount();
                    progressBar.setProgress((int) progress);
                    textViewProgress.setText(progress + " %");
                    if ((int) progress == 100.0) {
                        imageViewAdd.setImageResource(R.drawable.ic_upload);
                        inputImageName.getText().clear();
                        inputImageDes.getText().clear();
                        textViewProgress.setText(progress + " %");

                    }

                }
            });
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode==REQUEST_CODE_IMAGE && data!=null){
//            imageUri = data.getData();
//            isImageAdded=true;
//            imageViewAdd.setImageURI(imageUri);
//
//        }
        if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == Activity.RESULT_OK){
            Uri uri = CropImage.getPickImageResultUri(this, data);
                if (CropImage.isReadExternalStoragePermissionsRequired(this,uri)){
                    imageUri = uri;
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},0);
                } else {
                    startCrop(uri);
                }
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if(resultCode == RESULT_OK && data!=null){
                imageUri = result.getUri();
                isImageAdded=true;
                imageViewAdd.setImageURI(result.getUri());
                Toast.makeText(this, "Image Update Success",Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void startCrop(Uri uri) {
        CropImage.activity(uri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setMultiTouchEnabled(true)
                .start(this);
    }

    // API
    public void loadUniqueCode() {
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<UniqueCode> call = apiService.getUniqueCode();

        call.enqueue(new Callback<UniqueCode>() {
            @Override
            public void onResponse(Call<UniqueCode> call, Response<UniqueCode> response) {
                String code = "MeP7c5ne" + response.body().getUniqueCode();
                loadProvinceList(code);
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

                // masukkan daftar provinsi ke list string
                List<String> provs = new ArrayList<>();
                // isi data pertama dengan string 'Silakan Pilih!'
                provs.add(0, getString(R.string.txt_slct_prov));
                for (int i = 0; i < daftarProvinsi.size(); i++) {
                    provs.add(daftarProvinsi.get(i).getName());
                }


                final ArrayAdapter<String> adapter = new ArrayAdapter<>(AddActivity.this,
                        android.R.layout.simple_spinner_dropdown_item, provs);
                spinnerProv.setAdapter(adapter);

                spinnerProv.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if (!spinnerProv.getSelectedItem().toString().equals(getString(R.string.txt_slct_prov))) {
                            long idProv = daftarProvinsi.get(position - 1).getId();
                            spinnerProvData = daftarProvinsi.get(position - 1).getName();
                            loadKabupatenList(code, idProv);
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            }

            @Override
            public void onFailure(Call<Data> call, Throwable t) {

            }
        });
    }

    public void loadKabupatenList(final String code, final long idProv) {

        spinnerKec.setAdapter(null);
        spinnerKel.setAdapter(null);
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<Data> call = apiService.getKabupatenList(code, idProv);

        call.enqueue(new Callback<Data>() {
            @Override
            public void onResponse(Call<Data> call, Response<Data> response) {
                final List<Region> daftarKabupaten = response.body().getData();

                // masukkan daftar kabupaten ke list string
                List<String> kabs = new ArrayList<>();
                // isi data pertama dengan string 'Silakan Pilih!'
                kabs.add(0, getString(R.string.txt_slct_kab));
                for (int i = 0; i < daftarKabupaten.size(); i++) {
                    kabs.add(daftarKabupaten.get(i).getName());
                }


                // masukkan daftar kabupaten ke spinner
                final ArrayAdapter<String> adapter = new ArrayAdapter<>(AddActivity.this,
                        android.R.layout.simple_spinner_dropdown_item, kabs);
                spinnerKab.setAdapter(adapter);

                spinnerKab.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if (!spinnerKab.getSelectedItem().toString().equals(getString(R.string.txt_slct_kab))) {
                            long idKab = daftarKabupaten.get(position - 1).getId();
                            spinnerKabData = daftarKabupaten.get(position - 1).getName();
                            loadKecamatanList(code, idKab);
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            }

            @Override
            public void onFailure(Call<Data> call, Throwable t) {

            }
        });
    }

    public void loadKecamatanList(final String code, long idKab) {

        spinnerKel.setAdapter(null);
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<Data> call = apiService.getKecamatanList(code, idKab);

        call.enqueue(new Callback<Data>() {
            @Override
            public void onResponse(Call<Data> call, Response<Data> response) {
                final List<Region> daftarKecamatan = response.body().getData();

                // masukkan daftar kecamatan ke list string
                List<String> kecs = new ArrayList<>();
                // isi data pertama dengan string 'Silakan Pilih!'
                kecs.add(0, getString(R.string.txt_slct_kec));
                for (int i = 0; i < daftarKecamatan.size(); i++) {
                    kecs.add(daftarKecamatan.get(i).getName());
                }

                // masukkan daftar kecamatan ke spinner
                final ArrayAdapter<String> adapter = new ArrayAdapter<>(AddActivity.this,
                        android.R.layout.simple_spinner_dropdown_item, kecs);
                spinnerKec.setAdapter(adapter);

                spinnerKec.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if (!spinnerKec.getSelectedItem().toString().equals(getString(R.string.txt_slct_kec))) {
                            long idKec = daftarKecamatan.get(position - 1).getId();
                            spinnerKecData = daftarKecamatan.get(position - 1).getName();
                            loadKelurahanList(code, idKec);
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            }

            @Override
            public void onFailure(Call<Data> call, Throwable t) {

            }
        });
    }

    public void loadKelurahanList(final String code, final long idKec) {

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<Data> call = apiService.getKelurahanList(code, idKec);

        call.enqueue(new Callback<Data>() {
            @Override
            public void onResponse(Call<Data> call, Response<Data> response) {
                final List<Region> daftarKelurahan = response.body().getData();

                // masukkan daftar kelurahan ke list string
                List<String> kels = new ArrayList<>();
                // isi data pertama dengan string 'Silakan Pilih!'
                kels.add(0, getString(R.string.txt_slct_kel));
                for (int i = 0; i < daftarKelurahan.size(); i++) {
                    kels.add(daftarKelurahan.get(i).getName());
                }

                // masukkan daftar kelurahan ke spinner
                final ArrayAdapter<String> adapter = new ArrayAdapter<>(AddActivity.this,
                        android.R.layout.simple_spinner_dropdown_item, kels);
                spinnerKel.setAdapter(adapter);

                spinnerKel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if (!spinnerKel.getSelectedItem().toString().equals(getString(R.string.txt_slct_kel))) {
                            long idKel = daftarKelurahan.get(position - 1).getId();
                            spinnerKelData = daftarKelurahan.get(position - 1).getName();
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

            }

            @Override
            public void onFailure(Call<Data> call, Throwable t) {

            }
        });
    }



    private void showError(EditText input, String s){
        input.setError(s);
        input.requestFocus();
    }
}