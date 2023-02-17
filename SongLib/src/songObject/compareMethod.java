package songObject;
import java.util.Comparator;


public class compareMethod implements Comparator<Song> {

public int compare(Song s1, Song s2) {
		
		if(s1.getTitle().toLowerCase().compareTo(s2.getTitle().toLowerCase()) == 0) {
			
			return s1.getArtist().toLowerCase().compareTo(s2.getArtist().toLowerCase());
		} 
		
		return s1.getTitle().toLowerCase().compareTo(s2.getTitle().toLowerCase());
	}
	
	
}