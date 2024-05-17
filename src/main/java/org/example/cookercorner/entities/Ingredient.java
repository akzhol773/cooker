package org.example.cookercorner.entities;


import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Ingredient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String name;

    String amount;

    String unitOfMeasurement;

    @ManyToOne
    @JoinColumn(name = "recipe_id")
    Recipe recipe;


}
