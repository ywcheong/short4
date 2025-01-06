#!/bin/bash
set -e

# Use mongosh to initialize MongoDB with the secret password
mongosh <<EOF
db.getSiblingDB("short4").createCollection("shorturl");
db.getSiblingDB("short4").shorturl.createIndex({"token": 1});
EOF
