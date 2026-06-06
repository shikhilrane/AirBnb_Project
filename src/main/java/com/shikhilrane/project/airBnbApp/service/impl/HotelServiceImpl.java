package com.shikhilrane.project.airBnbApp.service.impl;

import com.shikhilrane.project.airBnbApp.dto.HotelDto;
import com.shikhilrane.project.airBnbApp.dto.HotelInfoDto;
import com.shikhilrane.project.airBnbApp.dto.RoomDto;
import com.shikhilrane.project.airBnbApp.entity.Hotel;
import com.shikhilrane.project.airBnbApp.entity.Room;
import com.shikhilrane.project.airBnbApp.entity.User;
import com.shikhilrane.project.airBnbApp.exception.ResourceNotFoundException;
import com.shikhilrane.project.airBnbApp.exception.UnAuthorisedException;
import com.shikhilrane.project.airBnbApp.repository.HotelRepository;
import com.shikhilrane.project.airBnbApp.service.HotelService;
import com.shikhilrane.project.airBnbApp.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class HotelServiceImpl implements HotelService {

    private final HotelRepository hotelRepository;      // Performs hotel database operations
    private final ModelMapper modelMapper;              // Converts DTOs and entities
    private final InventoryService inventoryService;    // Manages inventory generation and deletion

    // Creates a new hotel and assigns ownership to the current HOTEL_MANAGER
    @Override
    public HotelDto createNewHotel(HotelDto hotelDto) {
        log.info("Creating a new hotel with name: {}", hotelDto.getName());     // Logs hotel creation request
        Hotel hotel = modelMapper.map(hotelDto, Hotel.class);                   // Converts DTO into Entity
        hotel.setActive(false);                                                 // Newly created hotels are inactive by default
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();   // Fetches currently authenticated HOTEL_MANAGER
        hotel.setOwner(user);                                                   // Assigns current HOTEL_MANAGER as hotel owner
        Hotel saved = hotelRepository.save(hotel);                              // Saves hotel in database
        log.info("Created a new hotel with ID: {}", saved.getId());             // Logs generated hotel ID
        return modelMapper.map(saved, HotelDto.class);                          // Converts Entity back to DTO
    }

    // Fetches hotel details after validating hotel ownership
    @Override
    public HotelDto getHotelById(Long id) {
        log.info("Getting the hotel with ID: {}", id);                                                                                 // Logs fetch request
        Hotel hotel = hotelRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Hotel not found with ID: " + id)); // Fetches hotel or throws exception
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();           // Fetches currently authenticated HOTEL_MANAGER
        // Verifies that hotel belongs to current user
        if (!user.getId().equals(hotel.getOwner().getId())) {
            throw new UnAuthorisedException("This user does not own this hotel with id " + id);
        }
        return modelMapper.map(hotel, HotelDto.class);                                                                                 // Converts Entity to DTO
    }

    // Method to check if hotel exist in the DB or not
    public void validateHotelExists(Long id){
        boolean exist = hotelRepository.existsById(id);
        if (!exist) throw new ResourceNotFoundException("Hotel not found with ID : " + id);
    }

    // Updates hotel details after validating hotel ownership
    @Override
    public HotelDto updateHotelById(Long id, HotelDto hotelDto) {
        log.info("Updating the hotel with ID: {}", id);             // Logs update request
        Hotel hotel = hotelRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Hotel not found with ID: " + id)); // Fetches hotel or throws exception

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();           // Fetches currently authenticated HOTEL_MANAGER
        // Verifies that hotel belongs to current user
        if (!user.getId().equals(hotel.getOwner().getId())) {
            throw new UnAuthorisedException("This user does not own this hotel with id " + id);
        }

        modelMapper.map(hotelDto, Hotel.class);                 // Converts DTO into Entity
        hotel.setId(id);                                      // Sets ID so existing record gets updated
        Hotel savedHotel = hotelRepository.save(hotel);       // Updates hotel in database
        return modelMapper.map(savedHotel, HotelDto.class);         // Converts updated Entity to DTO
    }

    // Deletes hotel and associated inventories after validating hotel ownership
    @Override
    @Transactional
    public void deleteHotelById(Long id) {
        Hotel hotel = hotelRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found with ID: "+id));  // Fetches hotel or throws exception

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();           // Fetches currently authenticated HOTEL_MANAGER
        // Verifies that hotel belongs to current user
        if (!user.getId().equals(hotel.getOwner().getId())) {
            throw new UnAuthorisedException("This user does not own this hotel with id " + id);
        }

        for(Room room: hotel.getRooms()) {
            inventoryService.deleteFutureInventories(room);     // Deletes inventory records associated with each room
        }

        hotelRepository.deleteById(id);                         // Deletes hotel from database
    }

    // Activates hotel and generates one year of inventory after validating hotel ownership
    @Override
    @Transactional
    public void activateHotel(Long hotelId) {
        log.info("Activating the hotel with ID: {}", hotelId);                                              // Logs hotel activation request
        Hotel hotel = hotelRepository
                .findById(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found with ID: " + hotelId));   // Fetches hotel or throws exception

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();           // Fetches currently authenticated HOTEL_MANAGER
        // Verifies that hotel belongs to current user
        if (!user.getId().equals(hotel.getOwner().getId())) {
            throw new UnAuthorisedException("This user does not own this hotel with id " + hotelId);
        }

        hotel.setActive(true);                                                                              // Marks hotel as active and available for booking

        // assuming only do it once
        for(Room room: hotel.getRooms()) {                  // For each room in the hotel, generates inventory records for the next 1 year.
            inventoryService.initializeRoomForAYear(room);
        }
    }

    // Returns hotel information along with room details
    @Override
    public HotelInfoDto getHotelInfoById(Long hotelId) {
        log.info("Getting Hotel with ID: {}", hotelId);
        Hotel hotel = hotelRepository
                .findById(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found with ID: " + hotelId));   // Fetches hotel or throws exception

        List<RoomDto> rooms = hotel.getRooms()
                .stream()
                .map((element) -> modelMapper.map(element, RoomDto.class))
                .toList();                                                                                  // Converts all room entities into DTOs

        return new HotelInfoDto(modelMapper.map(hotel, HotelDto.class), rooms);                             // Returns hotel details with room details
    }
}

/*
    HotelServiceImpl

        Purpose : Handles hotel-related business operations.
                  Acts as the core business layer between Controllers and Repositories.

        Responsibilities :
            - Create new hotels
            - Fetch hotel details
            - Update hotel information
            - Delete hotels
            - Activate hotels
            - Validate hotel existence
            - Validate hotel ownership
            - Generate inventories for active hotels
            - Provide hotel information along with room details

        Methods :

            createNewHotel()
                - Creates a new hotel
                - Assigns currently logged-in HOTEL_MANAGER as owner
                - Marks hotel as inactive by default
                - Saves hotel into database

            getHotelById()
                - Fetches hotel using ID
                - Verifies ownership before returning data

            validateHotelExists()
                - Checks whether hotel exists
                - Throws exception if hotel is missing

            updateHotelById()
                - Updates hotel details
                - Verifies hotel ownership
                - Saves updated information

            deleteHotelById()
                - Verifies hotel ownership
                - Deletes future inventories of all rooms
                - Deletes hotel record

            activateHotel()
                - Verifies hotel ownership
                - Marks hotel as active
                - Generates inventory for every room
                - Makes hotel available for bookings

            getHotelInfoById()
                - Fetches hotel information
                - Fetches all room details
                - Returns HotelInfoDto response

        Business Use :
            - Allows HOTEL_MANAGER to manage properties
            - Supports hotel onboarding workflow
            - Controls hotel activation process
            - Maintains hotel inventory lifecycle
            - Protects hotel resources using ownership validation
            - Provides detailed hotel information for guests

        Hotel Creation Flow :

            HOTEL_MANAGER Login
                    ↓
             Create Hotel Request
                    ↓
            createNewHotel()
                    ↓
             Owner Assigned
                    ↓
              Active = false
                    ↓
               Hotel Saved

        Hotel Activation Flow :

              Inactive Hotel
                    ↓
            activateHotel()
                    ↓
             Ownership Check
                    ↓
             Active = true
                    ↓
          Generate Room Inventories
                    ↓
           Hotel Ready For Booking

        Hotel Information Flow :

             Hotel Request
                    ↓
             getHotelInfoById()
                    ↓
              Fetch Hotel
                    ↓
              Fetch Rooms
                    ↓
            Convert To DTOs
                    ↓
           Return HotelInfoDto

        Hotel Deletion Flow :

             Delete Request
                    ↓
             Ownership Check
                    ↓
          Delete Room Inventories
                    ↓
              Delete Hotel
                    ↓
             Cleanup Complete

        Ownership Validation Flow :

            Authenticated User
                    ↓
             Fetch Hotel Owner
                    ↓
               Compare IDs
                    ↓
            Access Granted / Denied

        Authorization Rules :

            Hotel Owner
                    ↓
             View Hotel

             Update Hotel

             Activate Hotel

             Delete Hotel

            Other Users
                    ↓
             Access Denied

        Inventory Generation Flow :

             Activate Hotel
                    ↓
              Fetch Rooms
                    ↓
       initializeRoomForAYear()
                    ↓
        Generate Daily Inventories
                    ↓
              Next 365 Days

        Security Features :
            - Ownership validation using User ID
            - Unauthorized access prevention
            - Role-based access enforcement
            - Protected hotel management operations
            - Secure activation workflow

        Data Relationships :

            User (Owner)
                    ↓
                 Hotel
                    ↓
                 Rooms
                    ↓
              Inventories
                    ↓
               Bookings

        Note :
            - Every hotel has a single owner.
            - Newly created hotels are inactive by default.
            - Only hotel owners can manage hotels.
            - Inventory generation occurs only after activation.
            - Hotel deletion removes associated future inventories.
            - Ownership validation is performed using authenticated user ID.

        This class acts as the central business service
        for hotel lifecycle and hotel management operations.
*/