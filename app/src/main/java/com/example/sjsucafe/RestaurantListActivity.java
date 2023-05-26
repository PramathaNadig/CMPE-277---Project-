package com.example.sjsucafe;

import static com.example.sjsucafe.Constants.BASE_URL;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class RestaurantListActivity extends AppCompatActivity {

    private static final String TAG = "RestaurantListActivity";
    private static final String API_URL = BASE_URL + "/restaurant";
    private RecyclerView mListView;
    private ArrayList<Restaurant> mRestaurantList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_list);
        mListView = findViewById(R.id.listView);
        mRestaurantList = new ArrayList<>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mListView.setLayoutManager(layoutManager);

        RestaurantListAdapter adapter = new RestaurantListAdapter(mRestaurantList);
        mListView.setAdapter(adapter);

        new FetchRestaurantsTask().execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_icon) {
            startActivity(new Intent(this, ChatActivity.class));
            return true;
        }
        if (item.getItemId() == R.id.action_icon_logout) {
            // Clear shared preference
            getSharedPreferences("com.example.sjsucafe", MODE_PRIVATE).edit().clear().apply();
            startActivity(new Intent(this, LoginActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class FetchRestaurantsTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            String responseString = null;
            try {
                OkHttpClient client = new OkHttpClient();
                String token = getSharedPreferences("com.example.sjsucafe", MODE_PRIVATE).getString("token", "");
                Request request = new Request.Builder()
                        .url(API_URL)
                        .header("Authorization", "Bearer " + token)
                        .build();
                Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    responseString = response.body().string();
                }
            } catch (IOException e) {
                Log.e(TAG, "Error fetching restaurants", e);
            }
            return responseString;
        }

        @Override
        protected void onPostExecute(String responseString) {
            if (responseString != null) {
                try {
                    JSONArray jsonArray = new JSONArray(responseString);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        Restaurant restaurant = new Restaurant();
                        restaurant.setId(jsonObject.getString("_id"));
                        restaurant.setName(jsonObject.getString("name"));
                        restaurant.setAddress(jsonObject.getString("address"));
                        restaurant.setPhone(jsonObject.getString("phone"));
                        restaurant.setEmail(jsonObject.getString("email"));
                        restaurant.setOpeningTime(jsonObject.getString("openingTime"));
                        restaurant.setClosingTime(jsonObject.getString("closingTime"));
                        restaurant.setDescription(jsonObject.getString("description"));
                        restaurant.setLogo(jsonObject.getString("logo"));
                        mRestaurantList.add(restaurant);
                    }
                    mListView.getAdapter().notifyDataSetChanged();
                } catch (JSONException e) {
                    Log.e(TAG, "Error parsing JSON", e);
                }
            } else {
                Log.e(TAG, "Empty response");
            }
        }
    }
}
