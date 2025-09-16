package com.programming.inventory_service;

import com.programming.inventory_service.model.Inventory;
import com.programming.inventory_service.repo.InventoryRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class InventoryServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(InventoryServiceApplication.class, args);
	}

	@Bean
	public CommandLineRunner loadData(InventoryRepository inventoryRepository) {

		return args -> {
			Inventory inventory = new Inventory();
			inventory.setSkuCode("SKU_01");
			inventory.setQuantity(100);

			Inventory inventory1 = new Inventory();
			inventory1.setSkuCode("SKU_02");
			inventory1.setQuantity(0);

			if (inventoryRepository.findAll().isEmpty()) {
				inventoryRepository.save(inventory);
				inventoryRepository.save(inventory1);
			}
		};
	}
}
