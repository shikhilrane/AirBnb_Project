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

    private final HotelRepository hotelRepository;
    private final ModelMapper modelMapper;
    private final InventoryService inventoryService;

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
        if (!user.equals(hotel.getOwner())) {
            throw new UnAuthorisedException("This user does not own this hotel with id " + id);             // Prevents unauthorized hotel access
        }
        return modelMapper.map(hotel, HotelDto.class);                                                                                 // Converts Entity to DTO
    }

    // Method to check if hotel exist in the DB or not
    public void validateHotelExists(Long id){
        boolean exist = hotelRepository.existsById(id);
        if (!exist) throw new ResourceNotFoundException("Employee not found with this ID : " + id);
    }

    // Updates hotel details after validating hotel ownership
    @Override
    public HotelDto updateHotelById(Long id, HotelDto hotelDto) {
        log.info("Updating the hotel with ID: {}", id);             // Logs update request
        Hotel hotel = hotelRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Hotel not found with ID: " + id)); // Fetches hotel or throws exception

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();           // Fetches currently authenticated HOTEL_MANAGER
        // Verifies that hotel belongs to current user
        if (!user.equals(hotel.getOwner())) {
            throw new UnAuthorisedException("This user does not own this hotel with id " + id);             // Prevents unauthorized hotel access
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
        if (!user.equals(hotel.getOwner())) {
            throw new UnAuthorisedException("This user does not own this hotel with id " + id);             // Prevents unauthorized hotel access
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
        if (!user.equals(hotel.getOwner())) {
            throw new UnAuthorisedException("This user does not own this hotel with id " + hotelId);        // Prevents unauthorized hotel access
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
                  Acts as a bridge between the Controller and Repository layers.

        Responsibilities :
            - Create hotels
            - Fetch hotel details
            - Update hotel information
            - Delete hotels
            - Activate hotels
            - Validate hotel existence
            - Validate hotel ownership
            - Generate inventory for active hotels

        Methods :

            createNewHotel()
                - Creates a new hotel
                - Assigns current HOTEL_MANAGER as owner
                - Creates hotel in INACTIVE state

            getHotelById()
                - Fetches hotel details
                - Verifies hotel ownership

            validateHotelExists()
                - Checks whether hotel exists
                - Throws exception if hotel is not found

            updateHotelById()
                - Updates hotel details
                - Verifies hotel ownership

            deleteHotelById()
                - Verifies hotel ownership
                - Deletes hotel inventories
                - Deletes hotel record

            activateHotel()
                - Verifies hotel ownership
                - Activates hotel
                - Generates one year inventory
                - Makes hotel available for booking

            getHotelInfoById()
                - Returns hotel information
                - Returns all room details

        Business Use :
            - Allows HOTEL_MANAGER to manage hotels
            - Supports hotel lifecycle management
            - Supports hotel activation workflow
            - Supports inventory generation
            - Prevents unauthorized hotel access
            - Ensures only owners manage hotels

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

        OR

              Hotel Created
                    ↓
             Manage Hotel
                    ↓
             Delete Hotel
                    ↓
          Inventories Deleted
                    ↓
            Hotel Removed

        Hotel Ownership Flow :

            HOTEL_MANAGER Login
                    ↓
              Create Hotel
                    ↓
             Owner Assigned
                    ↓
            Hotel Saved
                    ↓
           Hotel Management
                    ↓
           Ownership Check
                    ↓
          Access Granted / Denied

        Authorization Rules :

            Hotel Owner
                    ↓
            Can View Hotel

            Can Update Hotel

            Can Activate Hotel

            Can Delete Hotel

            Other Users
                    ↓
            Access Denied

        Inventory Generation Flow :

            Hotel Activated
                    ↓
             Fetch Rooms
                    ↓
            initializeRoomForAYear()
                    ↓
            Create Daily Inventory
                    ↓
            Next 365 Days

        Security Features :
            - Hotel ownership validation
            - Unauthorized access prevention
            - Role-based access control
            - Secure hotel management

        Note :
            - Every hotel has exactly one owner.
            - Only owners can manage hotels.
            - Newly created hotels are inactive.
            - Inventory is generated only after activation.
            - Hotel deletion removes associated inventories.
            - Ownership is determined using authenticated user.

        This class acts as the business layer
        for hotel management.
*/