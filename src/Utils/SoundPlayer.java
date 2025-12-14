package Utils;

import java.io.File;
import java.io.IOException;
import javax.sound.sampled.*;

/**
 * Utility class responsible for managing and playing sound effects and ambient
 * music in the game.
 * It uses the javax.sound.sampled library to handle audio clips.
 */
public class SoundPlayer {

    /**
     * Plays the sound effect associated with a click interaction.
     */
    public static void playClick() {
        playSound("src/Resources/Assets/Sound/minecraft_click.wav");
    }

    /**
     * Plays the sound effect associated with breaking an object.
     */
    public static void playBreak() {
        playSound("src/Resources/Assets/Sound/minecraft_break.wav");
    }

    /**
     * Plays the sound effect associated with teleportation.
     */
    public static void playTeleport() {
        playSound("src/Resources/Assets/Sound/minecraft_teleport.wav");
    }

    /**
     * Plays the sound effect when a lever is switched on.
     */
    public static void playLeverOn() {
        playSound("src/Resources/Assets/Sound/minecraft_lever_on.wav");
    }

    /**
     * Plays the sound effect when a lever is switched off.
     */
    public static void playLeverOff() {
        playSound("src/Resources/Assets/Sound/minecraft_lever_off.wav");
    }

    /**
     * Plays the sound effect when an item is picked up.
     */
    public static void playPickup() {
        playSound("src/Resources/Assets/Sound/minecraft_pickup.wav");
    }

    /**
     * Plays the sound effect associated with a question or notification (toast).
     */
    public static void playQuestion() {
        playSound("src/Resources/Assets/Sound/minecraft_toast.wav");
    }

    /**
     * Plays the explosion sound effect.
     */
    public static void playExplosion() {
        playSound("src/Resources/Assets/Sound/minecraft_explosion.wav");
    }

    /**
     * Plays the sound effect for interacting with a Redstone Block.
     */
    public static void playRedstoneBlock() {
        playSound("src/Resources/Assets/Sound/minecraft_redstoneblock.wav");
    }

    /**
     * Plays the sound effect for walking on Soul Sand.
     */
    public static void playSoulSand() {
        playSound("src/Resources/Assets/Sound/minecraft_soulsand.wav");
    }

    /**
     * Plays the standard footstep sound effect.
     */
    public static void playSteps() {
        playSound("src/Resources/Assets/Sound/minecraft_steps.wav");
    }

    /**
     * Reference to the clip used for the background cave ambience.
     * Kept static to allow stopping it later.
     */
    private static Clip ambienceClip;

    /**
     * Starts playing the cave ambience sound in a continuous loop.
     * If the ambience is already playing, this method does nothing.
     */
    public static void playCaveAmbience() {
        // Check if the clip is already active to prevent overlapping sounds
        if (ambienceClip != null && ambienceClip.isRunning()) {
            return;
        }
        try {
            File soundFile = new File("src/Resources/Assets/Sound/minecraft_cave_ambience.wav");

            // Verify if the file exists before attempting to play
            if (!soundFile.exists()) {
                return;
            }

            AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundFile);
            ambienceClip = AudioSystem.getClip();
            ambienceClip.open(audioIn);

            // Set the clip to loop continuously
            ambienceClip.loop(Clip.LOOP_CONTINUOUSLY);
            ambienceClip.start();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    /**
     * Stops the cave ambience sound if it is currently playing and releases
     * resources.
     */
    public static void stopCaveAmbience() {
        if (ambienceClip != null) {
            if (ambienceClip.isRunning()) {
                ambienceClip.stop();
            }
            ambienceClip.close(); // Close the clip to free system resources
            ambienceClip = null;
        }
    }

    /**
     * Plays a sound from the specified file path.
     * This method handles the low-level audio system operations.
     *
     * @param filePath the path to the audio file (.wav) to be played
     */
    public static void playSound(String filePath) {
        try {
            File soundFile = new File(filePath);

            // Ensure the file exists to avoid exceptions
            if (!soundFile.exists()) {
                return;
            }

            AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundFile);
            Clip clip = AudioSystem.getClip();
            clip.open(audioIn);
            clip.start();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            // Print the stack trace for debugging purposes if an audio error occurs
            e.printStackTrace();
        }
    }
}
