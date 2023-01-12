package com.example.demo.groups;

import com.example.demo.model.FileDocument;
import com.example.demo.model.Groups;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupRepository extends JpaRepository<Groups,Long> {

}
