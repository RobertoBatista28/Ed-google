package Utils;

import java.io.File;
import java.io.IOException;
import javax.sound.sampled.*;

public class SoundPlayer {

    public static void playClick() {
        playSound("src/Resources/Assets/Sound/minecraft_click.wav");
    }

    public static void playBreak() {
        playSound("src/Resources/Assets/Sound/minecraft_break.wav");
    }

    public static void playTeleport() {
        playSound("src/Resources/Assets/Sound/minecraft_teleport.wav");
    }

    public static void playLeverOn() {
        playSound("src/Resources/Assets/Sound/minecraft_lever_on.wav");
    }

    public static void playLeverOff() {
        playSound("src/Resources/Assets/Sound/minecraft_lever_off.wav");
    }

    public static void playPickup() {
        playSound("src/Resources/Assets/Sound/minecraft_pickup.wav");
    }

    public static void playQuestion() {
        playSound("src/Resources/Assets/Sound/youtube_question.wav");
    }

    public static void playSound(String filePath) {
        try {
            File soundFile = new File(filePath);
            if (!soundFile.exists()) {
                return;
            }
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundFile);
            Clip clip = AudioSystem.getClip();
            clip.open(audioIn);
            clip.start();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }
}
