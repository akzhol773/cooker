package org.example.cookercorner.entities;


import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.example.cookercorner.enums.Category;
import org.example.cookercorner.enums.Difficulty;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Recipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String recipeName;

    String description;

    @Enumerated(EnumType.STRING)
    Category category;

    @Enumerated(EnumType.STRING)
    Difficulty difficulty;

    String cookingTime;

    @ElementCollection
    List<Long> likes = new ArrayList<>();

    @ElementCollection
    List<Long> saves = new ArrayList<>();


    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL)
    List<Ingredient> ingredients = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "user_id")
    User createdBy;

    String photoUrl;

    @CreationTimestamp
    Date createdDate;

    @UpdateTimestamp
    Date updatedDate;

}
