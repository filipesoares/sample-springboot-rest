package br.com.springboot.rest.controller;

import java.time.LocalDateTime;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import br.com.springboot.rest.model.User;
import br.com.springboot.rest.repository.UserRepository;

@RestController
@RequestMapping(value="/users", produces=MediaType.APPLICATION_JSON_UTF8_VALUE)
public class UserController {

	@Autowired
	UserRepository repository;
	
	@GetMapping
	public ResponseEntity<Page<User>> list(
		@RequestParam(defaultValue="0", required=false) int page, 
		@RequestParam(defaultValue="100", required=false) int size,
		@RequestParam(required=false) String sort) {

		return new ResponseEntity<Page<User>>(repository.findAll(PageRequest.of(page, size, Direction.ASC, sort)), HttpStatus.OK);
		
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<User> fetch(@PathVariable("id") Long id){
		try{
			return new ResponseEntity<User>(repository.getOne(id), HttpStatus.OK);
		} catch (EntityNotFoundException e) {
			return new ResponseEntity<User>(HttpStatus.NOT_FOUND);
		}catch (Exception e) {
			return new ResponseEntity<User>(HttpStatus.BAD_GATEWAY);
		}
	}

	@PostMapping
	@Transactional
	public ResponseEntity<Void> create(@RequestBody User user, UriComponentsBuilder ucBuilder) {
		try {
			user.setCreated(LocalDateTime.now());
			repository.save(user);
			HttpHeaders headers = new HttpHeaders();
			headers.setLocation(ucBuilder.path("/users/{id}").buildAndExpand(user.getId()).toUri());
			return new ResponseEntity<Void>(headers, HttpStatus.CREATED);
		} catch (Exception e) {
			return new ResponseEntity<Void>(HttpStatus.BAD_GATEWAY);
		}
	}

	@PutMapping("/{id}")
	@Transactional
	public ResponseEntity<User> update(@PathVariable("id") Long id, @RequestBody User user, HttpServletRequest request) {
		try {
			repository.getOne(id);
			user.setId(id);
			user.setModified(LocalDateTime.now());
			return new ResponseEntity<User>(repository.save(user), HttpStatus.OK);
		} catch (EntityNotFoundException e) {
			return new ResponseEntity<User>(HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			return new ResponseEntity<User>(user, HttpStatus.BAD_GATEWAY);
		}
	}

	@DeleteMapping("/{id}")
	@Transactional
	public ResponseEntity<User> delete(@PathVariable("id") Long id) {
		try{
			repository.delete(repository.getOne(id));
			return new ResponseEntity<User>(HttpStatus.NO_CONTENT);
		} catch (EntityNotFoundException e) {
			return new ResponseEntity<User>(HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			return new ResponseEntity<User>(HttpStatus.BAD_GATEWAY);
		}
	}
}
