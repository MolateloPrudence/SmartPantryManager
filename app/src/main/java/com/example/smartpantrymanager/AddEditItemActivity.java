package com.example.smartpantrymanager;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.example.smartpantrymanager.database.DatabaseHelper;
import com.example.smartpantrymanager.models.PantryItem;

import java.util.Calendar;
import java.util.Locale;

public class AddEditItemActivity extends AppCompatActivity {

    private EditText etName, etQuantity, etExpiry;
    private AutoCompleteTextView actUnit;
    private MaterialButton btnSave, btnDelete;
    private DatabaseHelper dbHelper;

    private int itemId = -1; // -1 means add mode
    private boolean isEditMode = false;

    private final String[] units = {
            "pieces", "grams", "kilograms", "milliliters", "liters",
            "cups", "tablespoons", "teaspoons", "slices", "heads", "cloves", "bunches"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_item);

        dbHelper = new DatabaseHelper(this);

        // Setup Custom Back Button
        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        // Link Java variables to XML views
        etName = findViewById(R.id.etName);
        etQuantity = findViewById(R.id.etQuantity);
        etExpiry = findViewById(R.id.etExpiry);
        actUnit = findViewById(R.id.actUnit);
        btnSave = findViewById(R.id.btnSave);
        btnDelete = findViewById(R.id.btnDelete);

        // Setup the Unit Dropdown
        ArrayAdapter<String> unitAdapter = new ArrayAdapter<>(this, R.layout.item_dropdown, units);
        actUnit.setAdapter(unitAdapter);

        // Force the dropdown to show immediately when clicked, even without typing
        actUnit.setOnClickListener(v -> actUnit.showDropDown());

        // Hide the keyboard so it doesn't cover the dropdown menu
        actUnit.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                actUnit.showDropDown();
                // Hide soft keyboard
                android.view.inputmethod.InputMethodManager imm = (android.view.inputmethod.InputMethodManager) getSystemService(android.content.Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(actUnit.getWindowToken(), 0);
                }
            }
        });

        // Show Date Picker when clicking Expiry Date
        etExpiry.setOnClickListener(v -> showDatePicker());

        // Check if we are EDITING an existing item
        if (getIntent().hasExtra("ITEM_ID")) {
            itemId = getIntent().getIntExtra("ITEM_ID", -1);
            if (itemId != -1) {
                isEditMode = true;
                loadItemData();
                btnDelete.setVisibility(android.view.View.VISIBLE); // Show delete button
            }
        }

        btnSave.setOnClickListener(v -> saveItem());
        btnDelete.setOnClickListener(v -> deleteItem());
    }

    // Fills the form with existing data if we are editing
    private void loadItemData() {
        PantryItem item = dbHelper.getPantryItem(itemId);
        if (item != null) {
            etName.setText(item.getName());
            etQuantity.setText(String.valueOf(item.getQuantity()));
            actUnit.setText(item.getUnit(), false);
            if (item.getExpiryDate() != null) {
                etExpiry.setText(item.getExpiryDate());
            }
        }
    }

    // Pops up a calendar to pick a date
    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    String date = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, dayOfMonth);
                    etExpiry.setText(date);
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    // Validates input, then saves to database
    private void saveItem() {
        if (!validateInputs()) return;

        String name = etName.getText().toString().trim().toLowerCase();
        double quantity = Double.parseDouble(etQuantity.getText().toString().trim());
        String unit = actUnit.getText().toString().trim();
        String expiry = etExpiry.getText().toString().trim();

        PantryItem item = new PantryItem(name, quantity, unit, expiry.isEmpty() ? null : expiry);

        if (isEditMode) {
            item.setId(itemId);
            dbHelper.updatePantryItem(item);
            Toast.makeText(this, "Item updated", Toast.LENGTH_SHORT).show();
        } else {
            // Check if item already exists to prevent duplicates
            PantryItem existing = dbHelper.findPantryItemByName(name);
            if (existing != null) {
                existing.setQuantity(existing.getQuantity() + quantity);
                dbHelper.updatePantryItem(existing);
                Toast.makeText(this, "Quantity updated for existing item", Toast.LENGTH_SHORT).show();
            } else {
                dbHelper.addPantryItem(item);
                Toast.makeText(this, "Item added", Toast.LENGTH_SHORT).show();
            }
        }
        finish(); // Close screen and go back to the list
    }

    // Checks if the user filled everything out correctly
    private boolean validateInputs() {
        if (TextUtils.isEmpty(etName.getText().toString().trim())) {
            Toast.makeText(this, "Name is required", Toast.LENGTH_SHORT).show();
            return false;
        }

        String quantityStr = etQuantity.getText().toString().trim();
        if (TextUtils.isEmpty(quantityStr)) {
            Toast.makeText(this, "Quantity is required", Toast.LENGTH_SHORT).show();
            return false;
        }

        try {
            double quantity = Double.parseDouble(quantityStr);
            if (quantity <= 0) {
                Toast.makeText(this, "Quantity must be greater than 0", Toast.LENGTH_SHORT).show();
                return false;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid quantity number", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (TextUtils.isEmpty(actUnit.getText().toString().trim())) {
            Toast.makeText(this, "Please select a unit", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    // Pops up a confirmation dialog to delete
    private void deleteItem() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Item")
                .setMessage("Are you sure you want to delete this item?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    dbHelper.deletePantryItem(itemId);
                    Toast.makeText(this, "Item deleted", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}