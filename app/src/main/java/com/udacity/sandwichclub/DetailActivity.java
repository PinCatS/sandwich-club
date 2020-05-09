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

        if (getActionBar() != null)
            getActionBar().setDisplayHomeAsUpEnabled(true);

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
        // populate sandwich main name
        TextView textView = findViewById(R.id.sandwich_name_tv);
        textView.setText(sandwich.getMainName());

        // populate sandwich origin if presents
        String origin = sandwich.getPlaceOfOrigin();
        if (!origin.equals("")) {
            textView = findViewById(R.id.origin_tv);
            textView.setText(origin);
            textView.setVisibility(View.VISIBLE);
        }

        // populate "also known as" list if presents
        List<String> alsoKnownList = sandwich.getAlsoKnownAs();
        if (alsoKnownList.size() > 0) {
            String alsoKnownListString = convertToString(alsoKnownList);
            textView = findViewById(R.id.also_known_tv);
            textView.setText(alsoKnownListString);
            TextView labelTextView = findViewById(R.id.also_known_label_tv);
            LinearLayout linearLayout = findViewById(R.id.also_known_ll);

            // if alsoKnown list is too big to fit the screen, display it in the next line
            int textWidth = getTextWidth(textView) + getTextWidth(labelTextView);
            if (textWidth >= getDisplayWidth()) {
                linearLayout.setOrientation(LinearLayout.VERTICAL);
                setPaddingInDPs(textView, 16, 0, 16, 0);
            }
            linearLayout.setVisibility(View.VISIBLE);
        }

        // populate the description
        textView = findViewById(R.id.description_tv);
        textView.setText(sandwich.getDescription());

        // populate the ingredients
        textView = findViewById(R.id.ingredients_tv);
        textView.setText(convertToString(sandwich.getIngredients()));
    }

    /**
     * Converts list of items to the string of items separated by comma.
     *
     * @param list The List of items to convert
     * @return The string in a form "item, item, item"...
     */
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

    /**
     * Calculates the width of the text in a text view.
     *
     * @param tv The text view where a text is populated
     * @return the width in pixels of the text in a text view
     */
    private int getTextWidth(TextView tv) {
        String text = tv.getText().toString();
        Rect bounds = new Rect();
        Paint textPaint = tv.getPaint();
        textPaint.getTextBounds(text, 0, text.length(), bounds);
        return bounds.width();
    }

    /**
     * Calculates the display width.
     *
     * @return the width in pixels of the display
     */
    private int getDisplayWidth() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.x;
    }

    /**
     * Set paddings to the view in DP units.
     *
     * @param view   The view for which paddings should be set
     * @param left   The left padding in DP units
     * @param top    The top padding in DP units
     * @param right  The right padding in DP units
     * @param bottom The bottom padding in DP units
     */
    private void setPaddingInDPs(View view, int left, int top, int right, int bottom) {
        float scale = getResources().getDisplayMetrics().density;
        int leftDP = (int) (left * scale + 0.5f);
        int topDP = (int) (top * scale + 0.5f);
        int rightDP = (int) (right * scale + 0.5f);
        int bottomDP = (int) (bottom * scale + 0.5f);
        view.setPadding(leftDP, topDP, rightDP, bottomDP);
    }
}
