package com.senaadso.crudinventario.springbootsenaadso.services;

import org.springframework.data.jpa.repository.JpaRepository;
import com.senaadso.crudinventario.springbootsenaadso.models.Producto;

public interface ProductosRepository extends JpaRepository<Producto, Integer> {

}
