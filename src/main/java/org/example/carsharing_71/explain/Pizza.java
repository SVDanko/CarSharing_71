package org.example.carsharing_71.explain;

public class Pizza {
    // Обязательные параметры
    private final String dough; // тесто
    private final String sauce; // соус

    // Опциональные параметры
    private final boolean cheese;       // сыр
    private final boolean mushrooms;    // грибы
    private final boolean olives;    // оливки
    private final boolean bacon;    // бэкон
    private final String size;          // размер

    // Приватный конструктор - только Builder может создать Pizza
    private Pizza(Builder builder) {
        this.dough = builder.dough;
        this.sauce = builder.sauce;
        this.cheese = builder.cheese;
        this.mushrooms = builder.mushrooms;
        this.olives = builder.olives;
        this.bacon = builder.bacon;
        this.size = builder.size;
    }

    public String getDough() {
        return dough;
    }

    public String getSauce() {
        return sauce;
    }

    public boolean isCheese() {
        return cheese;
    }

    public boolean isMushrooms() {
        return mushrooms;
    }

    public boolean isOlives() {
        return olives;
    }

    public String getSize() {
        return size;
    }

    public boolean isBacon() {
        return bacon;
    }

    @Override
    public String toString() {
        return "Pizza{" +
                "dough='" + dough + '\'' +
                ", sauce='" + sauce + '\'' +
                ", cheese=" + cheese +
                ", mushrooms=" + mushrooms +
                ", olives=" + olives +
                ", bacon=" + bacon +
                ", size='" + size + '\'' +
                '}';
    }

    // Статический внутренний класс-строитель
    public static class Builder {
        private String dough; // тесто
        private String sauce; // соус

        private boolean cheese;       // сыр
        private boolean mushrooms;    // грибы
        private boolean olives;    // оливки
        private boolean bacon;    // бэкон
        private String size;          // размер

        // Конструктор Builder с обязательными параметрами
        public Builder(String dough, String sauce) {
            this.dough = dough;
            this.sauce = sauce;
        }

        // Методы для установки опциональных параметров
        // Каждый метод возвращает this для цепочки вызовов
        public Builder cheese(boolean cheese) {
            this.cheese = cheese;
            return this;
        }

        public Builder mushrooms(boolean mushrooms) {
            this.mushrooms = mushrooms;
            return this;
        }

        public Builder olives(boolean olives) {
            this.olives = olives;
            return this;
        }
        public Builder bacon(boolean bacon) {
            this.bacon = bacon;
            return this;
        }

        public Builder size(String size) {
            this.size = size;
            return this;
        }

        public Pizza build() {
            return new Pizza(this);
        }

    }
}
