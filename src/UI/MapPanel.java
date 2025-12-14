package UI;

import DataStructures.Iterator;
import GameEngine.GameManager;
import Models.Connection;
import Models.Player;
import Models.Room;
import Utils.GameConfig;
import Utils.ImageLoader;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.swing.*;

/**
 * MapPanel displays the game map with all rooms, walls, game objects, and players.
 * The panel renders the maze layout, handles player animations, manages game objects
 * (levers, pickaxes, ender pearls, questions), and displays status messages.
 * It uses double buffering and timer-based animation at 60 FPS for smooth player movement.
 *
 */
public class MapPanel extends JPanel {

    private final GameManager gameManager;
    private final BufferedImage floorTexture, entranceTexture, leverActiveTexture,
            leverInactiveTexture, questionMarkTexture, treasureTexture,
            pickaxeTexture, enderPearlTexture, steveTexture, alexTexture, villagerTexture,
            endermanTexture, zombieTexture, skeletonTexture, creeperTexture,
            dragonTexture, spiderTexture, slimeTexture, witherTexture;
    private String currentStatusMessage = "";
    private long statusMessageTime = 0;

    /**
     * Creates a new MapPanel for displaying the game map and all game elements.
     * Initializes the animation timer (60 FPS), loads all texture resources (floor,
     * entrance, levers, items, player skins), and sets up the panel dimensions
     * and background color.
     *
     * @param gameManager the GameManager instance controlling game state
     */
    public MapPanel(GameManager gameManager) {
        this.gameManager = gameManager;

        // Animation Timer (60 FPS)
        animationTimer = new Timer(16, e -> repaint());

        setPreferredSize(new Dimension(GameConfig.GAME_WINDOW_WIDTH, GameConfig.GAME_WINDOW_HEIGHT));
        setBackground(Color.decode(GameConfig.MAIN_WINDOW_BACKGROUND_COLOR));

        // Load Textures
        floorTexture = ImageLoader.getImage(GameConfig.TEXTURES_PATH + GameConfig.FLOOR_TEXTURE);
        entranceTexture = ImageLoader.getImage(GameConfig.TEXTURES_PATH + GameConfig.ENTRANCE_TEXTURE);
        leverActiveTexture = ImageLoader.getImage(GameConfig.ITENS_PATH + GameConfig.LEVER_ACTIVE_TEXTURE);

        // Load icons
        leverInactiveTexture = ImageLoader.getImage(GameConfig.ITENS_PATH + GameConfig.LEVER_INACTIVE_TEXTURE);
        questionMarkTexture = ImageLoader.getImage(GameConfig.ITENS_PATH + GameConfig.QUESTION_MARK_TEXTURE);
        treasureTexture = ImageLoader.getImage(GameConfig.ITENS_PATH + GameConfig.TREASURE_TEXTURE);
        pickaxeTexture = ImageLoader.getImage(GameConfig.ITENS_PATH + GameConfig.PICKAXE_TEXTURE);
        enderPearlTexture = ImageLoader.getImage(GameConfig.ITENS_PATH + GameConfig.ENDERPEARL_TEXTURE);

        // Load Player Skins
        steveTexture = ImageLoader.getImage(GameConfig.SKINS_PATH + GameConfig.HEAD_STEVE);
        alexTexture = ImageLoader.getImage(GameConfig.SKINS_PATH + GameConfig.HEAD_ALEX);
        villagerTexture = ImageLoader.getImage(GameConfig.SKINS_PATH + GameConfig.HEAD_VILLAGER);
        endermanTexture = ImageLoader.getImage(GameConfig.SKINS_PATH + GameConfig.HEAD_ENDERMAN);
        zombieTexture = ImageLoader.getImage(GameConfig.SKINS_PATH + GameConfig.HEAD_ZOMBIE);
        skeletonTexture = ImageLoader.getImage(GameConfig.SKINS_PATH + GameConfig.HEAD_SKELETON);
        creeperTexture = ImageLoader.getImage(GameConfig.SKINS_PATH + GameConfig.HEAD_CREEPER);
        dragonTexture = ImageLoader.getImage(GameConfig.SKINS_PATH + GameConfig.HEAD_DRAGON);
        spiderTexture = ImageLoader.getImage(GameConfig.SKINS_PATH + GameConfig.HEAD_SPIDER);
        slimeTexture = ImageLoader.getImage(GameConfig.SKINS_PATH + GameConfig.HEAD_SLIME);
        witherTexture = ImageLoader.getImage(GameConfig.SKINS_PATH + GameConfig.HEAD_WITHER);
    }

    // Animation State
    private final DataStructures.ArrayList.ArrayUnorderedList<PlayerAnimationPair> playerAnimations = new DataStructures.ArrayList.ArrayUnorderedList<>();
    private final Timer animationTimer;

    private static class PlayerAnimationPair {

        Player player;
        AnimationState state;

        /**
         * Creates a new PlayerAnimationPair associating a player with their animation state.
         *
         * @param p the player whose animation is tracked
         * @param s the AnimationState containing position and timing information
         */
        public PlayerAnimationPair(Player p, AnimationState s) {
            this.player = p;
            this.state = s;
        }
    }

    private AnimationState getAnimationState(Player p) {
        DataStructures.Iterator<PlayerAnimationPair> it = playerAnimations.iterator();
        while (it.hasNext()) {
            PlayerAnimationPair pair = it.next();
            if (pair.player.equals(p)) {
                return pair.state;
            }
        }
        return null;
    }

    private static class AnimationState {

        int startX, startY;
        int targetX, targetY;
        int currentX, currentY;
        long startTime;

        /**
         * Creates a new AnimationState with initial position coordinates.
         * Both start and target positions are initialized to the same values,
         * with timing information set to zero.
         *
         * @param x the initial x-coordinate for the animation
         * @param y the initial y-coordinate for the animation
         */
        AnimationState(int x, int y) {
            this.startX = x;
            this.startY = y;
            this.targetX = x;
            this.targetY = y;
            this.currentX = x;
            this.currentY = y;
            this.startTime = 0;
        }
    }

    /**
     * Sets and displays a temporary status message on the game map.
     * The message is displayed in a rounded rectangle box at the top of the panel
     * and automatically disappears after the configured delay time.
     *
     * @param msg the status message text to display
     */
    public void setStatusMessage(String msg) {
        this.currentStatusMessage = msg;
        this.statusMessageTime = System.currentTimeMillis();
        repaint();

        Timer t = new Timer(GameConfig.INFO_POPUP_DELAY, e -> {
            if (System.currentTimeMillis() - statusMessageTime >= GameConfig.INFO_POPUP_DELAY) {
                currentStatusMessage = "";
                repaint();
            }
        });
        t.setRepeats(false);
        t.start();
    }

    @Override
    /**
     * Renders the complete game map including all rooms, walls, game objects, and animated players.
     * Uses Graphics2D with anti-aliasing for smooth rendering. The map is centered on the panel
     * and includes room textures, wall segments (permanent and breakable), game objects (levers,
     * pickaxes, ender pearls, questions, treasure), and players with directional indicators.
     * Handles player animation using interpolation and manages status message display.
     */
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (gameManager == null || gameManager.getGameMap() == null) {
            return;
        }

        int cellSize = GameConfig.ROOM_SIZE;
        int mapPixelWidth = GameConfig.MAP_WIDTH * cellSize;
        int mapPixelHeight = GameConfig.MAP_HEIGHT * cellSize;

        // Calculate offsets to center the map on the panel
        int offsetX = (getWidth() - mapPixelWidth) / 2;
        int offsetY = (getHeight() - mapPixelHeight) / 2;

        // Draw all rooms and their contents
        for (int x = 0; x < GameConfig.MAP_WIDTH; x++) {
            for (int y = 0; y < GameConfig.MAP_HEIGHT; y++) {
                Room r = gameManager.getGameMap().getRoom(x, y);
                if (r == null) {
                    continue;
                }

                int px = offsetX + x * cellSize;
                int py = offsetY + y * cellSize;

                // Fill Cell
                if (r.isEntrance()) {
                    g2d.drawImage(entranceTexture, px, py, cellSize, cellSize, null);
                } else {
                    g2d.drawImage(floorTexture, px, py, cellSize, cellSize, null);
                }

                if (r.getCustomFloorImage() != null) {
                    g2d.drawImage(r.getCustomFloorImage(), px, py, cellSize, cellSize, null);
                } else if (r.getCustomFloorColor() != null) {
                    g2d.setColor(r.getCustomFloorColor());
                    g2d.fillRect(px, py, cellSize, cellSize);
                }

                // Draw Walls
                g2d.setStroke(new BasicStroke(GameConfig.WALL_THICKNESS));

                // North, South, West, East
                drawWallSegment(g2d, r, x, y - 1, px, py, px + cellSize, py);
                drawWallSegment(g2d, r, x, y + 1, px, py + cellSize, px + cellSize, py + cellSize);
                drawWallSegment(g2d, r, x - 1, y, px, py, px, py + cellSize);
                drawWallSegment(g2d, r, x + 1, y, px + cellSize, py, px + cellSize, py + cellSize);

                // Draw Lever
                if (r.hasLever()) {
                    Models.Lever lever = r.getLever();
                    BufferedImage leverImg = lever.isActive() ? leverActiveTexture : leverInactiveTexture;

                    g2d.setColor(new Color(0, 0, 0, 70));
                    int glowSize = (int) (cellSize * GameConfig.LEVER_SHADOW_SCALE);
                    int gx = px + (cellSize - glowSize) / 2;
                    int gy = py + (cellSize - glowSize) / 2;
                    g2d.fillOval(gx, gy, glowSize, glowSize);

                    int leverW = (int) (cellSize * GameConfig.LEVER_SIZE_SCALE);
                    int leverH = (int) (cellSize * GameConfig.LEVER_SIZE_SCALE);
                    int lx = px + (cellSize - leverW) / 2;
                    int ly = py + (cellSize - leverH) / 2;
                    g2d.drawImage(leverImg, lx, ly, leverW, leverH, null);
                }

                // Draw Question Mark
                if (r.hasQuestion()) {
                    int qSize = (int) (cellSize * 0.8);
                    int qx = px + (cellSize - qSize) / 2;
                    int qy = py + (cellSize - qSize) / 2;
                    g2d.drawImage(questionMarkTexture, qx, qy, qSize, qSize, null);
                }

                // Draw Pickaxe
                if (r.hasPickaxe()) {
                    g2d.setColor(new Color(0, 0, 0, 70));
                    int glowSize = (int) (cellSize * GameConfig.PICKAXE_SHADOW_SCALE);
                    int gx = px + (cellSize - glowSize) / 2;
                    int gy = py + (cellSize - glowSize) / 2;
                    g2d.fillOval(gx, gy, glowSize, glowSize);

                    int pSize = (int) (cellSize * GameConfig.PICKAXE_SIZE_SCALE);
                    int px2 = px + (cellSize - pSize) / 2;
                    int py2 = py + (cellSize - pSize) / 2;
                    g2d.drawImage(pickaxeTexture, px2, py2, pSize, pSize, null);

                }

                // Draw Ender Pearl
                if (r.hasEnderPearl()) {
                    g2d.setColor(new Color(0, 0, 0, 70));
                    int glowSize = (int) (cellSize * GameConfig.ENDERPEARL_SHADOW_SCALE);
                    int gx = px + (cellSize - glowSize) / 2;
                    int gy = py + (cellSize - glowSize) / 2;
                    g2d.fillOval(gx, gy, glowSize, glowSize);

                    int pSize = (int) (cellSize * GameConfig.ENDERPEARL_SIZE_SCALE);
                    int px2 = px + (cellSize - pSize) / 2;
                    int py2 = py + (cellSize - pSize) / 2;
                    g2d.drawImage(enderPearlTexture, px2, py2, pSize, pSize, null);
                }
            }
        }

        // Draw Treasure at Center
        if (treasureTexture != null) {
            int cx = GameConfig.MAP_WIDTH / 2;
            int cy = GameConfig.MAP_HEIGHT / 2;
            int px = offsetX + cx * cellSize;
            int py = offsetY + cy * cellSize;

            int treasureW = (int) (cellSize * GameConfig.TREASURE_SIZE_SCALE);
            int treasureH = (int) (cellSize * GameConfig.TREASURE_SIZE_SCALE);
            int tx = px + (cellSize - treasureW) / 2;
            int ty = py + (cellSize - treasureH) / 2;
            g2d.drawImage(treasureTexture, tx, ty, treasureW, treasureH, null);
        }

        // Draw Players and handle animation
        boolean isAnimating = false;
        DataStructures.Iterator<Player> it = gameManager.getPlayers().iterator();
        while (it.hasNext()) {
            Player p = it.next();
            if (p != null && p.getCurrentRoom() != null) {
                Room r = p.getCurrentRoom();

                // Calculate target screen position from room coordinates
                int targetPx = offsetX + r.getX() * cellSize;
                int targetPy = offsetY + r.getY() * cellSize;

                // Get or create animation state for this player
                AnimationState state = getAnimationState(p);
                if (state == null) {
                    state = new AnimationState(targetPx, targetPy);
                    playerAnimations.addToRear(new PlayerAnimationPair(p, state));
                }

                // Check if target position changed and initialize animation
                if (state.targetX != targetPx || state.targetY != targetPy) {
                    state.startX = state.currentX;
                    state.startY = state.currentY;
                    state.targetX = targetPx;
                    state.targetY = targetPy;
                    state.startTime = System.currentTimeMillis();
                    if (!animationTimer.isRunning()) {
                        animationTimer.start();
                    }
                }

                // Calculate smooth interpolated position using linear interpolation
                long now = System.currentTimeMillis();
                float progress = 1.0f;

                int duration = GameConfig.MOVEMENT_DURATION;
                int startRoomX = (state.startX - offsetX) / cellSize;
                int startRoomY = (state.startY - offsetY) / cellSize;
                Room startRoom = gameManager.getGameMap().getRoom(startRoomX, startRoomY);

                // Increase animation duration for soul sand (slower movement)
                if (startRoom != null && startRoom.isSoulSand()) {
                    duration = (int) (GameConfig.MOVEMENT_DURATION * 3.0);
                }

                if (duration > 0) {
                    progress = (float) (now - state.startTime) / duration;
                }

                if (progress < 1.0f) {
                    progress = Math.max(0.0f, progress);
                    isAnimating = true;
                    // Linear interpolation between start and target position
                    state.currentX = (int) (state.startX + (state.targetX - state.startX) * progress);
                    state.currentY = (int) (state.startY + (state.targetY - state.startY) * progress);
                } else {
                    state.currentX = state.targetX;
                    state.currentY = state.targetY;
                }

                // Use interpolated position for drawing
                int px = state.currentX;
                int py = state.currentY;

                // Select player skin texture based on character type
                BufferedImage playerSkin;
                String charType = p.getCharacterType();
                if (charType != null) {
                    playerSkin = switch (charType.toUpperCase()) {
                        case "STEVE" ->
                            steveTexture;
                        case "ALEX" ->
                            alexTexture;
                        case "VILLAGER" ->
                            villagerTexture;
                        case "ENDERMAN" ->
                            endermanTexture;
                        case "ZOMBIE" ->
                            zombieTexture;
                        case "SKELETON" ->
                            skeletonTexture;
                        case "CREEPER" ->
                            creeperTexture;
                        case "DRAGON" ->
                            dragonTexture;
                        case "SPIDER" ->
                            spiderTexture;
                        case "SLIME" ->
                            slimeTexture;
                        case "WITHER" ->
                            witherTexture;
                        default ->
                            steveTexture;
                    };
                } else {
                    playerSkin = steveTexture;
                }

                int playerW = (int) (cellSize * GameConfig.PLAYER_SIZE_SCALE);
                int playerH = (int) (cellSize * GameConfig.PLAYER_SIZE_SCALE);
                int playerX = px + (cellSize - playerW) / 2;
                int playerY = py + (cellSize - playerH) / 2;
                g2d.drawImage(playerSkin, playerX, playerY, playerW, playerH, null);

                // Highlight the current player with border and direction arrow
                if (p.equals(gameManager.getCurrentPlayer())) {
                    g2d.setColor(Color.decode(GameConfig.PLAYER_OVERLAY_COLOR_HEX));
                    g2d.setStroke(new BasicStroke(GameConfig.PLAYER_SHADOW_SCALE));
                    g2d.drawRect(playerX, playerY, playerW, playerH);

                    // Draw direction arrow indicating player's last movement
                    drawDirectionArrow(g2d, playerX, playerY, playerW, playerH, p.getLastDirection());
                }

                // Highlight target player when selecting ender pearl destination
                if (gameManager.isEnderPearlSelectionMode() && p.equals(gameManager.getSelectedTargetPlayer())) {
                    g2d.setColor(Color.RED);
                    g2d.setStroke(new BasicStroke(5));
                    g2d.drawRect(playerX, playerY, playerW, playerH);
                }
            }
        }

        if (!isAnimating && animationTimer.isRunning()) {
            animationTimer.stop();
        }

        // Draw Status Message Overlay
        if (currentStatusMessage != null && !currentStatusMessage.isEmpty()) {
            g2d.setFont(new Font("Arial", Font.BOLD, 24));
            FontMetrics fm = g2d.getFontMetrics();
            int textWidth = fm.stringWidth(currentStatusMessage);
            int textHeight = fm.getHeight();

            int boxX = (getWidth() - textWidth) / 2 - 20;
            int boxY = 75;
            int boxW = textWidth + 40;
            int boxH = textHeight + 20;

            g2d.setColor(new Color(0, 0, 0, 180));
            g2d.fillRoundRect(boxX, boxY, boxW, boxH, 15, 15);

            g2d.setColor(Color.WHITE);
            g2d.drawRoundRect(boxX, boxY, boxW, boxH, 15, 15);

            g2d.drawString(currentStatusMessage, boxX + 20, boxY + fm.getAscent() + 10);
        }
    }

    /**
     * Retrieves the connection between two adjacent rooms.
     * Searches through all connections of the given room to find one leading to
     * the target room at the specified coordinates.
     *
     * @param r the Room to search connections from
     * @param targetX the x-coordinate of the target room
     * @param targetY the y-coordinate of the target room
     * @return the Connection if one exists, null otherwise
     */
    private Connection getConnection(Room r, int targetX, int targetY) {
        Iterator<Connection> it = gameManager.getGameMap().getGraph().getConnections(r).iterator();
        while (it.hasNext()) {
            Connection c = it.next();
            Room target = c.getTo();
            if (target.getX() == targetX && target.getY() == targetY) {
                return c;
            }
        }
        return null;
    }

    /**
     * Draws a wall segment between two points if no connection exists.
     * Renders a permanent wall if no connection is found, or a breakable wall
     * if the connection is locked. Uses different colors for each wall type.
     *
     * @param g2d the Graphics2D context for rendering
     * @param r the Room from which the wall segment originates
     * @param tx the x-coordinate of the target room
     * @param ty the y-coordinate of the target room
     * @param x1 the starting x-coordinate of the line
     * @param y1 the starting y-coordinate of the line
     * @param x2 the ending x-coordinate of the line
     * @param y2 the ending y-coordinate of the line
     */
    private void drawWallSegment(Graphics2D g2d, Room r, int tx, int ty, int x1, int y1, int x2, int y2) {
        Connection c = getConnection(r, tx, ty);

        if (c == null) {
            // Permanent Wall
            g2d.setColor(Color.decode(GameConfig.WALL_COLOR_HEX));
            g2d.drawLine(x1, y1, x2, y2);
        } else if (c.isLocked()) {
            // Breakable Wall
            g2d.setColor(Color.decode(GameConfig.BREAKABLE_WALL_COLOR_HEX));
            g2d.drawLine(x1, y1, x2, y2);
        }
    }

    /**
     * Draws a directional arrow indicator on the player showing their last movement direction.
     * The arrow is drawn as a triangle pointing in one of four cardinal directions
     * (UP, DOWN, LEFT, RIGHT) based on the direction parameter.
     *
     * @param g2d the Graphics2D context for rendering
     * @param x the left x-coordinate of the player sprite
     * @param y the top y-coordinate of the player sprite
     * @param w the width of the player sprite
     * @param h the height of the player sprite
     * @param direction the direction string ("UP", "DOWN", "LEFT", "RIGHT", or null)
     */
    private void drawDirectionArrow(Graphics2D g2d, int x, int y, int w, int h, String direction) {
        if (direction == null) {
            return;
        }

        int arrowSize = 8;
        int[] xPoints = new int[3];
        int[] yPoints = new int[3];

        // Define arrow coordinates for each direction (triangle pointing outward)
        switch (direction.toUpperCase()) {
            case "UP" -> {
                xPoints[0] = x + w / 2 - arrowSize;
                yPoints[0] = y;
                xPoints[1] = x + w / 2 + arrowSize;
                yPoints[1] = y;
                xPoints[2] = x + w / 2;
                yPoints[2] = y - arrowSize;
            }
            case "DOWN" -> {
                xPoints[0] = x + w / 2 - arrowSize;
                yPoints[0] = y + h;
                xPoints[1] = x + w / 2 + arrowSize;
                yPoints[1] = y + h;
                xPoints[2] = x + w / 2;
                yPoints[2] = y + h + arrowSize;
            }
            case "LEFT" -> {
                xPoints[0] = x;
                yPoints[0] = y + h / 2 - arrowSize;
                xPoints[1] = x;
                yPoints[1] = y + h / 2 + arrowSize;
                xPoints[2] = x - arrowSize;
                yPoints[2] = y + h / 2;
            }
            case "RIGHT" -> {
                xPoints[0] = x + w;
                yPoints[0] = y + h / 2 - arrowSize;
                xPoints[1] = x + w;
                yPoints[1] = y + h / 2 + arrowSize;
                xPoints[2] = x + w + arrowSize;
                yPoints[2] = y + h / 2;
            }
        }
        g2d.fillPolygon(xPoints, yPoints, 3);
    }
}
