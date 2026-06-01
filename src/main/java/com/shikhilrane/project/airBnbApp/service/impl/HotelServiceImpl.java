package com.shikhilrane.project.airBnbApp.service.impl;

import com.shikhilrane.project.airBnbApp.dto.HotelDto;
import com.shikhilrane.project.airBnbApp.entity.Hotel;
import com.shikhilrane.project.airBnbApp.entity.Room;
import com.shikhilrane.project.airBnbApp.exception.ResourceNotFoundException;
import com.shikhilrane.project.airBnbApp.repository.HotelRepository;
import com.shikhilrane.project.airBnbApp.service.HotelService;
import com.shikhilrane.project.airBnbApp.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class HotelServiceImpl implements HotelService {

    private final HotelRepository hotelRepository;
    private final ModelMapper modelMapper;
    private final InventoryService inventoryService;

    @Override
    public HotelDto createNewHotel(HotelDto hotelDto) {
        log.info("Creating a new hotel with name: {}", hotelDto.getName());     // Logs hotel creation request
        Hotel hotel = modelMapper.map(hotelDto, Hotel.class);                   // Converts DTO into Entity
        hotel.setActive(false);                                                 // Newly created hotels are inactive by default
        Hotel saved = hotelRepository.save(hotel);                              // Saves hotel in database
        log.info("Created a new hotel with ID: {}", saved.getId());             // Logs generated hotel ID
        return modelMapper.map(saved, HotelDto.class);                          // Converts Entity back to DTO
    }

    @Override
    public HotelDto getHotelById(Long id) {
        log.info("Getting the hotel with ID: {}", id);                                                                                 // Logs fetch request
        Hotel hotel = hotelRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Hotel not found with ID: " + id)); // Fetches hotel or throws exception
        return modelMapper.map(hotel, HotelDto.class);                                                                                 // Converts Entity to DTO
    }

    // Method to check if hotel exist in the DB or not
    public void validateHotelExists(Long id){
        boolean exist = hotelRepository.existsById(id);
        if (!exist) throw new ResourceNotFoundException("Employee not found with this ID : " + id);
    }

    @Override
    public HotelDto updateHotelById(Long id, HotelDto hotelDto) {
        log.info("Updating the hotel with ID: {}", id);             // Logs update request
        validateHotelExists(id);                                    // Validates hotel existence
        Hotel mappedHotel = modelMapper.map(hotelDto, Hotel.class); // Converts DTO into Entity
        mappedHotel.setId(id);                                      // Sets ID so existing record gets updated
        Hotel savedHotel = hotelRepository.save(mappedHotel);       // Updates hotel in database
        return modelMapper.map(savedHotel, HotelDto.class);         // Converts updated Entity to DTO
    }

    @Override
    @Transactional
    public void deleteHotelById(Long id) {
        Hotel hotel = hotelRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found with ID: "+id));  // Fetches hotel or throws exception

        for(Room room: hotel.getRooms()) {
            inventoryService.deleteFutureInventories(room);     // Deletes inventory records associated with each room
        }

        hotelRepository.deleteById(id);                         // Deletes hotel from database
    }

    @Override
    @Transactional
    public void activateHotel(Long hotelId) {
        log.info("Activating the hotel with ID: {}", hotelId);                                            // Logs hotel activation request
        Hotel hotel = hotelRepository
                .findById(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found with ID: " + hotelId)); // Fetches hotel or throws exception
        hotel.setActive(true);                                                                            // Marks hotel as active and available for booking

        // assuming only do it once
        for(Room room: hotel.getRooms()) {                  // For each room in the hotel, generates inventory records for the next 1 year.
            inventoryService.initializeRoomForAYear(room);
        }
    }
}

/*
    HotelServiceImpl

        Purpose : Implements hotel-related business operations.
                  Acts as a bridge between the Controller and Repository layers.

        Responsibilities :
            - Create a new hotel
            - Get hotel details by ID
            - Update hotel information
            - Delete a hotel
            - Activate a hotel
            - Validate hotel existence

        Methods :

            createNewHotel()
                - Creates a new hotel record
                - Sets hotel status as inactive by default

            getHotelById()
                - Fetches hotel details using hotel ID

            updateHotelById()
                - Updates existing hotel information

            deleteHotelById()
                - Deletes a hotel from the database
                - Removes inventory records associated with hotel rooms before deletion

            activateHotel()
                - Changes hotel status from inactive to active
                - Generates one year of inventory records for all hotel rooms
                - Makes hotel available for booking

            validateHotelExists()
                - Checks whether a hotel exists
                - Throws exception if hotel is not found

        Business Use :
            - Handles hotel management operations
            - Applies business rules before database operations
            - Converts DTOs into entities and vice versa
            - Controls hotel activation lifecycle
            - Throws meaningful exceptions when hotel is not found
            - Automatically generates inventory when a hotel is activated
            - Removes room inventory before hotel deletion
            - Ensures hotels are booking-ready after activation

        Hotel Lifecycle :

               Hotel Created
                    ↓
                 INACTIVE
                    ↓
               activateHotel()
                    ↓
             Inventory Generated
                    ↓
                  ACTIVE
                    ↓
             Available For Booking

        This class acts as the business layer for hotel management.
*/