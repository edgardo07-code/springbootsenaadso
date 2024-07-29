package com.senaadso.crudinventario.springbootsenaadso.controllers;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import com.senaadso.crudinventario.springbootsenaadso.models.Producto;
import com.senaadso.crudinventario.springbootsenaadso.models.ProductoDto;
import com.senaadso.crudinventario.springbootsenaadso.services.ProductosRepository;

import jakarta.validation.Valid;

@Controller
@RequestMapping("productos")
public class ProductosController {

	@Autowired
	private ProductosRepository repo;

	@GetMapping({ "", "/" })
	public String mostrarListaProductos(Model model) {
		List<Producto> productos = repo.findAll(Sort.by(Sort.Direction.DESC, "id"));
		model.addAttribute("productos", productos);
		return "productos/index";
	}

	@GetMapping("/crear")
	public String showCreatePage(Model model) {
		ProductoDto productoDto = new ProductoDto();
		model.addAttribute("productoDto", productoDto);
		return "productos/crearproducto";
	}

	@PostMapping("/crear")
	public String CrearProducto(@Valid @ModelAttribute ProductoDto productoDto, BindingResult resultado) {
		if (productoDto.getArchivoImagen().isEmpty()) {
			resultado.addError(new FieldError("productoDto", "archivoImagen",
					"La foto del producto es obligatoria, por favor cargue una imagen"));
		}
		if (resultado.hasErrors()) {
			return "productos/crearproducto";
		}

		// Grabar Archivo de Imagen
		MultipartFile image = productoDto.getArchivoImagen();
		// Date fechaCreacion = new java.sql.Date(new java.util.Date().getTime());
		Date fechaCreacion = new Date(System.currentTimeMillis());

		String storageFileName = fechaCreacion.getTime() + "_" + image.getOriginalFilename();

		try {
			String uploadDir = "public/images/";
			Path uploadPath = Paths.get(uploadDir);

			if (!Files.exists(uploadPath)) {
				Files.createDirectories(uploadPath);
			}
			try (InputStream inputStream = image.getInputStream()) {
				Files.copy(inputStream, Paths.get(uploadDir + storageFileName), StandardCopyOption.REPLACE_EXISTING);
			}
		} catch (Exception ex) {
			System.out.println("Ha ocurrido un error al intentar grabar la imagen" + ex.getMessage());
		}

		Producto prod = new Producto();

		prod.setNombre(productoDto.getNombre());
		prod.setMarca(productoDto.getMarca());
		prod.setCategoria(productoDto.getCategoria());
		prod.setPrecio(productoDto.getPrecio());
		prod.setDescripcion(productoDto.getDescripcion());
		prod.setFechaCreacion((java.sql.Date) fechaCreacion);
		prod.setNombreArchivoImagen(storageFileName);

		repo.save(prod);

		return "redirect:/productos";
	}

	@GetMapping("/editar")
	public String showEditPage(Model model, @RequestParam int id) {
		try {
			Producto prod = repo.findById(id).get();
			model.addAttribute("producto", prod);

			ProductoDto productoDto = new ProductoDto();
			productoDto.setNombre(prod.getNombre());
			productoDto.setMarca(prod.getMarca());
			productoDto.setCategoria(prod.getCategoria());
			productoDto.setPrecio(prod.getPrecio());
			productoDto.setDescripcion(prod.getDescripcion());

			model.addAttribute("productoDto", productoDto);

		} catch (Exception ex) {
			System.out.println("Error al editar el producto, intente de nuevo" + ex.getMessage());
			return "/productos";
		}

		return "/productos/EditarProducto";
	}

	@PostMapping("editar")
	public String actualizarProducto(Model model, @RequestParam int id, @Valid @ModelAttribute ProductoDto productoDto,
			BindingResult resultado) {

		try {
			Producto producto = repo.findById(id).get();
			model.addAttribute("producto", producto);

			// Si se producen errores
			if (resultado.hasErrors()) {
				return "productos/EditarProducto";
			}

			if (!productoDto.getArchivoImagen().isEmpty()) {
				// Eliminar imagen antigua
				String dirDeImagenes = "public/images/";
				Path rutaAntiguaImagen = Paths.get(dirDeImagenes + producto.getNombreArchivoImagen());

				try {
					Files.delete(rutaAntiguaImagen);

				} catch (Exception ex) {
					System.out.println("Error: " + ex.getMessage());
				}

				// Grabar el archivo de la nueva imagen
				MultipartFile image = productoDto.getArchivoImagen();
				Date fechaCreacion = new Date(System.currentTimeMillis());
				String storageFileName = fechaCreacion.getTime() + "_" + image.getOriginalFilename();

				try (InputStream inputStream = image.getInputStream()) {
					Files.copy(inputStream, Paths.get(dirDeImagenes + storageFileName),
							StandardCopyOption.REPLACE_EXISTING);
				}
				producto.setNombreArchivoImagen(storageFileName);

			}

			producto.setNombre(productoDto.getNombre());
			producto.setMarca(productoDto.getMarca());
			producto.setCategoria(productoDto.getCategoria());
			producto.setPrecio(productoDto.getPrecio());
			producto.setDescripcion(productoDto.getDescripcion());

			repo.save(producto);

		} catch (Exception ex) {
			System.out.println("Error al editar el producto, intente de nuevo." + ex.getMessage());
		}

		return "redirect:/productos";
	}
	
	@GetMapping("/eliminar")
	public String eliminarProducto(@RequestParam int id) {
		
		try {
			Producto producto = repo.findById(id).get();
			
			// Eliminar imagen del producto
			Path rutaImagen = Paths.get("public/images" + producto.getNombreArchivoImagen());
			
			try {
				Files.delete(rutaImagen);
			} catch (Exception ex) {
				System.out.println("Error al eliminar la imagen del producto." + ex.getMessage());
			}
			
			repo.delete(producto);
			
		} catch (Exception ex) {
			System.out.println("Error al eliminar el producto, intente de nuevo" + ex.getMessage());
		}
		
		return "redirect:/productos";
	}
}
