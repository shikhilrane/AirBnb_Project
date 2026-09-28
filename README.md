# Airbnb Project

A Spring Boot REST API for hotel management and booking. The project provides endpoints for users, hotel managers, hotels, rooms, guests, room inventory, bookings, and Stripe payments.

## Tech stack

- Java 21
- Spring Boot 4.0.1
- Spring Web MVC and Spring Data JPA
- PostgreSQL
- Spring Security and JWT
- Stripe Checkout and webhooks
- OpenAPI / Swagger UI
- Maven

## Features

- User signup, login, and JWT refresh
- Role-based access and authenticated user profiles
- Hotel manager access requests with administrator approval
- Hotel and room management with ownership checks
- Room inventory and availability management
- Hotel search and hotel information
- Booking initialization, guest association, payment, cancellation, and status tracking
- Stripe payment webhook processing

## Setup

### Requirements

- Java 21
- Maven
- PostgreSQL
- Stripe account and API keys (for payment features)

### Database and secrets

The application is configured to connect to a PostgreSQL database named `airBnb`. Create this database in your local PostgreSQL instance. Configure the database URL, username, and password in `src/main/resources/application.properties`.

Set the following environment variables:

- `JWT_SECRET_KEY` — JWT signing secret
- `STRIPE_SECRET_KEY` — Stripe secret API key
- `STRIPE_WEBHOOK_SECRET` — Stripe webhook signing secret

Do not commit database credentials or secrets to source control.

### Run locally

```bash
git clone https://github.com/shikhilrane/AirBnb_Project.git
cd AirBnb_Project
mvn spring-boot:run
```

Base URL:

```text
http://localhost:8080/api/v1
```

Swagger UI:

```text
http://localhost:8080/api/v1/swagger-ui/index.html
```

OpenAPI JSON:

```text
http://localhost:8080/api/v1/v3/api-docs
```

## API endpoints

All paths below are relative to the `/api/v1` context path. For example, `POST /auth/login` is available at `http://localhost:8080/api/v1/auth/login`.

### Authentication — `/auth`

| Method | Endpoint | Description |
| --- | --- | --- |
| POST | `/auth/signup` | Register a new user |
| POST | `/auth/login` | Authenticate a user and return an authentication token |
| POST | `/auth/refresh` | Refresh an authentication token |

### User profile and bookings — `/users`

| Method | Endpoint | Description |
| --- | --- | --- |
| PATCH | `/users/profile` | Update the authenticated user's profile |
| GET | `/users/profile` | Retrieve the authenticated user's profile |
| GET | `/users/myBookings` | Retrieve the authenticated user's bookings |

### Guests — `/guests`

| Method | Endpoint | Description |
| --- | --- | --- |
| POST | `/guests` | Create a guest |
| GET | `/guests` | List guests |
| GET | `/guests/{guestId}` | Retrieve a guest by ID |
| PUT | `/guests/{guestId}` | Update a guest |
| PATCH | `/guests/{guestId}` | Partially update a guest |
| DELETE | `/guests/{guestId}` | Delete a guest |

### Hotel manager access requests

| Method | Endpoint | Description |
| --- | --- | --- |
| POST | `/hotel-manager-requests` | Submit a request to become a hotel manager |

### Administrator — manager requests

| Method | Endpoint | Description |
| --- | --- | --- |
| GET | `/admin/hotel-manager-requests` | List pending hotel manager requests |
| PATCH | `/admin/hotel-manager-requests/{requestId}/approve` | Approve a manager request |
| PATCH | `/admin/hotel-manager-requests/{requestId}/reject` | Reject a manager request |

### Hotels — `/hotels`

| Method | Endpoint | Description |
| --- | --- | --- |
| POST | `/hotels` | Create a hotel |
| GET | `/hotels` | List hotels |
| GET | `/hotels/{hotelId}` | Retrieve hotel details |
| PUT | `/hotels/{hotelId}` | Update hotel details |
| DELETE | `/hotels/{hotelId}` | Delete a hotel |
| PATCH | `/hotels/{hotelId}/activate` | Activate a hotel |
| GET | `/hotels/{hotelId}/bookings` | Retrieve bookings for a hotel |
| GET | `/hotels/{hotelId}/reports` | Retrieve a hotel report |

### Rooms — `/rooms/{hotelId}/rooms`

| Method | Endpoint | Description |
| --- | --- | --- |
| POST | `/rooms/{hotelId}/rooms` | Create a room in a hotel |
| GET | `/rooms/{hotelId}/rooms` | List rooms in a hotel |
| GET | `/rooms/{hotelId}/rooms/{roomId}` | Retrieve room details |
| PUT | `/rooms/{hotelId}/rooms/{roomId}` | Update room details |
| DELETE | `/rooms/{hotelId}/rooms/{roomId}` | Delete a room |

### Room inventory — `/inventory`

| Method | Endpoint | Description |
| --- | --- | --- |
| GET | `/inventory/rooms/{roomId}` | Retrieve room inventory and availability |
| PATCH | `/inventory/rooms/{roomId}` | Update room inventory |

### Hotel search — `/searchHotels`

| Method | Endpoint | Description |
| --- | --- | --- |
| GET | `/searchHotels/search` | Search for hotels using search criteria |
| GET | `/searchHotels/{hotelId}/info` | Retrieve public information for a hotel |

### Bookings — `/bookings`

| Method | Endpoint | Description |
| --- | --- | --- |
| POST | `/bookings/init` | Initialize a booking and reserve room inventory |
| POST | `/bookings/{bookingId}/addGuests` | Add guests to a booking |
| POST | `/bookings/{bookingId}/payments` | Create a Stripe checkout session |
| POST | `/bookings/{bookingId}/cancel` | Cancel a booking and trigger a refund flow when applicable |
| GET | `/bookings/{bookingId}/status` | Retrieve the current booking status |

### Stripe webhook

| Method | Endpoint | Description |
| --- | --- | --- |
| POST | `/webhook/payment` | Receive and process Stripe payment events |

## Authentication

Send the JWT in the authorization header for protected endpoints:

```text
Authorization: Bearer <token>
```

Access may depend on the user's role and ownership of the requested resource. Use Swagger UI to authorize requests and try the protected endpoints.

## License

No license is currently specified in this repository.
