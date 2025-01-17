package gr.hua.dit.rentalapp.repository;

import gr.hua.dit.rentalapp.entity.Administrator;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdministratorRepository extends JpaRepository<Administrator, Long> {
}
