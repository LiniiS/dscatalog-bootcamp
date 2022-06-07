package com.asantos.dscatalog.resources;

import java.net.URI;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.asantos.dscatalog.dto.UserDTO;
import com.asantos.dscatalog.dto.UserInsertDTO;
import com.asantos.dscatalog.services.UserService;

@RestController
@RequestMapping(value = "/users")
public class UserResource {

	@Autowired
	private UserService userService;

	@GetMapping()
	public ResponseEntity<Page<UserDTO>> findAllUsers(Pageable pageable) {
		Page<UserDTO> userDtoList = userService.findAllPaged(pageable);
		return ResponseEntity.ok().body(userDtoList);
	}

	@GetMapping(value = "/{id}")
	public ResponseEntity<UserDTO> findUserById(@PathVariable Long id) {
		UserDTO userDto = userService.findById(id);
		return ResponseEntity.ok().body(userDto);
	}

	@PostMapping()
	public ResponseEntity<UserDTO> addNewUser(@Valid @RequestBody UserInsertDTO userInsertDto) {
		// insertDTO devolve um dto que é armazenado em userDTO
		UserDTO userDto = userService.insert(userInsertDto);

		URI newResourceUri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
				.buildAndExpand(userDto.getId()).toUri();
		return ResponseEntity.created(newResourceUri).body(userDto);
	}

	@PutMapping(value = "/{id}")
	public ResponseEntity<UserDTO> updateUserInfo(@PathVariable Long id, @Valid @RequestBody UserDTO userDto) {
		userDto = userService.update(id, userDto);
		return ResponseEntity.ok().body(userDto);
	}

	@DeleteMapping(value = "/{id}")
	public ResponseEntity<UserDTO> deleteCategory(@PathVariable Long id) {
		userService.delete(id);
		return ResponseEntity.noContent().build();
	}

}
