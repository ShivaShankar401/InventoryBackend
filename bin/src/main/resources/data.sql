-- Seed demo users (plain passwords for demo only)
INSERT INTO users (id, name, email, password, role) VALUES
  (1, 'Admin User', 'admin@inventory.com', 'admin123', 'ADMIN')
ON DUPLICATE KEY UPDATE name=VALUES(name);

INSERT INTO users (id, name, email, password, role) VALUES
  (2, 'Staff User', 'staff@inventory.com', 'staff123', 'STAFF')
ON DUPLICATE KEY UPDATE name=VALUES(name);

-- Suppliers
INSERT INTO suppliers (id, name, contactInfo, email, address) VALUES
  (1, 'Acme Supplies', 'John Doe, +1-202-555-0134', 'contact@acme.com', '123 Market St, City'),
  (2, 'Global Traders', 'Jane Smith, +1-202-555-0188', 'sales@globaltraders.com', '45 Commerce Ave, City')
ON DUPLICATE KEY UPDATE name=VALUES(name);

-- Products
INSERT INTO products (id, name, description, category, supplier_id, quantity, price, warehouseLocation, reorderLevel) VALUES
  (1, 'Wireless Mouse', 'Ergonomic 2.4G mouse', 'Electronics', 1, 120, 12.99, 'A1', 30),
  (2, 'Keyboard', 'Mechanical keyboard', 'Electronics', 1, 40, 39.99, 'A1', 25),
  (3, 'Yoga Mat', 'Non-slip fitness mat', 'Sports', 2, 15, 19.99, 'B2', 20),
  (4, 'LED Bulb', '9W warm white', 'Home & Garden', 2, 200, 2.49, 'C3', 50)
ON DUPLICATE KEY UPDATE name=VALUES(name);

-- Inventory logs (optional)
INSERT INTO inventory_logs (id, product_id, type, quantity, notes, createdAt) VALUES
  (1, 1, 'IN', 50, 'Initial stock', NOW()),
  (2, 2, 'IN', 20, 'Initial stock', NOW()),
  (3, 3, 'IN', 10, 'Initial stock', NOW()),
  (4, 4, 'IN', 100, 'Initial stock', NOW())
ON DUPLICATE KEY UPDATE notes=VALUES(notes);
