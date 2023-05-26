package com.example.sjsucafe;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sjsucafe.objects.Cart;
import com.example.sjsucafe.objects.MenuItem;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CartActivity extends AppCompatActivity {

    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private OkHttpClient client = new OkHttpClient();
    private RecyclerView recyclerView;
    private CartAdapter cartAdapter;
    private Cart cart = new Cart("", "");
    private TextView totalCostTextView;
    private Button placeOrderButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        getUserCartFromApi();
        // Set up the RecyclerView
        recyclerView = findViewById(R.id.cart_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        cartAdapter = new CartAdapter(cart.getItems());
        recyclerView.setAdapter(cartAdapter);

        // Set up the total cost TextView
        totalCostTextView = findViewById(R.id.total_cost_text_view);
        totalCostTextView.setText(String.format("Total Cost: $%.2f", cart.getTotal()));

        placeOrderButton = findViewById(R.id.place_order_button);
        placeOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(CartActivity.this, "Order Placed!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(CartActivity.this, RestaurantListActivity.class);
                startActivity(intent);
            }
        });
    }

    public void getUserCartFromApi() {
        String url = Constants.BASE_URL + "/cart";
        SharedPreferences sharedPreferences = getSharedPreferences("com.example.sjsucafe", MODE_PRIVATE);
        String token = sharedPreferences.getString("token", "");
        Request request = new Request.Builder()
                .url(url)
                .header("Authorization", "Bearer " + token)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                }
                try {
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    System.out.println(jsonObject.toString());
                    cart.setId(jsonObject.getString("_id"));
                    cart.setUserId(jsonObject.getString("userId"));
                    cart.setRestaurantId(jsonObject.getString("restaurantId"));
                    cart.setTotal((float) jsonObject.getDouble("total"));
                    JSONArray itemsJsonArray = jsonObject.getJSONArray("items");
                    List<MenuItem> itemsList = new ArrayList<>();
                    for (int i = 0; i < itemsJsonArray.length(); i++) {
                        JSONObject itemJsonObject = itemsJsonArray.getJSONObject(i);

                        JSONObject itemJsonObjectWithId = itemJsonObject.getJSONObject("itemId");
                        String itemId = itemJsonObjectWithId.getString("_id");
                        String itemName = itemJsonObjectWithId.getString("name");
                        String itemDescription = itemJsonObjectWithId.getString("description");
                        float itemPrice = (float) itemJsonObjectWithId.getDouble("price");
                        String itemImage = itemJsonObjectWithId.getString("image");

                        int itemQuantity = itemJsonObject.getInt("quantity");
                        MenuItem item = new MenuItem(itemId, itemName, itemDescription, itemPrice, itemImage, itemQuantity);
                        itemsList.add(item);
                    }
                    cart.setItems(itemsList);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            cartAdapter.items = cart.getItems();
                            cartAdapter.notifyDataSetChanged();
                            updateTotalCost();
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

        private List<MenuItem> items;

        public CartAdapter(List<MenuItem> items) {
            this.items = items;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item_layout, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            MenuItem item = items.get(position);
            holder.nameTextView.setText(item.getName());
            holder.priceTextView.setText(String.format("$%.2f", item.getPrice()));
            System.out.println(Constants.BASE_URL + "/assets/" + item.getImage());
            holder.removeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    removeCart(item.getId());
                }
            });
            Picasso.get().load(Constants.BASE_URL + "/assets/" + item.getImage()).resize(350, 200).into(holder.previewImageView);
            holder.quantityTextView.setText(String.valueOf(item.getQuantity()));
            holder.plus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int quantity = item.getQuantity();
                    quantity++;
                    updateCart(item.getId(), quantity);
                }
            });

            holder.minus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int quantity = item.getQuantity();
                    quantity--;
                    if(quantity == 0){
                        removeCart(item.getId());
                    } else {
                        updateCart(item.getId(), quantity);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            public TextView nameTextView;
            public TextView priceTextView;
            public TextView quantityTextView;

            public ImageView previewImageView;
            public ImageView plus;
            public ImageView minus;

            public Button removeButton;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                nameTextView = itemView.findViewById(R.id.item_name_text_view);
                priceTextView = itemView.findViewById(R.id.item_price_text_view);
                plus = itemView.findViewById(R.id.increase_item_button);
                minus = itemView.findViewById(R.id.decrease_item_button);
                previewImageView = itemView.findViewById(R.id.item_image_view);
                quantityTextView = itemView.findViewById(R.id.item_quantity_text_view);
                removeButton = itemView.findViewById(R.id.remove_item_button);
            }
        }
    }

    private void updateTotalCost() {
        totalCostTextView.setText(String.format("Total Cost: $%.2f", cart.getTotal()));
    }

    private void updateCart(String itemId, int quantity){
        String url = Constants.BASE_URL + "/cart/add";
        SharedPreferences sharedPreferences = getSharedPreferences("com.example.sjsucafe", MODE_PRIVATE);
        String token = sharedPreferences.getString("token", "");

        OkHttpClient client = new OkHttpClient();
        MediaType mediaType = MediaType.parse("application/json");
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("itemId", itemId);
            requestBody.put("quantity", quantity);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(mediaType, requestBody.toString());
        Request request = new Request.Builder()
                .url(url)
                .put(body)
                .header("Authorization", "Bearer " + token)
                .header("Content-Type", "application/json")
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                // Handle failure
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    getUserCartFromApi();
                } else {
                    // Handle error
                }
            }
        });
    }

    public void removeCart(String itemId){
        String url = Constants.BASE_URL + "/cart/remove";
        SharedPreferences sharedPreferences = getSharedPreferences("com.example.sjsucafe", MODE_PRIVATE);
        String token = sharedPreferences.getString("token", "");

        OkHttpClient client = new OkHttpClient();
        MediaType mediaType = MediaType.parse("application/json");
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("itemId", itemId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(mediaType, requestBody.toString());
        Request request = new Request.Builder()
                .url(url)
                .put(body)
                .header("Authorization", "Bearer " + token)
                .header("Content-Type", "application/json")
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                // Handle failure
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    getUserCartFromApi();
                } else {
                    // Handle error
                }
            }
        });
    }
}
