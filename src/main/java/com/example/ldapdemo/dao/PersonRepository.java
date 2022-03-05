package com.example.ldapdemo.dao;

import com.example.ldapdemo.domain.HcUser;
import org.springframework.data.repository.CrudRepository;

import javax.naming.Name;

public interface PersonRepository extends CrudRepository<HcUser, Name> {

}