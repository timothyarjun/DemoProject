package com.interview.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.interview.demo.entity.Category;

public interface CategoryRepo extends JpaRepository<Category, Long> {

}
