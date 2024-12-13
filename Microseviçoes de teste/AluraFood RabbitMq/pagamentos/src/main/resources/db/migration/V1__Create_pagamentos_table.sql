CREATE TABLE pagamentos
(
    id                BIGINT AUTO_INCREMENT NOT NULL,
    valor             DECIMAL(19,2)      NOT NULL,
    nome              VARCHAR(100) NULL,
    numero            VARCHAR(19) NULL,
    expiracao         VARCHAR(7) NULL,
    codigo            VARCHAR(3) NULL,
    status            VARCHAR(255) NOT NULL,
    pedido_id         BIGINT(20)       NOT NULL,
    forma_depagamento BIGINT(20)       NOT NULL,
    CONSTRAINT pk_pagamentos PRIMARY KEY (id)
);