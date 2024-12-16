package scala

case class Actor(firstName: String, lastName: String)(implicit client: TMDBClient) {
  def id: Option[Int] = client.fetchActorId(firstName, lastName)

  def movies: Set[Movie] = id.map(client.fetchActorMovies).getOrElse(Set.empty)
}
