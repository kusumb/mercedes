package com.kusum.mercedesbenz.assignment.foodaggregator.service;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class FoodAggregatorService {
	
	@Autowired
	RestTemplate restTemplate;

	public List<Map<String, Object>> getAvailableListFromSupplier(String url) {
		return restTemplate.getForObject(url, List.class);
	}
	
	@Async
	public CompletableFuture<List<Map<String, Object>>> getAvailableListFromSupplierAsync(String url) {
		List<Map<String, Object>> result = restTemplate.getForObject(url, List.class);
		return CompletableFuture.completedFuture(result);
	}
	
	public Map<String, Object> getMapByName(List<Map<String, Object>> availabeItem, String nameKey, String inputName,
			String keyToBeRemoved) {
		Map<String, Object> map = 
				availabeItem.stream()
							.filter(m -> ((String)m.get(nameKey)).equalsIgnoreCase(inputName))
							.findFirst()
							.orElse(null);
		removeIdFromMap(map, keyToBeRemoved);
		return map;
	}

	public Map<String, Object> getMapByNameQty(List<Map<String, Object>> availabeItem, String nameKey, String inputName,
			String qtyKey, int inputQty, String keyToBeRemoved) {
		Map<String, Object> map = 
				availabeItem.stream()
							.filter(m -> ((String)m.get(nameKey)).equalsIgnoreCase(inputName))
							.filter(m -> (int)m.get(qtyKey) > inputQty)
							.findFirst()
							.orElse(null);
		if(map != null) {
			map.put(qtyKey, inputQty);
		}
		removeIdFromMap(map, keyToBeRemoved);
		return map;
	}

	public Map<String, Object> getMapByNameQtyPrice(List<Map<String, Object>> availabeItem, String nameKey, String inputName,
			String qtyKey, int inputQty, String priceKey, String inputPrice, String keyToBeRemoved) {
		Map<String, Object> map = 
				availabeItem.stream()
							.filter(m -> ((String)m.get(nameKey)).equalsIgnoreCase(inputName))
							.filter(m -> (int)m.get(qtyKey) > inputQty)
							.filter(m -> isPriceValid((int)m.get(qtyKey), (String)m.get(priceKey), inputQty, inputPrice))
							.findFirst()
							.orElse(null);
		
		if(map != null) {
			int quantity = (int)map.get(qtyKey);
			String price = (String)map.get(priceKey);
			double supllierPrice = Double.parseDouble(price.substring(1, price.length()));
			double actualPrice = getActualPrice(supllierPrice, quantity, inputQty);
			DecimalFormat df = new DecimalFormat("$0.00");
			map.put(qtyKey, inputQty);
			map.put(priceKey, df.format(actualPrice));
		}
		removeIdFromMap(map, keyToBeRemoved);
		return map;
	}
	
	private boolean isPriceValid(int quantity, String price, int inputQuantity, String inputPrice) {
		double acceptablePrice = Double.parseDouble(inputPrice.substring(1, inputPrice.length()));
		double supllierPrice = Double.parseDouble(price.substring(1, price.length()));
		double actualPrice = (supllierPrice/quantity) * inputQuantity;
		return acceptablePrice > actualPrice;
	}
	
	private double getActualPrice(double supllierPrice, int quantity, int inputQuantity) {
		return (supllierPrice/quantity) * inputQuantity;
	}

	private void removeIdFromMap(Map<String, Object> map, String keyToBeRemoved) {
		if (map != null) {
			map.remove(keyToBeRemoved);
		}
	}
}
