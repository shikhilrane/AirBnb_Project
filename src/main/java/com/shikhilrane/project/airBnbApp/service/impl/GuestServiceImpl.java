package com.shikhilrane.project.airBnbApp.service.impl;

import com.shikhilrane.project.airBnbApp.dto.GuestDto;
import com.shikhilrane.project.airBnbApp.entity.Booking;
import com.shikhilrane.project.airBnbApp.entity.Guest;
import com.shikhilrane.project.airBnbApp.entity.User;
import com.shikhilrane.project.airBnbApp.exception.ResourceNotFoundException;
import com.shikhilrane.project.airBnbApp.exception.UnAuthorisedException;
import com.shikhilrane.project.airBnbApp.repository.BookingRepository;
import com.shikhilrane.project.airBnbApp.repository.GuestRepository;
import com.shikhilrane.project.airBnbApp.service.GuestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.shikhilrane.project.airBnbApp.util.AppUtils.getCurrentUser;

@Service
@RequiredArgsConstructor
@Slf4j
public class GuestServiceImpl implements GuestService {

    private final ModelMapper modelMapper;                // Converts DTOs into entities and vice versa
    private final GuestRepository guestRepository;        // Performs guest database operations
    private final BookingRepository bookingRepository;    // Performs booking database operations

    // Creates a new guest profile for the authenticated user
    @Override
    public GuestDto createNewGuest(GuestDto guestDto) {
        User user = getCurrentUser();                                     // Retrieves currently authenticated user

        Guest guest = modelMapper.map(guestDto, Guest.class);             // Converts DTO into entity
        guest.setUser(user);                                              // Associates guest with current user

        guest = guestRepository.save(guest);                              // Saves guest record

        return modelMapper.map(guest, GuestDto.class);                    // Converts entity into DTO
    }

    // Retrieves all guests associated with the authenticated user
    @Override
    public List<GuestDto> getAllGuests() {
        User user = getCurrentUser();                                     // Retrieves currently authenticated user

        return guestRepository.findByUser(user)
                .stream()
                .map(guest -> modelMapper.map(guest, GuestDto.class))     // Converts guest entities into DTOs
                .collect(Collectors.toList());
    }

    // Retrieves guest details by identifier after ownership validation
    @Override
    public GuestDto getGuestById(Long guestId) {

        Guest guest = guestRepository.findById(guestId)
                .orElseThrow(() -> new ResourceNotFoundException("Guest not found with id: " + guestId)); // Fetches guest or throws exception

        User user = getCurrentUser();                                     // Retrieves currently authenticated user

        if (!user.getId().equals(guest.getUser().getId())) {
            throw new UnAuthorisedException("This user does not own guest with id: " + guestId);
        }

        return modelMapper.map(guest, GuestDto.class);                    // Converts entity into DTO
    }

    // Updates all guest information after ownership validation
    @Override
    @Transactional
    public GuestDto updateGuestById(Long guestId, GuestDto guestDto) {

        Guest guest = guestRepository.findById(guestId)
                .orElseThrow(() -> new ResourceNotFoundException("Guest not found with id: " + guestId)); // Fetches guest or throws exception

        User user = getCurrentUser();                                     // Retrieves currently authenticated user

        if (!user.getId().equals(guest.getUser().getId())) {
            throw new UnAuthorisedException("This user does not own guest with id: " + guestId);
        }

        guest.setName(guestDto.getName());                                // Updates guest name
        guest.setGender(guestDto.getGender());                            // Updates guest gender
        guest.setAge(guestDto.getAge());                                  // Updates guest age

        guest = guestRepository.save(guest);                              // Persists updated guest

        return modelMapper.map(
                guest,
                GuestDto.class
        );                                                                // Converts entity into DTO
    }

    // Partially updates guest fields after ownership validation
    @Override
    public GuestDto partiallyUpdateGuestById(Long guestId, Map<String, Object> updates) {

        Guest guest = guestRepository.findById(guestId)
                .orElseThrow(() -> new ResourceNotFoundException("Guest not found with id: " + guestId)); // Fetches guest or throws exception

        User user = getCurrentUser();                                     // Retrieves currently authenticated user

        if (!user.getId().equals(guest.getUser().getId())) {
            throw new UnAuthorisedException("This user does not own guest with id: " + guestId);
        }

        final Guest guestToUpdate = guest;

        updates.forEach((field, value) -> {
            Field fieldToBeUpdated = ReflectionUtils.findField(Guest.class, field); // Finds target field dynamically

            if (fieldToBeUpdated != null) {
                fieldToBeUpdated.setAccessible(true);                      // Allows access to private field
                ReflectionUtils.setField(fieldToBeUpdated, guestToUpdate, value); // Updates field value dynamically
            }
        });

        guest = guestRepository.save(guestToUpdate);                       // Saves updated guest

        return modelMapper.map(guest, GuestDto.class);                     // Converts entity into DTO
    }

    // Deletes a guest after ownership validation and removes guest from all bookings
    @Override
    @Transactional
    public void deleteGuestById(Long guestId) {

        Guest guest = guestRepository.findById(guestId)
                .orElseThrow(() -> new ResourceNotFoundException("Guest not found with id: " + guestId)); // Fetches guest or throws exception

        User user = getCurrentUser();                                     // Retrieves currently authenticated user

        if (!user.getId().equals(guest.getUser().getId())) {
            throw new UnAuthorisedException("This user does not own guest with id: " + guestId);
        }

        List<Booking> bookings = bookingRepository.findAll();             // Retrieves all bookings

        for (Booking booking : bookings) {
            booking.getGuests().remove(guest);                            // Removes guest from associated bookings
        }

        guestRepository.delete(guest);                                    // Deletes guest record
    }
}

/*
    GuestServiceImpl

        Purpose :
            Handles guest management operations
            for authenticated users.

        Responsibilities :
            - Create guest profiles
            - Retrieve guest information
            - Update guest information
            - Partially update guest information
            - Delete guest profiles
            - Validate guest ownership
            - Manage guest-booking associations

        Methods :

            createNewGuest()
                - Creates guest profile
                - Associates guest with current user

            getAllGuests()
                - Retrieves all guests of current user

            getGuestById()
                - Retrieves guest details
                - Validates ownership

            updateGuestById()
                - Updates guest details
                - Validates ownership

            partiallyUpdateGuestById()
                - Dynamically updates selected fields
                - Validates ownership

            deleteGuestById()
                - Removes guest from bookings
                - Deletes guest record
                - Validates ownership

        Guest Lifecycle :

            Guest Created
                    ↓
                ACTIVE
                    ↓
            Update Details
                    ↓
            Use In Booking
                    ↓
            Delete Guest

        Guest Management Flow :

            User
                ↓
            Create Guest
                ↓
            Guest Saved
                ↓
            Available For Booking

        Partial Update Flow :

            PATCH Request
                    ↓
            Field Validation
                    ↓
            Reflection Update
                    ↓
            Guest Saved

        Guest Deletion Flow :

            Delete Request
                    ↓
            Ownership Validation
                    ↓
            Remove From Bookings
                    ↓
            Delete Guest
                    ↓
            Success

        Security Features :
            - Guest ownership validation
            - Authenticated user access
            - Unauthorized access prevention
            - Transaction-safe updates

        Business Use :
            - Family bookings
            - Group bookings
            - Traveler profile management
            - Guest information management
            - Booking guest association

        Note :
            - Every guest belongs to a user.
            - Users can manage only their own guests.
            - Partial updates use ReflectionUtils.
            - Guest deletion removes booking associations.
            - Business logic is isolated in the service layer.

        This service acts as the central
        guest management component
        of the application.
*/