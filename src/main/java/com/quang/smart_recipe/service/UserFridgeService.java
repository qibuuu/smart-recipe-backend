package com.quang.smart_recipe.service;

import com.quang.smart_recipe.dto.request.FridgeItemRequestDTO;
import com.quang.smart_recipe.dto.response.FridgeItemResponseDTO;
import com.quang.smart_recipe.entity.Ingredient;
import com.quang.smart_recipe.entity.User;
import com.quang.smart_recipe.entity.UserFridge;
import com.quang.smart_recipe.mapper.UserFridgeMapper;
import com.quang.smart_recipe.repository.IngredientRepository;
import com.quang.smart_recipe.repository.UserFridgeRepository;
import com.quang.smart_recipe.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserFridgeService {
    private final UserFridgeRepository userFridgeRepository;
    private final UserRepository userRepository;
    private final IngredientRepository ingredientRepository;
    private final UserFridgeMapper fridgeMapper;

    public FridgeItemResponseDTO addItemToFridge(FridgeItemRequestDTO request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Ingredient ingredient = ingredientRepository.findById(request.getIngredientId())
                .orElseThrow(() -> new RuntimeException("Ingredient not found"));

        UserFridge newItem = new UserFridge();
        newItem.setUser(user);
        newItem.setIngredient(ingredient);
        newItem.setAmount(request.getAmount());
        newItem.setExpiryDate(request.getExpiryDate());

        UserFridge savedItem = userFridgeRepository.save(newItem);

        return fridgeMapper.toResponseDTO(savedItem);
    }

    public List<FridgeItemResponseDTO> getMyFridge(Long userId) {
        return userFridgeRepository.findByUserId(userId)
                .stream()
                .map(fridgeMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    public void deleteItem(Long fridgeItemId) {
        userFridgeRepository.deleteById(fridgeItemId);
    }
}
