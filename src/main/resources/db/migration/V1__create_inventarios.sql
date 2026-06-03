CREATE TABLE IF NOT EXISTS inventarios (
    id              BIGINT      NOT NULL AUTO_INCREMENT,
    fk_producto_id  BIGINT      NOT NULL,
    cantidad        INT         NOT NULL,
    estado          TINYINT(1)  NOT NULL,

    CONSTRAINT pk_inventarios   PRIMARY KEY (id),
    CONSTRAINT uq_inventarios_producto UNIQUE (fk_producto_id)
);