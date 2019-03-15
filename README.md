# scheduler-svc

## Prerequirements
    1. Oracle JDK 1.8+
    2. Maven 3.3.9
    3. Mongo DB

## Build and and Run application
    1. Build project: mvn clean install
    2. Run spring boot service: mvn spring-boot:run

## Run the application with active dev profile
    Add -Dspring.profiles.active=dev to command line arguments when running Spring boot app.

    Examples

    1. ```java -jar -Dspring.profiles.active=dev target/ge-adm-service-1.0-SNAPSHOT.jar```
    2. ```mvn spring-boot:run -Dspring.profiles.active=dev```


## Endpoints

# Scheduler service Base URI
http://localhost:8080/scheduler-api/v1

# Health check
http://localhost:8080/scheduler-api/v1/actuator/health

# Swagger UI
http://localhost:8080/scheduler-api/v1/swagger-ui.html

METHOD - POST
    1. /setPreferences

##########################################################

Calendar Service

In Calendar rest-controler, http method "generateTeachersAvailability" addresses to get the teachers
availability.

The controller passes the required dto object to the service layer -> CalendarService -
 'getTeachersAvailabilityForLearnerV2' method.

To get the teachers availability, we have implemented the schedule algorithm which gives the  teacher list with their
available slots.

1. Get the learner's local-start date, local-end date, prefered day and slot.
2. Apply the date operations on given start date and end date and find  the 22 days in between both the dates where
day should be "prefered day".
3. Convert the localDates to the zonedDateTime.
4. Convert the zonedDateTime to UTC.
5. query to the MongoDB, based of UTC - start and end dates, prefered UTC day, hour and miniute. the resultant list will
   be the collection of Calendar object.
6. Now, based on the each fetched teacher-id, call the platform API to get the teacher information(firstName, lastName)
   note: here, platform api accepts list of teacherIDs. Hence, catch all the teacherIDs in the list and send the list object
   as a request body to the platform-api.
7. Now, append the teacher information to the response-dto.
8. send back the response-Dto to the client.




