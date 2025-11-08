package com.fintrack.service;

import com.fintrack.dto.CategoryRequest;
import com.fintrack.dto.CategoryResponse;
import com.fintrack.entity.Category;
import com.fintrack.entity.TransactionType;
import com.fintrack.entity.User;
import com.fintrack.exception.ResourceNotFoundException;
import com.fintrack.exception.UnauthorizedException;
import com.fintrack.repository.CategoryRepository;
import com.fintrack.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserRepository userRepository;

    public List<CategoryResponse> getAllCategories(Authentication authentication) {
        User user = getCurrentUser(authentication);
        return categoryRepository.findByUserId(user.getId())
                .stream()
                .map(CategoryResponse::new)
                .collect(Collectors.toList());
    }

    public List<CategoryResponse> getCategoriesByType(TransactionType type, Authentication authentication) {
        User user = getCurrentUser(authentication);
        return categoryRepository.findByUserIdAndType(user.getId(), type)
                .stream()
                .map(CategoryResponse::new)
                .collect(Collectors.toList());
    }

    public CategoryResponse getCategoryById(Long id, Authentication authentication) {
        User user = getCurrentUser(authentication);
        Category category = categoryRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Catégorie non trouvée avec l'ID: " + id));
        return new CategoryResponse(category);
    }

    public CategoryResponse createCategory(CategoryRequest request, Authentication authentication) {
        User user = getCurrentUser(authentication);
        
        if (categoryRepository.existsByNameAndUserId(request.getName(), user.getId())) {
            throw new RuntimeException("Une catégorie avec ce nom existe déjà");
        }

        Category category = new Category();
        category.setName(request.getName());
        category.setDescription(request.getDescription());
        category.setIcon(request.getIcon());
        category.setColor(request.getColor());
        category.setType(request.getType());
        category.setUser(user);
        category.setIsDefault(false);

        Category savedCategory = categoryRepository.save(category);
        return new CategoryResponse(savedCategory);
    }

    public CategoryResponse updateCategory(Long id, CategoryRequest request, Authentication authentication) {
        User user = getCurrentUser(authentication);
        Category category = categoryRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Catégorie non trouvée avec l'ID: " + id));

        if (category.getIsDefault()) {
            throw new RuntimeException("Les catégories par défaut ne peuvent pas être modifiées");
        }

        category.setName(request.getName());
        category.setDescription(request.getDescription());
        category.setIcon(request.getIcon());
        category.setColor(request.getColor());
        category.setType(request.getType());

        Category updatedCategory = categoryRepository.save(category);
        return new CategoryResponse(updatedCategory);
    }

    public void deleteCategory(Long id, Authentication authentication) {
        User user = getCurrentUser(authentication);
        Category category = categoryRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Catégorie non trouvée avec l'ID: " + id));

        if (category.getIsDefault()) {
            throw new RuntimeException("Les catégories par défaut ne peuvent pas être supprimées");
        }

        categoryRepository.delete(category);
    }

    private User getCurrentUser(Authentication authentication) {
        String username = authentication.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UnauthorizedException("Utilisateur non trouvé"));
    }
}
