package com.example.ruralcaravan.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ruralcaravan.Activities.ListItemsActivity;
import com.example.ruralcaravan.Activities.PlanActivity;
import com.example.ruralcaravan.Adapters.CatalogueAdapter;
import com.example.ruralcaravan.Adapters.ProductsUnderPlanAdapter;
import com.example.ruralcaravan.R;
import com.example.ruralcaravan.ResponseClasses.ItemDetailedResponse;

import java.util.ArrayList;
import java.util.Arrays;

public class ProductsUnderPlan extends Fragment {

    private View rootView;
    private RecyclerView recyclerViewProductsUnderPlan;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView =  inflater.inflate(R.layout.fragment_products_under_plan, container, false);
        recyclerViewProductsUnderPlan = rootView.findViewById(R.id.recyclerViewProductsUnderPlan);
        recyclerViewProductsUnderPlan.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerViewProductsUnderPlan.setLayoutManager(linearLayoutManager);
        ProductsUnderPlanAdapter productsUnderPlanAdapter = new ProductsUnderPlanAdapter(getActivity(),
                Arrays.asList(((PlanActivity)getActivity()).getItemsDetails()));
        recyclerViewProductsUnderPlan.setAdapter(productsUnderPlanAdapter);

        return rootView;
    }

}
