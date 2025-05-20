package com.zosh.service;

import com.zosh.model.*;
import com.zosh.repository.AddressRepository;
import com.zosh.repository.OrderRepository;
import com.zosh.repository.OrderitemRepository;
import com.zosh.repository.UserRepository;
import com.zosh.request.OrderRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrderServiceImp implements OrderService{

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderitemRepository orderitemRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private  RestaurantService restaurantService;

    @Autowired
    private  CartService cartService;


    @Override
    public Order createOrder(OrderRequest orderRequest, User user) throws Exception {
        Address shippingAddress;

        // ✅ Trường hợp dùng địa chỉ cũ
        if (orderRequest.getAddressId() != null) {
            shippingAddress = addressRepository.findById(orderRequest.getAddressId())
                    .orElseThrow(() -> new Exception("Không tìm thấy địa chỉ với ID: " + orderRequest.getAddressId()));
        }
        // ✅ Trường hợp tạo địa chỉ mới
        else if (orderRequest.getDeliveryAddress() != null) {
            shippingAddress = addressRepository.save(orderRequest.getDeliveryAddress());

            // ✅ Ép thủ công: nếu địa chỉ chưa tồn tại trong user thì thêm vào
            boolean exists = user.getAddresses().stream().anyMatch(addr ->
                    addr.getStreet().equals(shippingAddress.getStreet()) &&
                            addr.getCity().equals(shippingAddress.getCity()) &&
                            addr.getState().equals(shippingAddress.getState()) &&
                            addr.getPostalCode().equals(shippingAddress.getPostalCode()) &&
                            addr.getCountry().equals(shippingAddress.getCountry())
            );

            if (!exists) {
                user.getAddresses().add(shippingAddress);
                userRepository.save(user);
            }
        } else {
            throw new Exception("Vui lòng cung cấp địa chỉ giao hàng");
        }

        // Lấy thông tin nhà hàng
        Restaurant restaurant = restaurantService.findRestaurantById(orderRequest.getRestaurantId());

        // Tạo đơn hàng
        Order createOrder = new Order();
        createOrder.setCustomer(user);
        createOrder.setRestaurant(restaurant);
        createOrder.setCreatedAt(new Date());
        createOrder.setOrderStatus("PENDING");
        createOrder.setDeliveryAddress(shippingAddress);  // gán địa chỉ giao hàng

        // Lấy giỏ hàng và chuyển thành các OrderItem
        Cart cart = cartService.findCartByUserId(user.getId());
        List<OrderItem> orderItems = new ArrayList<>();

        for (CartItem cartItem : cart.getItem()) {
            OrderItem orderItem = new OrderItem();
            orderItem.setFood(cartItem.getFood());
            orderItem.setIngredients(cartItem.getIngredients());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setTotalPrice(cartItem.getTotalPrice());

            OrderItem savedOrderItem = orderitemRepository.save(orderItem);
            orderItems.add(savedOrderItem);
        }

        Long totalPrice = cartService.calculateCartTotels(cart);
        createOrder.setItems(orderItems);
        createOrder.setToltalPrice(totalPrice);

        Order savedOrder = orderRepository.save(createOrder);
        restaurant.getOrders().add(savedOrder);

        return savedOrder;
    }


    @Override
    public Order updateOrder(Long orderId, String orderStatus) throws Exception {
        Order order = findOderById(orderId);
        if(orderStatus.equals("OUT_FOR_DELIVERY")
                || orderStatus.equals("DELIVERED")
                || orderStatus.equals("COMPLETED")
                || orderStatus.equals("PENDING")){
            order.setOrderStatus(orderStatus);
            return orderRepository.save(order);
        }

        throw  new Exception("Please Select a valid order status");
    }

    @Override
    public void cancelOrder(Long orderId) throws Exception {
        Order order = findOderById(orderId);
        orderRepository.deleteById(orderId);
    }

    @Override
    public List<Order> getUsersOrder(Long userId) throws Exception {
        return  orderRepository.findByCustomerId(userId);
    }

    @Override
    public List<Order> getRestaurantsOrder(Long restaurantId, String orderStatus) throws Exception {
        List<Order> orders = orderRepository.findByRestaurantId(restaurantId);
        if(orderStatus!=null){
            orders= orders.stream().filter(order -> order.getOrderStatus().equals(orderStatus)).collect(Collectors.toList());

        }
        return orders;
    }

    @Override
    public Order findOderById(Long orderId) throws Exception {
        Optional<Order> optionalOrder = orderRepository.findById(orderId);
        if (optionalOrder.isEmpty()){
            throw  new Exception("order not found");
        }
        return optionalOrder.get();
    }
}
