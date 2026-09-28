# Airbnb Project

A Spring Boot REST API for managing hotels, rooms, guests, and reservations. The project includes user authentication and role-based access, hotel manager approval, room availability inventory, and Stripe checkout for booking payments.

## Features

- User registration, login, JWT-based authentication, and profile management
- Role-based access for guests, hotel managers, and administrators
- Hotel manager access requests with admin approval or rejection
- Hotel and room management, including activation and ownership checks
- Room inventory generation and availability management
- Booking lifecycle operations, guest association, payment initiation, and cancellation
- Request validation and API documentation through Swagger UI

## API overview

| Area | Base path | Examples |
| --- | --- | --- |
| Guests | `/guests` | Create, list, retrieve, update, and delete guest records |
| Hotels | `/admin/hotels` | Hotel administration |
| Hotel manager requests | `/admin/hotel-manager-requests` | List pending requests; approve or reject a request |
| Bookings | `/bookings` | Initialize bookings, add guests, and initiate payment |

Admin approval routes:

- `GET /admin/hotel-manager-requests`
- `PATCH /admin/hotel-manager-requests/{requestId}/approve`
- `PATCH /admin/hotel-manager-requests/{requestId}/reject`

The API is documented with OpenAPI/Swagger. When the application is running locally, open [Swagger UI](http://localhost:8080/swagger-ui/index.html).

## Requirements

- Java (use the version configured by the project)
- Maven
- A database configured for the application
- Stripe credentials for payment features

## Run locally

1. Clone the repository:

   ```bash
   git clone https://github.com/shikhilrane/AirBnb_Project.git
   cd AirBnb_Project
   ```

2. Configure the database connection and any required secrets (including Stripe settings) using the application's configuration.

3. Start the Spring Boot application with Maven:

   ```bash
   mvn spring-boot:run
   ```

The API is configured for local development at `http://localhost:8080`.

## Authentication

Use the application's authentication endpoints to obtain a JWT, then include it on protected requests:

```
Authorization: Bearer <token>
```

Some operations require a specific role or ownership of the hotel/room being managed.

## Notes

- Hotel manager requests must be reviewed by an administrator before manager access is granted.
- Booking payment endpoints create a Stripe checkout session; complete the required Stripe configuration before using them.
- The API base URLs and Swagger server entries are defined by the application configuration.

## License

No license is specified in this repository yet.
