package com.quang.smart_recipe.service;

import com.quang.smart_recipe.dto.request.CartItemRequestDTO;
import com.quang.smart_recipe.entity.Ingredient;
import com.quang.smart_recipe.entity.ShoppingItem;
import com.quang.smart_recipe.exception.AppException;
import com.quang.smart_recipe.exception.ErrorCode;
import com.quang.smart_recipe.repository.IngredientRepository;
import com.quang.smart_recipe.repository.ShoppingItemRepository;
import com.quang.smart_recipe.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ShoppingItemService {

    private final ShoppingItemRepository shoppingItemRepository;
    private final IngredientRepository ingredientRepository;
    private final SecurityUtils securityUtils;

    public List<ShoppingItem> getMyCart() {
        return shoppingItemRepository.findAllByUserId(securityUtils.getCurrentUserId());
    }

    public ShoppingItem addItem(CartItemRequestDTO request) {
        ShoppingItem item = new ShoppingItem();
        item.setUserId(securityUtils.getCurrentUserId());
        item.setAmount(request.getAmount() != null ? request.getAmount() : 1.0f);

        String name = request.getIngredientName().trim();
        String unit = request.getUnit();

        // Nếu tên nguyên liệu có trong từ điển chuẩn → dùng tên và đơn vị chuẩn hóa
        Optional<Ingredient> master = ingredientRepository.findByNameIgnoreCase(name);
        if (master.isPresent()) {
            name = master.get().getName();
            unit = master.get().getUnit();
        } else if (unit == null) {
            unit = "";
        }

        item.setIngredientName(name);
        item.setUnit(unit);
        item.setNote(request.getNote());
        item.setBought(false);

        return shoppingItemRepository.save(item);
    }

    public ShoppingItem toggleBoughtStatus(Long itemId) {
        ShoppingItem item = shoppingItemRepository.findById(itemId)
                .orElseThrow(() -> new AppException(ErrorCode.CART_ITEM_NOT_FOUND));
        item.setBought(!item.isBought());
        return shoppingItemRepository.save(item);
    }

    public void deleteItem(Long itemId) {
        shoppingItemRepository.deleteById(itemId);
    }
}