package com.example.automarket;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CarAdapter extends RecyclerView.Adapter<CarAdapter.ViewHolder> {
    private List<Car> cars;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public TextView brandModelView;
        public TextView yearView;
        public TextView costView;

        public ViewHolder(View view) {
            super(view);
            imageView = view.findViewById(R.id.car_image);
            brandModelView = view.findViewById(R.id.brand_model);
            yearView = view.findViewById(R.id.year);
            costView = view.findViewById(R.id.cost);
        }
    }

    public CarAdapter(List<Car> cars) {
        this.cars = cars;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_car, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Car car = cars.get(position);
        holder.imageView.setImageResource(car.getImageRes());
        holder.brandModelView.setText(car.getBrand() + " " + car.getModel());
        holder.yearView.setText(String.valueOf(car.getYear()));
        holder.costView.setText(String.format("%.0f", car.getCost()));
    }

    @Override
    public int getItemCount() {
        return cars.size();
    }

    public void updateList(List<Car> newList) {
        cars = newList;
        notifyDataSetChanged();
    }
}