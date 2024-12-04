package scala
import org.json4s.DefaultFormats

implicit val formats: DefaultFormats = DefaultFormats

import scala.io.Source
import java.net.URLEncoder
import org.json4s._
import org.json4s.native.JsonMethods._
import scala.util.Try

// Définitions de types pour plus de clarté
type FullName = (String, String)

object Scalix extends App {

  val apiKey = "7bf42eb837c44ba3f7115b72d9299d81"

  def fetchJson(url: String): Option[JValue] = {
    Try {
      val response = Source.fromURL(url)
      val jsonString = response.mkString
      response.close()
      parse(jsonString)
    }.toOption
  }

  def findActorId(name: String, surname: String): Option[Int] = {
    val query = URLEncoder.encode(s"$name $surname", "UTF-8")
    val url = s"https://api.themoviedb.org/3/search/person?api_key=$apiKey&query=$query"
    fetchJson(url).flatMap { json =>
      (json \ "results").children.headOption.map(result => (result \ "id").extract[Int])
    }
  }

  def findActorMovies(actorId: Int): Set[(Int, String)] = {
    val url = s"https://api.themoviedb.org/3/person/$actorId/movie_credits?api_key=$apiKey"
    fetchJson(url).map { json =>
      (json \ "cast").children.flatMap { movie =>
        for {
          id <- (movie \ "id").extractOpt[Int]
          title <- (movie \ "title").extractOpt[String]
        } yield (id, title)
      }.toSet
    }.getOrElse(Set.empty)
  }

  def findMovieDirector(movieId: Int): Option[(Int, String)] = {
    val url = s"https://api.themoviedb.org/3/movie/$movieId/credits?api_key=$apiKey"
    fetchJson(url).flatMap { json =>
      (json \ "crew").children.flatMap { crewMember =>
        for {
          job <- (crewMember \ "job").extractOpt[String] if job == "Director"
          id <- (crewMember \ "id").extractOpt[Int]
          name <- (crewMember \ "name").extractOpt[String]
        } yield (id, name)
      }.headOption // Pick the first valid director if there are multiple
    }
  }


  def collaboration(actor1: FullName, actor2: FullName): Set[(String, String)] = {
    val id1Opt = findActorId(actor1._1, actor1._2)
    val id2Opt = findActorId(actor2._1, actor2._2)

    (id1Opt, id2Opt) match {
      case (Some(id1), Some(id2)) =>
        val movies1 = findActorMovies(id1)
        val movies2 = findActorMovies(id2)
        val commonMovies = movies1.intersect(movies2)

        commonMovies.flatMap { case (movieId, movieTitle) =>
          findMovieDirector(movieId).map { case (_, directorName) =>
            (movieTitle, directorName)
          }
        }
      case _ =>
        Set.empty
    }
  }

  val actor1 = ("Christian", "Bale")
  val actor2 = ("Michael", "Caine")

  println(s"Collaborations entre ${actor1._1} ${actor1._2} et ${actor2._1} ${actor2._2}:")
  val result = collaboration(actor1, actor2)
  result.foreach { case (movie, director) =>
    println(s"Film: $movie, Réalisateur: $director")
  }
}