-- ============================================================================
-- LakshanMart Seed Data (MySQL & H2 MySQL Mode Compatible)
-- Users, Categories, 20 Realistic Sample Products, Sample Orders & Reviews
-- ============================================================================

-- 1. Seed Users (Passwords: Admin@123 hashed via BCrypt cost 10)
-- Hash: $2a$10$wT0l/e40VvN5s6c6gW34yeqjQd5Q5rN3J4lWkK8N1h7V6HwYvQ4xW
INSERT INTO users (id, name, email, password_hash, role) VALUES
(1, 'Administrator', 'admin@lakshanmart.com', '$2a$10$wT0l/e40VvN5s6c6gW34yeqjQd5Q5rN3J4lWkK8N1h7V6HwYvQ4xW', 'ADMIN'),
(2, 'Bob Buyer', 'bob@lakshanmart.com', '$2a$10$wT0l/e40VvN5s6c6gW34yeqjQd5Q5rN3J4lWkK8N1h7V6HwYvQ4xW', 'BUYER'),
(3, 'Alice Merchant', 'alice@lakshanmart.com', '$2a$10$wT0l/e40VvN5s6c6gW34yeqjQd5Q5rN3J4lWkK8N1h7V6HwYvQ4xW', 'SELLER');

-- 2. Seed Categories
INSERT INTO categories (id, name, description, icon) VALUES
(1, 'Electronics', 'Laptops, noise-cancelling headphones, 4K monitors, and wireless accessories', '💻'),
(2, 'Fashion', 'Men and women apparel, designer footwear, watches, and outerwear', '👔'),
(3, 'Home & Kitchen', 'Espresso machines, smart cookers, electric toothbrushes, and appliances', '🍳'),
(4, 'Books', 'Software engineering, algorithms, system design, and personal development', '📚'),
(5, 'Sports & Fitness', 'Adjustable gym weights, yoga mats, fitness trackers, and workout gear', '🏋️');

-- 3. Seed 20 Realistic Products across all 5 Categories
-- Category 1: Electronics
INSERT INTO products (id, seller_id, category_id, name, description, price, stock_qty, category, image_url, rating) VALUES
(1, 3, 1, 'Sony WH-1000XM5 Wireless Noise-Cancelling Headphones', 'Industry-leading noise cancellation with two processors and 8 microphones. Up to 30-hour battery life with quick charging. Crystal clear hands-free calling with 4 beamforming microphones.', 349.99, 25, 'Electronics', 'https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=800&auto=format&fit=crop&q=80', 4.85),
(2, 3, 1, 'Apple MacBook Pro 14-inch M3 Chip (16GB RAM, 512GB SSD)', 'Supercharged by M3 chip featuring an 8-core CPU and 10-core GPU. Liquid Retina XDR display with 1000 nits sustained brightness. Up to 22 hours of battery life on a single charge.', 1599.00, 12, 'Electronics', 'https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=800&auto=format&fit=crop&q=80', 4.90),
(3, 3, 1, 'Dell UltraSharp 27-inch 4K UHD USB-C Hub Monitor (U2723QE)', 'IPS Black technology with 2000:1 contrast ratio and 98% DCI-P3 color gamut. Extensive connectivity including 90W USB-C power delivery, RJ45 Ethernet, and DisplayPort 1.4.', 489.50, 18, 'Electronics', 'https://images.unsplash.com/photo-1527443224154-c4a3942d3acf?w=800&auto=format&fit=crop&q=80', 4.70),
(4, 3, 1, 'Keychron K2 Wireless Mechanical Keyboard (RGB Backlit)', 'Compact 75% layout Bluetooth mechanical keyboard with Gateron G Pro Brown switches. Mac and Windows compatible with dedicated multimedia keys and hot-swappable switch design.', 89.99, 35, 'Electronics', 'https://images.unsplash.com/photo-1587829741301-dc798b83add3?w=800&auto=format&fit=crop&q=80', 4.65),
(5, 3, 1, 'Logitech MX Master 3S Advanced Wireless Performance Mouse', 'Quiet clicks and 8K DPI any-surface tracking. MagSpeed electromagnetic scrolling delivers 1,000 lines per second. Ergonomic hand-sculpted design with easy USB-C quick recharging.', 99.99, 40, 'Electronics', 'https://images.unsplash.com/photo-1615663245857-ac93bb7c39e7?w=800&auto=format&fit=crop&q=80', 4.80);

-- Category 2: Fashion
INSERT INTO products (id, seller_id, category_id, name, description, price, stock_qty, category, image_url, rating) VALUES
(6, 3, 2, 'Levi''s Men''s 511 Slim Fit Stretch Denim Jeans', 'A modern slim with room to move. Added stretch for all-day comfort. Woven with authentic cotton twill and finished in classic Indigo Rinse.', 59.50, 50, 'Fashion', 'https://images.unsplash.com/photo-1542272604-780c96856592?w=800&auto=format&fit=crop&q=80', 4.50),
(7, 3, 2, 'Nike Air Max 270 Men''s Running Shoes (Triple Black)', 'Features Nike''s biggest heel Air unit yet for a super-soft ride. Knit upper delivers lightweight support and breathability with dual-density foam sole cushioning.', 149.99, 30, 'Fashion', 'https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=800&auto=format&fit=crop&q=80', 4.75),
(8, 3, 2, 'Fossil Men''s Grant Stainless Steel Chronograph Watch', 'Vintage-inspired timepiece with Roman numeral indices, quartz chronograph movement, and genuine dark brown leather strap. Water resistant to 50 meters.', 119.00, 22, 'Fashion', 'https://images.unsplash.com/photo-1524805444758-089113d48a6d?w=800&auto=format&fit=crop&q=80', 4.60),
(9, 3, 2, 'Ray-Ban Classic Aviator Sunglasses (Gold Frame / Green Lens)', 'Timeless model originally designed for U.S. aviators in 1937. Crystal green polarized lenses offer 100% UV protection and exceptional visual clarity.', 163.00, 15, 'Fashion', 'https://images.unsplash.com/photo-1511499767150-a48a237f0083?w=800&auto=format&fit=crop&q=80', 4.80),
(10, 3, 2, 'The North Face Resolve 2 Waterproof Windproof Jacket', 'DryVent 2L breathable, waterproof, seam-sealed shell with mesh lining. Adjustable stowable hood and elastic cuffs ensure complete weather protection.', 99.00, 28, 'Fashion', 'https://images.unsplash.com/photo-1544441893-675973e31985?w=800&auto=format&fit=crop&q=80', 4.65);

-- Category 3: Home & Kitchen
INSERT INTO products (id, seller_id, category_id, name, description, price, stock_qty, category, image_url, rating) VALUES
(11, 3, 3, 'Nespresso VertuoPlus Coffee and Espresso Machine by De''Longhi', 'Single-serve coffee maker brewing authentic espresso and barista-style cups with Centrifusion technology. 40 oz water reservoir and motorized brew head.', 169.00, 20, 'Home & Kitchen', 'https://images.unsplash.com/photo-1517668808822-9ebb02f2a0e6?w=800&auto=format&fit=crop&q=80', 4.70),
(12, 3, 3, 'Instant Pot Duo Plus 9-in-1 Electric Pressure Cooker (6 Qt)', 'Replaces 9 appliances: pressure cooker, slow cooker, rice cooker, steamer, sauté pan, yogurt maker, and sterilizer. Easy-one-touch cooking with 15 smart programs.', 129.95, 35, 'Home & Kitchen', 'https://images.unsplash.com/photo-1544816155-12df9643f363?w=800&auto=format&fit=crop&q=80', 4.80),
(13, 3, 3, 'Philips Sonicare ProtectiveClean 6100 Electric Toothbrush', 'Improves gum health up to 100% compared to manual brushes. Pressure sensor protects teeth and gums with 3 intensities and 3 modes: Clean, White, and Gum Care.', 119.99, 45, 'Home & Kitchen', 'https://images.unsplash.com/photo-1559591937-e109d73d6e33?w=800&auto=format&fit=crop&q=80', 4.70),
(14, 3, 3, 'Dyson V8 Cordless Vacuum Cleaner with HEPA Filtration', 'Powerful digital motor engineered for homes with pets. Up to 40 minutes of run time with de-tangling Motorbar cleaner head and advanced whole-machine filtration.', 399.99, 14, 'Home & Kitchen', 'https://images.unsplash.com/photo-1558317374-067fb5f30001?w=800&auto=format&fit=crop&q=80', 4.85);

-- Category 4: Books
INSERT INTO products (id, seller_id, category_id, name, description, price, stock_qty, category, image_url, rating) VALUES
(15, 3, 4, 'Designing Data-Intensive Applications by Martin Kleppmann', 'The definitive guide to the architecture of reliable, scalable, and maintainable data systems. Deep-dives into storage engines, distributed transactions, and stream processing.', 42.99, 60, 'Books', 'https://images.unsplash.com/photo-1532012164546-f432f2e3777a?w=800&auto=format&fit=crop&q=80', 4.95),
(16, 3, 4, 'Clean Code: A Handbook of Agile Software Craftsmanship', 'Legendary software engineer Robert C. Martin provides timeless principles, patterns, and practices for writing robust, maintainable, and readable code.', 38.50, 55, 'Books', 'https://images.unsplash.com/photo-1544716278-ca5e3f4abd8c?w=800&auto=format&fit=crop&q=80', 4.80),
(17, 3, 4, 'Atomic Habits: An Easy & Proven Way to Build Good Habits', 'No matter your goals, James Clear offers a proven framework for improving every day. Practical strategies to form good habits, break bad ones, and master tiny behaviors.', 18.00, 80, 'Books', 'https://images.unsplash.com/photo-1512820790803-83ca734da794?w=800&auto=format&fit=crop&q=80', 4.90);

-- Category 5: Sports & Fitness
INSERT INTO products (id, seller_id, category_id, name, description, price, stock_qty, category, image_url, rating) VALUES
(18, 3, 5, 'Bowflex SelectTech 552 Adjustable Dumbbells (Pair)', 'Replaces 15 sets of weights. Adjusts from 5 to 52.5 lbs in 2.5-lb increments with smooth selection dials. Quiet workouts with durable molding around metal plates.', 429.00, 10, 'Sports & Fitness', 'https://images.unsplash.com/photo-1584735935682-2f2b69dff9d2?w=800&auto=format&fit=crop&q=80', 4.80),
(19, 3, 5, 'Manduka PRO Yoga Mat (6mm Thick, Ultra-Dense Cushioning)', 'Ultra-dense cushioning provides unparalleled joint support and stability. Guaranteed to never wear out, closed-cell surface keeps moisture and sweat from seeping into the mat.', 120.00, 25, 'Sports & Fitness', 'https://images.unsplash.com/photo-1545205597-3d9d02c29597?w=800&auto=format&fit=crop&q=80', 4.70),
(20, 3, 5, 'Fitbit Charge 6 Fitness & Health Tracker with Heart Rate Monitor', 'Advanced fitness tracker with built-in GPS, 40+ exercise modes, 24/7 heart rate tracking, EDA Stress scan, ECG app, and up to 7-day battery life.', 139.95, 30, 'Sports & Fitness', 'https://images.unsplash.com/photo-1576243345690-4e4b79b63288?w=800&auto=format&fit=crop&q=80', 4.55);

-- 4. Seed Delivered Order for Bob Buyer (Enables verified buyer reviews)
INSERT INTO orders (id, buyer_id, total_amount, status, shipping_address, payment_method) VALUES
(1, 2, 349.99, 'DELIVERED', '42 Technology Corridor, Guindy Tech Park, Chennai, Tamil Nadu 600032', 'UPI');

INSERT INTO order_items (id, order_id, product_id, product_name, product_image_url, quantity, unit_price) VALUES
(1, 1, 1, 'Sony WH-1000XM5 Wireless Noise-Cancelling Headphones', 'https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=800&auto=format&fit=crop&q=80', 1, 349.99);

-- 5. Seed Verified Product Review
INSERT INTO reviews (id, product_id, user_id, rating, comment) VALUES
(1, 1, 2, 5, 'Absolutely unmatched noise cancellation on long flights. Battery life easily lasts 30 hours and audio quality is pristine. Highly recommended!');
