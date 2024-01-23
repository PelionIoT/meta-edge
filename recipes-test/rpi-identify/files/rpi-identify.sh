#!/bin/sh

GREEN_SECONDS_ON=1
GREEN_SECONDS_OFF=1
RED_SECONDS_ON=1
RED_SECONDS_OFF=1

GREEN_LED=/sys/devices/platform/leds/leds/led0
RED_LED=/sys/devices/platform/leds/leds/led1

red_seconds="$RED_SECONDS_ON"
green_seconds="$GREEN_SECONDS_ON"
green_off=false
red_off=true

user_id=$(id -u)
if [ "${user_id}" -gt 0 ]; then
    echo "This script must be run via sudo (or as root)."
    exit 1
fi


# Assign the LED pins to GPIO
echo gpio > $GREEN_LED/trigger
echo gpio > $RED_LED/trigger

# Turn both LEDs off.
echo 0    > $GREEN_LED/brightness
echo 0    > $RED_LED/brightness

while true; do

    # Deal with GREEN.
    if [ "$green_off" = true ]; then
        if [ "$green_seconds" -gt 0 ]; then
            echo 0 > $GREEN_LED/brightness
            green_seconds=$((green_seconds - 1))
        fi
        if [ "$green_seconds" = 0 ]; then
            green_off=false
            green_seconds="$GREEN_SECONDS_ON"
        fi
    else
        if [ "$green_seconds" -gt 0 ]; then
            echo 1 > $GREEN_LED/brightness
            green_seconds=$((green_seconds - 1))
        fi
        if [ "$green_seconds" = 0 ]; then
            green_off=true
            green_seconds="$GREEN_SECONDS_OFF"
        fi
    fi

    # Deal with RED.
    if [ "$red_off" = true ]; then
        if [ "$red_seconds" -gt 0 ]; then
            echo 0 > $RED_LED/brightness
            red_seconds=$((red_seconds -1))
        fi
        if [ "$red_seconds" = 0 ]; then
            red_off=false
            red_seconds="$RED_SECONDS_ON"
        fi
    else
        if [ "$red_seconds" -gt 0 ]; then
            echo 1 > $RED_LED/brightness 
            red_seconds=$((red_seconds -1))
        fi
        if [ "$red_seconds" = 0 ]; then
            red_off=true
            red_seconds="$RED_SECONDS_OFF"
        fi
    fi

    sleep 1
done

