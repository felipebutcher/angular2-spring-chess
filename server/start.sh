#!/bin/bash
nohup java -jar -Xms256M -Xmx512M target/butcher-chess-0.1.0.jar > /dev/null 2>&1 &
