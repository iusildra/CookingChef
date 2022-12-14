package com.cookingchef.facade;

import com.cookingchef.dao.CartDAO;
import com.cookingchef.factory.PostgresFactory;
import com.cookingchef.model.CartEntry;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

public class CartFacade {
    private static AtomicReference<CartFacade> instance = new AtomicReference<>();
    private final CartDAO cartDAO;

    protected CartFacade() {
        var factory = new PostgresFactory();
        this.cartDAO = factory.getCartDAO();
    }

    public static CartFacade getCartFacade() {
        instance.compareAndSet(null, new CartFacade());
        return instance.get();
    }

    public Optional<CartEntry> addElementIntoCart(CartEntry cartEntry) throws SQLException {
        cartDAO.addElementIntoCartInDb(cartEntry);
        return Optional.of(cartEntry);
    }

    public void deleteCart(CartEntry cartEntry) throws SQLException {
        cartDAO.removeCartFromDb(cartEntry);
    }

    public void updateCart(CartEntry cartEntry) throws SQLException {
        cartDAO.updateCartInDb(cartEntry);
    }

    public Optional<CartEntry> getCartById(int ingredientId, int userId) throws SQLException {
        return cartDAO.getCartById(ingredientId, userId);
    }

    public List<CartEntry> getCartByUser(int userId) throws SQLException {
        return cartDAO.getCartByUser(userId);
    }
}
