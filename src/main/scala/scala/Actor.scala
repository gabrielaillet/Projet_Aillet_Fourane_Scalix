package scala

/**
 * Represents an actor with a first name and last name.
 *
 * @param firstName The first name of the actor.
 * @param lastName  The last name of the actor.
 * @param client    The implicit TMDBClient used to fetch additional actor details.
 */
case class Actor(firstName: String, lastName: String)(implicit client: TMDBClient) {
  /**
   * Fetches the ID of the actor.
   *
   * @return An optional actor ID if found.
   */
  def id: Option[Int] = client.fetchActorId(firstName, lastName)

  /**
   * Fetches the movies the actor has appeared in.
   *
   * @return A set of movies the actor has appeared in.
   */
  def movies: Set[Movie] = id.map(client.fetchActorMovies).getOrElse(Set.empty)
}