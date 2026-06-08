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

import static com.shikhilrane.project.airBnbApp.util.AppUtils.getCurrentUser;

@Service
@RequiredArgsConstructor
@Slf4j
public class HotelServiceImpl implements HotelService {

    private final HotelRepository hotelRepository;      // Performs hotel database operations
    private final ModelMapper modelMapper;              // Converts DTOs and entities
    private final InventoryService inventoryService;    // Manages inventory generation and deletion

    // Creates a new hotel and assigns ownership to the authenticated user
    @Override
    public HotelDto createNewHotel(HotelDto hotelDto) {
        log.info("Creating a new hotel with name: {}", hotelDto.getName());     // Logs hotel creation request

        Hotel hotel = modelMapper.map(hotelDto, Hotel.class);                   // Converts DTO into entity
        hotel.setActive(false);                                                 // Newly created hotels are inactive by default

        User user = (User) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();                                                // Retrieves authenticated user

        hotel.setOwner(user);                                                   // Assigns hotel ownership

        Hotel saved = hotelRepository.save(hotel);                              // Saves hotel

        log.info("Created a new hotel with ID: {}", saved.getId());             // Logs generated identifier

        return modelMapper.map(saved, HotelDto.class);                          // Converts entity into DTO
    }

    // Retrieves hotel details after ownership validation
    @Override
    public HotelDto getHotelById(Long id) {

        log.info("Getting the hotel with ID: {}", id);                          // Logs hotel retrieval request

        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found with ID: " + id)); // Fetches hotel

        User user = (User) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();                                                // Retrieves authenticated user

        if (!user.getId().equals(hotel.getOwner().getId())) {                   // Validates hotel ownership
            throw new UnAuthorisedException("This user does not own this hotel with id " + id);
        }

        return modelMapper.map(hotel, HotelDto.class);                          // Converts entity into DTO
    }

    // Validates whether a hotel exists
    public void validateHotelExists(Long id){

        boolean exist = hotelRepository.existsById(id);                         // Checks hotel existence

        if (!exist) {
            throw new ResourceNotFoundException("Hotel not found with ID : " + id);
        }
    }

    // Updates hotel information after ownership validation
    @Override
    public HotelDto updateHotelById(Long id, HotelDto hotelDto) {

        log.info("Updating the hotel with ID: {}", id);                         // Logs hotel update request

        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found with ID: " + id)); // Fetches hotel

        User user = (User) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();                                                // Retrieves authenticated user

        if (!user.getId().equals(hotel.getOwner().getId())) {                   // Validates hotel ownership
            throw new UnAuthorisedException("This user does not own this hotel with id " + id);
        }

        modelMapper.map(hotelDto, hotel);                                       // Maps updated fields onto existing entity

        hotel.setId(id);                                                        // Preserves existing identifier

        Hotel savedHotel = hotelRepository.save(hotel);                         // Saves updated hotel

        return modelMapper.map(savedHotel, HotelDto.class);                     // Converts entity into DTO
    }

    // Deletes a hotel and its future inventories after ownership validation
    @Override
    @Transactional
    public void deleteHotelById(Long id) {

        Hotel hotel = hotelRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found with ID: "+id)); // Fetches hotel

        User user = (User) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();                                                // Retrieves authenticated user

        if (!user.getId().equals(hotel.getOwner().getId())) {                   // Validates hotel ownership
            throw new UnAuthorisedException("This user does not own this hotel with id " + id);
        }

        for(Room room: hotel.getRooms()) {
            inventoryService.deleteFutureInventories(room);                     // Deletes future inventory records
        }

        hotelRepository.deleteById(id);                                         // Deletes hotel
    }

    // Activates a hotel and generates inventory for all rooms
    @Override
    @Transactional
    public void activateHotel(Long hotelId) {

        log.info("Activating the hotel with ID: {}", hotelId);                  // Logs hotel activation request

        Hotel hotel = hotelRepository
                .findById(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found with ID: " + hotelId)); // Fetches hotel

        User user = (User) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();                                                // Retrieves authenticated user

        if (!user.getId().equals(hotel.getOwner().getId())) {                   // Validates hotel ownership
            throw new UnAuthorisedException("This user does not own this hotel with id " + hotelId);
        }

        hotel.setActive(true);                                                  // Marks hotel as active

        for(Room room: hotel.getRooms()) {
            inventoryService.initializeRoomForAYear(room);                      // Generates one year of inventory
        }
    }

    // Retrieves hotel information along with room details
    @Override
    public HotelInfoDto getHotelInfoById(Long hotelId) {

        log.info("Getting Hotel with ID: {}", hotelId);                         // Logs hotel information request

        Hotel hotel = hotelRepository
                .findById(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found with ID: " + hotelId)); // Fetches hotel

        List<RoomDto> rooms = hotel.getRooms()
                .stream()
                .map((element) -> modelMapper.map(element, RoomDto.class))
                .toList();                                                      // Converts room entities into DTOs

        return new HotelInfoDto(
                modelMapper.map(hotel, HotelDto.class),
                rooms
        );                                                                      // Returns hotel and room information
    }

    // Retrieves all hotels owned by the authenticated user
    @Override
    public List<HotelDto> getAllHotels() {

        User user = getCurrentUser();                                            // Retrieves authenticated user

        log.info("Getting all hotels for the admin user with ID: {}", user.getId()); // Logs retrieval request

        List<Hotel> hotels = hotelRepository.findByOwner(user);                  // Retrieves owned hotels

        return hotels
                .stream()
                .map((element) -> modelMapper.map(element, HotelDto.class))      // Converts entities into DTOs
                .collect(Collectors.toList());
    }
}

/*
    HotelServiceImpl

        Purpose :
            Handles hotel lifecycle and hotel management operations.

        Responsibilities :
            - Create hotels
            - Retrieve hotel information
            - Update hotel information
            - Delete hotels
            - Activate hotels
            - Validate hotel ownership
            - Validate hotel existence
            - Generate room inventories
            - Retrieve hotel details with rooms

        Methods :

            createNewHotel()
                - Creates a hotel
                - Assigns ownership
                - Marks hotel as inactive

            getHotelById()
                - Retrieves hotel details
                - Validates ownership

            validateHotelExists()
                - Verifies hotel existence

            updateHotelById()
                - Updates hotel information
                - Validates ownership

            deleteHotelById()
                - Deletes future inventories
                - Deletes hotel
                - Validates ownership

            activateHotel()
                - Activates hotel
                - Generates inventories
                - Validates ownership

            getHotelInfoById()
                - Retrieves hotel details
                - Retrieves room details

            getAllHotels()
                - Retrieves all hotels owned by current user

        Hotel Lifecycle :

            Hotel Created
                    ↓
                INACTIVE
                    ↓
            activateHotel()
                    ↓
                 ACTIVE
                    ↓
          Available For Booking

        Hotel Creation Flow :

            Authenticated User
                    ↓
             Create Hotel
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
             Fetch Hotel
                    ↓
             Fetch Rooms
                    ↓
            Convert To DTOs
                    ↓
             API Response

        Hotel Deletion Flow :

             Delete Request
                    ↓
             Ownership Check
                    ↓
          Delete Future Inventories
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

        Business Use :
            - Hotel onboarding
            - Hotel management
            - Hotel activation
            - Inventory generation
            - Hotel ownership management
            - Guest hotel information

        Security Features :
            - Ownership validation
            - Unauthorized access prevention
            - Protected hotel management operations
            - Authenticated access control

        Note :
            - Every hotel has a single owner.
            - Newly created hotels are inactive by default.
            - Only hotel owners can manage hotels.
            - Inventory generation occurs after activation.
            - Hotel deletion removes future inventories.
            - Ownership validation uses authenticated user identity.

        This service acts as the central
        hotel management and hotel lifecycle
        component of the application.
*/