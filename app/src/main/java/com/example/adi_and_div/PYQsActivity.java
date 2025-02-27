package com.example.adi_and_div;

import android.os.Bundle;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.adi_and_div.LinksAdapter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PYQsActivity extends AppCompatActivity {

    private Spinner branchSpinner, semesterSpinner, examTypeSpinner;
    private RecyclerView linksRecyclerView;

    private LinksAdapter linksAdapter;
    private Map<String, List<String>> pyqsData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pyqs);

        branchSpinner = findViewById(R.id.spinner_branch);
        semesterSpinner = findViewById(R.id.spinner_semester);
        examTypeSpinner = findViewById(R.id.spinner_exam_type);
        linksRecyclerView = findViewById(R.id.linksRecyclerView);

        // Initialize dummy data
        initializeData();

        // Initialize RecyclerView
        linksRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        linksAdapter = new LinksAdapter(this, Arrays.asList("No links available"));
        linksRecyclerView.setAdapter(linksAdapter);

        // Set up spinners
        setupSpinners();

        // Update links when spinner values change
        setupSpinnerListeners();
    }

    private void initializeData() {
        pyqsData = new HashMap<>();
        pyqsData.put("CSE_1_Midsem", Arrays.asList("https://example.com/link1", "https://example.com/link2"));
        pyqsData.put("CSE_1_Endsem", Arrays.asList("https://example.com/link3", "https://example.com/link4"));
        pyqsData.put("ECE_1_Midsem", Arrays.asList("https://example.com/link5", "https://example.com/link6"));
        pyqsData.put("ECE_2_Endsem", Arrays.asList("https://example.com/link7", "https://example.com/link8"));
        pyqsData.put("ECE_2_Endsem", Arrays.asList("https://example.com/link7", "https://example.com/link8"));
        pyqsData.put("CSE_2_Endsem", Arrays.asList("https://example.com/link7", "https://example.com/link8"));
        pyqsData.put("CSE_2_Endsem", Arrays.asList("https://example.com/link7", "https://example.com/link8"));
        pyqsData.put("MECH_1_Endsem", Arrays.asList("https://example.com/link7", "https://example.com/link8"));
        pyqsData.put("MECH_2_Endsem", Arrays.asList("https://example.com/link7", "https://example.com/link8"));
        pyqsData.put("CSE_2_Midsem", Arrays.asList("https://example.com/link7", "https://example.com/link8"));
    }

    private void setupSpinners() {
        String[] branches = {"CSE", "ECE", "MECH"};
        String[] semesters = {"1", "2", "3", "4", "5"};
        String[] examTypes = {"Midsem", "Endsem"};

        ArrayAdapter<String> branchAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, branches);
        branchAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        branchSpinner.setAdapter(branchAdapter);

        ArrayAdapter<String> semesterAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, semesters);
        semesterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        semesterSpinner.setAdapter(semesterAdapter);

        ArrayAdapter<String> examTypeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, examTypes);
        examTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        examTypeSpinner.setAdapter(examTypeAdapter);
    }

    private void setupSpinnerListeners() {
        AdapterView.OnItemSelectedListener listener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, android.view.View view, int position, long id) {
                updateLinks();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        };

        branchSpinner.setOnItemSelectedListener(listener);
        semesterSpinner.setOnItemSelectedListener(listener);
        examTypeSpinner.setOnItemSelectedListener(listener);
    }

    private void updateLinks() {
        String branch = (String) branchSpinner.getSelectedItem();
        String semester = (String) semesterSpinner.getSelectedItem();
        String examType = (String) examTypeSpinner.getSelectedItem();

        String key = branch + "_" + semester + "_" + examType;
        Log.d("PYQsActivity", "Key: " + key);

        List<String> links = pyqsData.getOrDefault(key, Arrays.asList("No links available"));
        linksAdapter = new LinksAdapter(this, links);
        linksRecyclerView.setAdapter(linksAdapter);
    }
}
