-- ============================================================
-- SEED DATA — demonstrates all key scenarios for reviewers
-- ============================================================

-- -------  CATEGORIES  -------
INSERT INTO categories (id, name, slug) VALUES
(1, 'Cement',            'cement'),
(2, 'Steel & TMT Bars',  'steel-tmt-bars'),
(3, 'Bricks & Blocks',   'bricks-blocks'),
(4, 'Paint & Coatings',  'paint-coatings'),
(5, 'Plumbing',          'plumbing'),
(6, 'Electrical',        'electrical'),
(7, 'Sand & Aggregates', 'sand-aggregates'),
(8, 'Wood & Timber',     'wood-timber');

-- -------  PRODUCTS  -------
INSERT INTO products (id, name, slug, description, brand, unit, category_id, active) VALUES
-- Cement
(1,  'UltraTech PPC Cement 50 kg',       'ultratech-ppc-cement-50kg',       'Portland Pozzolana Cement suitable for all types of construction. Provides excellent durability and resistance to chemical attacks.', 'UltraTech', 'bag',   1, TRUE),
(2,  'ACC Gold Cement 50 kg',             'acc-gold-cement-50kg',            'Premium quality cement with superior strength and fineness. Ideal for high-rise buildings and heavy structures.',               'ACC',       'bag',   1, TRUE),
(3,  'Ambuja Plus Cement 50 kg',          'ambuja-plus-cement-50kg',         'High-performance cement with moisture resistance. Best suited for foundations and basement construction.',                    'Ambuja',    'bag',   1, TRUE),
(4,  'Birla A1 Cement 50 kg',             'birla-a1-cement-50kg',            'Strong and durable cement from the house of Aditya Birla Group. Suitable for general purpose construction.',                 'Birla',     'bag',   1, TRUE),
(5,  'Dalmia DSP Cement 50 kg',           'dalmia-dsp-cement-50kg',          'Double Strength Portland cement offering superior compressive strength for critical structural applications.',               'Dalmia',    'bag',   1, TRUE),
-- Steel
(6,  'TATA Tiscon 8mm TMT Bar',           'tata-tiscon-8mm-tmt',             'High-strength ribbed TMT bar with excellent ductility. BIS certified for earthquake-resistant construction.',               'TATA',      'kg',    2, TRUE),
(7,  'JSW NeoSteel 10mm TMT Bar',         'jsw-neosteel-10mm-tmt',           'Corrosion-resistant TMT bar manufactured with CRM technology. Ideal for RCC slabs and beams.',                             'JSW',       'kg',    2, TRUE),
(8,  'SAIL TMT 12mm Bar',                 'sail-tmt-12mm-bar',               'Government-grade TMT bar from Steel Authority of India. Used in large infrastructure and commercial projects.',             'SAIL',      'kg',    2, TRUE),
-- Bricks
(9,  'Red Clay Bricks',                   'red-clay-bricks',                 'Traditional kiln-fired red bricks with excellent compressive strength. Standard size 230x110x75mm.',                       NULL,        'thousand', 3, TRUE),
(10, 'AAC Blocks 600x200x200mm',          'aac-blocks-600x200x200',          'Autoclaved Aerated Concrete blocks. Lightweight, thermal insulating, and fire resistant.',                                 NULL,        'piece', 3, TRUE),
(11, 'Fly Ash Bricks',                    'fly-ash-bricks',                  'Eco-friendly bricks made from fly ash and cement. Uniform shape with low water absorption.',                               NULL,        'thousand', 3, TRUE),
-- Paint
(12, 'Asian Paints Apex Exterior 20L',    'asian-paints-apex-exterior-20l',  'Premium exterior emulsion with anti-algal properties and superior weather resistance. Available in multiple shades.',      'Asian Paints', 'bucket', 4, TRUE),
(13, 'Berger WeatherCoat All Guard 20L',  'berger-weathercoat-allguard-20l', 'All-weather exterior paint with silicon-enriched formula for long-lasting protection.',                                    'Berger',    'bucket', 4, TRUE),
(14, 'Nerolac Excel Total 10L',           'nerolac-excel-total-10l',         'Dual-action paint with anti-bacterial and anti-fungal properties. Suitable for both interior and exterior.',                'Nerolac',   'bucket', 4, TRUE),
-- Plumbing
(15, 'Astral CPVC Pipe 1 inch',           'astral-cpvc-pipe-1inch',          'CPVC pipes for hot and cold water systems. Lead-free, corrosion-free, and maintenance-free.',                               'Astral',    'meter', 5, TRUE),
(16, 'Supreme UPVC Pipe 4 inch',          'supreme-upvc-pipe-4inch',         'UPVC drainage pipe with ring-fit joints. ISI marked for underground and above-ground drainage.',                           'Supreme',   'meter', 5, TRUE),
-- Electrical
(17, 'Havells Lifeline 1.5 sq mm Wire 90m', 'havells-lifeline-1.5sqmm-90m', 'FR-LSH insulated single core cable. Flame retardant with low smoke and halogen-free properties.',                         'Havells',   'coil',  6, TRUE),
(18, 'Polycab FR 2.5 sq mm Wire 90m',     'polycab-fr-2.5sqmm-90m',         'Flame retardant PVC insulated copper wire. Suitable for domestic and light commercial wiring.',                             'Polycab',   'coil',  6, TRUE),
-- Sand
(19, 'River Sand',                         'river-sand',                      'Natural river sand suitable for plastering and concreting. Clean and well-graded.',                                        NULL,        'cubic meter', 7, TRUE),
(20, 'M-Sand Manufactured Sand',           'm-sand-manufactured',             'Machine-crushed manufactured sand as river sand alternative. Consistent grading and zero impurities.',                     NULL,        'cubic meter', 7, TRUE),
-- Inactive product (demonstrates platform-level delisting)
(21, 'Discontinued Brand X Cement 50 kg',  'discontinued-brand-x-cement',     'This product has been discontinued by the manufacturer.',                                                                 'Brand X',   'bag',   1, FALSE);

-- -------  SELLERS  -------
-- Demonstrates different statuses: APPROVED, PENDING, REJECTED
INSERT INTO sellers (id, business_name, slug, contact_email, contact_phone, address, city, status) VALUES
(1, 'Shree Traders',             'shree-traders',             'shree@example.com',    '9876543210', '45 Industrial Area, Kothrud',          'Pune',       'APPROVED'),
(2, 'BuildMart India',           'buildmart-india',           'info@buildmart.com',   '9876543211', '12 Andheri East, MIDC',                'Mumbai',     'APPROVED'),
(3, 'Raj Enterprises',           'raj-enterprises',           'raj@example.com',      '9876543212', '78 Karol Bagh, Main Market',           'Delhi',      'APPROVED'),
(4, 'Kumar Supplies',            'kumar-supplies',            'kumar@example.com',    '9876543213', '33 Whitefield Road',                   'Bangalore',  'PENDING'),
(5, 'Mehta Building Materials',  'mehta-building-materials',  'mehta@example.com',    '9876543214', '21 CG Road, Navrangpura',              'Ahmedabad',  'APPROVED'),
(6, 'Gupta Hardware',            'gupta-hardware',            'gupta@example.com',    '9876543215', '56 T Nagar, Pondy Bazaar',             'Chennai',    'REJECTED');

-- -------  SELLER LISTINGS  -------
-- Key scenarios:
--   • Multiple sellers on the same product (product 1 has 5 sellers)
--   • Different prices for the same product
--   • Different stock levels (including 0 → unavailable)
--   • PENDING/REJECTED sellers have listings (hidden from buyers)
--   • Some listings marked unavailable

INSERT INTO seller_listings (product_id, seller_id, price, stock_quantity, min_order_quantity, available) VALUES
-- UltraTech PPC (5 sellers — the star product for comparison)
(1,  1,  390.00,  500,  10,  TRUE),     -- Shree (APPROVED)
(1,  2,  385.00,  1200, 25,  TRUE),     -- BuildMart (APPROVED) — cheapest approved
(1,  3,  405.00,  300,  5,   TRUE),     -- Raj (APPROVED) — most expensive but lowest MOQ
(1,  4,  380.00,  800,  10,  TRUE),     -- Kumar (PENDING) — cheapest overall, but hidden
(1,  5,  395.00,  50,   10,  TRUE),     -- Mehta (APPROVED) — low stock

-- ACC Gold (3 sellers)
(2,  1,  370.00,  300,  10,  TRUE),
(2,  2,  365.00,  800,  20,  TRUE),
(2,  5,  375.00,  200,  10,  TRUE),

-- Ambuja Plus (2 sellers)
(3,  3,  380.00,  600,  10,  TRUE),
(3,  2,  375.00,  400,  15,  TRUE),

-- Birla A1 (2 sellers — one out of stock)
(4,  1,  360.00,  0,    10,  FALSE),    -- Shree: out of stock → unavailable
(4,  5,  355.00,  150,  5,   TRUE),

-- Dalmia DSP (single seller)
(5,  3,  345.00,  700,  10,  TRUE),

-- TATA Tiscon 8mm (3 sellers)
(6,  1,  72.00,   5000, 100, TRUE),
(6,  3,  75.00,   3000, 50,  TRUE),
(6,  5,  70.00,   8000, 200, TRUE),     -- cheapest but highest MOQ

-- JSW NeoSteel 10mm (2 sellers)
(7,  2,  68.00,   4000, 100, TRUE),
(7,  5,  71.00,   2500, 100, TRUE),

-- SAIL TMT 12mm (2 sellers — one REJECTED)
(8,  1,  65.00,   6000, 200, TRUE),
(8,  6,  60.00,   10000, 100, TRUE),    -- Gupta (REJECTED) — hidden from buyers

-- Red Clay Bricks
(9,  1,  8500.00, 50,   1,   TRUE),
(9,  3,  8000.00, 30,   2,   TRUE),

-- AAC Blocks
(10, 2,  55.00,   10000, 100, TRUE),
(10, 5,  58.00,   5000,  50,  TRUE),

-- Fly Ash Bricks (single seller)
(11, 3,  6500.00, 40,   1,   TRUE),

-- Asian Paints Apex
(12, 2,  3200.00, 100,  1,   TRUE),
(12, 5,  3350.00, 60,   1,   TRUE),

-- Berger WeatherCoat (single seller)
(13, 1,  2800.00, 80,   1,   TRUE),

-- Nerolac Excel (unavailable listing)
(14, 2,  1800.00, 120,  1,   FALSE),    -- BuildMart paused this listing

-- Astral CPVC
(15, 5,  85.00,   2000, 10,  TRUE),

-- Supreme UPVC
(16, 1,  220.00,  500,  5,   TRUE),
(16, 3,  210.00,  300,  10,  TRUE),

-- Havells Wire (single seller)
(17, 2,  2100.00, 200,  1,   TRUE),

-- Polycab Wire (single seller)
(18, 5,  2400.00, 150,  1,   TRUE),

-- River Sand
(19, 1,  3500.00, 100,  1,   TRUE),
(19, 3,  3200.00, 200,  2,   TRUE),

-- M-Sand
(20, 2,  2800.00, 300,  1,   TRUE),
(20, 5,  2900.00, 150,  2,   TRUE);

-- Reset sequences to continue after our explicit IDs
SELECT setval('categories_id_seq', (SELECT MAX(id) FROM categories));
SELECT setval('products_id_seq',   (SELECT MAX(id) FROM products));
SELECT setval('sellers_id_seq',    (SELECT MAX(id) FROM sellers));
