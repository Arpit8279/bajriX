package com.bajriX.bajriX.service;

import com.bajriX.bajriX.dto.response.CategoryResponse;
import com.bajriX.bajriX.repository.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findAllByOrderByNameAsc().stream()
                .map(c -> new CategoryResponse(c.getId(), c.getName(), c.getSlug()))
                .toList();
    }
}
