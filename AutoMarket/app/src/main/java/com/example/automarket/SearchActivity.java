package com.example.automarket;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SearchActivity extends AppCompatActivity {
    private AutoCompleteTextView brandField, modelField;
    private Spinner yearFromSpinner, yearToSpinner, costFromSpinner, costToSpinner;
    private Button matchesButton;
    private List<Car> allCars;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        allCars = DataUtil.getCarList();
        initializeViews();
        setupAdapters();
        setupListeners();
    }

    private void initializeViews() {
        brandField = findViewById(R.id.brand_field);
        modelField = findViewById(R.id.model_field);
        yearFromSpinner = findViewById(R.id.year_from);
        yearToSpinner = findViewById(R.id.year_to);
        costFromSpinner = findViewById(R.id.cost_from);
        costToSpinner = findViewById(R.id.cost_to);
        matchesButton = findViewById(R.id.matches_button);
        matchesButton.setEnabled(false);
    }

    private void setupAdapters() {

        ArrayAdapter<String> brandAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line,
                new ArrayList<>(DataUtil.getBrands(allCars)));
        brandField.setAdapter(brandAdapter);


        ArrayAdapter<String> modelAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line,
                new ArrayList<>(DataUtil.getModels(allCars)));
        modelField.setAdapter(modelAdapter);


        List<Integer> years = new ArrayList<>(DataUtil.getYears(allCars));
        Collections.sort(years);
        ArrayAdapter<Integer> yearAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, years);
        yearFromSpinner.setAdapter(yearAdapter);
        yearToSpinner.setAdapter(yearAdapter);


        List<String> costs = Arrays.asList("0", "10000", "20000", "30000", "40000", "50000");
        ArrayAdapter<String> costAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, costs);
        costFromSpinner.setAdapter(costAdapter);
        costToSpinner.setAdapter(costAdapter);


        if (!years.isEmpty()) {
            yearFromSpinner.setSelection(0);
            yearToSpinner.setSelection(years.size() - 1);
        }
        costFromSpinner.setSelection(0);
        costToSpinner.setSelection(costs.size() - 1);
    }

    private void setupListeners() {
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                checkMatches();
            }
        };

        brandField.addTextChangedListener(textWatcher);
        modelField.addTextChangedListener(textWatcher);

        AdapterView.OnItemSelectedListener spinnerListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                checkMatches();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        };

        yearFromSpinner.setOnItemSelectedListener(spinnerListener);
        yearToSpinner.setOnItemSelectedListener(spinnerListener);
        costFromSpinner.setOnItemSelectedListener(spinnerListener);
        costToSpinner.setOnItemSelectedListener(spinnerListener);

        matchesButton.setOnClickListener(v -> returnFilteredCars());
    }

    private void checkMatches() {
        List<Car> filtered = filterCars();
        matchesButton.setEnabled(!filtered.isEmpty());
    }

    private List<Car> filterCars() {
        List<Car> filtered = new ArrayList<>(allCars);


        String brand = brandField.getText().toString();
        if (!brand.isEmpty()) {
            filtered.removeIf(car -> !car.getBrand().toLowerCase().contains(brand.toLowerCase()));
        }

        String model = modelField.getText().toString();
        if (!model.isEmpty()) {
            filtered.removeIf(car -> !car.getModel().toLowerCase().contains(model.toLowerCase()));
        }

        int yearFrom = (int) yearFromSpinner.getSelectedItem();
        int yearTo = (int) yearToSpinner.getSelectedItem();
        filtered.removeIf(car -> car.getYear() < yearFrom || car.getYear() > yearTo);

        double costFrom = Double.parseDouble((String) costFromSpinner.getSelectedItem());
        double costTo = Double.parseDouble((String) costToSpinner.getSelectedItem());
        filtered.removeIf(car -> car.getCost() < costFrom || car.getCost() > costTo);

        return filtered;
    }

    private void returnFilteredCars() {
        Intent result = new Intent();
        result.putParcelableArrayListExtra("filteredCars", new ArrayList<>(filterCars()));
        setResult(RESULT_OK, result);
        finish();
    }
}