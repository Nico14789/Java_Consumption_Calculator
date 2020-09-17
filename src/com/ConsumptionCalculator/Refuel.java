package com.ConsumptionCalculator;

import java.io.Serializable;
import java.util.Comparator;

public class Refuel implements Serializable, Comparable <Refuel> {
    private final float oldKilometerReading;
    private final float newKilometerReading;
    private final float liter;

    public Refuel(float i_oKilo, float i_nKilo, float i_liter) {
        this.oldKilometerReading = i_oKilo;
        this.newKilometerReading = i_nKilo;
        this.liter = i_liter;
    }

    public float getOldKilometerReading() {
        return oldKilometerReading;
    }

    public float getNewKilometerReading() {
        return newKilometerReading;
    }

    public float getLiter() {
        return liter;
    }

    public float getConsumption() {
        if( (this.newKilometerReading - this.oldKilometerReading) <= 0){
            return 0;
        }
        return this.liter * 100 / (this.newKilometerReading - this.oldKilometerReading);
    }

    public int compareTo(Refuel o) { //method to compare two Refuel objects via comparing all Fields individual
        return Comparator.comparing(Refuel::getOldKilometerReading)
                .thenComparing(Refuel::getNewKilometerReading)
                .thenComparing(Refuel::getLiter)
                .compare(this, o);
    }
}