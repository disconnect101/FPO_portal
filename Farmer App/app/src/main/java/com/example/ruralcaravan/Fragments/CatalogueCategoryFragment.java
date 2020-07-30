package com.example.ruralcaravan.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ruralcaravan.Adapters.CatalogueCategoryAdapter;
import com.example.ruralcaravan.R;

import java.util.ArrayList;

public class CatalogueCategoryFragment extends Fragment {

    private View rootView;
    private RecyclerView recyclerViewCatalogueCategory;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_catalogue_category, container, false);

        recyclerViewCatalogueCategory = rootView.findViewById(R.id.recyclerViewCatalogueCategory);
        recyclerViewCatalogueCategory.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManagerDaily = new LinearLayoutManager(getActivity());
        recyclerViewCatalogueCategory.setLayoutManager(linearLayoutManagerDaily);

        ArrayList<String> categories = new ArrayList<>();
        categories.add("Seeds");
        categories.add("Fertilizers");
        categories.add("Pesticides");
        categories.add("Equipments");
        categories.add("Others");

        ArrayList<Integer> categoryIcons = new ArrayList<>();
        categoryIcons.add(R.drawable.seeds);
        categoryIcons.add(R.drawable.fertilizers);
        categoryIcons.add(R.drawable.pesticides);
        categoryIcons.add(R.drawable.equipment);
        categoryIcons.add(R.drawable.others);

        CatalogueCategoryAdapter catalogueCategoryAdapter = new CatalogueCategoryAdapter(getActivity(), categories, categoryIcons);
        recyclerViewCatalogueCategory.setAdapter(catalogueCategoryAdapter);
        recyclerViewCatalogueCategory.setOverScrollMode(View.OVER_SCROLL_NEVER);

        return rootView;
    }
}
