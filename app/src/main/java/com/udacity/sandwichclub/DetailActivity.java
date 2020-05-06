package com.udacity.sandwichclub;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;
import com.udacity.sandwichclub.model.Sandwich;
import com.udacity.sandwichclub.utils.JsonUtils;

import java.util.List;

public class DetailActivity extends AppCompatActivity {

    public static final String EXTRA_POSITION = "extra_position";
    private static final int DEFAULT_POSITION = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ImageView ingredientsIv = findViewById(R.id.image_iv);

        Intent intent = getIntent();
        if (intent == null) {
            closeOnError();
        }

        int position = intent.getIntExtra(EXTRA_POSITION, DEFAULT_POSITION);
        if (position == DEFAULT_POSITION) {
            // EXTRA_POSITION not found in intent
            closeOnError();
            return;
        }

        String[] sandwiches = getResources().getStringArray(R.array.sandwich_details);
        String json = sandwiches[position];
        Sandwich sandwich = JsonUtils.parseSandwichJson(json);
        Log.d("DetailActivity", "Sandwich: " + sandwich);
        if (sandwich == null) {
            // Sandwich data unavailable
            closeOnError();
            return;
        }

        populateUI(sandwich);
        Picasso.with(this)
                .load(sandwich.getImage())
                .into(ingredientsIv);

        setTitle(sandwich.getMainName());
    }

    private void closeOnError() {
        finish();
        Toast.makeText(this, R.string.detail_error_message, Toast.LENGTH_SHORT).show();
    }

    private void populateUI(Sandwich sandwich) {
        TextView textView = findViewById(R.id.sandwich_name_tv);
        textView.setText(sandwich.getMainName());

        String origin = sandwich.getPlaceOfOrigin();
        if (!origin.equals("")) {
            textView = findViewById(R.id.origin_tv);
            textView.setText(origin);
            textView.setVisibility(View.VISIBLE);
        }

        List<String> alsoKnownList = sandwich.getAlsoKnownAs();
        if (alsoKnownList.size() > 0) {
            textView = findViewById(R.id.also_known_tv);
            textView.setText(convertToString(alsoKnownList));
            findViewById(R.id.also_known_ll).setVisibility(View.VISIBLE);
        }

        textView = findViewById(R.id.description_tv);
        textView.setText(sandwich.getDescription());

        textView = findViewById(R.id.ingredients_tv);
        textView.setText(convertToString(sandwich.getIngredients()));
    }

    private String convertToString(List<String> list) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            if (i != list.size() - 1) {
                builder.append(list.get(i)).append(", ");
            } else {
                builder.append(list.get(i));
            }
        }
        return builder.toString();
    }
}
