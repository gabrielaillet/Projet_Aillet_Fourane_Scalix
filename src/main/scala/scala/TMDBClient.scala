package scala

import org.json4s.*
import org.json4s.native.JsonMethods.*
import java.net.URLEncoder
import Scalix.client

/**
 * Client for interacting with the TMDB API.
 *
 * @param apiKey The API key for authenticating requests.
 */
class TMDBClient(apiKey: String) {
  implicit val formats: DefaultFormats = DefaultFormats

  /**
   * Fetches the ID of an actor based on their name.
   *
   * @param firstName The first name of the actor.
   * @param lastName  The last name of the actor.
   * @return An optional actor ID if found.
   */
  def fetchActorId(firstName: String, lastName: String): Option[Int] = {
    val query = URLEncoder.encode(s"$firstName $lastName", "UTF-8")
    val url = s"https://api.themoviedb.org/3/search/person?api_key=$apiKey&query=$query"
    val filename = s"actor_${firstName}_$lastName.json"
    val response = CacheManager.getCachedResponse(filename, url)
    parse(response).extractOpt[JValue].flatMap { json =>
      (json \ "results").children.headOption.map { result =>
        (result \ "id").extract[Int]
      }
    }
  }

  /**
   * Fetches the movies of an actor based on their ID.
   *
   * @param actorId The ID of the actor.
   * @return A set of movies the actor has appeared in.
   */
  def fetchActorMovies(actorId: Int): Set[Movie] = {
    val url = s"https://api.themoviedb.org/3/person/$actorId/movie_credits?api_key=$apiKey"
    val filename = s"actor$actorId.json"
    val response = CacheManager.getCachedResponse(filename, url)
    parse(response).extractOpt[JValue].map { json =>
      (json \ "cast").children.flatMap { movie =>
        for {
          id <- (movie \ "id").extractOpt[Int]
          title <- (movie \ "title").extractOpt[String]
        } yield Movie(id, title)
      }.toSet
    }.getOrElse(Set.empty)
  }

  /**
   * Fetches the director of a movie based on its ID.
   *
   * @param movieId The ID of the movie.
   * @return An optional Director if found.
   */
  def fetchMovieDirector(movieId: Int): Option[Director] = {
    val url = s"https://api.themoviedb.org/3/movie/$movieId/credits?api_key=$apiKey"
    val filename = s"movie$movieId.json"
    val response = CacheManager.getCachedResponse(filename, url)
    parse(response).extractOpt[JValue].flatMap { json =>
      (json \ "crew").children.flatMap { crewMember =>
        for {
          job <- (crewMember \ "job").extractOpt[String] if job == "Director"
          id <- (crewMember \ "id").extractOpt[Int]
          name <- (crewMember \ "name").extractOpt[String]
        } yield Director(id, name)
      }.headOption
    }
  }
}