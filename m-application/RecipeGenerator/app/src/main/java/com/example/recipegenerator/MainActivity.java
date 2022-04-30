package com.example.recipegenerator;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {


    private static final String TAG = "chechthejson";
    private ImageView imageView;
    private Button selectBtn, uploadBtn, captureBtn;
    ActivityResultLauncher<String> mGetContent;
    ActivityResultLauncher<Intent> mGetCamera;
    private Bitmap bitmap;
    public static List<Recipe> recipes;
    private Handler mainHandeler = new Handler();
    private ProgressDialog progressDialog;
    private static final String url = "http://192.168.43.211/Android%20Tutorials/upload_image.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.imageView);
        selectBtn = findViewById(R.id.select_btn);
        uploadBtn = findViewById(R.id.upload_btn);
        captureBtn = findViewById(R.id.capture_btn);
        recipes =  new ArrayList<>();

        // get the image from storage

        mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {
                if (result != null){
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),result);
                        imageView.setImageBitmap(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    }else{
                    Toast.makeText(MainActivity.this, "You didn't select an image", Toast.LENGTH_SHORT).show();
                }


            }
        });

        // start the camera

        mGetCamera = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {

                if (result.getData() != null){
                    Bundle bundle = result.getData().getExtras();
                    bitmap = (Bitmap) bundle.get("data");
                    imageView.setImageBitmap(bitmap);
                }else{
                    Toast.makeText(MainActivity.this, "You didn't capture an image", Toast.LENGTH_SHORT).show();
                }



            }
        });

        selectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mGetContent.launch("image/");


            }
        });

        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{
                            Manifest.permission.CAMERA
                    }, 100);
        }

        captureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if(intent.resolveActivity(getPackageManager()) != null){
                    mGetCamera.launch(intent);
                }else{
                    Toast.makeText(MainActivity.this,"There is no app that support this action",Toast.LENGTH_SHORT).show();
                }

            }
        });

        // upload the image


        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (bitmap != null ){
                    new fetchData().start();
                }else {
                    Toast.makeText(MainActivity.this,"Please put an image",Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    class  fetchData extends Thread{
        @Override
        public void run() {
            mainHandeler.post(new Runnable() {
                @Override
                public void run() {
                    progressDialog = new ProgressDialog(MainActivity.this);
                    progressDialog.setMessage("Fetching Data");
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                }
            });

            uploadImage();

            mainHandeler.post(new Runnable() {
                @Override
                public void run() {
                    if (progressDialog.isShowing()){
                        progressDialog.dismiss();
                        // listAdapter.notifyDataSetChanged();
                    }
                }
            });
        }
    }


    private void uploadImage() {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 75, byteArrayOutputStream);
        byte[] imageInByte = byteArrayOutputStream.toByteArray();
        String encodedImage = Base64.encodeToString(imageInByte, Base64.DEFAULT);
        // Log.d("TAG", "uploadImage: "+encodedImage);


        StringRequest request=new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response)
            {

                Toast.makeText(getApplicationContext(),"wait for the result",Toast.LENGTH_LONG).show();
                Log.d(TAG, "onResponse1: "+response.toString());



                try {
                    JSONObject arr = new JSONObject(response);
                    JSONObject arr2 =new JSONObject(arr.get("remarks").toString()) ;

                    for (int i = 0; i<=arr2.length();i++) {
                        Recipe recipe = new Recipe();
                        JSONObject arr3 = arr2.getJSONObject("res_"+(i+1));
                        JSONArray ingredients = arr3.getJSONArray("Ingredients");
                        recipe.setIngredients(ingredients);
                        Log.d(TAG, "onResponse2: "+recipe.getIngredients());

                        JSONArray instructions = arr3.getJSONArray("Instructions");
                        recipe.setInstructions(instructions);
                        Log.d(TAG, "onResponse2: "+recipe.getInstructions());
                        Object title = arr3.get("Title");
                        recipe.setTitle((String) title);
                        Log.d(TAG, "onResponse2: "+title);
                        Object recipe_no = arr3.get("recipe_no");
                        recipe.setRecipeNo((Integer) recipe_no);
                        Log.d(TAG, "onResponse2: "+recipe_no);

                        recipes.add(recipe);


                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (response != null){
//                    Bundle bundle = new Bundle();
//                    bundle.putStringArray(recipes.);
//                    RecipeFragment fragInfo = new RecipeFragment();
//                    fragInfo.setArguments(bundle);
                    getSupportFragmentManager().beginTransaction()
                            .setReorderingAllowed(true)
                            .add(R.id.frame_layout, RecipeFragment.class, null)
                            .commit();
                }else{

                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),error.toString(),Toast.LENGTH_LONG).show();
            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError
            {
                Map<String,String> map=new HashMap<String, String>();
                map.put("IMG",encodedImage);
                return map;
            }
        };


        RequestQueue queue= Volley.newRequestQueue(getApplicationContext());
        queue.add(request);




    }
}