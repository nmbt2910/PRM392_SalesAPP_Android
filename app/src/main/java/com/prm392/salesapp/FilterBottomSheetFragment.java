package com.prm392.salesapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.prm392.salesapp.viewmodel.ProductViewModel;

import java.util.ArrayList;
import java.util.List;

public class FilterBottomSheetFragment extends BottomSheetDialogFragment {

    private ProductViewModel productViewModel;

    public static FilterBottomSheetFragment newInstance() {
        return new FilterBottomSheetFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_filter, container, false);

        productViewModel = new ViewModelProvider(requireActivity()).get(ProductViewModel.class);

        final Spinner sortSpinner = view.findViewById(R.id.sort_spinner);
        final Spinner categorySpinner = view.findViewById(R.id.filter_category_spinner);
        final Button applyButton = view.findViewById(R.id.apply_filters_button);
        final Button clearButton = view.findViewById(R.id.clear_button);

        // Setup Sort Spinner
        ArrayAdapter<CharSequence> sortAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.sort_options, android.R.layout.simple_spinner_item);
        sortAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sortSpinner.setAdapter(sortAdapter);

        // Observe and Setup Category Spinner
        productViewModel.getCategories().observe(getViewLifecycleOwner(), categories -> {
            List<String> allCategories = new ArrayList<>();
            allCategories.add("All");
            allCategories.addAll(categories);
            ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, allCategories);
            categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            categorySpinner.setAdapter(categoryAdapter);
        });

        applyButton.setOnClickListener(v -> {
            String selectedCategory = (String) categorySpinner.getSelectedItem();
            int sortOption = sortSpinner.getSelectedItemPosition();

            productViewModel.applyFiltersAndSort(selectedCategory, sortOption);
            dismiss();
        });

        clearButton.setOnClickListener(v -> {
            productViewModel.clearFilters();
            sortSpinner.setSelection(0);
            categorySpinner.setSelection(0);
            dismiss();
        });

        return view;
    }
}