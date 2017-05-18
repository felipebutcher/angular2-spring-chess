#!/bin/bash
nohup java -jar -Xms128M -Xmx256M target/butcher-chess-0.1.0.jar > /dev/null 2>&1 &
