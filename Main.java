import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

/**
 * Shopping Wishlist Manager (PS No. 40)
 * Console application: add items with prices, display the wishlist,
 * sort items by price, remove an item and view the total cost.
 * Data is stored in wishlist.txt (loaded at start, saved on exit).
 *
 * Concepts: singly linked list (Node class), manual sorting, methods, file handling.
 *
 * Author: Harsh Netam (ERP ID: RU-25-10554)
 */

public class Main {

    private static final String FILE_NAME = "wishlist.txt";

    public static void main(String[] args) {
        WishlistManager manager = new WishlistManager();
        FileHandler fileHandler = new FileHandler(FILE_NAME);
        fileHandler.load(manager);

        Scanner sc = new Scanner(System.in);
        boolean running = true;
        while (running) {
            showMenu();
            String choice = readLine(sc);
            if (choice == null) {
                break; // input ended
            }
            switch (choice.trim()) {
                case "1": addItem(manager, sc); break;
                case "2": manager.displayAll(); break;
                case "3":
                    manager.sortByPrice();
                    manager.displayAll();
                    break;
                case "4": removeItem(manager, sc); break;
                case "5":
                    System.out.println("Total wishlist cost: " + Item.formatPrice(manager.totalCost())
                            + " | Items: " + manager.getSize());
                    break;
                case "0": running = false; break;
                default: System.out.println("Invalid choice. Please enter 0 to 5.");
            }
        }

        fileHandler.save(manager);
        System.out.println("Wishlist saved to " + FILE_NAME + ". Goodbye!");
        sc.close();
    }

    private static void showMenu() {
        System.out.println();
        System.out.println("===== Shopping Wishlist Manager =====");
        System.out.println("1. Add Item");
        System.out.println("2. Display All");
        System.out.println("3. Sort by Price (low to high)");
        System.out.println("4. Remove Item");
        System.out.println("5. Total Cost");
        System.out.println("0. Exit and Save");
        System.out.print("Enter choice: ");
    }

    private static String readLine(Scanner sc) {
        return sc.hasNextLine() ? sc.nextLine() : null;
    }

    private static void addItem(WishlistManager manager, Scanner sc) {
        System.out.print("Item: ");
        String name = readLine(sc);
        if (name == null) return;
        name = name.trim().replace('|', '-'); // '|' is the file separator
        if (name.isEmpty()) {
            System.out.println("Item name cannot be empty.");
            return;
        }
        System.out.print("Price: ");
        String priceText = readLine(sc);
        if (priceText == null) return;
        double price;
        try {
            price = Double.parseDouble(priceText.trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid price. Please enter a number.");
            return;
        }
        Item item = manager.addItem(name, price);
        if (item == null) {
            System.out.println("Price must be greater than 0.");
        } else {
            System.out.println("Item added. ID: " + item);
        }
    }

    private static void removeItem(WishlistManager manager, Scanner sc) {
        System.out.print("ID: ");
        String id = readLine(sc);
        if (id == null) return;
        id = id.trim();
        if (manager.removeById(id)) {
            System.out.println("Item " + id + " removed.");
        } else {
            System.out.println("Item " + id + " not found.");
        }
    }
}

/** One wishlist entry: ID, name and price. */
class Item {
    private final String id;
    private final String name;
    private final double price;

    Item(String id, String name, double price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }

    String getId() { return id; }
    String getName() { return name; }
    double getPrice() { return price; }

    /** Prints whole prices without decimals (2500) and others with two decimals (99.50). */
    static String formatPrice(double p) {
        if (p == Math.floor(p) && p < 1e15) {
            return String.valueOf((long) p);
        }
        return String.format("%.2f", p);
    }

    @Override
    public String toString() {
        return id + " | " + name + " | " + formatPrice(price);
    }
}

/** One node of the singly linked list: data + next. */
class Node {
    Item data;
    Node next;

    Node(Item data) {
        this.data = data;
        this.next = null;
    }
}

/** Singly linked list of wishlist items with all wishlist operations. */
class WishlistManager {
    private Node head;
    private int size;
    private int nextId = 101; // first generated ID is W101

    /** Adds a new item at the end of the list. Returns the item, or null if the price is invalid. */
    Item addItem(String name, double price) {
        if (price <= 0 || Double.isNaN(price) || Double.isInfinite(price)) {
            return null;
        }
        Item item = new Item("W" + nextId, name, price);
        nextId++;
        append(item);
        return item;
    }

    /** Adds an item read from the file (keeps its saved ID) and moves the ID counter forward. */
    void addLoaded(String id, String name, double price) {
        append(new Item(id, name, price));
        if (id.length() > 1 && (id.charAt(0) == 'W' || id.charAt(0) == 'w')) {
            try {
                int num = Integer.parseInt(id.substring(1));
                if (num >= nextId) {
                    nextId = num + 1;
                }
            } catch (NumberFormatException e) {
                // ignore: ID is not in the W### format
            }
        }
    }

    private void append(Item item) {
        Node node = new Node(item);
        if (head == null) {
            head = node;
        } else {
            Node cur = head;
            while (cur.next != null) {
                cur = cur.next;
            }
            cur.next = node;
        }
        size++;
    }

    /** Prints every item with a serial number. */
    void displayAll() {
        if (head == null) {
            System.out.println("Wishlist is empty.");
            return;
        }
        int n = 1;
        for (Node cur = head; cur != null; cur = cur.next) {
            System.out.println(n + ". " + cur.data);
            n++;
        }
    }

    /** Sorts by price, low to high, using a hand-written bubble sort (no built-in sort). IDs do not change. */
    void sortByPrice() {
        if (head == null || head.next == null) {
            return;
        }
        boolean swapped;
        do {
            swapped = false;
            for (Node cur = head; cur.next != null; cur = cur.next) {
                if (cur.data.getPrice() > cur.next.data.getPrice()) {
                    Item temp = cur.data;
                    cur.data = cur.next.data;
                    cur.next.data = temp;
                    swapped = true;
                }
            }
        } while (swapped);
    }

    /** Removes the item with the given ID (linear search). Other IDs stay unchanged. */
    boolean removeById(String id) {
        Node prev = null;
        Node cur = head;
        while (cur != null) {
            if (cur.data.getId().equalsIgnoreCase(id)) {
                if (prev == null) {
                    head = cur.next;
                } else {
                    prev.next = cur.next;
                }
                size--;
                return true;
            }
            prev = cur;
            cur = cur.next;
        }
        return false;
    }

    double totalCost() {
        double total = 0;
        for (Node cur = head; cur != null; cur = cur.next) {
            total += cur.data.getPrice();
        }
        return total;
    }

    int getSize() { return size; }

    Node getHead() { return head; }
}

/** Loads the wishlist from a text file and saves it back. One item per line: ID|name|price. */
class FileHandler {
    private final String fileName;

    FileHandler(String fileName) {
        this.fileName = fileName;
    }

    void load(WishlistManager manager) {
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) {
                    continue;
                }
                String[] parts = line.split("\\|");
                if (parts.length != 3) {
                    continue; // skip a damaged line
                }
                try {
                    double price = Double.parseDouble(parts[2].trim());
                    if (price > 0) {
                        manager.addLoaded(parts[0].trim(), parts[1].trim(), price);
                    }
                } catch (NumberFormatException e) {
                    // skip a line with a bad price
                }
            }
        } catch (IOException e) {
            // file not found on first run: start with an empty wishlist
        }
    }

    void save(WishlistManager manager) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(fileName))) {
            for (Node cur = manager.getHead(); cur != null; cur = cur.next) {
                pw.println(cur.data.getId() + "|" + cur.data.getName() + "|" + Item.formatPrice(cur.data.getPrice()));
            }
        } catch (IOException e) {
            System.out.println("Error: could not save to " + fileName);
        }
    }
}
