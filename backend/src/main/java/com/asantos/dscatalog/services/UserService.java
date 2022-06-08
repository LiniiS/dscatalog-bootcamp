package com.asantos.dscatalog.services;

import java.util.Optional;

import javax.persistence.EntityNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.asantos.dscatalog.dto.RoleDTO;
import com.asantos.dscatalog.dto.UserDTO;
import com.asantos.dscatalog.dto.UserInsertDTO;
import com.asantos.dscatalog.dto.UserUpdateDTO;
import com.asantos.dscatalog.entities.Role;
import com.asantos.dscatalog.entities.User;
import com.asantos.dscatalog.repositories.RoleRepository;
import com.asantos.dscatalog.repositories.UserRepository;
import com.asantos.dscatalog.services.exceptions.DataBaseException;
import com.asantos.dscatalog.services.exceptions.ResourceNotFoundException;

@Service
public class UserService implements UserDetailsService{

	//logger
	private static Logger logger = LoggerFactory.getLogger(UserService.class);
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private RoleRepository roleRepository;
	
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	@Transactional(readOnly = true)
	public Page<UserDTO> findAllPaged(Pageable pageable) {
		Page<User> userList = userRepository.findAll(pageable);
		return userList.map(user -> new UserDTO(user));

	}

	@Transactional(readOnly = true)
	public UserDTO findById(Long id) {
		Optional<User> optionalUser = userRepository.findById(id);
		User user = optionalUser.orElseThrow(() -> new ResourceNotFoundException("Resource not found!!!"));
		return new UserDTO(user);
	}

	@Transactional
	public UserDTO insert(UserInsertDTO userDto) {

		User newUser = new User();
		copyDtoToEntity(userDto, newUser);
		//senha precisa ser hasheada
		newUser.setPassword(passwordEncoder.encode(userDto.getPassword()));
		
		newUser = userRepository.save(newUser);

		return new UserDTO(newUser);
	}

	@Transactional
	public UserDTO update(Long id, UserUpdateDTO userUpdateDto) {
		try {
			@SuppressWarnings("deprecation")
			User userUpdated = userRepository.getOne(id);
			copyDtoToEntity(userUpdateDto, userUpdated);
			userUpdated = userRepository.save(userUpdated);
			return new UserDTO(userUpdated);
		} catch (EntityNotFoundException e) {
			throw new ResourceNotFoundException("Id not found: " + id);
		}

	}

	public void delete(Long id) {

		try {
			userRepository.deleteById(id);
		} catch (EmptyResultDataAccessException e) {
			throw new ResourceNotFoundException("Id not found: " + id);
		} catch (DataIntegrityViolationException e) {
			throw new DataBaseException("Attention! Data Base Integrity Violation!");
		}

	}
	
	
	private void copyDtoToEntity(UserDTO userDto, User user) {
		user.setFirstName(userDto.getFirstName());
		user.setLasName(userDto.getLastName());
		user.setEmail(userDto.getEmail());
		
		user.getRoles().clear();
		for(RoleDTO roleDto : userDto.getRoles()) {
			@SuppressWarnings("deprecation")
			Role role = roleRepository.getOne(roleDto.getId());
			user.getRoles().add(role);
		}
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		User user = userRepository.findByEmail(username);
		if(null == user) {
			logger.error("User not found: " + username);
			throw new UsernameNotFoundException("Email not found!");
		}
		logger.info("User found: " + username);
		return user;
	}

}
