package control;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import songObject.Song;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class SongLibrary {

    private static final String FILENAME = "src/songlist.json";

    public List<Song> load() {
        List<Song> songList = new ArrayList<>();
        try (FileReader reader = new FileReader(FILENAME)) {
            Type listType = new TypeToken<List<Song>>() {}.getType();
            songList = new Gson().fromJson(reader, listType);
        } catch (FileNotFoundException e) {
            System.out.println("Song library file not found. Returning empty list.");
        } catch (IOException e) {
            System.out.println("Error reading song library file. Returning empty list.");
        }
        return songList;
    }

    public void save(List<Song> songList) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (Writer writer = new FileWriter(FILENAME)) {
            gson.toJson(songList, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
