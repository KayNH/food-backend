package com.zosh.service;

import com.zosh.dto.RestaurantDto;
import com.zosh.model.Address;
import com.zosh.model.Restaurant;
import com.zosh.model.User;
import com.zosh.repository.AddressRepository;
import com.zosh.repository.RestaurantReponsitory;
import com.zosh.repository.UserRepository;
import com.zosh.request.CreateRestaurantRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class RestaurantServiceImp implements  RestaurantService{
    @Autowired
    private RestaurantReponsitory restaurantReponsitory;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private  UserService userService;

    @Autowired
    UserRepository userRepository;

    @Override
    public Restaurant createRestaurant(CreateRestaurantRequest req, User user) {
        Address address= addressRepository.save(req.getAddress());
        Restaurant restaurant = new Restaurant();
        restaurant.setAddress(address);
        restaurant.setContactInformation(req.getContactInformation());
        restaurant.setCuisineType(req.getCuisineType());
        restaurant.setDescriptions(req.getDescriptions());
        restaurant.setImages(req.getImages());
        restaurant.setName(req.getName());
        restaurant.setOpeningHours(req.getOpeningHours());
        restaurant.setRegistrationDate(LocalDateTime.now());
        restaurant.setOwner(user);
        restaurant.setOpen(true);
        restaurant.setNumRating(0);
        return restaurantReponsitory.save(restaurant);
    }

    @Override
    public Restaurant updateRestaurant(Long restaurantId, CreateRestaurantRequest updatedRestaurant) throws Exception {
        Restaurant restaurant = findRestaurantById(restaurantId);

        if(restaurant.getCuisineType()!=null){
            restaurant.setCuisineType(updatedRestaurant.getCuisineType());

        }
        if(restaurant.getDescriptions()!=null){
            restaurant.setDescriptions(updatedRestaurant.getDescriptions());
        }
        if(restaurant.getName()!=null){
            restaurant.setName(updatedRestaurant.getName());

        }
        if(restaurant.getImages()!=null){
            restaurant.setImages(updatedRestaurant.getImages());
        }
        if(restaurant.getContactInformation()!=null){
            restaurant.setContactInformation(updatedRestaurant.getContactInformation());
        }
        if (updatedRestaurant.getAddress() != null) {
            Address currentAddress = restaurant.getAddress();
            Address updatedAddress = updatedRestaurant.getAddress();

            if (currentAddress == null) {
                // Nếu nhà hàng chưa có địa chỉ trước đó
                Address newAddress = addressRepository.save(updatedAddress);
                restaurant.setAddress(newAddress);
            } else {
                // Nếu địa chỉ đã tồn tại → cập nhật từng phần
                if (updatedAddress.getStreet() != null) currentAddress.setStreet(updatedAddress.getStreet());
                if (updatedAddress.getCity() != null) currentAddress.setCity(updatedAddress.getCity());
                if (updatedAddress.getState() != null) currentAddress.setState(updatedAddress.getState());
                if (updatedAddress.getPostalCode() != null) currentAddress.setPostalCode(updatedAddress.getPostalCode());
                if (updatedAddress.getCountry() != null) currentAddress.setCountry(updatedAddress.getCountry());

                addressRepository.save(currentAddress);
            }
        }
        return  restaurantReponsitory.save(restaurant);
    }

    @Override
    public void deteteRestaurant(Long restaurantId) throws Exception {
        Restaurant restaurant = findRestaurantById(restaurantId);
        restaurantReponsitory.delete(restaurant);


    }

    @Override
    public List<Restaurant> getAllRestaurant() {
        return restaurantReponsitory.findAll();

    }

    @Override
    public List<Restaurant> searchRestaurant(String keyword) {
        return restaurantReponsitory.findBySearchQuery(keyword);
    }

    @Override
    public Restaurant findRestaurantById(Long id) throws Exception {
        Optional<Restaurant> opt = restaurantReponsitory.findById(id);
        if(opt.isEmpty()){
            throw  new Exception("restaurant not found with Id" + id);
        }
        return opt.get();
    }

    @Override
    public Restaurant getRestaurantByUserId(Long userId) throws Exception {
        Restaurant restaurant = restaurantReponsitory.findByOwnerId(userId);
        if(restaurant==null){
            throw new Exception("restaurant not found with owner Id"+ userId);
        }
        return restaurant;
    }

    @Override
    public RestaurantDto addToFavorites(Long restaurantId, User user) throws Exception {
        Restaurant restaurant = findRestaurantById(restaurantId);

        RestaurantDto dto = new RestaurantDto();
        dto.setDescriptions(restaurant.getDescriptions());
        dto.setImages(restaurant.getImages());
        dto.setTitle(restaurant.getName());
        dto.setId(restaurantId);

        boolean isFavorited = false;
        List<RestaurantDto> favorites = user.getFavorites();
        for (RestaurantDto favorite : favorites){
            if(favorite.getId().equals(restaurantId)){
                isFavorited=true;
                break;
            }
        }
        if (isFavorited){
            favorites.removeIf(favorite -> favorite.getId().equals(restaurantId) );
        } else {
            favorites.add(dto);
        }
        userRepository.save(user);
        return dto;
    }

    @Override
    public Restaurant updateRestaurantStatus(Long id) throws Exception {
         Restaurant restaurant = findRestaurantById(id);
         restaurant.setOpen(!restaurant.isOpen());
         return restaurantReponsitory.save(restaurant);
    }
}
