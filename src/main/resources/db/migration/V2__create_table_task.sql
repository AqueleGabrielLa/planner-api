CREATE TABLE task (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    title VARCHAR(255) NOT NULL,
    description TEXT,
    completed BOOLEAN NOT NULL DEFAULT FALSE,
    user_id UUID NOT NULL REFERENCES user_account(id),
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);