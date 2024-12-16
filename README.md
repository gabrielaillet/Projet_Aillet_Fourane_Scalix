## Scalix

### Project Overview  
Scalix is a Scala-based application that interacts with the TMDB API, among the functionalities implemented :
- Fetching the ID of an actor based on their name.
- Fetching the movies of an actor based on their ID.
- Fetching the director of a movie based on its ID.
- Finding common movies between two actors and their directors.

### Generative AI Tools
This project utilized GitHub Copilot, an AI-powered code completion tool, to assist in writing and refactoring code.
It was used mostly to generate comments, as well as complete some code parts if the implementation idea was already clear.

### How to Run the Application
1. **Install Scala and SBT**: Ensure you have Scala and SBT (Simple Build Tool) installed on your system.
2. **Navigate to the Project Directory**: Open a terminal and navigate to the project directory.
3. **Run the Application**: Use the following SBT command to run the application:
   ```sh
   sbt run

### Design Choices

#### Object-Oriented Design
The application was refactored to use an object-oriented architecture by introducing classes for Actor, Movie, Director and TMDBClient. This approach encapsulates data and behavior within classes, improving code organization and readability.

#### Dependencies
- **Scala**: Version 3.3.4
- **Java**: Version 11+ (required for Scala 3 compatibility)
- **SBT**: Scala Build Tool for dependency management
- **json4s**: Library for JSON parsing

### Section 5 Comments
In section 5, we discussed the transition to an object-oriented architecture. This change was made to improve code modularity and maintainability. The TMDBClient class was introduced to handle API interactions, separating this concern from the Actor and Movie classes. This design choice enhances the separation of concerns, making the code more modular and easier to test. However, it also introduces additional complexity and a slight performance overhead. Overall, the transition to an object-oriented design was a positive change that improved the overall structure of the application.