package com.kurenkievtimur;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Duration;
import java.util.Hashtable;

import static java.lang.System.currentTimeMillis;

public class Main {
    public static void main(String[] args) throws IOException {
        File file1 = new File("src\\main\\resources\\Directory.txt");
        File file2 = new File("src\\main\\resources\\find.txt");

        String[] directory = Files.readAllLines(file1.toPath()).toArray(String[]::new);
        String[] names = Files.readAllLines(file2.toPath()).toArray(String[]::new);

        long start = currentTimeMillis();
        int count = linearSearchCount(directory, names);
        long end = currentTimeMillis();

        Duration duration = Duration.ofMillis(end - start);
        printLinearSearchTime(duration, count, names.length);

        bubbleSortJumpingSearch(duration, directory, names);
        quickSortBinaryBinarySearch(directory, names);
        hashTable(directory, names);
    }

    public static int linearSearchCount(String[] directory, String[] names) {
        int count = 0;
        for (String name : names) {
            for (String line : directory) {
                if (line.contains(name)) {
                    count++;
                    break;
                }
            }
        }

        return count;
    }

    public static void printLinearSearchTime(Duration duration, int count, int size) {
        System.out.println("Start searching (linear search)...");

        String timed = timeConverter(duration);
        System.out.printf("Found %d / %d entries. %s%n", count, size, timed);
    }

    public static boolean bubbleSort(String[] directory, long millis) {
        long start = currentTimeMillis();
        for (int i = 0; i < directory.length - 1; i++) {
            if (currentTimeMillis() - start > millis) {
                return false;
            }
            for (int j = 0; j < directory.length - i - 1; j++) {
                String name1 = directory[j].split(" ", 2)[1];
                String name2 = directory[j + 1].split(" ", 2)[1];
                if (name1.compareTo(name2) > 0) {
                    String temp = directory[j];
                    directory[j] = directory[j + 1];
                    directory[j + 1] = temp;
                }
            }
        }

        return true;
    }

    public static int jumpingSearchCount(String[] directory, String[] names) {
        int count = 0;
        for (String name : names) {
            int curr = 0;
            int prev = 0;
            int last = directory.length;
            int step = (int) Math.floor(Math.sqrt(last));

            while (name.compareTo(directory[curr].split(" ", 2)[1]) < 0) {
                prev = curr;
                curr = Math.min(curr + step, last);
            }

            while (name.compareTo(directory[curr].split(" ", 2)[1]) > 0) {
                curr = curr - 1;
                if (curr <= prev)
                    break;
            }

            if (directory[curr].split(" ", 2)[1].equals(name)) {
                count++;
            }
        }

        return count;
    }

    public static void bubbleSortJumpingSearch(Duration duration, String[] directory, String[] names) {
        long startSort = System.currentTimeMillis();
        boolean isUnLongToo = bubbleSort(directory, duration.toMillis() * 10);
        long endSort = System.currentTimeMillis();
        Duration sortTime = Duration.ofMillis(endSort - startSort);

        int count;
        long startSearch = System.currentTimeMillis();
        if (isUnLongToo) {
            count = jumpingSearchCount(directory, names);
        } else {
            count = linearSearchCount(directory, names);
        }
        long endSearch = System.currentTimeMillis();
        Duration searchTime = Duration.ofMillis(endSearch - startSearch);

        printBubbleSortJumpingSearchTime(sortTime, searchTime, count, names.length, isUnLongToo);
    }

    public static void printBubbleSortJumpingSearchTime(Duration sort, Duration search, int count, int size, boolean isUnTooLong) {
        System.out.println("\nStart searching (bubble sort + jump search)...");

        String summaryTimed = timeConverter(sort.plus(search));
        System.out.printf("Found %d / %d entries. Time taken: %s%n", count, size, summaryTimed);

        String sortTimed = timeConverter(sort);
        System.out.printf("Sorting time: %s %s", sortTimed, isUnTooLong ? "\n" : "STOPPED, moved to linear search\n");

        String searchTimed = timeConverter(search);
        System.out.printf("Searching time: %s%n", searchTimed);
    }


    public static void quickSort(String[] directory, int left, int right) {
        if (left < right) {
            int partition = partition(directory, left, right);

            quickSort(directory, left, partition - 1);
            quickSort(directory, partition + 1, right);
        }
    }

    private static int partition(String[] directory, int left, int right) {
        String pivot = directory[right].split(" ", 2)[1];
        int i = left - 1;
        for (int j = left; j < right; j++) {
            if (directory[j].split(" ", 2)[1].compareTo(pivot) < 0) {
                i++;

                String temp = directory[i];
                directory[i] = directory[j];
                directory[j] = temp;
            }
        }

        String temp = directory[i + 1];
        directory[i + 1] = directory[right];
        directory[right] = temp;

        return i + 1;
    }

    public static int binarySearchCount(String[] directory, String[] names) {
        int count = 0;

        for (String name : names) {
            int left = 0;
            int right = directory.length - 1;

            while (left <= right) {
                int middle = (left + right) / 2;

                if (directory[middle].split(" ", 2)[1].equals(name)) {
                    count++;
                    break;
                } else if (directory[middle].split(" ", 2)[1].compareTo(name) > 0) {
                    right = middle - 1;
                } else {
                    left = middle + 1;
                }
            }
        }

        return count;
    }

    public static void quickSortBinaryBinarySearch(String[] directory, String[] names) {
        long startSort = System.currentTimeMillis();
        quickSort(directory, 0, directory.length - 1);
        long endSort = System.currentTimeMillis();
        Duration sortTime = Duration.ofMillis(endSort - startSort);

        long startSearch = System.currentTimeMillis();
        int count = binarySearchCount(directory, names);
        long endSearch = System.currentTimeMillis();
        Duration searchTime = Duration.ofMillis(endSearch - startSearch);

        printQuickSortBinarySearchTime(sortTime, searchTime, count, names.length);
    }

    public static void printQuickSortBinarySearchTime(Duration sort, Duration search, int count, int size) {
        System.out.println("\nStart searching (quick sort + binary search)...");

        String summaryTimed = timeConverter(sort.plus(search));
        System.out.printf("Found %d / %d entries. Time taken: %s%n", count, size, summaryTimed);

        String sortTimed = timeConverter(sort);
        System.out.printf("Sorting time: %s%n", sortTimed);

        String searchTimed = timeConverter(search);
        System.out.printf("Searching time: %s%n", searchTimed);
    }

    public static Hashtable<String, String> createHashTable(String[] directory) {
        Hashtable<String, String> hashtable = new Hashtable<>();
        for (String line : directory) {
            String[] strings = line.split(" ", 2);
            hashtable.put(strings[1], strings[0]);
        }

        return hashtable;
    }

    public static int hashTableSearchCount(Hashtable<String, String> hashtable, String[] names) {
        int count = 0;

        for (String name : names) {
            if (hashtable.containsKey(name)) {
                count++;
            }
        }

        return count;
    }

    public static void hashTable(String[] directory, String[] names) {
        System.out.println("\nStart searching (hash table)...");

        long startCreate = System.currentTimeMillis();
        Hashtable<String, String> hashTable = createHashTable(directory);
        long endCreate = System.currentTimeMillis();
        Duration createTime = Duration.ofMillis(endCreate - startCreate);

        long startSearch = System.currentTimeMillis();
        int count = hashTableSearchCount(hashTable, names);
        long endSearch = System.currentTimeMillis();
        Duration searchTime = Duration.ofMillis(endSearch - startSearch);

        printHashTableTime(createTime, searchTime, count, names.length);
    }

    public static void printHashTableTime(Duration create, Duration search, int count, int size) {
        String summaryTimed = timeConverter(create.plus(search));
        System.out.printf("Found %d / %d entries. Time taken: %s%n", count, size, summaryTimed);

        String createTimed = timeConverter(create);
        System.out.printf("Creating time: %s%n", createTimed);

        String searchTimed = timeConverter(search);
        System.out.printf("Searching time: %s%n", searchTimed);
    }

    public static String timeConverter(Duration duration) {
        long minutes = duration.toMinutesPart();
        long seconds = duration.toSecondsPart();
        long millis = duration.toMillisPart();

        return String.format("%d min. %d sec. %d ms.", minutes, seconds, millis);
    }
}