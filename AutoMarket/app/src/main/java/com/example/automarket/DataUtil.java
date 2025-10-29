package com.example.automarket;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DataUtil {
    public static List<Car> getCarList() {
        List<Car> cars = new ArrayList<>();

        cars.add(new Car("Volkswagen", "CX-5", 2011, "компетенций, вместительный, комфортный, стильный, комфортный...", 24000, R.drawable.volkswagen_cx5));
        cars.add(new Car("BMW", "Civic", 2005, "Расстояния, комфортный, эффективный, современный, роскошь...", 18000, R.drawable.bmw_civic));
        cars.add(new Car("BMW", "Silverado", 1997, "экологичный, быстрый, мощный, экономичный, роскошный, комфорт...", 22000, R.drawable.bmw_silverado));
        cars.add(new Car("Audi", "Outback", 2011, "удобный, инновационный, комфортный, вместительный, стиль...", 30000, R.drawable.audi_outback));
        cars.add(new Car("Jeep", "Sorento", 2020, "компактный, надежный, мощный, мощный, комфортный, удобный экон...", 35000, R.drawable.jeep_sorento));
        cars.add(new Car("BMW", "RX", 1958, "модный, компактный, комфортный, современный, маневренный, простор...", 40000, R.drawable.bmw_rx));
        cars.add(new Car("Hyundai", "Civic", 2007, "спортивный, экологичный, современный, инновационный, манев...", 15000, R.drawable.hyundai_civic));
        return cars;
    }

    public static Set<String> getBrands(List<Car> cars) {
        Set<String> brands = new HashSet<>();
        for (Car car : cars) {
            brands.add(car.getBrand());
        }
        return brands;
    }

    public static Set<String> getModels(List<Car> cars) {
        Set<String> models = new HashSet<>();
        for (Car car : cars) {
            models.add(car.getModel());
        }
        return models;
    }

    public static Set<Integer> getYears(List<Car> cars) {
        Set<Integer> years = new HashSet<>();
        for (Car car : cars) {
            years.add(car.getYear());
        }
        return years;
    }
}