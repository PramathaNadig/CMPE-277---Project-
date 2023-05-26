package com.example.sjsucafe;

import static com.example.sjsucafe.Constants.BASE_URL;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class RestaurantListAdapter extends RecyclerView.Adapter<RestaurantListAdapter.RestaurantViewHolder> {

    private static List<Restaurant> restaurantList;

    public RestaurantListAdapter(List<Restaurant> restaurantList) {
        RestaurantListAdapter.restaurantList = restaurantList;
    }

    @NonNull
    @Override
    public RestaurantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_restaurant, parent, false);
        return new RestaurantViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RestaurantViewHolder holder, int position) {
        Restaurant restaurant = restaurantList.get(position);
        holder.bind(restaurant);
    }

    @Override
    public int getItemCount() {
        return restaurantList.size();
    }

    public static class RestaurantViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView nameTextView;
        private TextView addressTextView;
        private ImageView logoImageView;

        public RestaurantViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            addressTextView = itemView.findViewById(R.id.addressTextView);
            logoImageView = itemView.findViewById(R.id.logoImageView);
            itemView.setOnClickListener(this);
        }

        public void bind(Restaurant restaurant) {
            nameTextView.setText(restaurant.getName());
            addressTextView.setText(restaurant.getAddress());
            // Load the restaurant logo image using a library like Glide or Picasso
            // logoImageView.setImageResource(restaurant.getLogo());
            // Crop the image to a square
            Picasso.get().load(BASE_URL + "/assets/" + restaurant.getLogo()).into(logoImageView);
        }

        @Override
        public void onClick(View v) {
            System.out.println("RestaurantViewHolder.onClick");
            int position = getAdapterPosition();
            Restaurant restaurant = restaurantList.get(position);
            Intent intent = new Intent(v.getContext(), MenuActivity.class);
            intent.putExtra("restaurantId", restaurant.getId());
            v.getContext().startActivity(intent);
        }
    }
}
