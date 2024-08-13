#!/bin/bash

log_file="logfile.log"
json_files=$(find . -type f -name '*.json' -o -name '*.JSON')

# Check if the log file exists and remove it if it does
if [ -f $log_file ] ; then
    rm $log_file
fi

# Loop through the JSON files and append the data to the log file
while :
do
    for file in $json_files; do
        echo "Reading data from file: $file"
        while read -r line; do
            echo "$line" >> $log_file
        done < "$file"
    done
    sleep 3
done
