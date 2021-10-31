package com.caixeta.gildedrose.service;

import com.caixeta.gildedrose.dto.ItemResponse;
import com.caixeta.gildedrose.repository.ItemRepository;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemService {

    private final ItemRepository itemRepository;
    private final ViewsService viewsService;

    public ItemService(ItemRepository itemRepository, ViewsService viewsService) {
        this.itemRepository = itemRepository;
        this.viewsService = viewsService;
    }

    public List<ItemResponse> getAllItems() {
        var items = itemRepository.findAll();
        return items
                .stream()
                .map(item -> new ItemResponse(item.getId(), item.getName(), item.getDescription(), item.getPrice()))
                .collect(Collectors.toList());
    }

    public ItemResponse getItemById(Long itemId) {
        var item = itemRepository
                .findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("Item not found"));

        var quantityViewsItem = viewsService.insertNewView(item);

        System.out.println("Quantidade de items: " + quantityViewsItem);

        if (quantityViewsItem >= 10) {
            var increaseValue = (item.getPrice() * 10) / 100;
            item.setPrice(item.getPrice() + increaseValue);
            itemRepository.save(item);
            viewsService.deleteAllViewsByItemId(item.getId());
        }
        return new ItemResponse(item.getId(), item.getName(), item.getDescription(), item.getPrice());
    }

}
