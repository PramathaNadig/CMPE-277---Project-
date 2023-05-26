package com.example.sjsucafe;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sjsucafe.adapters.MenuListAdapter;
import com.example.sjsucafe.objects.Menu;
import com.example.sjsucafe.objects.MenuItem;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MenuActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MenuListAdapter adapter;
    private ProgressBar progressBar;
    private TextView errorTextView;
    private Button checkoutButton;
    private String restaurantId;
    private List<MenuItem> menuItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        // Get the restaurant ID from the intent extra
        Intent intent = getIntent();
        restaurantId = intent.getStringExtra("restaurantId");

        // Initialize the views
        recyclerView = findViewById(R.id.menuRecyclerView);
        progressBar = findViewById(R.id.menuProgressBar);
        errorTextView = findViewById(R.id.menuErrorTextView);
        checkoutButton = findViewById(R.id.checkoutButton);
        // Set up the RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MenuListAdapter(menuItems);
        recyclerView.setAdapter(adapter);

        checkoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, CartActivity.class);
                startActivity(intent);
            }
        });

        SharedPreferences sharedPreferences = getSharedPreferences("com.example.sjsucafe", MODE_PRIVATE);
        String token = sharedPreferences.getString("token", "");
        // Make the API call
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(Constants.BASE_URL + "/menu/restaurant/" + restaurantId)
                .addHeader("Authorization", "Bearer " + token)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.e("MenuActivity", "API call failed", e);
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    errorTextView.setVisibility(View.VISIBLE);
                    Toast.makeText(MenuActivity.this, "API call failed", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    Log.e("MenuActivity", "API call failed with status code " + response.code());
                    runOnUiThread(() -> {
                        progressBar.setVisibility(View.GONE);
                        errorTextView.setVisibility(View.VISIBLE);
                        Toast.makeText(MenuActivity.this, "API call failed with status code " + response.code(), Toast.LENGTH_LONG).show();
                    });
                } else {
                    String responseBody = response.body().string();
                    System.out.println(responseBody);
                    Gson gson = new Gson();
                    Menu menu = gson.fromJson(responseBody, Menu.class);
                    menuItems.clear();
                    menuItems.addAll(Arrays.asList(menu.getItems()));
                    System.out.println(menuItems);
                    runOnUiThread(() -> {
                        progressBar.setVisibility(View.GONE);
                        recyclerView.getAdapter().notifyDataSetChanged();
                    });
                }
            }
        });


    }
}
