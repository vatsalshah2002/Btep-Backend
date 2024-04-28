package com.ecomm.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecomm.exception.ProductException;
import com.ecomm.exception.UserException;
import com.ecomm.model.Cart;
import com.ecomm.model.CartItem;
import com.ecomm.model.User;
import com.ecomm.request.AddItemRequest;
import com.ecomm.response.ApiResponse;
import com.ecomm.service.CartService;
import com.ecomm.service.UserService;

@RestController
@RequestMapping("/api/cart")
public class CartController {
    
    private CartService cartService;
    private UserService userService;
    
    public CartController(CartService cartService, UserService userService) {
        this.cartService = cartService;
        this.userService = userService;
    }
    
    // Endpoint to find a user's cart
    @GetMapping("/")
    public ResponseEntity<Cart> findUserCartHandler(@RequestHeader("Authorization") String jwt) throws UserException {
        // Find the user profile based on JWT token
        User user = userService.findUserProfileByJwt(jwt);
        
        // Find the user's cart
        Cart cart = cartService.findUserCart(user.getId());
        
        System.out.println("cart - " + cart.getUser().getEmail());
        
        return new ResponseEntity<>(cart, HttpStatus.OK);
    }
    
    // Endpoint to add an item to the cart
    @PutMapping("/add")
    public ResponseEntity<CartItem> addItemToCart(@RequestBody AddItemRequest req, 
            @RequestHeader("Authorization") String jwt) throws UserException, ProductException {
        // Find the user profile based on JWT token
        User user = userService.findUserProfileByJwt(jwt);
        
        // Add the item to the cart
        CartItem item = cartService.addCartItem(user.getId(), req);
        
        // Create an API response 
        ApiResponse res = new ApiResponse("Item Added To Cart Successfully", true);
        res.setMessage("Item added");
        res.setStatus(true);
        
        return new ResponseEntity<>(item, HttpStatus.ACCEPTED);
    }
}
