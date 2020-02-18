package edu.wmich.cs3310.hw1.main;

import java.io.*;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.Random;

public class Main {
    static LinkedList<Weapon> itemsLinkedList;
    static Weapon[] inventory;
    static Random random = new Random();
    static FileWriter fileWriter;
    static StringBuilder stringBuilder;


    /**
     * Reads data from file, then using method {@code split(String regex)}
     * of class {@code String} store read data as {@code Weapon} object.
     */
    static void readFile() {
        itemsLinkedList = new LinkedList<>();
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(new File("hw1input_items.txt")));
            String tmp, weapon[];
            //skip first line
            bufferedReader.readLine();
            while ((tmp = bufferedReader.readLine()) != null) {
                weapon = tmp.split(",");
                itemsLinkedList.add(new Weapon(weapon[0], weapon[1], weapon[2], weapon[3]));
            }
        } catch (IOException e) {
            System.err.println("Exception while reading items.txt");
            e.printStackTrace();
        }
    }

    /**
     * Fills whole inventory by items from itemsLinkedList and randomly set currentStrength
     * between minStrength and maxStrength values both inclusively.
     * If all elements from itemsLinkedList was used, again start from first element.
     *
     * @param bagsNum number of bags
     */
    static void fillInventory(int bagsNum) {
        int currentStrength, minStrength, maxStrength;
        inventory = new Weapon[bagsNum * 25];
        int j = 0;
        for (int i = 0; i < inventory.length; i++) {
            minStrength = itemsLinkedList.get(j).getMinStrength();
            maxStrength = itemsLinkedList.get(j).getMaxStrength();
            currentStrength = random.nextInt(maxStrength - minStrength + 1) + minStrength;
            itemsLinkedList.get(j).setCurrentStrength(currentStrength);
            inventory[i] = itemsLinkedList.get(j);
            j = (j + 1) % itemsLinkedList.size();
        }
    }

    /**
     * Search (with option to choose linear or binary approach and number of times) randomly choose item
     * from origin itemsLinkedList by name and rarity.
     * Note: in case if there was a chosen binarySearch, This can't guarantee good results because of the inventory
     * sorted by name but we need to find item which will be same per name and rarity
     *
     * @param count    repeats count for search
     * @param isLinear in case true will be use linearSearch, otherwise binarySearch
     */
    static void search(long count, boolean isLinear) {
        int foundIndex = -1;
        Weapon key = itemsLinkedList.get(0);
        long start, end = 0;
        for (int i = 0; i < count; i++) {
            foundIndex = -1;
            key = itemsLinkedList.get(random.nextInt(itemsLinkedList.size()));
            start = System.nanoTime();
            if (isLinear) {
                for (int j = 0; j < inventory.length; j++) {
                    if (inventory[j].getItemName().equals(key.getItemName()) && inventory[j].getRarity().equals(key.getRarity())) {
                        foundIndex = j;
                        break;
                    }
                }
            } else {
                foundIndex = Arrays.binarySearch(inventory, key, Comparator.comparing(Weapon::getItemName));
            }
            end += (System.nanoTime() - start);
        }
        if (count == 1) {
            stringBuilder.append(isLinear ? "Linear search.\n" : "Binary search.\n");
            stringBuilder.append("Searching for ").append(key.getRarity()).append(" ").append(key.getItemName()).append("...\n");
            if (foundIndex > -1 && inventory[foundIndex].getRarity().equals(key.getRarity())) {
                stringBuilder.append("Found in bag ").append(foundIndex / 25 + 1).append(", slot ").append(foundIndex - (25 * (foundIndex / 25)) + 1);
                stringBuilder.append(". Strength: ").append(inventory[foundIndex].getCurrentStrength()).append(".\n");
            } else {
                stringBuilder.append("Item not found\n");
            }
            stringBuilder.append("Single search time: ").append(end).append(" nanoseconds.\n");
        } else {
            stringBuilder.append("Average search time: ").append(end / count).append(" nanoseconds.\n");
        }
    }

    /**
     * Prints first 5 items from each bag
     */
    static void printInventory() {
        for (int i = 0; i < inventory.length / 25; i++) {
            stringBuilder.append("Bag ").append(i + 1).append(":\n");
            for (int j = 0; j < 5; j++) {
                stringBuilder.append(inventory[i * 25 + j].getRarity()).append(" ").append(inventory[i * 25 + j].getItemName()).append(", ");
                stringBuilder.append(inventory[i * 25 + j].getCurrentStrength()).append('\n');
            }
            stringBuilder.append("...\n");
        }
    }

    /**
     * Sorts inventory from index {@code fromIndex}, inclusive, to index {@code toIndex}, exclusive,
     * by name using default java sort function which provides stable merge sort, guarantees complexity O(n log n)
     * in worst case.
     *
     * @param fromIndex index of first element (inclusive)
     * @param toIndex   index of last element (exclusive)
     */
    static void sortByName(int fromIndex, int toIndex) {
        Arrays.sort(inventory, fromIndex, toIndex, Comparator.comparing(Weapon::getItemName));
    }

    /**
     * Sorts inventory on range from index {@code fromIndex}, inclusive, to index {@code toIndex}, exclusive
     * with same names by currentStrength.
     * Approach: compare current itemName with next if names are different calls sort method and sort on range
     * startIndex to i+1. To avoid situation when last item has same name as previous when i == toIndex-2
     * sort range from startIndex, inclusive, to toIndex, exclusive.
     *
     * @param fromIndex index of first element (inclusive)
     * @param toIndex   index of last element (exclusive)
     */
    static void sortByCurrentStrength(int fromIndex, int toIndex) {
        int startIndex = fromIndex;
        for (int i = fromIndex; i < toIndex - 1; i++) {
            if (!inventory[i].getItemName().equals(inventory[i + 1].getItemName())) {
                Arrays.sort(inventory, startIndex, i + 1, Comparator.comparing(Weapon::getCurrentStrength));
                startIndex = i + 1;
            } else if (i == toIndex - 2) {
                Arrays.sort(inventory, startIndex, toIndex, Comparator.comparing(Weapon::getCurrentStrength));
            }
        }
    }

    /**
     * At the beginning  creates new inventory.
     * Sorts each bag separately be name then by each name subgroup by currentStrength
     * then sort whole inventory at time.
     */
    static void multiMergeSort() {
        fillInventory(100000/*inventory.length / 25*/);
        long start = System.nanoTime(), end;
        int bag = 25;
        for (int i = 0; i < inventory.length / 25; i++) {
            sortByName(i * 25, bag);
            sortByCurrentStrength(i * 25, bag);
            bag += 25;
        }
        sortByName(0, inventory.length);
        sortByCurrentStrength(0, inventory.length);
        end = System.nanoTime() - start;
        stringBuilder.append("Multi-Merge Sort time: ").append(end).append('\n');
    }

    public static void main(String[] args) {
        int[] testCases = {1, 10, 100, 1000, 10000};
        stringBuilder = new StringBuilder();
        readFile();
        long start, end;
        for (int testCase : testCases) {
            stringBuilder = new StringBuilder();
            stringBuilder.append("n= ").append(testCase).append('\n');
            fillInventory(testCase);
            if (testCase <= 10) {
                stringBuilder.append("Bags before sorting:\n");
                printInventory();
            }
            search(1, true);
            search(1000, true);
            start = System.nanoTime();
            sortByName(0, inventory.length);
            sortByCurrentStrength(0, inventory.length);
            end = System.nanoTime() - start;
            stringBuilder.append("Simple sort time: ").append(end).append('\n');
            multiMergeSort();
            if (testCase <= 10) {
                stringBuilder.append("Bags after sorting:\n");
                printInventory();
            }
            search(1, false);
            search(1000, false);
            System.out.println(stringBuilder);
            //create new file or use existing to save output
            try {
                fileWriter = new FileWriter(new File(testCase + ".txt"));
                fileWriter.write(stringBuilder.toString());
                fileWriter.flush();
            } catch (IOException e) {
                System.out.println("Writing/creating error");
                e.printStackTrace();
            }
        }
    }
}
