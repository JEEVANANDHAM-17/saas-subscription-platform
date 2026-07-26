-- Flyway V1: create the initial subscription application schema.
CREATE TABLE users (
                       id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
                       public_id CHAR(36) NOT NULL,
                       first_name VARCHAR(100) NOT NULL,
                       last_name VARCHAR(100) NULL,
                       email VARCHAR(255) NOT NULL,
                       password_hash VARCHAR(255) NOT NULL,
                       role ENUM('ADMIN', 'STAFF') NOT NULL DEFAULT 'STAFF',
                       status ENUM('PENDING', 'ACTIVE', 'BLOCKED') NOT NULL DEFAULT 'PENDING',
                       created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
                       updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6)
        ON UPDATE CURRENT_TIMESTAMP(6),

                       PRIMARY KEY (id),
                       UNIQUE KEY uk_users_public_id (public_id),
                       UNIQUE KEY uk_users_email (email)
) ENGINE = InnoDB;


CREATE TABLE customers (
                           id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
                           public_id CHAR(36) NOT NULL,
                           name VARCHAR(200) NOT NULL,
                           email VARCHAR(255) NOT NULL,
                           phone VARCHAR(30) NULL,
                           billing_address JSON NULL,
                           status ENUM('ACTIVE', 'INACTIVE') NOT NULL DEFAULT 'ACTIVE',
                           created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
                           updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6)
        ON UPDATE CURRENT_TIMESTAMP(6),

                           PRIMARY KEY (id),
                           UNIQUE KEY uk_customers_public_id (public_id),
                           UNIQUE KEY uk_customers_email (email)
) ENGINE = InnoDB;


CREATE TABLE plans (
                       id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
                       public_id CHAR(36) NOT NULL,
                       code VARCHAR(50) NOT NULL,
                       name VARCHAR(100) NOT NULL,
                       description VARCHAR(500) NULL,
                       price DECIMAL(19, 4) NOT NULL,
                       currency CHAR(3) NOT NULL,
                       billing_interval ENUM('MONTHLY', 'YEARLY') NOT NULL,
                       trial_days SMALLINT UNSIGNED NOT NULL DEFAULT 0,
                       status ENUM('DRAFT', 'ACTIVE', 'ARCHIVED') NOT NULL DEFAULT 'DRAFT',
                       created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
                       updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6)
        ON UPDATE CURRENT_TIMESTAMP(6),

                       PRIMARY KEY (id),
                       UNIQUE KEY uk_plans_public_id (public_id),
                       UNIQUE KEY uk_plans_code (code),
                       KEY idx_plans_status (status),

                       CONSTRAINT chk_plans_price CHECK (price >= 0)
) ENGINE = InnoDB;


CREATE TABLE subscriptions (
                               id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
                               public_id CHAR(36) NOT NULL,
                               customer_id BIGINT UNSIGNED NOT NULL,
                               plan_id BIGINT UNSIGNED NOT NULL,
                               status ENUM(
        'TRIALING',
        'ACTIVE',
        'PAST_DUE',
        'CANCELLED',
        'EXPIRED'
    ) NOT NULL,
                               started_at DATETIME(6) NOT NULL,
                               trial_ends_at DATETIME(6) NULL,
                               current_period_start DATETIME(6) NOT NULL,
                               current_period_end DATETIME(6) NOT NULL,
                               cancel_at_period_end BOOLEAN NOT NULL DEFAULT FALSE,
                               cancelled_at DATETIME(6) NULL,
                               created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
                               updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6)
        ON UPDATE CURRENT_TIMESTAMP(6),
                               version BIGINT UNSIGNED NOT NULL DEFAULT 0,

                               PRIMARY KEY (id),
                               UNIQUE KEY uk_subscriptions_public_id (public_id),
                               KEY idx_subscriptions_customer_status (customer_id, status),
                               KEY idx_subscriptions_renewal (status, current_period_end),

                               CONSTRAINT fk_subscriptions_customer
                                   FOREIGN KEY (customer_id) REFERENCES customers (id),
                               CONSTRAINT fk_subscriptions_plan
                                   FOREIGN KEY (plan_id) REFERENCES plans (id)
) ENGINE = InnoDB;


CREATE TABLE subscription_history (
                                      id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
                                      subscription_id BIGINT UNSIGNED NOT NULL,
                                      old_plan_id BIGINT UNSIGNED NULL,
                                      new_plan_id BIGINT UNSIGNED NULL,
                                      action ENUM(
        'CREATED',
        'UPGRADED',
        'DOWNGRADED',
        'CANCEL_SCHEDULED',
        'CANCELLED',
        'RENEWED',
        'EXPIRED'
    ) NOT NULL,
                                      changed_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),

                                      PRIMARY KEY (id),
                                      KEY idx_subscription_history_timeline (subscription_id, changed_at),

                                      CONSTRAINT fk_subscription_history_subscription
                                          FOREIGN KEY (subscription_id) REFERENCES subscriptions (id),
                                      CONSTRAINT fk_subscription_history_old_plan
                                          FOREIGN KEY (old_plan_id) REFERENCES plans (id),
                                      CONSTRAINT fk_subscription_history_new_plan
                                          FOREIGN KEY (new_plan_id) REFERENCES plans (id)
) ENGINE = InnoDB;


CREATE TABLE invoices (
                          id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
                          public_id CHAR(36) NOT NULL,
                          customer_id BIGINT UNSIGNED NOT NULL,
                          subscription_id BIGINT UNSIGNED NOT NULL,
                          invoice_number VARCHAR(50) NOT NULL,
                          amount DECIMAL(19, 4) NOT NULL,
                          currency CHAR(3) NOT NULL,
                          status ENUM('DRAFT', 'OPEN', 'PAID', 'VOID', 'FAILED')
        NOT NULL DEFAULT 'DRAFT',
                          issue_date DATE NOT NULL,
                          due_date DATE NOT NULL,
                          paid_at DATETIME(6) NULL,
                          created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
                          updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6)
        ON UPDATE CURRENT_TIMESTAMP(6),

                          PRIMARY KEY (id),
                          UNIQUE KEY uk_invoices_public_id (public_id),
                          UNIQUE KEY uk_invoices_number (invoice_number),
                          KEY idx_invoices_subscription (subscription_id),
                          KEY idx_invoices_due (status, due_date),

                          CONSTRAINT chk_invoices_amount CHECK (amount >= 0),
                          CONSTRAINT fk_invoices_customer
                              FOREIGN KEY (customer_id) REFERENCES customers (id),
                          CONSTRAINT fk_invoices_subscription
                              FOREIGN KEY (subscription_id) REFERENCES subscriptions (id)
) ENGINE = InnoDB;


CREATE TABLE payments (
                          id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
                          public_id CHAR(36) NOT NULL,
                          invoice_id BIGINT UNSIGNED NOT NULL,
                          idempotency_key VARCHAR(100) NOT NULL,
                          transaction_reference VARCHAR(100) NULL,
                          amount DECIMAL(19, 4) NOT NULL,
                          currency CHAR(3) NOT NULL,
                          payment_method ENUM('MOCK_CARD', 'MOCK_BANK') NOT NULL,
                          status ENUM('PENDING', 'SUCCEEDED', 'FAILED') NOT NULL DEFAULT 'PENDING',
                          failure_reason VARCHAR(500) NULL,
                          paid_at DATETIME(6) NULL,
                          created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
                          updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6)
        ON UPDATE CURRENT_TIMESTAMP(6),

                          PRIMARY KEY (id),
                          UNIQUE KEY uk_payments_public_id (public_id),
                          UNIQUE KEY uk_payments_idempotency_key (idempotency_key),
                          UNIQUE KEY uk_payments_transaction_reference (transaction_reference),
                          KEY idx_payments_invoice_status (invoice_id, status),

                          CONSTRAINT chk_payments_amount CHECK (amount > 0),
                          CONSTRAINT fk_payments_invoice
                              FOREIGN KEY (invoice_id) REFERENCES invoices (id)
) ENGINE = InnoDB;
