package com.fintrack.dto;

import com.fintrack.entity.TransactionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * DTO pour créer ou modifier une catégorie.
 */
public class CategoryRequest {

    @NotBlank(message = "Le nom de la catégorie est obligatoire")
    @Size(min = 2, max = 50, message = "Le nom doit contenir entre 2 et 50 caractères")
    private String name;

    @Size(max = 255, message = "La description ne doit pas dépasser 255 caractères")
    private String description;

    @NotBlank(message = "L'icône est obligatoire")
    @Size(max = 50)
    private String icon;

    @NotBlank(message = "La couleur est obligatoire")
    @Size(max = 7)
    private String color;

    @NotNull(message = "Le type est obligatoire")
    private TransactionType type;

    // Constructeurs
    public CategoryRequest() {
    }

    public CategoryRequest(String name, String icon, String color, TransactionType type) {
        this.name = name;
        this.icon = icon;
        this.color = color;
        this.type = type;
    }

    // Getters et Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }
}
