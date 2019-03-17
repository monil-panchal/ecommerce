package com.ecommerce.util;

import java.util.Comparator;

import org.springframework.stereotype.Service;

import com.ecommerce.db.model.Inventory;

@Service
public class InventorySortComparator {

	public Comparator<Inventory.Supplier> sortByPriceAndQuantity() {
		Comparator<Inventory.Supplier> sortByPrice = Comparator.comparing(Inventory.Supplier::getPrice);

		Comparator<Inventory.Supplier> sortByQuantity = Comparator.comparing(Inventory.Supplier::getQuantity)
				.reversed();

		Comparator<Inventory.Supplier> sortInventoryByPriceAndQuantity = sortByPrice.thenComparing(sortByQuantity);
		return sortInventoryByPriceAndQuantity;

	}

}
