-- ============================================================================
-- LakshanMart Seed Data (MySQL & H2 MySQL Mode Compatible)
-- Users, Categories, 25 Realistic Sample Products across 5 Categories, Sample Orders & Reviews
-- ============================================================================

-- 1. Seed Users (Passwords: Admin@123 hashed via BCrypt cost 10)
-- Hash: $2a$10$TOHNnd.ancRoovsISsVc../eEsO0bOtApVuZM66es527wnS2yOM6.
INSERT INTO users (id, name, email, password_hash, role) VALUES
(1, 'Administrator', 'admin@lakshanmart.com', '$2a$10$TOHNnd.ancRoovsISsVc../eEsO0bOtApVuZM66es527wnS2yOM6.', 'ADMIN'),
(2, 'Bob Buyer', 'bob@lakshanmart.com', '$2a$10$TOHNnd.ancRoovsISsVc../eEsO0bOtApVuZM66es527wnS2yOM6.', 'BUYER'),
(3, 'Alice Merchant', 'alice@lakshanmart.com', '$2a$10$TOHNnd.ancRoovsISsVc../eEsO0bOtApVuZM66es527wnS2yOM6.', 'SELLER'),
(4, 'Rahul Sharma', 'rahul@lakshanmart.com', '$2a$10$TOHNnd.ancRoovsISsVc../eEsO0bOtApVuZM66es527wnS2yOM6.', 'SELLER');

-- 2. Seed Categories
INSERT INTO categories (id, name, description, icon) VALUES
(1, 'Electronics', 'Smartphones, laptops, noise-cancelling headphones, and mechanical keyboards', '💻'),
(2, 'Fashion', 'T-shirts, leather jackets, running shoes, luxury watches, and backpacks', '👔'),
(3, 'Home & Kitchen', 'Espresso machines, stand mixers, luxury bedsheets, and cookware sets', '🍳'),
(4, 'Books', 'Fiction best-sellers, tech/engineering manuals, and self-help & finance', '📚'),
(5, 'Sports & Fitness', 'Premium yoga mats, dumbbell sets, motorized treadmills, and fitness bands', '🏋️');

-- 3. Seed 25 Realistic Products across all 5 Categories (5 products each, INR ₹ pricing)
-- Category 1: Electronics
INSERT INTO products (id, seller_id, category_id, name, description, price, stock_qty, category, image_url, rating) VALUES
(1, 3, 1, 'Apple iPhone 15 Pro (128GB, Natural Titanium) - A17 Pro Chip', 'Industry-leading A17 Pro chip with 6-core GPU. Pro camera system featuring 48MP main sensor, 3x Telephoto, and aerospace-grade titanium design with USB-C.', 129900.00, 18, 'Electronics', 'https://images.unsplash.com/photo-1592750475338-74b7b21085ab?w=800&auto=format&fit=crop&q=80', 4.90),
(2, 3, 1, 'Apple MacBook Pro 14-inch (M3 Pro Chip, 18GB RAM, 512GB SSD)', 'Supercharged by Apple M3 Pro chip with 11-core CPU and 14-core GPU. Stunning Liquid Retina XDR display with 1000 nits sustained brightness and up to 18-hour battery.', 199900.00, 12, 'Electronics', 'https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=800&auto=format&fit=crop&q=80', 4.95),
(3, 3, 1, 'Sony WH-1000XM5 Wireless Noise-Cancelling Headphones', 'Industry-leading noise cancellation with two processors and 8 microphones. Up to 30-hour battery life with quick charging. Crystal clear hands-free calling with beamforming mic array.', 29990.00, 25, 'Electronics', 'https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=800&auto=format&fit=crop&q=80', 4.85),
(4, 4, 1, 'Samsung Galaxy Watch 6 Classic (47mm, Bluetooth & LTE)', 'Iconic rotating physical bezel with sapphire crystal glass. Advanced sleep coaching, 24/7 ECG heart monitoring, Body Composition BIA sensor, and personalized HR zones.', 36999.00, 22, 'Electronics', 'https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=800&auto=format&fit=crop&q=80', 4.70),
(5, 4, 1, 'Keychron K2 Wireless Mechanical Keyboard (RGB Backlit)', 'Compact 75% layout Bluetooth mechanical keyboard with Gateron G Pro Brown switches. Mac and Windows compatible with dedicated multimedia keys and hot-swappable switch design.', 8499.00, 35, 'Electronics', 'https://images.unsplash.com/photo-1587829741301-dc798b83add3?w=800&auto=format&fit=crop&q=80', 4.80);

-- Category 2: Fashion
INSERT INTO products (id, seller_id, category_id, name, description, price, stock_qty, category, image_url, rating) VALUES
(6, 3, 2, 'Levi''s Men''s 100% Premium Combed Cotton Crew Neck T-Shirt', 'Crafted from ultra-soft 100% combed cotton jersey. Breathable, durable, and styled in a regular modern fit with subtle embroidered Levi''s red tab logo.', 1299.00, 65, 'Fashion', 'https://images.unsplash.com/photo-1521572267360-ee0c2909d518?w=800&auto=format&fit=crop&q=80', 4.50),
(7, 3, 2, 'Bareskin Handcrafted Genuine Lambskin Leather Biker Jacket', 'Handmade from 100% authentic supple lambskin leather. Features heavy-duty YKK asymmetrical zippers, quilted shoulder accents, satin lining, and windproof protection.', 14999.00, 15, 'Fashion', 'https://images.unsplash.com/photo-1551028719-00167b16eac5?w=800&auto=format&fit=crop&q=80', 4.85),
(8, 4, 2, 'Nike Air Zoom Pegasus 40 Men''s Road Running Shoes', 'Springy ride for every run. Tuned single-layer engineered mesh for breathable comfort with dual Nike Zoom Air units at the forefoot and heel for responsive energy return.', 10495.00, 28, 'Fashion', 'https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=800&auto=format&fit=crop&q=80', 4.75),
(9, 4, 2, 'Fossil Townsman Automatic Skeleton Dial Stainless Steel Watch', 'Exquisite skeleton mechanical dial revealing automatic self-winding movement. Premium 44mm stainless steel case with scratch-resistant mineral crystal and exhibition caseback.', 18495.00, 20, 'Fashion', 'https://images.unsplash.com/photo-1524805444758-089113d48a6d?w=800&auto=format&fit=crop&q=80', 4.70),
(10, 3, 2, 'Wildcraft 45L Ergonomic Water-Resistant Rucksack Backpack', 'Engineered with abrasion-resistant nylon fabric, cushioned back ventilation, dedicated 15.6-inch laptop sleeve, and multi-compartment organizers for travel and hiking.', 3299.00, 40, 'Fashion', 'https://images.unsplash.com/photo-1553062407-98eeb64c6a62?w=800&auto=format&fit=crop&q=80', 4.60);

-- Category 3: Home & Kitchen
INSERT INTO products (id, seller_id, category_id, name, description, price, stock_qty, category, image_url, rating) VALUES
(11, 3, 3, 'De''Longhi Dedica Deluxe Pump Espresso and Cappuccino Maker', 'Sleek 6-inch compact stainless steel Italian espresso machine with 15-bar professional pressure. Premium manual frother creates rich, velvety foam for lattes and flat whites.', 22990.00, 16, 'Home & Kitchen', 'https://images.unsplash.com/photo-1517668808822-9ebb02f2a0e6?w=800&auto=format&fit=crop&q=80', 4.80),
(12, 3, 3, 'KitchenAid Artisan Series 5-Quart Tilt-Head Stand Mixer', 'Iconic culinary centerpiece with 10 speeds and planetary mixing action. Includes 5-quart stainless steel bowl, coated flat beater, dough hook, and wire whisk.', 42500.00, 10, 'Home & Kitchen', 'https://images.unsplash.com/photo-1594385208974-2e75f8d7bb48?w=800&auto=format&fit=crop&q=80', 4.90),
(13, 4, 3, 'Spaces 100% Egyptian Cotton 400TC Luxury King Bedsheet Set', 'Woven from long-staple 100% Egyptian cotton in a silky sateen weave. Includes 1 king flat sheet (108x108 inch) and 2 matching pillowcases with antimicrobial finish.', 3999.00, 50, 'Home & Kitchen', 'https://images.unsplash.com/photo-1522771739844-6a9f6d5f14af?w=800&auto=format&fit=crop&q=80', 4.65),
(14, 4, 3, 'Prestige Deluxe Hard Anodised Non-Stick 5-Piece Cookware Set', 'Heavy gauge hard-anodised aluminum construction with 3-layer durable non-stick coating. Induction and gas stove compatible with stay-cool riveted handles.', 5490.00, 35, 'Home & Kitchen', 'https://images.unsplash.com/photo-1584269600464-37b1b58a9fe7?w=800&auto=format&fit=crop&q=80', 4.70),
(15, 3, 3, 'Philips Digital Air Fryer with Rapid Air Technology (4.1L)', 'Cook with up to 90% less fat using Rapid Air technology. 7 pre-set cooking options on digital touchscreen, Keep Warm function, and QuickClean dishwasher-safe basket.', 8999.00, 30, 'Home & Kitchen', 'https://images.unsplash.com/photo-1544816155-12df9643f363?w=800&auto=format&fit=crop&q=80', 4.75);

-- Category 4: Books
INSERT INTO products (id, seller_id, category_id, name, description, price, stock_qty, category, image_url, rating) VALUES
(16, 3, 4, 'The Midnight Library: A Novel by Matt Haig (International Bestseller)', 'Between life and death there is a library where shelves go on forever. Winner of the Goodreads Choice Award for Fiction, exploring alternate choices and the truly good life.', 499.00, 80, 'Books', 'https://images.unsplash.com/photo-1544716278-ca5e3f4abd8c?w=800&auto=format&fit=crop&q=80', 4.85),
(17, 3, 4, 'Designing Data-Intensive Applications by Martin Kleppmann', 'The definitive guide to the architecture of reliable, scalable, and maintainable data systems. Deep-dives into storage engines, distributed transactions, and stream processing.', 2450.00, 60, 'Books', 'https://images.unsplash.com/photo-1532012164546-f432f2e3777a?w=800&auto=format&fit=crop&q=80', 4.95),
(18, 4, 4, 'Clean Code: A Handbook of Agile Software Craftsmanship', 'Legendary software engineer Robert C. Martin provides timeless principles, patterns, and practices for writing robust, maintainable, and readable code.', 1850.00, 55, 'Books', 'https://images.unsplash.com/photo-1512820790803-83ca734da794?w=800&auto=format&fit=crop&q=80', 4.85),
(19, 4, 4, 'The Psychology of Money: Timeless Lessons by Morgan Housel', 'Doing well with money isn''t necessarily about what you know. It''s about how you behave. 19 short stories exploring the strange ways people think about wealth and greed.', 399.00, 100, 'Books', 'https://images.unsplash.com/photo-1589829085413-56de8ae18c73?w=800&auto=format&fit=crop&q=80', 4.90),
(20, 3, 4, 'Atomic Habits: An Easy & Proven Way to Build Good Habits', 'No matter your goals, James Clear offers a proven framework for improving every day. Practical strategies to form good habits, break bad ones, and master tiny behaviors.', 550.00, 90, 'Books', 'https://images.unsplash.com/photo-1544947950-fa07a98d237f?w=800&auto=format&fit=crop&q=80', 4.95);

-- Category 5: Sports & Fitness
INSERT INTO products (id, seller_id, category_id, name, description, price, stock_qty, category, image_url, rating) VALUES
(21, 3, 5, 'Manduka PRO High-Density 6mm Non-Slip Premium Yoga Mat', 'Ultra-dense cushioning provides unparalleled joint support and stability. Guaranteed to never wear out, closed-cell surface keeps moisture and sweat from seeping into the mat.', 6999.00, 25, 'Sports & Fitness', 'https://images.unsplash.com/photo-1545205597-3d9d02c29597?w=800&auto=format&fit=crop&q=80', 4.80),
(22, 3, 5, 'Bowflex SelectTech 552 Adjustable Dumbbells (Pair 2.5kg to 24kg)', 'Replaces 15 sets of weights. Adjusts from 5 to 52.5 lbs in 2.5-lb increments with smooth selection dials. Quiet workouts with durable molding around metal plates.', 34990.00, 12, 'Sports & Fitness', 'https://images.unsplash.com/photo-1584735935682-2f2b69dff9d2?w=800&auto=format&fit=crop&q=80', 4.85),
(23, 4, 5, 'PowerMax Fitness TDM-100S Motorized Auto-Lubricating Smart Treadmill', '2.0 HP DC motor delivering up to 14.8 km/hr speed with 6-level manual incline. Features dual spring shock absorption, LED display, Heart Rate sensor, and AUX/USB input.', 44990.00, 8, 'Sports & Fitness', 'https://images.unsplash.com/photo-1576678927484-cc907957088c?w=800&auto=format&fit=crop&q=80', 4.70),
(24, 4, 5, 'Fitbit Charge 6 Advanced Fitness Tracker with Built-in GPS', 'Advanced fitness tracker with built-in GPS, 40+ exercise modes, 24/7 heart rate tracking, EDA Stress scan, ECG app, and up to 7-day battery life.', 14999.00, 30, 'Sports & Fitness', 'https://images.unsplash.com/photo-1576243345690-4e4b79b63288?w=800&auto=format&fit=crop&q=80', 4.65),
(25, 3, 5, 'Boldfit Heavy Duty Resistance Bands Set with Handles & Anchor', 'Stackable up to 150 lbs resistance with 5 color-coded tube bands, comfortable foam handles, reinforced door anchor, ankle straps, and travel carrying pouch.', 1499.00, 50, 'Sports & Fitness', 'https://images.unsplash.com/photo-1517838277536-f5f99be501cd?w=800&auto=format&fit=crop&q=80', 4.60);

-- 4. Seed Delivered Orders for Bob Buyer (Enables verified buyer reviews & seller earnings)
INSERT INTO orders (id, buyer_id, total_amount, status, shipping_address, payment_method) VALUES
(1, 2, 29990.00, 'DELIVERED', '42 Technology Corridor, Guindy Tech Park, Chennai, Tamil Nadu 600032', 'UPI'),
(2, 2, 1850.00, 'DELIVERED', '42 Technology Corridor, Guindy Tech Park, Chennai, Tamil Nadu 600032', 'CARD'),
(3, 2, 10495.00, 'SHIPPED', '42 Technology Corridor, Guindy Tech Park, Chennai, Tamil Nadu 600032', 'COD');

INSERT INTO order_items (id, order_id, product_id, product_name, product_image_url, quantity, unit_price) VALUES
(1, 1, 3, 'Sony WH-1000XM5 Wireless Noise-Cancelling Headphones', 'https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=800&auto=format&fit=crop&q=80', 1, 29990.00),
(2, 2, 18, 'Clean Code: A Handbook of Agile Software Craftsmanship', 'https://images.unsplash.com/photo-1512820790803-83ca734da794?w=800&auto=format&fit=crop&q=80', 1, 1850.00),
(3, 3, 8, 'Nike Air Zoom Pegasus 40 Men''s Road Running Shoes', 'https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=800&auto=format&fit=crop&q=80', 1, 10495.00);

-- 5. Seed Verified Product Reviews
INSERT INTO reviews (id, product_id, user_id, rating, comment) VALUES
(1, 3, 2, 5, 'Absolutely unmatched noise cancellation on flights and daily commute. Battery easily lasts 30 hours and audio quality is pristine. Highly recommended!'),
(2, 18, 2, 5, 'A timeless guide for every software engineer. Uncle Bob''s principles completely transformed how our engineering team crafts clean code.');
