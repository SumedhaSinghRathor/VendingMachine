import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Collections;

enum Coin {
    PENNY(1), NICKEL(5), DIME(10), QUARTER(25);

    private final int denomination;

    Coin(int denomination) {
        this.denomination = denomination;
    }

    public int getDenomination() {
        return denomination;
    }
}

enum Item {
    SKITTLES("Skittles", 15), TWIX("Twix", 35), SNICKERS("Snickers", 25);

    private final String name;
    private final int price;

    Item(String name, int price) {
        this.name = name;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

}

final class Inventory<T> {
    private final Map<T, Integer> inventory = new HashMap<>();

    public int getQuantity(T item) {
        return inventory.getOrDefault(item, 0);
    }

    public void add(T item, int quantity) {
        int count = inventory.getOrDefault(item, 0);
        inventory.put(item, count + quantity);
    }

    public void deduct(T item) {
        if(hasItem(item)) {
            int count = inventory.get(item);
            inventory.put(item, count - 1);
        }
    }

    public boolean hasItem(T item) {
        return getQuantity(item) > 0;
    }

    public void clear() {
        inventory.clear();
    }
}

interface Selector {
    void selectItem(Item item);
    int checkPrice(Item item);
    void insertCoin(Coin coin);
    Map<Item, List<Coin>> purchaseItem();
    List<Coin> refund();
    void reset();
}

class VendingMachine implements Selector {
    private final Inventory<Coin> coinInventory = new Inventory<>();
    private final Inventory<Item> itemInventory = new Inventory<>();
    private int currentBalance;
    private Item currentItem;

    public VendingMachine() {
        initializeMachine();
    }

    private void initializeMachine() {
        for (Coin coin : Coin.values()) {
            coinInventory.add(coin, 10);
        }
        for (Item item : Item.values()) {
            itemInventory.add(item, 5);
        }
    }

    @Override
    public void selectItem(Item item) {
        if (itemInventory.hasItem(item)) {
            currentItem = item;
            System.out.println("Selected Item: " + item.getName());
        } else {
            System.out.println("Item is out of stock");
        }
    }

    @Override
    public int checkPrice(Item item) {
        return item.getPrice();
    }

    @Override
    public void insertCoin(Coin coin) {
        currentBalance += coin.getDenomination();
        coinInventory.add(coin, 1);
        System.out.println("Inserted " + coin + ". Current Balance: " + currentBalance);
    }

    @Override
    public Map<Item, List<Coin>> purchaseItem() {
        if (currentItem == null) {
            throw new IllegalStateException("No item selected");
        }
        if (currentBalance >= currentItem.getPrice()) {
            int changeAmount = currentBalance - currentItem.getPrice();
            List<Coin> change = getChange(changeAmount);
            itemInventory.deduct(currentItem);
            currentBalance = 0;
            System.out.println("Dispensing " + currentItem.getName());
            return Collections.singletonMap(currentItem, change);
        } else {
            throw new IllegalStateException("Insufficient balance,");
        }
    }

    @Override
    public List<Coin> refund() {
        List<Coin> refund = getChange(currentBalance);
        currentBalance = 0;
        return refund;
    }

    @Override
    public void reset() {
        itemInventory.clear();
        coinInventory.clear();
        currentBalance = 0;
        initializeMachine();
    }

    private List<Coin> getChange(int amount) {
        List<Coin> change = new ArrayList<>();

        while(amount > 0) {
            if (amount >= Coin.QUARTER.getDenomination() && coinInventory.hasItem(Coin.QUARTER)) {
                change.add(Coin.QUARTER);
                amount -= Coin.QUARTER.getDenomination();
            } else if (amount >= Coin.DIME.getDenomination() && coinInventory.hasItem(Coin.DIME)) {
                change.add(Coin.DIME);
                amount -= Coin.DIME.getDenomination();
            } else if (amount >= Coin.NICKEL.getDenomination() && coinInventory.hasItem(Coin.NICKEL)) {
                change.add(Coin.NICKEL);
                amount -= Coin.NICKEL.getDenomination();
            } else if (amount >= Coin.PENNY.getDenomination() && coinInventory.hasItem(Coin.PENNY)) {
                change.add(Coin.PENNY);
                amount -= Coin.PENNY.getDenomination();
            } else {
                throw new IllegalStateException("Insufficient change available");
            }
        }

        return change;
    }
}

public class Main {
    public static void main(String[] args) {
        VendingMachine vm = new VendingMachine();

        vm.selectItem(Item.TWIX);
        vm.insertCoin(Coin.QUARTER);
        vm.insertCoin(Coin.QUARTER);

        vm.purchaseItem();
        List<Coin> refund = vm.refund();
        System.out.println("Refunded Coins: " + refund);
    }
}