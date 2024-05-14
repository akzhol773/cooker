package org.example.cookercorner.services;


import org.example.cookercorner.entities.Role;
import org.springframework.stereotype.Service;

import java.util.Optional;

public interface RoleService {
   Optional <Role> getUserRole();
}
