package view;

import java.awt.event.ActionEvent;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.io.FileWriter;




import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import songObject.Song;
import songObject.compareMethod;
import control.SongLibrary;

public class Controller implements Initializable {

    // UI elements
    @FXML
    private ListView<String> songList;
    @FXML
    private TextField titleField;
    @FXML
    private TextField artistField;
    @FXML
    private TextField albumField;
    @FXML
    private TextField yearField;
    @FXML
    private Button addButton;
    @FXML
    private Button deleteButton;
    @FXML
    private Button editButton;
    @FXML
    private Label currentArtistLabel;
    @FXML
    private Label currentAlbumLabel;
    @FXML
    private Label currentYearLabel;
    @FXML
    private Label currentTitleLabel;

    private List<Song> songList1;
    private SongLibrary songLibrary;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Create instance of SongLibrary
        songLibrary = new SongLibrary();

        // Load song library from file using Gson
        try {
            String json = Files.readString(Path.of("src/songlist.json"));
            Type songListType = new TypeToken<List<Song>>() {}.getType();
            songList1 = new Gson().fromJson(json, songListType);
        } catch (IOException e) {
            songList1 = songLibrary.load();
        }

        if (songList1 == null) {
            songList1 = new ArrayList<Song>();
        }

        // Sort the song list
        songList1.sort(new compareMethod());

        // Populate song list view
        ObservableList<String> songListViewItems = FXCollections.observableArrayList();
        for (Song song : songList1) {
            songListViewItems.add(song.getTitle() + " - " + song.getArtist());
        }
        songList.setItems(songListViewItems);

        // Select the first song in the list
        if (!songList1.isEmpty()) {
            songList.getSelectionModel().select(0);
            displaySongDetails(songList1.get(0));
        }

        // Add action listeners to UI elements
        addButton.setOnAction(e -> addSong());
        songList.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> handleSongSelection());
    }


    @FXML
    private void handleSongSelection() {
        // Get the selected song from the list view
        int selectedIndex = songList.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0) {
            Song selectedSong = songList1.get(selectedIndex);
            displaySongDetails(selectedSong);
        }
    }

    private void displaySongDetails(Song song) {
        if (song != null) {
            currentTitleLabel.setText(song.getTitle());
            currentArtistLabel.setText(song.getArtist());
            currentAlbumLabel.setText(song.getAlbum());
            currentYearLabel.setText(song.getYear());
        }
    }

    @FXML
    private void addSong() {
        // Get input fields from UI
        String title = titleField.getText().trim();
        String artist = artistField.getText().trim();
        String album = albumField.getText().trim();
        String year = yearField.getText().trim();

        // Check if song with same title and artist already exists in the library
        for (Song song : songList1) {
            if (song.getTitle().equalsIgnoreCase(title) && song.getArtist().equalsIgnoreCase(artist)) {
                // Song already exists in library
                Alert alert = new Alert(Alert.AlertType.ERROR, "This song already exists in the library.", ButtonType.OK);
                alert.showAndWait();
                return;
            }
        }

        // Add new song to library
        Song newSong = new Song(title, artist, album, year);
        songList1.add(newSong);
        songList1.sort(new compareMethod());

        // Update UI
        ObservableList<String> songListViewItems = FXCollections.observableArrayList();
        for (Song song : songList1) {
            songListViewItems.add(song.getTitle() + " - " + song.getArtist());
        }
        songList.setItems(songListViewItems);
        songList.getSelectionModel().select(songList1.indexOf(newSong));
        displaySongDetails(newSong);
        
        // Save the updated song list to file using Gson
        songLibrary.save(songList1);
     }
    
}
    
