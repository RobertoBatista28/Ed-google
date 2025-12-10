package Utils;

import java.awt.Dimension;
import java.awt.Toolkit;

public class GameConfig {

    // Configurações do labirinto
    public static final int MAP_WIDTH = 21;
    public static final int MAP_HEIGHT = 21;
    public static final int ROOM_SIZE = calculateOptimalRoomSize();

    // Calcula o tamanho com base na resolução do ecrã
    private static int calculateOptimalRoomSize() {
        try {
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            int maxWidth = (screenSize.width - 100) / MAP_WIDTH;
            int maxHeight = (screenSize.height - 250) / MAP_HEIGHT;

            return Math.min(50, Math.min(maxWidth, maxHeight));
        } catch (java.awt.AWTError | java.awt.HeadlessException e) {
            return 50;
        }
    }

    public static final String WALL_COLOR_HEX = "#000000";
    public static final int WALL_THICKNESS = 3;
    public static final int PATH_LINE_THICKNESS = 5;
    public static final String BREAKABLE_WALL_COLOR_HEX = "#FF0000";
    public static final String PLAYER_OVERLAY_COLOR_HEX = "#FFFFFF";
    public static final double BRAIDING_RATE = 0.12;
    public static final double RANDOM_EVENT_PROBABILITY = 0.02;

    public static final int LEVERS_COUNT = 16;
    public static final int PICKAXES_COUNT = 8;
    public static final int ENDERPEARLS_COUNT = 4;
    public static final int QUESTIONS_COUNT = 24;

    public static final double LEVER_SIZE_SCALE = 0.8;
    public static final double PICKAXE_SIZE_SCALE = 0.7;
    public static final double ENDERPEARL_SIZE_SCALE = 0.56;
    public static final double PLAYER_SIZE_SCALE = 0.7;
    public static final double TREASURE_SIZE_SCALE = 2.4;

    public static final double LEVER_SHADOW_SCALE = 0.6;
    public static final double PICKAXE_SHADOW_SCALE = 0.6;
    public static final double ENDERPEARL_SHADOW_SCALE = 0.62;
    public static final int PLAYER_SHADOW_SCALE = 2;

    // Configurações da Janela de Jogo
    public static final int GAME_WINDOW_WIDTH = ROOM_SIZE * MAP_WIDTH + 70;
    public static final int GAME_WINDOW_HEIGHT = ROOM_SIZE * MAP_HEIGHT + 200;

    // Configurações da Janela Principal
    public static final int MAIN_WINDOW_WIDTH = 1100;
    public static final int MAIN_WINDOW_HEIGHT = 700;
    public static final String MAIN_WINDOW_TITLE = "LABIRINTO DA GLÓRIA";
    public static final String GAME_VERSION = "v3.5.1";
    public static final String MAIN_WINDOW_BACKGROUND_COLOR = "#000000";
    public static final String STATS_PANEL_BACKGROUND_COLOR = "#323232";
    public static final String BUTTONS_BACKGROUND_COLOR = "#A0A0A0";
    public static final double MAIN_MENU_LOGO_SCALE = 0.7;
    public static final String TEXT_COLOR_HOVER_HEX = "#A0A0A0";

    // Configurações dos Jogadores
    public static final int PLAYER_NAME_MAX_LENGTH = 16;
    public static final int PLAYER_SETUP_LABEL_WIDTH = 65;
    public static final int PLAYER_SETUP_FIELD_WIDTH = 140;
    public static final int PLAYER_SETUP_ROW_HEIGHT = 28;
    public static final int PLAYER_SETUP_VERTICAL_GAP = 10;
    public static final int MOVEMENT_DURATION = 125;

    // Configurações da janela de lançamento de dados
    public static final int DICE_ANIMATION_DELAY = 80;
    public static final int DICE_ANIMATION_MAX_STEPS = 8;
    public static final int DICE_DIALOG_DELAY = 1250;

    // Configurações do pop-up informativo
    public static final int INFO_POPUP_DELAY = 2000;

    // Lista de endereços de recursos
    public static final String TEXTURES_PATH = "src/Resources/Assets/Textures/";
    public static final String ITENS_PATH = "src/Resources/Assets/Itens/";
    public static final String SKINS_PATH = "src/Resources/Assets/Skins/";
    public static final String BACKGROUND_PATH = "src/Resources/Assets/Backgrounds/";
    public static final String UI_PATH_TEXTURE = "src/Resources/Assets/UI/";
    public static final String RANDOM_EVENTS_PATH = "src/Utils/random_events.json";
    public static final String QUESTIONS_PATH = "src/Resources/Questions/questions.json";
    public static final String MAP_LOADER_PATH = "src/Resources/Maps";

    // Texturas do mapa
    public static final String FLOOR_TEXTURE = "floor-texture-main.jpg";
    public static final String ENTRANCE_TEXTURE = "entrance-texture.jpg";
    public static final String REDSTONE_BLOCK_TEXTURE = "redstone-block.jpg";
    public static final String SOUL_SAND_TEXTURE = "soul_sand.jpg";

    // Texturas dos icones
    public static final String LEVER_ACTIVE_TEXTURE = "lever-bottom.png";
    public static final String LEVER_INACTIVE_TEXTURE = "lever-top.png";
    public static final String QUESTION_MARK_TEXTURE = "question-mark.png";
    public static final String TREASURE_TEXTURE = "treasure.png";
    public static final String PICKAXE_TEXTURE = "pickaxe.png";
    public static final String ENDERPEARL_TEXTURE = "ender-pearl.png";

    // Texturas das skins
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

    // Texturas de background
    public static final String MAIN_MENU_BACKGROUND_TEXTURE = "main-menu-background.jpg";
    public static final String SETUP_BACKGROUND_TEXTURE = "setup-background.jpg";

    // Texturas do UI
    public static final String LONG_BUTTON_TEXTURE = "long-button.png";
    public static final String HOVER_LONG_BUTTON_TEXTURE = "long-button-hover.png";
    public static final String SHORT_BUTTON_TEXTURE = "short-button.png";
    public static final String HOVER_SHORT_BUTTON_TEXTURE = "short-button-hover.png";
    public static final String LOGO_TEXTURE = "logo.png";

    // Configuração do AI
    public static final double AI_QUESTIONS_RATE = 0.66;
    public static final double AI_QUESTIONS_THINKING = 750;
    public static final int AI_WALK_DELAY = 200;
    public static final int AI_INITIAL_DELAY = 2000;

    // Configurações da Janela de Resultados
    public static final int REPORT_WINDOW_WIDTH = 750;
    public static final int REPORT_WINDOW_HEIGHT = 950;
    public static final String REPORT_ACCENT_COLOR_HEX = "#DC3232";
    public static final String REPORT_TEXT_PRIMARY_COLOR_HEX = "#F0F0F5";
    public static final String REPORT_TEXT_SECONDARY_COLOR_HEX = "#B4B4BE";
}
