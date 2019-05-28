package com.mitrais.rms.repository;

import com.mitrais.rms.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository <Role,Integer>{
    Role findFirstByName(String name);
}
