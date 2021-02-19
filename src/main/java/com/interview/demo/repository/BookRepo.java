package com.interview.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.interview.demo.entity.Booking;

public interface BookRepo extends JpaRepository<Booking, Long> {

}
