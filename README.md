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

1. **Open the Project in NetBeans**:
   - Open Apache NetBeans IDE.
   - Go to **File** > **Open Project...**
   - Navigate to the project directory and select the `SmartCampus-API` folder (which contains the `pom.xml` file).
   - Wait for NetBeans to load the project and resolve any Maven dependencies.

2. **Configure a Server**:
   - Ensured that Java EE compatible server configured in NetBeans (such as Apache Tomcat 9 or GlassFish/Payara).


3. **Build the Project**:
   - Right-click the `SmartCampus` project in the **Projects** window.
   - Select **Clean and Build**. This will compile the code and package it into a `.war` file.

4. **Launch the Server**:
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

