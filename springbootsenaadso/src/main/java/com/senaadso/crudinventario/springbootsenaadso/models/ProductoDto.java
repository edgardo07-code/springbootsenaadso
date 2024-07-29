package com.senaadso.crudinventario.springbootsenaadso.models;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public class ProductoDto {
	@NotEmpty(message = "El Nombre es obligatorio")
	private String nombre;
	
	@NotEmpty(message = "La Marca es obligatoria")
	private String marca;
	
	@NotEmpty(message = "El nombre de la categoría es obligatorio")
	private String categoria;
	
	@Min(0)
	private double precio;
	
	@Size(min=10, message="La descripción debe tener más de 10 caracteres")
	@Size(max=2000, message="La descripción no debe exceder los 2000 caracteres")
	private String descripcion;
	
	private MultipartFile archivoImagen;

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getMarca() {
		return marca;
	}

	public void setMarca(String marca) {
		this.marca = marca;
	}

	public String getCategoria() {
		return categoria;
	}

	public void setCategoria(String categoria) {
		this.categoria = categoria;
	}

	public double getPrecio() {
		return precio;
	}

	public void setPrecio(double precio) {
		this.precio = precio;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public MultipartFile getArchivoImagen() {
		return archivoImagen;
	}

	public void setArchivoImagen(MultipartFile archivoImagen) {
		this.archivoImagen = archivoImagen;
	}
	
	
	
}
