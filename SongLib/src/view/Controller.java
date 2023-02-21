// Dhruv Dinesh
// Jaqueline Carreon

package view;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;




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
import javafx.scene.layout.HBox;

import persistence.Persist;
import songObject.Song;
import songObject.compareMethod;

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
    @FXML
    private HBox aedHBOX;

    private List<Song> songList1;
    private Persist persist;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Create instance of SongLibrary
        persist = new Persist();

        // Load song library from file using Gson
        try {
            String json = Files.readString(Path.of("src/songlist.json"));
            Type songListType = new TypeToken<List<Song>>() {}.getType();
            songList1 = new Gson().fromJson(json, songListType);
        } catch (IOException e) {
            songList1 = persist.load();
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
        deleteButton.setOnAction(e -> deleteSong());
        editButton.setOnAction(e -> initalizeEdit());
        songList.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> handleSongSelection());
    }
    public boolean isValidYear(String yearStr) {
        if (yearStr == null || yearStr.length() != 4) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Invalid year format");
            alert.setContentText("Year must be in YYYY format.");
            alert.showAndWait();
            return false;
        }

        try {
            int year = Integer.parseInt(yearStr);
            if (year <= 0) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText("Invalid year");
                alert.setContentText("Year must be a positive integer.");
                alert.showAndWait();
                return false;
            }
            return true;
        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Invalid year");
            alert.setContentText("Year must be a valid integer.");
            alert.showAndWait();
            return false;
        }
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
        
        if (!year.isEmpty() && !isValidYear(year)) {
            return;
        }
        
          //Checks for no empty title & artist
        if(artist.isEmpty() && title.isEmpty()) {
       	 Alert alert = new Alert(Alert.AlertType.ERROR, "Title and Artist textfield cannot be empty.", ButtonType.OK);
            alert.showAndWait();
            return;
       }
        if(title.isEmpty()) {
        	 Alert alert = new Alert(Alert.AlertType.ERROR, "Song title textfield cannot be empty.", ButtonType.OK);
             alert.showAndWait();
             return;
        }
    
        if(artist.isEmpty()) {
        	 Alert alert = new Alert(Alert.AlertType.ERROR, "Artist textfield cannot be empty.", ButtonType.OK);
             alert.showAndWait();
             return;
        }
        
        

        // Check if song with same title and artist already exists in the library
        for (Song song : songList1) {
            if (song.getTitle().equalsIgnoreCase(title) && song.getArtist().equalsIgnoreCase(artist)) {
                // Song already exists in library
                Alert alert = new Alert(Alert.AlertType.ERROR, "This song already exists in the library.", ButtonType.OK);
                alert.showAndWait();
                return;
            }
        }
        
         // After passing the alerts
        //Confirmation
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Song Addition Alert");
        alert.setContentText("Do you want to add this song?");
        	 
        Optional<ButtonType> result = alert.showAndWait();
        	 
        if(result.get() == ButtonType.OK) {

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
        persist.save(songList1);
        
        //clearing text field after adding
    	titleField.clear();
    	artistField.clear();
    	albumField.clear();
    	yearField.clear();
            
     }else{
        return;
        }
        
    }
    
    
        
    private void deleteSong() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Song Deletion Alert");
        alert.setContentText("Do you want to delete this song?");
        Optional<ButtonType> result = alert.showAndWait();

        if(result.get() == ButtonType.OK) {
            int songID = songList.getSelectionModel().getSelectedIndex();
            songList.getItems().remove(songID);
            songList1.remove(songID);
            persist.save(songList1);

            // Select next song in list, or previous song if deleted song was first in list
            if (songID == 0 && !songList.getItems().isEmpty()) {
                songList.getSelectionModel().select(0);
            } else {
                songList.getSelectionModel().selectNext();
            }

            if(songList.getSelectionModel().isEmpty()) {
                songList.getSelectionModel().select(songID - 1);
            }
            
            //If the songlist is empty, it clears the current details section
           if(songList.getItems().isEmpty()) {
            currentTitleLabel.setText(null);
            currentArtistLabel.setText(null);
            currentAlbumLabel.setText(null);
            currentYearLabel.setText(null);
            }
            
            
        } else {
            return;
        }
    }

    
    private void initalizeEdit() {
    	
    	Button editFinal = new Button("Finalize Edit");
    	Button editCancel = new Button("Cancel Edit");
    	
    	aedHBOX.getChildren().clear();
    	aedHBOX.getChildren().addAll(editFinal,editCancel);
    	
    	//editButton.setText("Finalize Edit");
    	int songID = songList.getSelectionModel().getSelectedIndex();
    	Song editSong = songList1.get(songID);
    	
    	titleField.setText(editSong.getTitle());
    	artistField.setText(editSong.getArtist());
    	albumField.setText(editSong.getAlbum());
    	yearField.setText(editSong.getYear());
    	
    	
    	/// User cancels edit and buttons return to normal
    	editCancel.setOnAction(e -> restore());
    	
    	//User finalizes finalizes the edit
    	editFinal.setOnAction(e -> editSong());	
    	
    }
    
    private void restore() {
    	
    	titleField.clear();
    	artistField.clear();
    	albumField.clear();
    	yearField.clear();
    	
    	
    	aedHBOX.getChildren().clear();
    	aedHBOX.getChildren().addAll(addButton,editButton,deleteButton);	
    	
    	
    }
    
    
    private void editSong() {
        
        int editID = songList.getSelectionModel().getSelectedIndex();
    	Song editSong = songList1.get(editID);
    	
    	//Save the old title & artist names
    	String oldTitle = editSong.getTitle();
    	String oldArtist = editSong.getArtist();
    	
    	
    	// Grabs the edited text fields 
    	String title = titleField.getText().trim();
        String artist = artistField.getText().trim();
        String album = albumField.getText().trim();
        String year = yearField.getText().trim();
        
        if (!year.isEmpty() && !isValidYear(year)) {
            return;
        }
    	
        ///// If song title or artist name is empty warning
        if(artist.isEmpty() && title.isEmpty()) {
          	 Alert alert = new Alert(Alert.AlertType.ERROR, "Title and Artist textfield cannot be empty.", ButtonType.OK);
               alert.showAndWait();
               return;
          }
           if(title.isEmpty()) {
           	 Alert alert = new Alert(Alert.AlertType.ERROR, "Song title textfield cannot be empty.", ButtonType.OK);
                alert.showAndWait();
                return;
           }
       
           if(artist.isEmpty()) {
           	 Alert alert = new Alert(Alert.AlertType.ERROR, "Artist textfield cannot be empty.", ButtonType.OK);
                alert.showAndWait();
                return;
           }
    	
         // Check if song with same title and artist already exists in the library
           Boolean titleMatch = title.equalsIgnoreCase(oldTitle);
           Boolean artistMatch = artist.equalsIgnoreCase(artist);
           Boolean both = titleMatch && artistMatch;

                      
           ///skip the comparison to itself
        	   for (Song song : songList1) {
        		   if(both) {
        			   continue;
        		   }
        		   else if (song.getTitle().equalsIgnoreCase(title) && song.getArtist().equalsIgnoreCase(artist)) {
                   // Song already exists in library
                   Alert alert = new Alert(Alert.AlertType.ERROR, "This song already exists in the library.", ButtonType.OK);
                   alert.showAndWait();
                   return;
        		   }
        	   }
        
        
        
         //Passing Error Alerts
         //Confirmation from user that they want to edit the song
         Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
         alert.setTitle("Song Edit Alert");
         alert.setContentText("Do you want to edit this song?");
         	 
         Optional<ButtonType> result = alert.showAndWait();
         	 
         if(result.get() == ButtonType.OK) {
           
        
    	
        	 ////removing the song
        	 int songID = songList.getSelectionModel().getSelectedIndex();
        	 songList.getItems().remove(editID);
        	 songList1.remove(editID);
    	
    	
        	 //add updated song
        	 Song updated = new Song(title,artist,album,year);
        	 songList1.add(updated);
        	 songList1.sort(new compareMethod());

        	 // Update UI
        	 ObservableList<String> songListViewItems = FXCollections.observableArrayList();
        	 for (Song song : songList1) {
        		 songListViewItems.add(song.getTitle() + " - " + song.getArtist());
        	 }
        	 songList.setItems(songListViewItems);
        	 songList.getSelectionModel().select(songList1.indexOf(updated));
        	 displaySongDetails(updated);
        	 
        	 //saving the changes to the library
             persist.save(songList1);
        	 
        	 
        	 //after updating we move out of the edit phase
        	 restore();
        	 
        	 
         }else {
        	 return;
         }    
        

    }
    
}
