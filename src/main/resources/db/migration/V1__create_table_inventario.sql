CREATE TABLE IF NOT EXISTS stock_db (
                             id BIGINT AUTO_INCREMENT PRIMARY KEY,
                             fk_producto_id BIGINT NOT NULL,
                             cantidad INT NOT NULL,
                             estado BOOLEAN NOT NULL DEFAULT TRUE,

    -- Restricción para asegurar que no se duplique el inventario de un mismo producto
                             CONSTRAINT uk_inventario_producto UNIQUE (fk_producto_id)
);

CREATE TABLE IF NOT EXISTS stock_db_dev (
                                           id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                           fk_producto_id BIGINT NOT NULL,
                                           cantidad INT NOT NULL,
                                           estado BOOLEAN NOT NULL DEFAULT TRUE,

    -- Restricción para asegurar que no se duplique el inventario de un mismo producto
                                           CONSTRAINT uk_inventario_producto UNIQUE (fk_producto_id)
    );

CREATE TABLE IF NOT EXISTS stock_db_test (
                                           id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                           fk_producto_id BIGINT NOT NULL,
                                           cantidad INT NOT NULL,
                                           estado BOOLEAN NOT NULL DEFAULT TRUE,

    -- Restricción para asegurar que no se duplique el inventario de un mismo producto
                                           CONSTRAINT uk_inventario_producto UNIQUE (fk_producto_id)
    );