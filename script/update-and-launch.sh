#!/bin/bash

docker pull ghcr.io/raffifu/money-tracker-bot &&
docker-compose up -d --no-deps telegram-bot
