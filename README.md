# SmartCampus-API
The SmartCampus API is a RESTful service built with Java JAX-RS and Tomcat 9 for facilities management. It manages rooms and sensors (CO2, occupancy), offers a versioned /api/v1 endpoint, ensures data integrity, and uses exception mappers and logging filters for reliability and observability.

## API Design Overview

- **Technology Stack**: Pure Java JAX-RS (No Spring Boot or other external frameworks are used).
- **Data Storage**: In-memory data structures (e.g., `HashMap` and `ArrayList`) are utilized to manage state. No external database technologies like SQL Server are involved, ensuring lightweight, standalone operation.
- **Base Path**: The API is versioned and operates under the `/api/v1` base path.
- **Resource Architecture**:
  - `Discovery Resource (/api/v1/)`: Returns API metadata and links to available resource collections.
  - `Rooms (/api/v1/rooms)`: Endpoints to create, list, view, and delete campus rooms.
  - `Sensors (/api/v1/sensors)`: Endpoints to register new sensors, view sensors (with optional filtering by type), and fetch sensor details.
  - `Sensor Readings (/api/v1/sensors/{sensorId}/readings)`: Sub-resource locator for sensors to fetch historical data and publish new readings.
- **Error Handling**: Custom exception mappers and standardized error responses are used to provide meaningful feedback (e.g., trying to delete a room containing active sensors returns a specific `409 Conflict` or `400 Bad Request` styled exception).

## Build and Launch Instructions

This project is built using Maven and is fully compatible with Apache NetBeans IDE.

1. **Download the ZIP form the Repo**:
  - https://github.com/ViharaSenanayake/SmartCampus-API/

2. **Open the Project in NetBeans**:
   - Open Apache NetBeans IDE.
   - Go to **File** > **Import Project** > **From ZIP...**
   - Navigate to the project directory and select the `SmartCampus-API` folder (which contains the `pom.xml` file).
   - Wait for NetBeans to load the project and resolve any Maven dependencies.

3. **Configure a Server**:
   - Ensured that Java EE compatible server configured in NetBeans (such as Apache Tomcat 9 or GlassFish/Payara).


4. **Build the Project**:
   - Right-click the `SmartCampus` project in the **Projects** window.
   - Select **Clean and Build**. This will compile the code and package it into a `.war` file.

5. **Launch the Server**:
   - Right-click the project again and select **Run**.
   - NetBeans will automatically deploy the application to your configured server and launch the default browser. 
   - By default, the API will be accessible at `http://localhost:8080/SmartCampus/api/v1` 

## Sample API Interactions

Here are five curl commands you can use to test successful interactions with the SmartCampus API. 

### 1. View API Discovery Metadata
Retrieve the API metadata and links to collections.
```bash
curl -X GET "http://localhost:8080/SmartCampus/api/v1/" \
  -H "Accept: application/json"
```

### 2. List All Rooms
Retrieve a list of all existing rooms in the campus.
```bash
curl -X GET "http://localhost:8080/SmartCampus/api/v1/rooms" \
  -H "Accept: application/json"
```

### 3. Create a New Room
Register a new room in the system.
```bash
curl -X POST "http://localhost:8080/SmartCampus/api/v1/rooms" \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -d '{"name": "Library Study Room 1", "capacity": 6}'
```

### 4. Register a New Sensor
Create a new temperature sensor for an existing room (e.g., Room `R1`).
```bash
curl -X POST "http://localhost:8080/SmartCampus/api/v1/sensors" \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -d '{"type": "TEMPERATURE", "status": "ACTIVE", "currentValue": 22.5, "roomId": "R1"}'
```

### 5. Publish a Sensor Reading
Submit a new sensor reading to an existing sensor (e.g., Sensor `S1`).
```bash
curl -X POST "http://localhost:8080/SmartCampus/api/v1/sensors/S1/readings" \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -d '{"value": 460.0}'
```


# Question 1: 

We explicitly use the @Consumes(MediaType.APPLICATION_JSON) annotation on the POST method. Explain the technical consequences if a client attempts to send data in a different format, such as text/plain or application/xml. How does JAX-RS handle this mismatch?

Answer: 

When the @Consumes(MediaType.APPLICATION_JSON) annotation is defined at the JAX-RS resource method, it shows that this method supports processing only requests having the Content-Type header value equal to application/json. In case the client sends data using another type of content header, e.g., Content-Type: text/plain or Content-Type: application/xml, JAX-RS reacts differently: 

1.	Failed Request Routing: In the request routing stage, when JAX-RS (e.g., Jersey framework) inspects the media type in the Content-Type header of the incoming request against the media types specified in the @Consumes annotation for available methods, a mismatch happens. The JAX-RS framework will skip the target method and proceed to the next candidate method. 

2.	HTTP 415 Status Code: Before invoking the method, JAX-RS intercepts this conflict and returns the HTTP 415 Unsupported Media Type response back to the client. It means that the server will not process this request because its payload has an unsupported media type.


3.	Skipping Deserialization: Because the server rejects this request at the routing stage, the JSON message body reader will not be called.

# Question 2: 

You implemented this filtering using @QueryParam. Contrast this with an alternative design where the type is part of the URL path (e.g., /api/v1/sensors/type/CO2). Why is the query parameter approach generally considered superior for filtering and searching collections?

Answer:

There are many more reasons why @QueryParam (/api/v1/sensors?type=CO2) is preferable to the path parameter variant (/api/v1/sensors/type/CO2) in RESTful API development when working with collections, as follows: 

1.	Resource Identification or Resource Filtering: According to best practices, paths describe resource location while query parameters modify the identified resources. This means that /sensors identifies the resource collection (or /sensors/{id} for an individual sensor), while CO2 is used as a modifier of that resource (type of sensors). 

2.	Combinability and Orderlessness: Query parameters are flexible and can be added, removed, and mixed in any order. For example, if in addition to CO2 we need to add filtering criteria like sensor status (ACTIVE) and the room number (R1), then our query is just /sensors?type=CO2&status=ACTIVE&roomId=R1, while with path parameters we would get an impossible nested route /sensors/type/CO2/status/ACTIVE/room/R1. 
3.	Optionality: Since query parameters are optional by nature, we can easily request both full or partial sets of sensors without changing @GET routes. To do this, the same endpoint @Path("sensors") and @GET can handle all queries, while with path parameters, we need to define many different paths with conditional logic.

4.	RESTful Conventions: The query string for filtering adheres to web and RESTful conventions, making the API predictable and user-friendly as it follows the standard conventions used in web searches

# Question 3: 

Discuss the architectural benefits of the Sub-Resource Locator pattern. How does delegating logic to separate classes help manage complexity in large APIs compared to defining every nested path (e.g., sensors/{id}/readings/{rid}) in one massive controller class?

Answer:

The Sub-Resource Locator design pattern comes with great architectural advantages in making a big REST API scalable by passing nested routing responsibilities to separate classes (such as routing from SensorResource to SensorReadingResource). Some of the advantages of the pattern versus using one giant controller are as follows:

1.	Concern Separation (Modularity): One big controller that does everything related to nested routes turns out to be a "God class" with hundreds or even thousands of lines of codes. Using sub-resource locators breaks up the problem into different classes for each resource. The SensorResource will handle sensors, and the SensorReadingResource will handle only the readings.

2.	State Encapsulation: If you use one giant controller, then each nested method such as getReadingById will have both the parent id (sensorId) and child rid as the arguments of the method. But in the case of sub-resource locators, the parent id such as sensorId will be injected into the constructor of the sub-resource class once.

3.	Readability & Maintainability: Small, tightly focused classes will make code navigation, debugging, testing, and maintenance considerably more comfortable for the developer. Should there be a problem in reading sensors’ values, developers will know which class to check out right away. Furthermore, working in teams, having different classes for different tasks will reduce merging issues in Git by having developers edit different files at once.

4.	Objects-Oriented Pattern: From an object-oriented standpoint, this pattern is ideal because it perfectly maps the URL’s object hierarchy into a Java object hierarchy.

# Question 4: 

Why is HTTP 422 often considered more semantically accurate than a standard 404 when the issue is a missing reference inside a valid JSON payload?

Answer:

A response status code HTTP 404 Not Found typically implies that the resource indicated by the routing URI doesn't exist (for instance, GET /api/v1/sensors/999). But in cases where the client makes a request to an existing endpoint, such as POST /api/v1/sensors, with proper JSON format but the referenced ID is non-existent, returning a 404 status code would be highly misleading for the client. In such instances, the client may wrongly conclude that there is either an error with the endpoint URL or a typo on its part. 
A more appropriate approach is to use a response status code HTTP 422 Unprocessable Entity, which conveys the following message to the client: "The server recognizes the content-type and JSON format is correct, but it cannot process the instructions in the payload."

# Question 5: 

From a cybersecurity standpoint, explain the risks associated with exposing internal Java stack traces to external API consumers. What specific information could an attacker gather from such a trace?

Answer:

Leakage of your backend stack traces in their raw form is another serious Information Disclosure attack vector. It gives adversaries the full picture of how the backend systems are set up, helping them discover and abuse potential weaknesses in these applications.

Here are some specific details an attacker can obtain from leaked information:  
1.	Versions of framework and library dependencies: The stack trace shows the package name and exact version number used by various packages. This information enables the attacker to look up these versions in the CVEs database to check if there are any known issues with them.   

2.	Application internals: Stack traces may expose the internal structure of the software package in terms of its classes and internal paths.  


3.	Database and infrastructure info: When a backend error occurs and causes the exception, it may inadvertently expose the names of tables, database constraints, as well as third party API points of interaction with the application.  

4.	Abuse of the logic: Since the adversary can see the precise order of method invocations that cause the program to fail, he can use this knowledge to craft a special input that will lead to DoS situations.

# Question 6: 

Why is it advantageous to use JAX-RS filters for cross-cutting concerns like logging, rather than manually inserting Logger.info() statements inside every single resource method?

Answer:

There are numerous reasons why using JAX-RS filters (ContainerRequestFilter/ContainerResponseFilter) to handle logging is better than doing Logger.info() in every method.

First, separation of concerns - the aspect of adding logs to your program is completely different from your core business logic. Therefore, implementing such an approach by means of filters can help separate logging logic and make the resource methods clean.

Secondly, logging through a filter will always be consistent since a filter acts as a single interception point for all inbound requests and outbound responses. In case the logger is placed directly in the methods, it may happen that you forget about one or more endpoints, which is not good at all when it comes to system observation and monitoring.

Thirdly, using filters allows you to avoid maintenance problems in the future. Changing timestamps' format, adding trace ID for distributed tracing, hiding sensitive information – everything becomes much simpler with filters as opposed to manual logging in resource methods.

Lastly, using filters enables you to get precise timing for your endpoint processing times.
