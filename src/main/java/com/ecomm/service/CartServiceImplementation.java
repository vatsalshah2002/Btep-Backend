package com.ecomm.service;

import org.springframework.stereotype.Service;

import com.ecomm.exception.ProductException;
import com.ecomm.model.Cart;
import com.ecomm.model.CartItem;
import com.ecomm.model.Product;
import com.ecomm.model.User;
import com.ecomm.repository.CartRepository;
import com.ecomm.request.AddItemRequest;

@Service
public class CartServiceImplementation implements CartService {
    
    private CartRepository cartRepository; 
    private CartItemService cartItemService;
    private ProductService productService;
    
    public CartServiceImplementation(CartRepository cartRepository, CartItemService cartItemService, ProductService productService) {
        this.cartRepository = cartRepository;
        this.productService = productService;
        this.cartItemService = cartItemService;
    }
    
    // Method to create a new cart for a user
    @Override
    public Cart createCart(User user) {
        Cart cart = new Cart();
        cart.setUser(user);
        Cart createdCart = cartRepository.save(cart);
        return createdCart;
    }

    // Method to add a cart item to the user's cart
    @Override
    public CartItem addCartItem(Long userId, AddItemRequest req) throws ProductException {
        // Find the user's cart
        Cart cart = cartRepository.findByUserId(userId);
        // Find the product based on the request
        Product product = productService.findProductById(req.getProductId());
        
        // Check if the cart item already exists
        CartItem isPresent = cartItemService.isCartItemExist(cart, product, req.getSize(), userId);
        
        if (isPresent == null) { // If the cart item does not exist
            // Create a new cart item
            CartItem cartItem = new CartItem();
            cartItem.setProduct(product);
            cartItem.setCart(cart);
            cartItem.setQuantity(req.getQuantity());
            cartItem.setUserId(userId);
            
            // Calculate the price based on quantity and discounted price of the product
            int price = req.getQuantity() * product.getDiscountedPrice();
            cartItem.setPrice(price);
            cartItem.setSize(req.getSize());
            
            // Create the cart item and add it to the cart
            CartItem createdCartItem = cartItemService.createCartItem(cartItem);
            cart.getCartItems().add(createdCartItem);
            
            return createdCartItem;
        }
        
        // If the cart item already exists, return the existing cart item
        return isPresent;
    }

    // Method to find and update the user's cart details
    @Override
    public Cart findUserCart(Long userId) {
        // Find the user's cart
        Cart cart = cartRepository.findByUserId(userId);
        
        int totalPrice = 0;
        int totalDiscountedPrice = 0;
        int totalItem = 0;
        
        // Calculate total price, discounted price, and total items in the cart
        for (CartItem cartsItem : cart.getCartItems()) {
            totalPrice += cartsItem.getPrice();
            totalDiscountedPrice += cartsItem.getDiscountedPrice();
            totalItem += cartsItem.getQuantity();
        }
        
        // Set the calculated values in the cart object
        cart.setTotalPrice(totalPrice);
        cart.setTotalItem(cart.getCartItems().size());
        cart.setTotalDiscountedPrice(totalDiscountedPrice);
        cart.setDiscount(totalPrice - totalDiscountedPrice);
        cart.setTotalItem(totalItem);
        
        // Save and return the updated cart
        return cartRepository.save(cart);
    }

}

