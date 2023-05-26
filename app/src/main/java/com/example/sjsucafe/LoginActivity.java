package com.example.sjsucafe;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class LoginActivity extends AppCompatActivity {

    private EditText editTextEmail;
    private EditText editTextPassword;
    private Button buttonLogin;
    private TextView buttonSignup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Check if the user is already logged in
        SharedPreferences prefs = getSharedPreferences("com.example.sjsucafe", MODE_PRIVATE);
        String token = prefs.getString("token", "");
        if (!token.isEmpty()) {
            Intent intent = new Intent(LoginActivity.this, RestaurantListActivity.class);
            finish();
            startActivity(intent);
        }
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
        buttonSignup = findViewById(R.id.buttonSignup);

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = editTextEmail.getText().toString();
                String password = editTextPassword.getText().toString();

                UserLoginTask task = new UserLoginTask(email, password);
                task.execute();
            }
        });

        buttonSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });
    }

    private class UserLoginTask extends AsyncTask<Void, Void, String> {

        private final String mEmail;
        private final String mPassword;

        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                // Create a JSON object with the email and password
                JSONObject loginData = new JSONObject();
                loginData.put("email", mEmail);
                loginData.put("password", mPassword);

                // Send the HTTP POST request to the login endpoint
                URL url = new URL(Constants.BASE_URL + "/user/login");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);
                connection.getOutputStream().write(loginData.toString().getBytes());

                // Read the response from the server
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder stringBuilder = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line + "\n");
                }
                reader.close();

                return stringBuilder.toString();

            } catch (JSONException | IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                JSONObject jsonResult = new JSONObject(result);
                if (!jsonResult.getString("token").isEmpty()) {
                    // Save the token to shared preferences
                    SharedPreferences preferences = getSharedPreferences("com.example.sjsucafe", MODE_PRIVATE);
                    preferences.edit().putString("token", jsonResult.getString("token")).apply();

                    // Login successful, navigate to the main activity
                    Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                    // TODO: navigate to the main activity
                    Intent intent = new Intent(LoginActivity.this, RestaurantListActivity.class);
                    // end the current activity
                    finish();
                    startActivity(intent);
                } else {
                    // Login failed, display an error message
                    Toast.makeText(LoginActivity.this, jsonResult.getString("message"), Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
