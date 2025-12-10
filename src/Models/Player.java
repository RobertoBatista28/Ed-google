package Models;

import DataStructures.ArrayList.ArrayUnorderedList;
import DataStructures.Iterator;
import java.awt.Color;

public class Player {

    // ----------------------------------------------------------------
    // Fields
    // ----------------------------------------------------------------
    // Identity & Configuration
    private String name;
    private Color color;
    private boolean isBot;
    private String characterType;

    // Game State
    private Room currentRoom;
    private int remainingMoves;
    private String lastDirection;
    private ArrayUnorderedList<Item> inventory;

    // Statistics
    private int totalMoves;
    private ArrayUnorderedList<Room> path;
    private int leverInteractions;
    private int questionsCorrect;
    private int questionsIncorrect;
    private int itemsCollected;
    private int itemsUsed;
    private int pickaxesUsed;
    private int enderPearlsUsed;
    private int pickaxesCollected;
    private int enderPearlsCollected;

    // ----------------------------------------------------------------
    // Constructor
    // ----------------------------------------------------------------
    public Player(String name, Room startRoom, Color color, boolean isBot, String characterType) {
        if (name.length() > Utils.GameConfig.PLAYER_NAME_MAX_LENGTH) {
            this.name = name.substring(0, Utils.GameConfig.PLAYER_NAME_MAX_LENGTH);
        } else {
            this.name = name;
        }
        this.currentRoom = startRoom;
        this.color = color;
        this.isBot = isBot;
        this.characterType = characterType;
        this.remainingMoves = 0;
        this.lastDirection = "DOWN";
        this.inventory = new ArrayUnorderedList<>();
        this.totalMoves = 0;
        this.path = new ArrayUnorderedList<>();
        this.path.add(startRoom);

        // All statistics start at 0
        this.leverInteractions = 0;
        this.questionsCorrect = 0;
        this.questionsIncorrect = 0;
        this.itemsCollected = 0;
        this.itemsUsed = 0;
        this.pickaxesUsed = 0;
        this.enderPearlsUsed = 0;
        this.pickaxesCollected = 0;
        this.enderPearlsCollected = 0;
    }

    // ----------------------------------------------------------------
    // Core Getters & Setters
    // ----------------------------------------------------------------
    public String getName() {
        return name;
    }

    public Color getColor() {
        return color;
    }

    public boolean isBot() {
        return isBot;
    }

    public String getCharacterType() {
        return characterType;
    }

    public Room getCurrentRoom() {
        return currentRoom;
    }

    public void setCurrentRoom(Room currentRoom) {
        this.currentRoom = currentRoom;
    }

    public int getMoves() {
        return remainingMoves;
    }

    public void setMoves(int moves) {
        this.remainingMoves = moves;
    }

    public String getLastDirection() {
        return lastDirection;
    }

    public void setLastDirection(String direction) {
        this.lastDirection = direction;
    }

    // ----------------------------------------------------------------
    // Movement Logic
    // ----------------------------------------------------------------
    public void moveTaken() {
        if (this.remainingMoves > 0) {
            this.remainingMoves--;
            this.totalMoves++;
        }
    }

    // ----------------------------------------------------------------
    // Inventory Management
    // ----------------------------------------------------------------
    public ArrayUnorderedList<Item> getInventory() {
        return inventory;
    }

    public boolean addItem(Item item) {
        if (inventory.size() < 3) {
            inventory.add(item);
            return true;
        }
        return false;
    }

    public Item useItem(int index) {
        if (index >= 0 && index < inventory.size()) {
            return inventory.remove(index);
        }
        return null;
    }

    public Item getItem(int index) {
        if (index < 0 || index >= inventory.size()) {
            return null;
        }
        Iterator<Item> it = inventory.iterator();
        int i = 0;
        while (it.hasNext()) {
            Item item = it.next();
            if (i == index) {
                return item;
            }
            i++;
        }
        return null;
    }

    // Specific Item Helpers
    public void addPickaxe() {
        addItem(new Item("Pickaxe", "Tool"));
    }

    public void usePickaxe() {
        Iterator<Item> it = inventory.iterator();
        int index = 0;
        while (it.hasNext()) {
            Item item = it.next();
            if (item.getName().equals("Pickaxe")) {
                inventory.remove(index);
                return;
            }
            index++;
        }
    }

    public int getPickaxeCount() {
        int count = 0;
        Iterator<Item> it = inventory.iterator();
        while (it.hasNext()) {
            if (it.next().getName().equals("Pickaxe")) {
                count++;
            }
        }
        return count;
    }

    public int getEnderPearlCount() {
        int count = 0;
        Iterator<Item> it = inventory.iterator();
        while (it.hasNext()) {
            if (it.next().getName().equals("Ender Pearl")) {
                count++;
            }
        }
        return count;
    }

    // ----------------------------------------------------------------
    // Statistics
    // ----------------------------------------------------------------
    public int getTotalMoves() {
        return totalMoves;
    }

    public void addToPath(Room room) {
        path.add(room);
    }

    public ArrayUnorderedList<Room> getPath() {
        return path;
    }

    public void incrementLeverInteractions() {
        this.leverInteractions++;
    }

    public int getLeverInteractions() {
        return leverInteractions;
    }

    public void incrementQuestionsCorrect() {
        this.questionsCorrect++;
    }

    public int getQuestionsCorrect() {
        return questionsCorrect;
    }

    public void incrementQuestionsIncorrect() {
        this.questionsIncorrect++;
    }

    public int getQuestionsIncorrect() {
        return questionsIncorrect;
    }

    public void incrementItemsCollected() {
        this.itemsCollected++;
    }

    public int getItemsCollected() {
        return itemsCollected;
    }

    public void incrementItemsUsed() {
        this.itemsUsed++;
    }

    public int getItemsUsed() {
        return itemsUsed;
    }

    public void incrementPickaxesUsed() {
        this.pickaxesUsed++;
    }

    public int getPickaxesUsed() {
        return pickaxesUsed;
    }

    public void incrementEnderPearlsUsed() {
        this.enderPearlsUsed++;
    }

    public int getEnderPearlsUsed() {
        return enderPearlsUsed;
    }

    public void incrementPickaxesCollected() {
        this.pickaxesCollected++;
    }

    public int getPickaxesCollected() {
        return pickaxesCollected;
    }

    public void incrementEnderPearlsCollected() {
        this.enderPearlsCollected++;
    }

    public int getEnderPearlsCollected() {
        return enderPearlsCollected;
    }
}
