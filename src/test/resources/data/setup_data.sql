-- 1. Xóa dữ liệu cũ (Theo thứ tự ngược lại để tránh lỗi FK)
DELETE FROM ticket;
DELETE FROM show_time_seat;
DELETE FROM show_time;
DELETE FROM seat;
DELETE FROM room;
DELETE FROM movie;
DELETE FROM users;

-- 2. Tạo User (Để test trường user_holding)
INSERT INTO users (user_id, username, email, password, role, created_at)
VALUES ('00000000-0000-0000-0000-000000000001', 'test_user', 'test@example.com', '12345678', 'USER', NOW());

-- 3. Tạo Phim
INSERT INTO movie (movie_id, title, duration, genre, rating, description, release_at)
VALUES ('11111111-1111-1111-1111-111111111111', 'Oppenheimer', 180, 'Drama', 9, 'Biographical thriller', '2023-07-21');

-- 4. Tạo Phòng chiếu
INSERT INTO room (room_id, room_name, total_seats)
VALUES ('22222222-2222-2222-2222-222222222222', 'IMAX Premium', 100);

-- 5. Tạo Ghế (Seat vật lý trong phòng)
-- 5. Tạo Ghế vật lý (Đã sửa lại UUID chuẩn 36 ký tự)
INSERT INTO seat (seat_id, room_id, seat_number)
VALUES
    ('33333333-3333-3333-3333-333333333333', '22222222-2222-2222-2222-222222222222', 'A1'),
    ('33333333-3333-3333-3333-333333444434', '22222222-2222-2222-2222-222222222222', 'A2'),
    ('33333333-3333-3333-3333-444445323333', '22222222-2222-2222-2222-222222222222', 'A3'),
    ('33322233-3433-3233-3333-333123953333', '22222222-2222-2222-2222-222222222222', 'A4'),
    ('31111333-3423-3333-3333-333256833333', '22222222-2222-2222-2222-222222222222', 'A5');


-- 6. Tạo Suất chiếu (ShowTime)
INSERT INTO show_time (show_time_id, movie_id, room_id, start_time, end_time, base_price)
VALUES ('44444444-4444-4444-4444-444444444444',
        '11111111-1111-1111-1111-111111111111',
        '22222222-2222-2222-2222-222222222222',
        NOW() + INTERVAL '1 hour',
        NOW() + INTERVAL '4 hour',
        100.00);

-- 7. Tạo Trạng thái ghế cho suất chiếu (ShowTimeSeat) - ĐÂY LÀ ĐỐI TƯỢNG CHÍNH ĐỂ TEST LOCK
-- Ghế 1: Đang trống (AVAILABLE) để test tranh chấp
INSERT INTO show_time_seat (show_time_seat_id, show_time_id, seat_id, seat_status, is_modified)
VALUES ('55555555-5555-5555-5555-555555555555',
        '44444444-4444-4444-4444-444444444444',
        '33333333-3333-3333-3333-333333333333',
        'AVAILABLE',
        false);

-- Ghế 2: Đã bị đặt (Để test trường hợp lỗi/rollback)
INSERT INTO show_time_seat (show_time_seat_id, show_time_id, seat_id, seat_status, is_modified)
VALUES ('66666666-6666-6666-6666-666666666666',
        '44444444-4444-4444-4444-444444444444',
        '33333333-3333-3333-3333-333333444434', -- Đảm bảo seat_id này đã tồn tại ở bảng seat
        'HOLDING',
        false);

