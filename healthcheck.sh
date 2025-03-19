#!/bin/sh

service_name=$1
check_type=$2
param=$3

healthy() {
  echo "$check_type: $service_name is healthy"
  exit 0
}

unhealthy() {
  echo "$check_type: $service_name is unhealthy"
  exit 1
}

undefined() {
  echo "Unknown check type $check_type for $service_name"
  exit 1
}

check_api() {
  if curl -f "$param" | jq -e '.status == "UP"' > /dev/null; then
    healthy
  else
    unhealthy
  fi
}

check_worker() {
  if pgrep -f "$param" > /dev/null; then
    healthy
  else
    unhealthy
  fi
}

case "$check_type" in
  api)
    check_api
    ;;
  worker)
    check_worker
    ;;
  *)
    undefined
    ;;
esac