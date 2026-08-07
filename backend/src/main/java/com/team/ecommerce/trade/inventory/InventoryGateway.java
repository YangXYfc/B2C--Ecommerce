package com.team.ecommerce.trade.inventory;

import java.util.List;

public interface InventoryGateway {

    void check(List<InventoryItem> items);

    void deduct(List<InventoryItem> items);

    void restore(List<InventoryItem> items);
}
