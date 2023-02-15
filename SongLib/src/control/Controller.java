package control;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import songObject.Song;
import songObject.compareMethod;
import control.SongLibrary;

public class Controller implements Initializable {

    // UI elements
    @FXML
    private ListView<String> songListView;
    @FXML
    private TextField songNameField;
    @FXML
    private TextField artistNameField;
    @FXML
    private TextField albumNameField;
    @FXML
    private TextField yearField;
    @FXML
    private Button addButton;
    @FXML
    private Button deleteButton;

    private List<Song> songList;
    private SongLibrary songLibrary;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Create instance of SongLibrary
        songLibrary = new SongLibrary();
        // Load song library from file
        songList = songLibrary.load();
        
        // Sort the song list
        songList.sort(new compareMethod());
        
        // Populate song list view
        ObservableList<String> songListViewItems = FXCollections.observableArrayList();
        for (Song song : songList) {
            songListViewItems.add(song.getTitle() + " - " + song.getArtist());
        }
        songListView.setItems(songListViewItems);
        
        // Select the first song in the list
        if (!songList.isEmpty()) {
            songListView.getSelectionModel().select(0);
            displaySongDetails(songList.get(0));
        }
        
        // Add action listeners to UI elements
        addButton.setOnAction(e -> addSong());
    //  deleteButton.setOnAction(e -> deleteSong()); yet to be made
        songListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> handleSongSelection());
    }
    
    @FXML
    private void handleSongSelection() {
        // Get the selected song from the list view
        int selectedIndex = songListView.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0) {
            Song selectedSong = songList.get(selectedIndex);
            displaySongDetails(selectedSong);
        }
    }

    
    
    
    private void displaySongDetails(Song song) {
        songNameField.setText(song.getTitle());
        artistNameField.setText(song.getArtist());
        albumNameField.setText(song.getAlbum());
        yearField.setText(song.getYear());
    }


    private void addSong() {
        // Get input fields from UI
        String title = songNameField.getText().trim();
        String artist = artistNameField.getText().trim();
        String album = albumNameField.getText().trim();
        String year = yearField.getText().trim();

        // Check if song with same title and artist already exists in the library
        for (Song song : songList) {
            if (song.getTitle().equalsIgnoreCase(title) && song.getArtist().equalsIgnoreCase(artist)) {
                // Song already exists in library
                Alert alert = new Alert(Alert.AlertType.ERROR, "This song already exists in the library.", ButtonType.OK);
                alert.showAndWait();
                return;
            }
        }

        // Add new song to library
        Song newSong = new Song(title, artist, album, year);
        songList.add(newSong);
        songList.sort(new compareMethod());

        // Update UI
        ObservableList<String> songListViewItems = FXCollections.observableArrayList();
        for (Song song : songList) {
            songListViewItems.add(song.getTitle() + " - " + song.getArtist());
        }
        songListView.setItems(songListViewItems);
        songListView.getSelectionModel().select(songList.indexOf(newSong));
        displaySongDetails(newSong);

        // Save library to file
        songLibrary.save(songList);
    }

}
