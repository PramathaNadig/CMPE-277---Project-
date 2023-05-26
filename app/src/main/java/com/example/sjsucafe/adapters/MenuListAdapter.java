package com.example.sjsucafe.adapters;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sjsucafe.Constants;
import com.example.sjsucafe.R;
import com.example.sjsucafe.objects.MenuItem;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class MenuListAdapter extends RecyclerView.Adapter<MenuListAdapter.MenuItemViewHolder> {
    public static final MediaType JSON
            = MediaType.get("application/json; charset=utf-8");

    private List<MenuItem> menuItems;

    public MenuListAdapter(List<MenuItem> menuItems) {
        this.menuItems = menuItems;
    }

    @NonNull
    @Override
    public MenuItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_menu, parent, false);
        return new MenuItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MenuItemViewHolder holder, int position) {
        MenuItem menuItem = menuItems.get(position);
        holder.bind(menuItem);
        holder.addToCartButton.setOnClickListener(v -> {
            SharedPreferences sharedPreferences = v.getContext().getSharedPreferences("com.example.sjsucafe", MODE_PRIVATE);
            String token = sharedPreferences.getString("token", "");

            System.out.println("Add to cart button clicked");
            JSONObject requestBody = new JSONObject();
            try {
                requestBody.put("itemId", menuItem.getId());
                requestBody.put("quantity", 1); // or any other quantity you want to add
            } catch (JSONException e) {
                e.printStackTrace();
            }

            RequestBody body = RequestBody.create(requestBody.toString(), JSON);

            // create the request object with the necessary headers and body
            Request request = new Request.Builder()
                    .url(Constants.BASE_URL + "/cart/add")
                    .addHeader("Authorization", "Bearer " + token)
                    .put(body)
                    .build();

            // create the OkHttpClient instance and enqueue the request
            OkHttpClient client = new OkHttpClient();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    // handle the error here
                    e.printStackTrace();
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    // handle the response here
                    System.out.println("Item added to cart successfully");
                    System.out.println(response.body().string());
                    // Show toast on UI thread
                    v.post(() -> {
                        Toast.makeText(v.getContext(), "Item added to cart successfully", Toast.LENGTH_SHORT).show();
                    });
                }
            });
        });
    }

    @Override
    public int getItemCount() {
        System.out.println("menuItems.size() = " + menuItems.size());
        return menuItems.size();
    }

    public static class MenuItemViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageView;
        private TextView nameTextView;
        private TextView descriptionTextView;
        private TextView priceTextView;

        private Button addToCartButton;

        public MenuItemViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.itemImageView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            descriptionTextView = itemView.findViewById(R.id.descriptionTextView);
            priceTextView = itemView.findViewById(R.id.priceTextView);
            addToCartButton = itemView.findViewById(R.id.addToCartButton);
        }

        public void bind(MenuItem menuItem) {
            nameTextView.setText(menuItem.getName());
            descriptionTextView.setText(menuItem.getDescription());
            priceTextView.setText(String.format("$%.2f", menuItem.getPrice()));
            Picasso.get().load(Constants.BASE_URL + "/assets/" + menuItem.getImage()).resize(350, 200).into(imageView);
        }
    }
}

