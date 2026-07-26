-- ============================================================
-- DurusTours catalog seed data
-- Compatible with H2 (dev/test) and PostgreSQL.
--
-- Note: rows use explicit IDs so they are stable references for future
-- reservations seed data. If this is ever run against PostgreSQL, the
-- "tours_id_seq" identity sequence must be advanced past 4 afterwards
-- (e.g. SELECT setval('tours_id_seq', 4)), since PostgreSQL identity
-- columns don't auto-advance on explicit-value inserts the way H2 does.
-- ============================================================

DELETE FROM tours;

INSERT INTO tours (id, category, title, description, duration_minutes, duration_label, base_price, combo, active)
VALUES
    (1, '50_MIN_CRUISE', 'Douro Bridges Tour',
        '50-minute boat cruise highlighting Porto and Gaia''s iconic 6 bridges.',
        50, '50 minutes', 15.00, FALSE, TRUE),

    (2, '50_MIN_CRUISE', 'Bridges Tour + Burmester Wine Cellar Combo',
        'Includes the 50-minute cruise and a guided visit/tasting at Cave Burmester. Voucher is valid for 48 hours once activated, either at the boat dock or the wine cellar.',
        50, '50 minutes (boat) + self-paced wine cellar visit', 25.00, TRUE, TRUE),

    (3, 'FULL_DAY_CRUISE', 'Porto to Regua Cruise',
        'Scenic full-day journey upstream to Regua including breakfast, lunch, and return trip.',
        NULL, 'Full day (approx. 10-12 hours)', 95.00, FALSE, TRUE),

    (4, 'FULL_DAY_CRUISE', 'Porto to Pinhao Cruise',
        'Deep journey into the heart of the UNESCO Douro Wine Region to Pinhao with full meals included.',
        NULL, 'Full day (approx. 12-14 hours)', 125.00, FALSE, TRUE);
