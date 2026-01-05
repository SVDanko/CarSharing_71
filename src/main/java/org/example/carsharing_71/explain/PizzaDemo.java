package org.example.carsharing_71.explain;

public class PizzaDemo {
    public static void main(String[] args) {
        // Простая пицца - Маргарита
        Pizza margarita = new Pizza.Builder("тонкое", "томатный")
                .cheese(true)
                .size("small")
                .build();

        System.out.println("Маргарита: " + margarita);
        // Мясная


        // Вегетарианская
        Pizza vegan = new Pizza.Builder("цельнозерновое", "томатный")
                .cheese(true)
                .mushrooms(true)
                .olives(true)
                .size("medium")
                .build();

        System.out.println("Вегетарианская: " + vegan);

        // Салями
        Pizza salyami = new Pizza.Builder("толстое", "барбекю")
                .cheese(true)
                .bacon(true)
                .size("large")
                .build();

        System.out.println("Салями: " + salyami);

    }
}
