package com.udacity.sandwichclub;

import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
            String alsoKnownListString = convertToString(alsoKnownList);
            textView = findViewById(R.id.also_known_tv);
            textView.setText(alsoKnownListString);
            TextView labelTextView = findViewById(R.id.also_known_label_tv);
            LinearLayout linearLayout = findViewById(R.id.also_known_ll);

            // If alsoKnown list is too big to fit the screen, display it in the next line
            int textWidth = getTextWidth(alsoKnownListString, textView) +
                    getTextWidth(getString(R.string.detail_also_known_as_label), labelTextView);
            if (textWidth >= getDisplayWidth()) {
                linearLayout.setOrientation(LinearLayout.VERTICAL);
                setPaddingInDPs(textView, 16, 0, 16, 0);
            }
            linearLayout.setVisibility(View.VISIBLE);
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

    private int getTextWidth(String text, TextView tv) {
        Rect bounds = new Rect();
        Paint textPaint = tv.getPaint();
        textPaint.getTextBounds(text, 0, text.length(), bounds);
        return bounds.width();
    }

    private int getDisplayWidth() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.x;
    }

    private void setPaddingInDPs(View view, int left, int top, int right, int bottom) {
        float scale = getResources().getDisplayMetrics().density;
        int leftDP = (int) (left * scale + 0.5f);
        int topDP = (int) (top * scale + 0.5f);
        int rightDP = (int) (right * scale + 0.5f);
        int bottomDP = (int) (bottom * scale + 0.5f);
        view.setPadding(leftDP, topDP, rightDP, bottomDP);
    }
}
