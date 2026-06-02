package com.shikhilrane.project.airBnbApp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class HotelInfoDto {
    private HotelDto hotel;          // Basic hotel information
    private List<RoomDto> rooms;     // All room types available in the hotel
}

/*
    HotelInfoDto

        Purpose : Combines hotel details and room details
                  into a single response object.

        This DTO contains :
            - Hotel information
            - List of rooms available in the hotel

        Business Use :
            - Used when guest opens hotel details page.
            - Avoids multiple API calls for hotel and rooms.
            - Returns complete hotel information in a single response.

        Example Response :

            {
                "hotel": {
                    "id": 1,
                    "name": "Taj Hotel",
                    "city": "Mumbai"
                },
                "rooms": [
                    {
                        "id": 1,
                        "type": "Single Room",
                        "basePrice": 4000
                    },
                    {
                        "id": 2,
                        "type": "Deluxe Room",
                        "basePrice": 7000
                    }
                ]
            }

        Flow :

            Guest
              ↓
            Hotel Details API
              ↓
            HotelInfoDto
              ↓
            Hotel Information + Room Information

        Note :
            - This is not a database entity.
            - It is used only for API responses.
            - Helps guests view complete hotel details before booking.

        This DTO represents complete information of a hotel.
*/