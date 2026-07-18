#!/bin/sh

# connect to the MongoDB and check if the replica set is initialized
STATUS=$(mongosh -u "$MONGO_INITDB_ROOT_USERNAME" -p "$MONGO_INITDB_ROOT_PASSWORD" --authenticationDatabase admin --quiet --eval "rs.status().ok" 2>/dev/null)

if [ "$STATUS" = "1" ]; then
  # the MongoDB replicaset was already initialized
  exit 0
else
  # the MongoDB replicaset not initialized, so we initialize it
  mongosh -u "$MONGO_INITDB_ROOT_USERNAME" -p "$MONGO_INITDB_ROOT_PASSWORD" --authenticationDatabase admin --quiet --eval 'rs.initiate({_id:"rs0",members:[{_id:0,host:"mongodb-audit:27017"}]})' && exit 0 || exit 1
fi
