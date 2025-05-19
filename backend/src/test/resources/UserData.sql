INSERT INTO tbl_user (
    user_name, password, email, phone_no, uuid, creation_date, created_by, last_updated, updated_by, enabled, version
) VALUES (
             'john_doe', 'hashed_password_123', 'john.doe@example.com', '123-456-7890', '550e8400-e29b-41d4-a716-446655440000',
             '2024-10-11 10:00:00', 'admin', '2024-10-11 12:00:00', 'admin', TRUE, 1
         );
