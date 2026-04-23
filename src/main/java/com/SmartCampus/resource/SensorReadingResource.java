
package com.SmartCampus.resource;

import com.SmartCampus.dao.MockDatabase;
import com.SmartCampus.exception.SensorUnavailableException;
import com.SmartCampus.model.Sensor;
import com.SmartCampus.model.SensorReading;
import com.SmartCampus.model.ErrorMessage;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.List;

public class SensorReadingResource {

    private final String sensorId;

    // Constructor receives the sensorId from the parent resource (String)
    public SensorReadingResource(String sensorId) {
        this.sensorId = sensorId;
    }

    // GET /api/v1/sensors/{sensorId}/readings
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<SensorReading> getReadings() {
        return MockDatabase.getReadingsForSensor(sensorId);
    }

    // POST /api/v1/sensors/{sensorId}/readings
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addReading(SensorReading reading) {
        // Find the parent sensor
        Sensor sensor = MockDatabase.sensors.stream()
                .filter(s -> s.getId().equals(sensorId))
                .findFirst()
                .orElse(null);
    if (sensor == null) {
        ErrorMessage error = new ErrorMessage(
            404,
            "SENSOR_NOT_FOUND",
            "Sensor with ID " + sensorId + " does not exist."
        );
        return Response.status(Response.Status.NOT_FOUND)
                .entity(error)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

        // Ensure the sensor is not in MAINTENANCE
        if ("MAINTENANCE".equalsIgnoreCase(sensor.getStatus())) {
            throw new SensorUnavailableException("Sensor " + sensorId + " is in maintenance mode and cannot accept readings.");
        }

        // Set ID and timestamp
        reading.setId(MockDatabase.nextReadingId());
        reading.setTimestamp(System.currentTimeMillis()); // epoch milliseconds

        // Store reading in DataStore 
        MockDatabase.addReading(sensorId, reading);

        // Update parent sensor's currentValue
        sensor.setCurrentValue(reading.getValue());

        // Build response
        URI location = URI.create("/api/v1/sensors/" + sensorId + "/readings/" + reading.getId());
        return Response.created(location).entity(reading).build();
    }
}
