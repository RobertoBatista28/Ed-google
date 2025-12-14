package Utils;

import java.awt.Dimension;
import java.awt.Toolkit;

/**
 * GameConfig contains all static configuration constants for the 
 * Labyrinth of Glory game. This class centralizes game parameters,
 * UI dimensions, file paths, and various game settings.
 * All constants are public static final for easy access throughout
 * the application.
 *
 */
public class GameConfig {

    /**
     * The width of the game map in number of rooms (grid cells).
     */
    public static final int MAP_WIDTH = 21;

    /**
     * The height of the game map in number of rooms (grid cells).
     */
    public static final int MAP_HEIGHT = 21;

    /**
     * The size of each room (cell) in pixels, calculated dynamically based on screen size.
     */
    public static final int ROOM_SIZE = calculateOptimalRoomSize();

    /**
     * Calculates the optimal room size in pixels based on the user's screen resolution.
     * 
     * This method ensures the game window fits within the screen boundaries while maintaining
     * a reasonable minimum size.
     *
     * @return the calculated size of a room in pixels (width and height)
     */
    private static int calculateOptimalRoomSize() {
        try {
            // Get the screen dimensions using the AWT Toolkit
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            
            // Calculate max possible width/height allowing for some margins (100px width, 250px height)
            int maxWidth = (screenSize.width - 100) / MAP_WIDTH;
            int maxHeight = (screenSize.height - 250) / MAP_HEIGHT;

            // Return the smallest dimension to ensure square rooms fit, capped at 50 pixels
            return Math.min(50, Math.min(maxWidth, maxHeight));
        } catch (java.awt.AWTError | java.awt.HeadlessException e) {
            // Fallback size if graphical environment is not available
            return 50;
        }
    }

    /* * Visual Configuration Settings 
     * Constants defining colors and dimensions for map rendering.
     */
    
    /** Hex color code for standard walls. */
    public static final String WALL_COLOR_HEX = "#000000";
    
    /** Thickness of the walls in pixels. */
    public static final int WALL_THICKNESS = 3;
    
    /** Thickness of the path line drawn during movement visualization. */
    public static final int PATH_LINE_THICKNESS = 5;
    
    /** Hex color code for breakable walls (interaction with Pickaxe). */
    public static final String BREAKABLE_WALL_COLOR_HEX = "#FF0000";
    
    /** Hex color code for the player overlay highlight. */
    public static final String PLAYER_OVERLAY_COLOR_HEX = "#FFFFFF";

    /* * Game Mechanics Configuration
     * Probabilities and limits for map generation and game events.
     */

    /** * The probability of removing a dead-end to create loops in the maze. 
     * A value of 0.11 implies an 11% chance.
     */
    public static final double BRAIDING_RATE = 0.11;
    
    /** Probability of triggering a random event when moving to a corridor. */
    public static final double RANDOM_EVENT_PROBABILITY = 0.03;

    /*
     * Object Counts for Map Generation
     */
    public static final int LEVERS_COUNT = 20;
    public static final int PICKAXES_COUNT = 12;
    public static final int ENDERPEARLS_COUNT = 8;
    public static final int QUESTIONS_COUNT = 24;

    /*
     * Rendering Scales
     * Multipliers for sizing images relative to the room size.
     */
    public static final double LEVER_SIZE_SCALE = 0.8;
    public static final double PICKAXE_SIZE_SCALE = 0.7;
    public static final double ENDERPEARL_SIZE_SCALE = 0.56;
    public static final double PLAYER_SIZE_SCALE = 0.7;
    public static final double TREASURE_SIZE_SCALE = 2.4;

    public static final double LEVER_SHADOW_SCALE = 0.6;
    public static final double PICKAXE_SHADOW_SCALE = 0.6;
    public static final double ENDERPEARL_SHADOW_SCALE = 0.62;
    public static final int PLAYER_SHADOW_SCALE = 2;

    /*
     * Window Dimensions
     */
    
    /** Width of the main game window in pixels. */
    public static final int GAME_WINDOW_WIDTH = ROOM_SIZE * MAP_WIDTH + 70;
    
    /** Height of the main game window in pixels. */
    public static final int GAME_WINDOW_HEIGHT = ROOM_SIZE * MAP_HEIGHT + 240;

    /** Width of the main menu window. */
    public static final int MAIN_WINDOW_WIDTH = 1100;
    
    /** Height of the main menu window. */
    public static final int MAIN_WINDOW_HEIGHT = 700;
    
    public static final String MAIN_WINDOW_TITLE = "MAZECRAFT";
    public static final String GAME_VERSION = "v4.2.2";
    
    public static final String MAIN_WINDOW_BACKGROUND_COLOR = "#000000";
    public static final String STATS_PANEL_BACKGROUND_COLOR = "#323232";
    public static final String BUTTONS_BACKGROUND_COLOR = "#A0A0A0";
    public static final double MAIN_MENU_LOGO_SCALE = 0.7;
    public static final String TEXT_COLOR_HOVER_HEX = "#A0A0A0";

    /*
     * Player Setup Configuration
     */
    public static final int PLAYER_NAME_MAX_LENGTH = 16;
    public static final int PLAYER_SETUP_LABEL_WIDTH = 65;
    public static final int PLAYER_SETUP_FIELD_WIDTH = 140;
    public static final int PLAYER_SETUP_ROW_HEIGHT = 28;
    public static final int PLAYER_SETUP_VERTICAL_GAP = 10;
    
    /** Duration of the movement animation in milliseconds. */
    public static final int MOVEMENT_DURATION = 125;

    /*
     * Dice Animation Configuration
     */
    public static final int DICE_ANIMATION_DELAY = 80;
    public static final int DICE_ANIMATION_MAX_STEPS = 8;
    public static final int DICE_DIALOG_DELAY = 1250;

    /** Delay for informational popups in milliseconds. */
    public static final int INFO_POPUP_DELAY = 2000;

    /*
     * Resource Paths
     * Locations of assets within the project structure.
     */
    public static final String TEXTURES_PATH = "src/Resources/Assets/Textures/";
    public static final String ITENS_PATH = "src/Resources/Assets/Itens/";
    public static final String SKINS_PATH = "src/Resources/Assets/Skins/";
    public static final String BACKGROUND_PATH = "src/Resources/Assets/Backgrounds/";
    public static final String UI_PATH_TEXTURE = "src/Resources/Assets/UI/";
    public static final String RANDOM_EVENTS_PATH = "src/Utils/random_events.json";
    public static final String QUESTIONS_PATH = "src/Resources/Questions/questions.json";
    public static final String MAP_LOADER_PATH = "src/Resources/Maps";

    /*
     * Asset Filenames
     */
    
    // Map Textures
    public static final String FLOOR_TEXTURE = "floor-texture-main.jpg";
    public static final String ENTRANCE_TEXTURE = "entrance-texture.jpg";
    public static final String REDSTONE_BLOCK_TEXTURE = "redstone-block.jpg";
    public static final String SOUL_SAND_TEXTURE = "soul_sand.jpg";
    public static final String COBBLESTONE_TEXTURE = "cobblestone.jpg";

    // Item Icons
    public static final String LEVER_ACTIVE_TEXTURE = "lever-bottom.png";
    public static final String LEVER_INACTIVE_TEXTURE = "lever-top.png";
    public static final String QUESTION_MARK_TEXTURE = "question-mark.png";
    public static final String TREASURE_TEXTURE = "treasure.png";
    public static final String PICKAXE_TEXTURE = "pickaxe.png";
    public static final String ENDERPEARL_TEXTURE = "ender-pearl.png";

    // Player Skins
    public static final String HEAD_STEVE = "head-steve.png";
    public static final String HEAD_ALEX = "head-alex.png";
    public static final String HEAD_VILLAGER = "head-villager.png";
    public static final String HEAD_ENDERMAN = "head-enderman.png";
    public static final String HEAD_ZOMBIE = "head-zombie.png";
    public static final String HEAD_SKELETON = "head-skeleton.png";
    public static final String HEAD_CREEPER = "head-creeper.png";
    public static final String HEAD_DRAGON = "head-dragon.png";
    public static final String HEAD_SPIDER = "head-spider.png";
    public static final String HEAD_SLIME = "head-slime.png";
    public static final String HEAD_WITHER = "head-wither.png";

    // UI Backgrounds
    public static final String MAIN_MENU_BACKGROUND_TEXTURE = "main-menu-background.jpg";
    public static final String SETUP_BACKGROUND_TEXTURE = "setup-background.jpg";

    // UI Components
    public static final String LONG_BUTTON_TEXTURE = "long-button.png";
    public static final String HOVER_LONG_BUTTON_TEXTURE = "long-button-hover.png";
    public static final String SHORT_BUTTON_TEXTURE = "short-button.png";
    public static final String HOVER_SHORT_BUTTON_TEXTURE = "short-button-hover.png";
    public static final String TINY_BUTTON_TEXTURE = "tiny-button.png";
    public static final String HOVER_TINY_BUTTON_TEXTURE = "tiny-button-hover.png";
    public static final String LOGO_TEXTURE = "logo.png";

    /*
     * AI (Bot) Configuration
     */
    
    /** Probability threshold for the AI to choose to answer a question. */
    public static final double AI_QUESTIONS_RATE = 0.66;
    
    /** Simulated thinking time for the AI when answering questions (ms). */
    public static final double AI_QUESTIONS_THINKING = 750;
    
    /** Delay between AI steps during movement (ms). */
    public static final int AI_WALK_DELAY = 200;
    
    /** Initial delay before AI starts its turn (ms). */
    public static final int AI_INITIAL_DELAY = 2000;

    /*
     * Report Window Configuration
     */
    public static final int REPORT_WINDOW_WIDTH = 750;
    public static final int REPORT_WINDOW_HEIGHT = 1000;
    public static final String REPORT_ACCENT_COLOR_HEX = "#DC3232";
    public static final String REPORT_TEXT_PRIMARY_COLOR_HEX = "#F0F0F5";
    public static final String REPORT_TEXT_SECONDARY_COLOR_HEX = "#B4B4BE";

    /*
     * Map Editor Configuration
     */
    public static final int MAP_EDITOR_WINDOW_WIDTH = 1075;
    public static final int MAP_EDITOR_WINDOW_HEIGHT = 850;
    public static final int MAP_EDITOR_MENU_WIDTH = 250;
    public static final int LEVER_LINE_THICKNESS = 2;
    public static final int MAP_EDITOR_CELL_SIZE = 35;
    
    // Editor Tool Constants
    public static final int MAP_EDITOR_TOOL_WALL = 0;
    public static final int MAP_EDITOR_TOOL_PICKAXE = 1;
    public static final int MAP_EDITOR_TOOL_ENDERPEARL = 2;
    public static final int MAP_EDITOR_TOOL_LEVER = 3;
    public static final int MAP_EDITOR_TOOL_QUESTION = 4;
}