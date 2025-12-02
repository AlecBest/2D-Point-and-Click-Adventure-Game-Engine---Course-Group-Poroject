package ca.uwo.cs2212.group21;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class SoundManager {
    private MediaPlayer backgroundPlayer;
    private Map<String, Media> soundCache = new HashMap<>();

    private boolean footstepsPlaying = false;

    public void playSFootSteps() {
        if (footstepsPlaying) return; // already playing → do not overlap

        footstepsPlaying = true;
        try {
            Media media = loadMedia("footsteps.mp3");
            if (media != null) {
                MediaPlayer effectPlayer = new MediaPlayer(media);
                effectPlayer.setVolume(0.8); // Slightly louder for effects
                effectPlayer.play();
                effectPlayer.setOnEndOfMedia(() -> footstepsPlaying = false);
            }
        } catch (Exception e) {
            System.err.println("Error playing sound effect: " + e.getMessage());
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
}
