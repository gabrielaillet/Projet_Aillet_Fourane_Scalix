package scala

import java.io.{File, PrintWriter}
import scala.io.Source
import scala.collection.mutable

/**
 * Manages caching of API responses to reduce redundant network requests.
 */
object CacheManager {

  private val cacheDir: String = "data" // Directory for file-based (secondary) cache
  private val primaryCache: mutable.Map[String, String] = mutable.Map() // Primary in-memory cache

  /**
   * Retrieves a cached response or fetches it from the URL if not cached.
   *
   * @param filename The filename used for secondary cache
   * @param url      The URL to fetch data if no cache exists
   * @return         The content of the response as a String
   */
  def getCachedResponse(filename: String, url: String): String = {
    primaryCache.getOrElse(filename, handleSecondaryCache(filename, url))
  }

  /**
   * Handles fetching the response from the secondary cache or the URL if needed.
   *
   * @param filename The filename used for secondary cache
   * @param url      The URL to fetch data if no cache exists
   * @return         The content of the response as a String
   */
  private def handleSecondaryCache(filename: String, url: String): String = {
    val file = new File(s"$cacheDir/$filename")
    if (file.exists()) {
      println(s"Secondary cache hit: $filename")
      val response = loadFromFile(file)
      primaryCache.put(filename, response)
      response
    } else {
      println(s"Cache miss: Fetching data for $filename")
      fetchAndCache(file, url)
    }
  }

  /**
   * Fetches data from the URL and writes it to both primary and secondary caches.
   *
   * @param file The file for secondary cache
   * @param url  The URL to fetch data
   * @return     The fetched response as a String
   */
  private def fetchAndCache(file: File, url: String): String = {
    val response = fetchFromURL(url)
    cacheResponse(file, response)
    response
  }

  /**
   * Fetches content from a given URL.
   *
   * @param url The URL to fetch data from
   * @return    The fetched content as a String
   */
  private def fetchFromURL(url: String): String = {
    Source.fromURL(url).mkString
  }

  /**
   * Writes content to both the secondary and primary caches.
   *
   * @param file    The file for secondary cache
   * @param content The content to cache
   */
  private def cacheResponse(file: File, content: String): Unit = {
    writeToFile(file, content)
    primaryCache.put(file.getName, content)
  }

  /**
   * Reads content from a file.
   *
   * @param file The file to read from
   * @return     The content of the file as a String
   */
  private def loadFromFile(file: File): String = {
    Source.fromFile(file).mkString
  }

  /**
   * Writes content to a file.
   *
   * @param file    The file to write the content to
   * @param content The content to write
   */
  private def writeToFile(file: File, content: String): Unit = {
    file.getParentFile.mkdirs() // Ensure directory exists
    val writer = new PrintWriter(file)
    try {
      writer.write(content)
    } finally {
      writer.close()
    }
  }
}
