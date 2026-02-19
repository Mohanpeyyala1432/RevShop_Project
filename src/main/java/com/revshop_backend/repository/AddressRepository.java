package com.revshop_backend.repository;

import com.revshop_backend.model.Address;
import com.revshop_backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {

    Address findByUser(User user);

}
