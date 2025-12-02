package ca.uwo.cs2212.group21;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class SoundManager {
    private MediaPlayer backgroundPlayer;
    private MediaPlayer footstepsPlayer;
    private Map<String, Media> soundCache = new HashMap<>();

    private boolean footstepsPlaying = false;

    /**
     * Plays footsteps sound once. Won't overlap if already playing.
     */
    public void playSFootSteps() {
        if (footstepsPlaying) return; // already playing → do not overlap

        footstepsPlaying = true;
        try {
            Media media = loadMedia("footsteps.mp3");
            if (media != null) {
                MediaPlayer effectPlayer = new MediaPlayer(media);
                effectPlayer.setVolume(0.8);
                effectPlayer.play();
                effectPlayer.setOnEndOfMedia(() -> footstepsPlaying = false);
            }
        } catch (Exception e) {
            System.err.println("Error playing sound effect: " + e.getMessage());
        }
    }

    /**
     * Starts looping footsteps for continuous movement.
     * Call stopFootsteps() when movement ends.
     */
    public void startFootsteps() {
        if (footstepsPlayer != null) {
            footstepsPlayer.stop();
        }
        try {
            Media media = loadMedia("footsteps.mp3");
            if (media != null) {
                footstepsPlayer = new MediaPlayer(media);
                footstepsPlayer.setCycleCount(MediaPlayer.INDEFINITE);
                footstepsPlayer.setVolume(0.8);
                footstepsPlayer.play();
            }
        } catch (Exception e) {
            System.err.println("Error playing footsteps: " + e.getMessage());
        }
    }

    /**
     * Stops the looping footsteps sound.
     */
    public void stopFootsteps() {
        if (footstepsPlayer != null) {
            footstepsPlayer.stop();
            footstepsPlayer = null;
        }
    }

    // Allows for background music to be played until stopped
    public void playBackgroundMusic(String filename) {
        try {
            if (backgroundPlayer != null) {
                backgroundPlayer.stop();
            }
            Media media = loadMedia(filename);
            if (media != null) {
                backgroundPlayer = new MediaPlayer(media);
                backgroundPlayer.setCycleCount(MediaPlayer.INDEFINITE);
                backgroundPlayer.setVolume(0.5); // Default volume
                backgroundPlayer.play();
            }
        } catch (Exception e) {
            System.err.println("Error playing background music: " + e.getMessage());
        }
    }

    // Allows for sound effects to be played
    public void playSoundEffect(String filename) {
        try {
            Media media = loadMedia(filename);
            if (media != null) {
                MediaPlayer effectPlayer = new MediaPlayer(media);
                effectPlayer.setVolume(0.8); // Slightly louder for effects
                effectPlayer.play();
            }
        } catch (Exception e) {
            System.err.println("Error playing sound effect: " + e.getMessage());
        }
    }

    // Stops the background music
    public void stopBackgroundMusic() {
        if (backgroundPlayer != null) {
            backgroundPlayer.stop();
        }
    }

    public void playNPCsound() {
        try {
            Media media = loadMedia("npc.mp3");
            if (media != null) {
                MediaPlayer effectPlayer = new MediaPlayer(media);
                effectPlayer.setVolume(0.8); // Slightly louder for effects
                effectPlayer.play();
            }
        } catch (Exception e) {
            System.err.println("Error playing sound effect: " + e.getMessage());
        }
    }

    // Loads the media file from the sounds folder
    private Media loadMedia(String filename) {
        if (soundCache.containsKey(filename)) {
            return soundCache.get(filename);
        }
        try {
            URL resource = getClass().getResource("/sounds/" + filename);
            if (resource == null) {
                System.err.println("Sound file not found: " + filename);
                return null;
            }
            Media media = new Media(resource.toString());
            soundCache.put(filename, media);
            return media;
        } catch (Exception e) {
            System.err.println("Error loading media: " + e.getMessage());
            return null;
        }
    }

    // Button sound effects
    public void playStartButtonSound() {
        playSoundEffect("buttons/startbutton.mp3");
    }

    public void playLoadGameSound() {
        playSoundEffect("buttons/loadgame.mp3");
    }

    public void playInventorySound() {
        playSoundEffect("buttons/inventory.mp3");
    }

    public void playNextButtonSound() {
        playSoundEffect("buttons/nextbutton.mp3");
    }

    public void playDialogueCloseSound() {
        playSoundEffect("buttons/dialogueclosebutton.mp3");
    }

    public void playDropButtonSound() {
        playSoundEffect("buttons/dropbutton.mp3");
    }

    public void playQuitButtonSound() {
        playSoundEffect("buttons/quitbutton.mp3");
    }

    public void playEscButtonSound() {
        playSoundEffect("buttons/escbutton.mp3");
    }

    /**
     * Plays success.mp3 followed by success2.mp3 in sequence.
     * The second sound plays automatically when the first one ends.
     */
    public void playSuccessSequence() {
        try {
            Media media1 = loadMedia("success.mp3");
            if (media1 != null) {
                MediaPlayer player1 = new MediaPlayer(media1);
                player1.setVolume(0.8);
                player1.setOnEndOfMedia(() -> {
                    // Play success2.mp3 after success.mp3 finishes
                    playSoundEffect("success2.mp3");
                });
                player1.play();
            }
        } catch (Exception e) {
            System.err.println("Error playing success sequence: " + e.getMessage());
        }
    }
}
