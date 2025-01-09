#!/bin/bash
set -e

# Use mongosh to initialize MongoDB with the secret password
mongosh <<EOF
for (db_name of ["short4", "short4_test"]) {
db.getSiblingDB(db_name).createCollection("shorturl");
db.getSiblingDB(db_name).shorturl.createIndex({"token": 1});
}
EOF
