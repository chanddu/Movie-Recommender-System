import java.io.Serializable;

public class Movie implements Serializable {
    private Integer movieID;
    private String title;
    private String genres;

    public Movie(Integer movieID, String title, String genres) {
        this.movieID = movieID;
        this.title = title;
        this.genres = genres;
    }

    public Integer getMovieID() {
        return movieID;
    }

    public String getTitle() {
        return title;
    }

    public String getGenre() {
        return genres;
    }

    public String toString() {
        return "Movie [movieId=" + movieID + ", title=" + title + ", genres=" + genres + "]";
    }
}
