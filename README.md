# Shopping Wishlist Manager

A Java console application to manage a shopping wishlist. Add items with prices, display the list, sort by price, remove an item and view the total cost. Data is saved to `wishlist.txt`.

**PS No.:** 40 | **Course:** Data Structures and Algorithms (RU-200-01-00029)
**Author:** Harsh Netam (ERP ID: RU-25-10554) | Rungta International Skills University, Bhilai

## Concepts used
- Singly linked list built with a `Node` class (data + next)
- Self-written sorting (bubble sort on the list, no built-in sort or search)
- Methods and classes (`Item`, `Node`, `WishlistManager`, `FileHandler`, `Main`)
- File handling (load at start, save on exit)

## Features
| Option | Action |
|--------|--------|
| 1 | Add Item (price must be greater than 0, IDs start at W101) |
| 2 | Display All |
| 3 | Sort by Price (low to high) |
| 4 | Remove Item by ID (other IDs do not change) |
| 5 | Total Cost and item count |
| 0 | Exit and Save |

## How to run
Requires JDK 11 or later.

```bash
javac Main.java
java Main
```

Or, without a separate compile step:

```bash
java Main.java
```

## Sample run
```
Enter choice: 1
Item: Headphones
Price: 2500
Item added. ID: W101 | Headphones | 2500

Enter choice: 1
Item: Backpack
Price: 1200
Item added. ID: W102 | Backpack | 1200

Enter choice: 3
1. W102 | Backpack | 1200
2. W101 | Headphones | 2500

Enter choice: 5
Total wishlist cost: 3700 | Items: 2

Enter choice: 4
ID: W999
Item W999 not found.
```

## Test cases
| Case | Input | Expected output |
|------|-------|-----------------|
| Normal | Option 1: Headphones, 2500 | `Item added. ID: W101 \| Headphones \| 2500` |
| Boundary | Only W101 exists; option 5 | `Total wishlist cost: 2500 \| Items: 1` |
| Invalid | Option 4, ID: W999 | `Item W999 not found.` |
| Different valid | Existing W101; add Backpack 1200; sort; total | `W102 \| Backpack \| 1200` first, then `W101 \| Headphones \| 2500`; `Total wishlist cost: 3700 \| Items: 2` |

## Data file format
`wishlist.txt` is created on first exit, one item per line:
```
W102|Backpack|1200
W101|Headphones|2500
```

## Time complexity (n = number of items)
| Operation | Time |
|-----------|------|
| Add item | O(n) |
| Display all / total cost | O(n) |
| Remove by ID | O(n) |
| Sort by price | O(n^2) worst case |
| Load / save | O(n) |
