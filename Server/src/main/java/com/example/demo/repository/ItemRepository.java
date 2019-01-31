package com.example.demo.repository;

import com.example.demo.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item,Integer> {

    Item findItemById(int id);
    Item findTopByOrderByIdDesc();
    Item findTopByNameOrderByIdDesc(String name);

}
