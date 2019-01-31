package com.example.demo.service;


import com.example.demo.model.Item;
import com.example.demo.repository.ItemRepository;
import org.springframework.stereotype.Service;


@Service
public class ItemServiceImpl {
    private ItemRepository itemRepository;



    public ItemServiceImpl(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    public Iterable<Item> list() {
        return itemRepository.findAll();
    }

    public void save(Item items) {
        itemRepository.save(items);
    }

    public Long exists(){
        return itemRepository.count();
    }

    public Item getLastByName(String name){
        return itemRepository.findTopByNameOrderByIdDesc(name);
    }

    public Iterable<Item> saveAll(Iterable<Item> items) { return itemRepository.saveAll(items); }
}
