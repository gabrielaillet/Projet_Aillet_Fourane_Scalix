package scala

/**
 * Represents a movie with an ID and title.
 *
 * @param id     The unique identifier of the movie.
 * @param title  The title of the movie.
 * @param client The implicit TMDBClient used to fetch additional movie details.
 */
case class Movie(id: Int, title: String)(implicit client: TMDBClient) {
  /**
   * Fetches the director of the movie.
   *
   * @return An optional Director if found.
   */
  def director: Option[Director] = client.fetchMovieDirector(id)
}