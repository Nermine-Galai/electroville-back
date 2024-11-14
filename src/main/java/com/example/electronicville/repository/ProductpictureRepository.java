package com.example.electronicville.repository;

import com.example.electronicville.models.Productpicture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductpictureRepository extends JpaRepository<Productpicture, Integer> {

}

