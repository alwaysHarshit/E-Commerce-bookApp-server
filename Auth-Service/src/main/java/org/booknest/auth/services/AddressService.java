package org.booknest.auth.services;

import org.booknest.auth.dto.AddressRequestDTO;
import org.booknest.auth.entity.AddressEntity;
import org.booknest.auth.entity.UserEntity;
import org.booknest.auth.exception.*;
import org.booknest.auth.exception.InvalidUser;
import org.booknest.auth.repo.AddressRepo;
import org.booknest.auth.repo.UserRepo;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import java.util.List;


@Service
public class AddressService {

    private final AddressRepo addressRepo;
    private final UserRepo userRepo;

    public AddressService(AddressRepo addressRepo, UserRepo userRepo) {
        this.addressRepo = addressRepo;
        this.userRepo = userRepo;
    }

    public List<AddressEntity> getAllAddresses() {
        return addressRepo.findAll();
    }

    public AddressEntity getAddressById(Long addressId) {
        return addressRepo
                .findById(addressId)
                .orElseThrow(() -> new AddressNotFound("Address is not found"));
    }

    public AddressEntity addAddress(AddressRequestDTO request, UserDetails userDetails) {

        //get user from db
        UserEntity user = userRepo.findById(Long.parseLong(userDetails.getUsername())).orElseThrow(() -> new UserNotFoundException("User is not found"));
        AddressEntity newAddress = AddressEntity.builder()
                .addressLine1(request.getAddressLine1())
                .addressLine2(request.getAddressLine2())
                .landmark(request.getLandmark())
                .city(request.getCity())
                .state(request.getState())
                .country(request.getCountry())
                .postalCode(request.getPostalCode())
                .isDefault(true)
                .user(user)
                .build();
        return addressRepo.save(newAddress);
    }

    public AddressEntity updateAddress(Long addressId, AddressRequestDTO request, UserDetails userDetails) {

        AddressEntity dbAddress = addressRepo
                .findById(addressId)
                .orElseThrow(() ->
                        new AddressNotFound("Address not found")
                );

        //verify address belongs to authenticated user.
        if(!dbAddress.getUser().getId().equals(Long.parseLong(userDetails.getUsername()))){
            throw  new InvalidUser("Invalid user");
        }

        if (request.getAddressLine1() != null) {
            dbAddress.setAddressLine1(request.getAddressLine1());
        }

        if (request.getAddressLine2() != null) {
            dbAddress.setAddressLine2(request.getAddressLine2());
        }

        if (request.getLandmark() != null) {
            dbAddress.setLandmark(request.getLandmark());
        }

        if (request.getCity() != null) {
            dbAddress.setCity(request.getCity());
        }

        if (request.getState() != null) {
            dbAddress.setState(request.getState());
        }

        if (request.getCountry() != null) {
            dbAddress.setCountry(request.getCountry());
        }

        if (request.getPostalCode() != null) {
            dbAddress.setPostalCode(request.getPostalCode());
        }

        return addressRepo.save(dbAddress);
    }

    public void deleteAddress(Long addressId, UserDetails userDetails) {

        AddressEntity dbAddress = addressRepo
                .findById(addressId)
                .orElseThrow(() ->
                        new AddressNotFound("Address not found")
                );

        //verify address belongs to authenticated user.
        if(!dbAddress.getUser().getId().equals(Long.parseLong(userDetails.getUsername()))){
            throw  new InvalidUser("Invalid user");
        }
        addressRepo.delete(dbAddress);
    }
}
