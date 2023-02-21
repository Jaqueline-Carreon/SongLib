// Dhruv Dinesh
// Jaqueline Carreon

package songObject;

public class Song {
    private String title;
    private String artist;
    private String album;
    private String year;

    public Song(String title, String artist, String album, String year) {
        this.title = title.replace("|", "");
        this.artist = artist.replace("|", "");
        this.album = album.replace("|", "");
        this.year = year;
    }

    public Song() {
        this.title = "";
        this.artist = "";
        this.album = "";
        this.year = "";
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title.replace("|", "");
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist.replace("|", "");
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album.replace("|", "");
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }
}
