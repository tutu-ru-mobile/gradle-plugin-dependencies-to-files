#!/bin/bash
./gradlew :include_build:plugin-dependencies-to-disk:tasks && \
  ./gradlew sample-kts:build --refresh-dependencies && \
  ./gradlew sample-groovy:build --refresh-dependencies && \
  echo "[SUCCESS]" || echo "[FAIL!]"

# --refresh-dependencies

