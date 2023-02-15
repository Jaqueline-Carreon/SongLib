package control;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import songObject.Song;

public class SongLibrary {
    private static final String FILENAME = "song_library.txt";

    public SongLibrary() {
    }

    public void save(List<Song> songs) {
        try {
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(FILENAME));
            out.writeObject(songs);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Song> load() {
        List<Song> songs = new ArrayList<>();
        try {
            ObjectInputStream in = new ObjectInputStream(new FileInputStream(FILENAME));
            songs = (List<Song>) in.readObject();
            in.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return songs;
    }
}
