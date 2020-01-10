package com.project.controller;

import com.project.model.City;
import com.project.repository.CityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class CityController {

    @Autowired
    private CityRepository cityRepository;

    @PostMapping("/city")
    public ResponseEntity<Object> createCity(@RequestBody City city){
        City cityFound = cityRepository.findCityByName(city.getName());
        if(cityFound != null) return ResponseEntity.status(HttpStatus.CONFLICT).build();
        return ResponseEntity.ok(cityRepository.save(city));
    }

    @GetMapping("/city/{id}")
    public ResponseEntity<Object> getCity(@PathVariable Long id){
        City city = cityRepository.findCityById(id);
        if(city == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(city);
    }

    @PutMapping("/city")
    public ResponseEntity<Object> updateCity(@RequestBody City city){
        return ResponseEntity.ok(cityRepository.save(city));
    }

    @DeleteMapping("/city/{id}")
    public ResponseEntity<Object> deleteCity(@PathVariable Long id){
        City city = cityRepository.findCityById(id);
        if(city == null) return ResponseEntity.notFound().build();
        cityRepository.delete(city);
        return ResponseEntity.noContent().build();
    }

}
