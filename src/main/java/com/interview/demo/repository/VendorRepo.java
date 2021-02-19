package com.interview.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.interview.demo.entity.Vendor;

public interface VendorRepo extends JpaRepository<Vendor, Long> {

}
