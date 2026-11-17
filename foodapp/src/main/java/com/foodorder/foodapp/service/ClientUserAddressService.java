package com.foodorder.foodapp.service;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;

import lombok.AllArgsConstructor;

import com.foodorder.foodapp.dto.user_address.CreateUserAddressDTO;
import com.foodorder.foodapp.dto.user_address.DetailUserAddressDTO;
import com.foodorder.foodapp.exception.BadRequestException;
import com.foodorder.foodapp.model.User;
import com.foodorder.foodapp.model.UserAddress;
import com.foodorder.foodapp.repository.UserAddressRepository;

@Service
@AllArgsConstructor
public class ClientUserAddressService {
  // The address list is returned in full (order form needs all of them), so the number per user is capped
  public static final int MAX_ADDRESSES_PER_USER = 10;

  private final ModelMapper modelMapper;
  private final UserAddressRepository userAddressRepository;

  public List<DetailUserAddressDTO> getAllUserAddress(User currentUser) {
    List<UserAddress> userAddresses = userAddressRepository.findByUserId(currentUser.getId());

    return userAddresses.stream()
        .map(userAddress -> modelMapper.map(userAddress, DetailUserAddressDTO.class))
        .collect(Collectors.toList());
  }

  public DetailUserAddressDTO createUserAddress(User currentUser, CreateUserAddressDTO createUserAddressDTO) {
    if (userAddressRepository.countByUserId(currentUser.getId()) >= MAX_ADDRESSES_PER_USER) {
      throw new BadRequestException("user_address.limit.exceeded", MAX_ADDRESSES_PER_USER);
    }

    UserAddress userAddress = modelMapper.map(createUserAddressDTO, UserAddress.class);
    userAddress.setUser(currentUser);

    UserAddress savedUserAddress = userAddressRepository.save(userAddress);

    return modelMapper.map(savedUserAddress, DetailUserAddressDTO.class);
  }

  public DetailUserAddressDTO updateUserAddress(User currentUser, Long id, CreateUserAddressDTO createUserAddressDTO) {
    UserAddress existingAddress = userAddressRepository.findByUserIdAndId(currentUser.getId(), id)
        .orElseThrow(() -> new RuntimeException("user_address.not.found"));

    existingAddress.setUser(currentUser);
    existingAddress.setReceiverName(createUserAddressDTO.getReceiverName());
    existingAddress.setPhoneNumber(createUserAddressDTO.getPhoneNumber());
    existingAddress.setCity(createUserAddressDTO.getCity());
    existingAddress.setDistrict(createUserAddressDTO.getDistrict());
    existingAddress.setStreetAddress(createUserAddressDTO.getStreetAddress());

    existingAddress = userAddressRepository.save(existingAddress);

    return modelMapper.map(existingAddress, DetailUserAddressDTO.class);
  }

  public void deleteUserAddress(User currentUser, Long id) {
    UserAddress userAddress = userAddressRepository.findByUserIdAndId(currentUser.getId(), id)
        .orElseThrow(() -> new RuntimeException("user_address.not.found"));

    userAddressRepository.delete(userAddress);
  }
}
