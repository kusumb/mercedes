package com.kusum.mercedesbenz.assignment.foodaggregator.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.kusum.mercedesbenz.assignment.foodaggregator.bean.RequestBean;
import com.kusum.mercedesbenz.assignment.foodaggregator.service.FoodAggregatorService;

import static com.kusum.mercedesbenz.assignment.foodaggregator.constant.FoodAggregatorConstant.*;

@RestController
public class FoodAggregatorController {

	private List<Map<String, Object>> fruitInMemoryCache = null;
	private List<Map<String, Object>> vegetableInMemoryCache = null;
	private List<Map<String, Object>> grainInMemoryCache = null;

	@Autowired
	FoodAggregatorService service;

	@PostMapping("/buy-item")
	public ResponseEntity<Object> buyItem(@RequestBody RequestBean request) {
		String itemName = request.getItemName();
		List<Map<String, Object>> availabeItem = service.getAvailableListFromSupplier(FRUITS_API_URL);
		Map<String, Object> requestedItem = service.getMapByName(availabeItem, KEY_NAME, itemName, KEY_ID);
		if (requestedItem == null) {
			availabeItem = service.getAvailableListFromSupplier(VEGETABLES_API_URL);
			requestedItem = service.getMapByName(availabeItem, KEY_PRODUCT_NAME, itemName, KEY_PRODUCT_ID);
		}
		if (requestedItem == null) {
			availabeItem = service.getAvailableListFromSupplier(GRAINS_API_URL);
			requestedItem = service.getMapByName(availabeItem, KEY_ITEM_NAME, itemName, KEY_ITEM_ID);
		}
		return getResponse(requestedItem);
	}

	@PostMapping("/buy-item-qty")
	public ResponseEntity<Object> buyItemWithQuantity(@RequestBody RequestBean request) {
		String itemName = request.getItemName();
		int quantity = request.getQuantity();

		List<Map<String, Object>> availabeItem = service.getAvailableListFromSupplier(FRUITS_API_URL);
		Map<String, Object> requestedItem = service.getMapByNameQty(availabeItem, KEY_NAME, itemName, KEY_QUANTITY, quantity, KEY_ID);

		if (requestedItem == null) {
			availabeItem = service.getAvailableListFromSupplier(VEGETABLES_API_URL);
			requestedItem = service.getMapByNameQty(availabeItem, KEY_PRODUCT_NAME, itemName, KEY_QUANTITY, quantity, KEY_PRODUCT_ID);
		}

		if (requestedItem == null) {
			availabeItem = service.getAvailableListFromSupplier(GRAINS_API_URL);
			requestedItem = service.getMapByNameQty(availabeItem, KEY_ITEM_NAME, itemName, KEY_QUANTITY, quantity, KEY_ITEM_ID);
		}
		return getResponse(requestedItem);
	}

	@PostMapping("/buy-item-qty-price")
	public ResponseEntity<Object> buyItemWithQuantityPrice(@RequestBody RequestBean request) {
		String itemName = request.getItemName();
		int quantity = request.getQuantity();
		String price = request.getPrice();
		
		List<Map<String, Object>> availabeItem = null;

		if (fruitInMemoryCache == null) {
			availabeItem = service.getAvailableListFromSupplier(FRUITS_API_URL);
			fruitInMemoryCache = new ArrayList<>();
			availabeItem.forEach(m -> {
				Map<String, Object> map1 = new HashMap<>(m);
				map1.remove(KEY_ID);
				fruitInMemoryCache.add(map1);
			});
		} else {
			availabeItem = new ArrayList<>();
			for(Map<String, Object> m : fruitInMemoryCache) {
				Map<String, Object> map1 = new HashMap<>(m);
				availabeItem.add(map1);
			}
		}
		Map<String, Object> requestedItem = service.getMapByNameQtyPrice(availabeItem, KEY_NAME, itemName, KEY_QUANTITY,
				quantity, KEY_PRICE, price, KEY_ID);

		if (requestedItem == null) {
			if (vegetableInMemoryCache == null) {
				availabeItem = service.getAvailableListFromSupplier(VEGETABLES_API_URL);
				vegetableInMemoryCache = new ArrayList<>();
				availabeItem.forEach(m -> {	
					Map<String, Object> map1 = new HashMap<>(m);
					map1.remove(KEY_PRODUCT_ID);
					vegetableInMemoryCache.add(map1);
				});
			} else {
				availabeItem = new ArrayList<>();
				for(Map<String, Object> m : vegetableInMemoryCache) {
					Map<String, Object> map1 = new HashMap<>(m);
					availabeItem.add(map1);
				}
			}
			requestedItem = service.getMapByNameQtyPrice(availabeItem, KEY_PRODUCT_NAME, itemName, KEY_QUANTITY, quantity,
					KEY_PRICE, price, KEY_PRODUCT_ID);
		}

		if (requestedItem == null) {
			if (grainInMemoryCache == null) {
				availabeItem = service.getAvailableListFromSupplier(GRAINS_API_URL);
				grainInMemoryCache = new ArrayList<>();
				availabeItem.forEach(m -> {	
					Map<String, Object> map1 = new HashMap<>(m);
					map1.remove(KEY_ITEM_ID);
					grainInMemoryCache.add(map1);
				});
			} else {
				availabeItem = new ArrayList<>();
				for(Map<String, Object> m : grainInMemoryCache) {
					Map<String, Object> map1 = new HashMap<>(m);
					availabeItem.add(map1);
				}
			}
			requestedItem = service.getMapByNameQtyPrice(availabeItem, KEY_ITEM_NAME, itemName, KEY_QUANTITY, quantity, KEY_PRICE,
					price, KEY_ITEM_ID);
		}
		return getResponse(requestedItem);
	}

	@GetMapping("/show-summary")
	public Map<String, Object> showSummary() {

		Map<String, Object> availableItemSummary = new HashMap<>();
		if (fruitInMemoryCache != null) {
			availableItemSummary.put(KEY_FRUITS, fruitInMemoryCache);
		} else {
			availableItemSummary.put(KEY_FRUITS, "No Fruits Found");
		}
		if (vegetableInMemoryCache != null) {
			availableItemSummary.put(KEY_VEGETABLES, vegetableInMemoryCache);
		} else {
			availableItemSummary.put(KEY_VEGETABLES, "No Vegetables Found");
		}
		if (grainInMemoryCache != null) {
			availableItemSummary.put(KEY_GRAINS, grainInMemoryCache);
		} else {
			availableItemSummary.put(KEY_GRAINS, "No Grains Found");
		}
		return availableItemSummary;
	}
	
	@PostMapping("/fast-buy-item")
	public ResponseEntity<Object> fastBuyItem(@RequestBody RequestBean request) throws InterruptedException, ExecutionException {
		String itemName = request.getItemName();
		
		CompletableFuture<List<Map<String, Object>>> availableFruits = service.getAvailableListFromSupplierAsync(FRUITS_API_URL);
		CompletableFuture<List<Map<String, Object>>> availableVegetables = service.getAvailableListFromSupplierAsync(VEGETABLES_API_URL);
		CompletableFuture<List<Map<String, Object>>> availableGrains = service.getAvailableListFromSupplierAsync(GRAINS_API_URL);
		
		CompletableFuture.allOf(availableFruits, availableVegetables, availableGrains).join();
		
		Map<String, Object> requestedItem = service.getMapByName(availableFruits.get(), KEY_NAME, itemName, KEY_ID);
		
		if(requestedItem == null) {
			requestedItem = service.getMapByName(availableVegetables.get(), KEY_PRODUCT_NAME, itemName, KEY_PRODUCT_ID);
		}
		
		if(requestedItem == null) {
			requestedItem = service.getMapByName(availableGrains.get(), KEY_ITEM_NAME, itemName, KEY_ITEM_ID);
		}
		return getResponse(requestedItem);
	}

	private ResponseEntity<Object> getResponse(Map<String, Object> map) {
		if (map != null && !map.isEmpty()) {
			return new ResponseEntity<Object>(map, HttpStatus.OK);
		} else {
			return new ResponseEntity<Object>("NOT_FOUND", HttpStatus.OK);
		}
	}
}
