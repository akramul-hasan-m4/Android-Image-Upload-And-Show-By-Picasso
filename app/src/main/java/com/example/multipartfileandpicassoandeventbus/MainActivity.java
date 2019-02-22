package com.example.multipartfileandpicassoandeventbus;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;

import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.multipartfileandpicassoandeventbus.config.RemoteDataService;
import com.example.multipartfileandpicassoandeventbus.config.RetrofitClientInstance;
import com.example.multipartfileandpicassoandeventbus.model.EventModel;
import com.example.multipartfileandpicassoandeventbus.model.UploadFileResponse;
import com.vansuita.pickimage.bean.PickResult;
import com.vansuita.pickimage.bundle.PickSetup;
import com.vansuita.pickimage.dialog.PickImageDialog;
import com.vansuita.pickimage.listeners.IPickCancel;
import com.vansuita.pickimage.listeners.IPickResult;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = "MainActivityTag";
    public static final String RESPONSE = "response";
    private Button btnAdd;
    private Button go;
    private ImageView imageView;

    private String filePath;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static final String[] PERMISSIONS_STORAGE = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    List<String> imageUrls = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init(){
        btnAdd = findViewById(R.id.addImageButton);
        go = findViewById(R.id.go);
        imageView = findViewById(R.id.imageView);

        imageView.setOnClickListener(this);
        btnAdd.setOnClickListener(this);
        go.setOnClickListener(this);

        verifyStoragePermissions(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.imageView){
            pickImage();
        }else if(v.getId() == R.id.addImageButton) {
            fileUpload(filePath);
            go.setVisibility(View.VISIBLE);
        }else if(v.getId() == R.id.go){
            Intent intent = new Intent(this, PicassoImageViewPagerActivity.class);
            if (imageUrls.size() > 0){
                intent.putStringArrayListExtra("urls", (ArrayList<String>) imageUrls);
            }
            startActivity(intent);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(EventModel event){
        if (event.isTagMatchWith(RESPONSE)) {
            String responseMessage = "Response from Server:\n" + event.getMessage();
            Toast.makeText(this, responseMessage, Toast.LENGTH_SHORT).show();
        }
    }

    private static void verifyStoragePermissions(Activity activity) {
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
        }
    }

    private void fileUpload(String filePath) {
        RemoteDataService apiInterface = RetrofitClientInstance.getRetrofitInstance().create(RemoteDataService.class);
        File file = new File(filePath);
        RequestBody requestFile = RequestBody.create(MediaType.parse("image"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);

        Call<UploadFileResponse> call = apiInterface.uploadFile(body);
        call.enqueue(new Callback<UploadFileResponse>() {
            @Override
            public void onResponse(@NonNull Call<UploadFileResponse> call, @NonNull Response<UploadFileResponse> response) {

                if (response.isSuccessful()){
                    UploadFileResponse responseModel = response.body();

                    if (responseModel != null) {
                        imageUrls.add(responseModel.getFileDownloadUri());
                        EventBus.getDefault().post(new EventModel(RESPONSE, responseModel.getFileDownloadUri()));
                    } else
                        EventBus.getDefault().post(new EventModel(RESPONSE, "ResponseModel is NULL"));
                }
            }

            @Override
            public void onFailure(@NonNull Call<UploadFileResponse> call, @NonNull Throwable t) {
                Log.d(TAG, "onFailure: "+t.getMessage());
                call.cancel();
                EventBus.getDefault().post(new EventModel(RESPONSE, t.getMessage()));
            }
        });
    }

    private void pickImage(){
        PickImageDialog.build(new PickSetup())
            .setOnPickResult(new IPickResult() {
                @Override
                public void onPickResult(PickResult r) {
                    imageView.setImageURI(r.getUri());
                    filePath = r.getPath();
                    btnAdd.setVisibility(View.VISIBLE);
                }
            })
            .setOnPickCancel(new IPickCancel() {
                @Override
                public void onCancelClick() {
                    Toast.makeText(MainActivity.this, "Cancel", Toast.LENGTH_SHORT).show();
                }
            }).show(getSupportFragmentManager());
    }
}
