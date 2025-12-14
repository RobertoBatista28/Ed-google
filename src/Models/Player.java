package Models;

import DataStructures.ArrayList.ArrayUnorderedList;
import DataStructures.Iterator;

/**
 * Player represents a player in the game, managing identity, game state,
 * inventory, and game statistics. Each player has a character type,
 * current position on the game map, remaining moves, and an inventory
 * for collecting items. The class tracks comprehensive game statistics
 * including moves, questions answered, items collected, and special
 * item usage throughout the game session.
 */
public class Player {

    // ----------------------------------------------------------------
    // Fields
    // ----------------------------------------------------------------
    // Identity & Configuration
    private String name;
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
    /**
     * Creates a new Player with the specified name, starting room,
     * bot status, and character type. Initializes the player's inventory,
     * path tracking, and all statistics counters to zero. If the player
     * name exceeds the maximum length, it is truncated to fit the limit.
     *
     * @param name the name of the player (truncated if exceeds max length)
     * @param startRoom the initial room where the player begins
     * @param isBot true if this is an AI-controlled player, false if human-controlled
     * @param characterType the character type for visual representation
     */
    public Player(String name, Room startRoom, boolean isBot, String characterType) {
        if (name.length() > Utils.GameConfig.PLAYER_NAME_MAX_LENGTH) {
            this.name = name.substring(0, Utils.GameConfig.PLAYER_NAME_MAX_LENGTH);
        } else {
            this.name = name;
        }
        this.currentRoom = startRoom;
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
    /**
     * Returns the name of this player.
     *
     * @return the player's name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns whether this player is controlled by the computer.
     *
     * @return true if this is a bot player, false if human-controlled
     */
    public boolean isBot() {
        return isBot;
    }

    /**
     * Returns the character type of this player.
     *
     * @return the character type for visual representation
     */
    public String getCharacterType() {
        return characterType;
    }

    /**
     * Returns the room this player is currently in.
     *
     * @return the player's current room
     */
    public Room getCurrentRoom() {
        return currentRoom;
    }

    /**
     * Sets the player's current room to the specified room.
     *
     * @param currentRoom the room to set as the player's current location
     */
    public void setCurrentRoom(Room currentRoom) {
        this.currentRoom = currentRoom;
    }

    /**
     * Returns the number of remaining moves for this player.
     *
     * @return the remaining movement points
     */
    public int getMoves() {
        return remainingMoves;
    }

    /**
     * Sets the number of remaining moves for this player.
     *
     * @param moves the number of movement points to set
     */
    public void setMoves(int moves) {
        this.remainingMoves = moves;
    }

    /**
     * Returns the last direction the player moved.
     *
     * @return the last movement direction as a string
     */
    public String getLastDirection() {
        return lastDirection;
    }

    /**
     * Sets the last direction the player moved.
     *
     * @param direction the movement direction to set
     */
    public void setLastDirection(String direction) {
        this.lastDirection = direction;
    }

    // ----------------------------------------------------------------
    // Movement Logic
    // ----------------------------------------------------------------
    /**
     * Decrements the remaining moves by one and increments the total
     * moves counter. Only decreases remaining moves if the player has
     * moves available.
     */
    public void moveTaken() {
        if (this.remainingMoves > 0) {
            this.remainingMoves--;
            this.totalMoves++;
        }
    }

    // ----------------------------------------------------------------
    // Inventory Management
    // ----------------------------------------------------------------
    /**
     * Returns the player's inventory list.
     *
     * @return the ArrayUnorderedList containing the player's items
     */
    public ArrayUnorderedList<Item> getInventory() {
        return inventory;
    }

    /**
     * Adds an item to the player's inventory if there is space available.
     * The inventory can hold a maximum of 3 items.
     *
     * @param item the item to add to the inventory
     * @return true if the item was successfully added, false if inventory is full
     */
    public boolean addItem(Item item) {
        if (inventory.size() < 3) {
            inventory.add(item);
            return true;
        }
        return false;
    }

    /**
     * Removes and returns the item at the specified index from the inventory.
     *
     * @param index the position of the item to remove
     * @return the item that was removed, or null if index is out of bounds
     */
    public Item useItem(int index) {
        if (index >= 0 && index < inventory.size()) {
            return inventory.remove(index);
        }
        return null;
    }

    /**
     * Returns the item at the specified index without removing it from the inventory.
     *
     * @param index the position of the item to retrieve
     * @return the item at the specified index, or null if index is out of bounds
     */
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

    /**
     * Adds a pickaxe item to the player's inventory.
     */
    public void addPickaxe() {
        addItem(new Item("Pickaxe", "Tool"));
    }

    /**
     * Removes a pickaxe from the player's inventory by searching
     * for the first item with the name "Pickaxe" and removing it.
     */
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

    /**
     * Returns the count of pickaxes currently in the player's inventory.
     *
     * @return the number of pickaxes in the inventory
     */
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

    /**
     * Returns the count of ender pearls currently in the player's inventory.
     *
     * @return the number of ender pearls in the inventory
     */
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
    /**
     * Returns the total number of moves made by this player during the game.
     *
     * @return the total move count
     */
    public int getTotalMoves() {
        return totalMoves;
    }

    /**
     * Adds a room to the player's movement path history.
     *
     * @param room the room to add to the path
     */
    public void addToPath(Room room) {
        path.add(room);
    }

    /**
     * Returns the complete path of rooms visited by this player.
     *
     * @return the ArrayUnorderedList containing all rooms in the player's path
     */
    public ArrayUnorderedList<Room> getPath() {
        return path;
    }

    /**
     * Increments the lever interaction counter by one.
     */
    public void incrementLeverInteractions() {
        this.leverInteractions++;
    }

    /**
     * Returns the total number of lever interactions by this player.
     *
     * @return the lever interaction count
     */
    public int getLeverInteractions() {
        return leverInteractions;
    }

    /**
     * Increments the correct questions counter by one.
     */
    public void incrementQuestionsCorrect() {
        this.questionsCorrect++;
    }

    /**
     * Returns the number of questions answered correctly by this player.
     *
     * @return the count of correct answers
     */
    public int getQuestionsCorrect() {
        return questionsCorrect;
    }

    /**
     * Increments the incorrect questions counter by one.
     */
    public void incrementQuestionsIncorrect() {
        this.questionsIncorrect++;
    }

    /**
     * Returns the number of questions answered incorrectly by this player.
     *
     * @return the count of incorrect answers
     */
    public int getQuestionsIncorrect() {
        return questionsIncorrect;
    }

    /**
     * Increments the items collected counter by one.
     */
    public void incrementItemsCollected() {
        this.itemsCollected++;
    }

    /**
     * Returns the total number of items collected by this player.
     *
     * @return the items collected count
     */
    public int getItemsCollected() {
        return itemsCollected;
    }

    /**
     * Increments the items used counter by one.
     */
    public void incrementItemsUsed() {
        this.itemsUsed++;
    }

    /**
     * Returns the total number of items used by this player.
     *
     * @return the items used count
     */
    public int getItemsUsed() {
        return itemsUsed;
    }

    /**
     * Increments the pickaxes used counter by one.
     */
    public void incrementPickaxesUsed() {
        this.pickaxesUsed++;
    }

    /**
     * Returns the total number of pickaxes used by this player.
     *
     * @return the pickaxes used count
     */
    public int getPickaxesUsed() {
        return pickaxesUsed;
    }

    /**
     * Increments the ender pearls used counter by one.
     */
    public void incrementEnderPearlsUsed() {
        this.enderPearlsUsed++;
    }

    /**
     * Returns the total number of ender pearls used by this player.
     *
     * @return the ender pearls used count
     */
    public int getEnderPearlsUsed() {
        return enderPearlsUsed;
    }

    /**
     * Increments the pickaxes collected counter by one.
     */
    public void incrementPickaxesCollected() {
        this.pickaxesCollected++;
    }

    /**
     * Returns the total number of pickaxes collected by this player.
     *
     * @return the pickaxes collected count
     */
    public int getPickaxesCollected() {
        return pickaxesCollected;
    }

    /**
     * Increments the ender pearls collected counter by one.
     */
    public void incrementEnderPearlsCollected() {
        this.enderPearlsCollected++;
    }

    /**
     * Returns the total number of ender pearls collected by this player.
     *
     * @return the ender pearls collected count
     */
    public int getEnderPearlsCollected() {
        return enderPearlsCollected;
    }
}
