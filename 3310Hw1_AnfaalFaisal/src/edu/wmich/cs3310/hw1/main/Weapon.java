package edu.wmich.cs3310.hw1.main;

public class Weapon {
    private String itemName;
    private int minStrength;
    private int maxStrength;
    private int currentStrength;
    private String rarity;

    Weapon(String itemName, String minStrength, String maxStrength, String rarity){
        this.itemName = itemName;
        this.minStrength=Integer.parseInt(minStrength);
        this.maxStrength=Integer.parseInt(maxStrength);
        this.rarity=rarity;
    }

    public String getItemName() {
        return itemName;
    }

    public int getMinStrength() {
        return minStrength;
    }

    public int getMaxStrength(){
        return maxStrength;
    }

    public int getCurrentStrength(){
        return currentStrength;
    }

    public String getRarity() {
        return rarity;
    }

    public void setCurrentStrength(int currentStrength){
        this.currentStrength=currentStrength;
    }
}
