package com.example.demo.User;


import com.example.demo.JWT.RoleRepository;
import com.example.demo.model.ERole;
import com.example.demo.model.Role;
import com.example.demo.model.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class UserServices {
    @Autowired
    UserRepository userRepository;
    @Autowired
    RoleRepository roleRepository;
        public Set<Role> getRole(String name){
            User user= userRepository.findByUsername(name);
            Set<Role> roles=user.getRoles();
            return roles;
        }

        public boolean isAdmin(Set<Role> roles){
            boolean ok=false;
                if (roles.contains(roleRepository.findByName(ERole.ROLE_ADMIN) ))
                    ok=true;
            return ok;
        }

        public User getUser(String name){
            return userRepository.findByUsername(name);
        }
}
