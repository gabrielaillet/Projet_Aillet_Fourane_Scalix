package scala

/**
 * Main application object for finding collaborations between actors.
 */
object Scalix extends App {
  implicit val client: TMDBClient = new TMDBClient("7bf42eb837c44ba3f7115b72d9299d81")

  private val actor1 = Actor("Christian", "Bale")
  private val actor2 = Actor("Michael", "Caine")

  /**
   * Finds common movies between two actors and their directors.
   *
   * @param actor1 The first actor.
   * @param actor2 The second actor.
   * @return       A set of tuples containing movie titles and director names.
   */
  private def collaboration(actor1: Actor, actor2: Actor): Set[(String, String)] = {
    val movies1 = actor1.movies
    val movies2 = actor2.movies
    val commonMovies = movies1.intersect(movies2)

    commonMovies.flatMap { movie =>
      movie.director.map { director =>
        (movie.title, director.name)
      }
    }
  }

  println(s"Collaborations between ${actor1.firstName} ${actor1.lastName} and ${actor2.firstName} ${actor2.lastName}:")
  val result = collaboration(actor1, actor2)
  result.foreach { case (movie, director) =>
    println(s"Film: $movie, Director: $director")
  }
}