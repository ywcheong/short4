#!/bin/bash
set -e

# Read the password from the Docker secret file
MONGO_ROOT_PASSWORD=$(cat /run/secrets/mongodb_root_password)
MONGO_USER_PASSWORD=$(cat /run/secrets/mongodb_user_password)

# Use mongosh to initialize MongoDB with the secret password
mongosh <<EOF
use admin
db.createUser({
    user: "root",
    pwd: "$MONGO_ROOT_PASSWORD",
    roles: [{ role: "root", db: "admin" }]
});
db.getSiblingDB("short4").createUser({
    user: "short4_user",
    pwd: "$MONGO_USER_PASSWORD",
    roles: [{ role: "readWrite", db: "short4" }]
});
EOF
