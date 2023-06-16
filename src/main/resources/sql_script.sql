-- 주문 테이블 생성
-- 예약어는 backtick 으로 감싸야 한다.

CREATE TABLE `springbatch`.`orders` (
    `id` INT NOT NULL AUTO_INCREMENT,
    `order_item` VARCHAR(45) NULL,
    `price` INT NULL,
    `order_date` DATE NULL,
    PRIMARY KEY (`id`)
);

CREATE TABLE `springbatch`.`accounts` (
    `id` INT NOT NULL AUTO_INCREMENT,
    `order_item` VARCHAR(45) NULL,
    `price` INT NULL,
    `order_date` DATE NULL,
    `account_date` DATE NULL,
    PRIMARY KEY (`id`)
);

INSERT INTO `springbatch`.`orders`(`order_item`, `price`, `order_date`) values ('카카오 선물', 15000, '2022-03-01');
INSERT INTO `springbatch`.`orders`(`order_item`, `price`, `order_date`) values ('교보문고', 16000, '2022-03-01');
INSERT INTO `springbatch`.`orders`(`order_item`, `price`, `order_date`) values ('치킨', 20000, '2022-03-02');
INSERT INTO `springbatch`.`orders`(`order_item`, `price`, `order_date`) values ('커피', 2000, '2022-03-02');
INSERT INTO `springbatch`.`orders`(`order_item`, `price`, `order_date`) values ('피자', 23000, '2022-03-03');
INSERT INTO `springbatch`.`orders`(`order_item`, `price`, `order_date`) values ('마라탕', 13000, '2022-03-04');
INSERT INTO `springbatch`.`orders`(`order_item`, `price`, `order_date`) values ('커피', 5000, '2022-03-05');
INSERT INTO `springbatch`.`orders`(`order_item`, `price`, `order_date`) values ('교보문고', 15000, '2022-03-05');
INSERT INTO `springbatch`.`orders`(`order_item`, `price`, `order_date`) values ('아이스크림', 1000, '2022-03-06');
INSERT INTO `springbatch`.`orders`(`order_item`, `price`, `order_date`) values ('카카오 선물', 29000, '2022-03-06');

select *
from `springbatch`.`orders`;



