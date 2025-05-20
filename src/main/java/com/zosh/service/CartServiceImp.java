package com.zosh.service;

import com.zosh.model.Cart;
import com.zosh.model.CartItem;
import com.zosh.model.Food;
import com.zosh.model.User;
import com.zosh.repository.CartItemRepository;
import com.zosh.repository.CartReponsitory;
import com.zosh.repository.FoodResponsitory;
import com.zosh.request.AddCartItemRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CartServiceImp implements  CartService{

    @Autowired
    private CartReponsitory cartReponsitory;

    @Autowired
    private UserService userService;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private FoodService foodService;

    @Override
    public CartItem addItemToCart(AddCartItemRequest req, String jwt) throws Exception {
        User user = userService.findUserByJwtToken(jwt);

        Food food= foodService.findFoodById(req.getFoodId());

        Cart cart= cartReponsitory.findByCustomerId(user.getId());

        for(CartItem cartItem: cart.getItem()){
            if(cartItem.getFood().equals(food)){
                int newQuantity= cartItem.getQuantity()+req.getQuantity();
                return  updateCartItemQuantity(cartItem.getId(),newQuantity);
            }
        }
        CartItem newCartItem = new CartItem();
        newCartItem.setFood(food);
        newCartItem.setCart(cart);
        newCartItem.setQuantity(req.getQuantity());
        newCartItem.setIngredients(req.getIngredients());
        newCartItem.setTotalPrice(req.getQuantity()*food.getPrice());

        CartItem savedCartItem = cartItemRepository.save(newCartItem);

        cart.getItem().add(savedCartItem);

        return savedCartItem;
    }

    @Override
    public CartItem updateCartItemQuantity(Long cartItemId, int quantity) throws Exception {
        Optional<CartItem> cartItemOptional= cartItemRepository.findById(cartItemId);
        if(cartItemOptional.isEmpty()){
            throw  new Exception("cart item not fount");
        }
        CartItem item = cartItemOptional.get();
        item.setQuantity(quantity);
        item.setTotalPrice(item.getFood().getPrice()*quantity);

        return cartItemRepository.save(item);
    }

    @Override
    public Cart removeItemFromCart(Long cartItemId, String jwt) throws Exception {

        User user = userService.findUserByJwtToken(jwt);

        Cart cart= cartReponsitory.findByCustomerId(user.getId());

        Optional<CartItem> cartItemOptional= cartItemRepository.findById(cartItemId);
        if(cartItemOptional.isEmpty()){
            throw  new Exception("cart item not fount");
        }
        CartItem item = cartItemOptional.get();
        cart.getItem().remove(item);
        return cartReponsitory.save(cart);
    }

    @Override
    public Long calculateCartTotels(Cart cart) throws Exception {
        Long total= 0L;
        for(CartItem cartItem: cart.getItem()){
            total+=cartItem.getFood().getPrice()*cartItem.getQuantity();
        }
        return total;
    }

    @Override
    public Cart findCartById(Long id) throws Exception {
        Optional<Cart> optionalCart = cartReponsitory.findById(id);
        if(optionalCart.isEmpty()){
            throw new Exception("cart not found with id"+ id);
        }
        return optionalCart.get();
    }

    @Override
    public Cart findCartByUserId(Long userId) throws Exception {
       // User user= userService.findUserByJwtToken(jwt);

        Cart cart= cartReponsitory.findByCustomerId(userId);
        cart.setTotal(calculateCartTotels(cart));
        return cart;
    }

    @Override
    public Cart clearCart(Long userId) throws Exception {
       // User user= userService.findUserByJwtToken(jwt);
        Cart cart  = findCartByUserId(userId);
        cart.getItem().clear();
        return cartReponsitory.save(cart);
    }
}
