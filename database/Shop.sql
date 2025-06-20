DROP DATABASE IF EXISTS shop;
CREATE DATABASE shop DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE shop;

CREATE TABLE `categories` (
                             `id` BIGINT NOT NULL AUTO_INCREMENT,
                             `name` VARCHAR(45) NOT NULL,
                             `code` VARCHAR(20) NOT NULL,
                             PRIMARY KEY (`id`),
                             UNIQUE INDEX `unique_category_name` (`name`)
);

INSERT INTO `categories` (`name`, `code`) VALUES
                                              ('Meat', '111222333'),
                                              ('Milk', '444555666'),
                                              ('Vegetables', '777888999');

CREATE TABLE `products` (
                                `pr_id` BIGINT NOT NULL AUTO_INCREMENT,
                                `pr_name` VARCHAR(45) NOT NULL,
                                `category_id` BIGINT NULL,
                                PRIMARY KEY (`pr_id`),
                                UNIQUE INDEX `unique_product_name` (`pr_name`),
                                CONSTRAINT `fk_product_category`
                                    FOREIGN KEY (`category_id`)
                                        REFERENCES `categories` (`id`)
                                        ON DELETE SET NULL
                                        ON UPDATE CASCADE
);

INSERT INTO `products` (`pr_name`, `category_id`) VALUES
                                                         ('Fish', 1),
                                                         ('Beef', 1),
                                                         ('Milk', 2),
                                                         ('Cheese', 2),
                                                         ('Potato', 3),
                                                         ('Tomato', 3),
                                                         ('Carrot', 3),
                                                         ('Ice-cream', 2),
                                                         ('Mushrooms', 3);




CREATE TABLE `suppliers` (
                            `id` BIGINT NOT NULL AUTO_INCREMENT,
                            `name` VARCHAR(45) NOT NULL,
                            `surname` VARCHAR(45) NOT NULL,
                            `phone_number` VARCHAR(20) NULL,
                            `pr_id` BIGINT NULL,
                            PRIMARY KEY (`id`),
                            CONSTRAINT `fk_supplier_product`
                                FOREIGN KEY (`pr_id`)
                                    REFERENCES `products` (`pr_id`)
                                    ON DELETE SET NULL
                                    ON UPDATE CASCADE
);

INSERT INTO `suppliers` (`name`, `surname`, `phone_number`, `pr_id`) VALUES
                                                                        ('John', 'Smith', '123456789', 1),
                                                                        ('Emma', 'Johnson', '234567890', 2),
                                                                        ('Michael', 'Williams', '345678901', 3),
                                                                        ('Sarah', 'Brown', '456789012', 4),
                                                                        ('David', 'Jones', '567890123', 5),
                                                                        ('Lisa', 'Garcia', '678901234', 6),
                                                                        ('James', 'Miller', '789012345', 7),
                                                                        ('Jennifer', 'Davis', '890123456', 8),
                                                                        ('Matthew', 'Rodriguez', '901234567', 9);

CREATE TABLE `users` (
                         `id` INT NOT NULL AUTO_INCREMENT,
                         `username` VARCHAR(64) NOT NULL,
                         `password` VARCHAR(64) NOT NULL,
                         `authority` VARCHAR(64) NOT NULL,
                         PRIMARY KEY (`id`),
                         UNIQUE INDEX `unique_username` (`username`)
);

INSERT INTO `users` (`username`, `password`, `authority`) VALUES
                                                              ( 'admin', '$2a$10$jrryFNptnoGWwyWhxc47eeeHpin/LPOut7J221Xv4DB3qTswVcvJS',
                                                               'ROLE_ADMIN'),
                                                              ('user', '$2a$10$I0BOCCDqRH6905RIlUmgd.2L008fmT3QvFtjEynyJQ2WoKDFRNGo6',
                                                               'ROLE_USER'),
                                                              ('manager', '$2a$10$I0BOCCDqRH6905RIlUmgd.2L008fmT3QvFtjEynyJQ2WoKDFRNGo6', 'ROLE_MANAGER');
