#!/bin/bash


while read line
do
    category="${line%_*}"
    fixname="${line#*_}"
    if [ "$fixname" != "$line" ]; then
        showname="$fixname $category"
    else
        showname="$category"
    fi
    imagename="${category,,}"

    echo '		{'
    echo '			"id":"'$line'",'
    echo '			"category":"'$category'",'
    echo '			"showname":"'$showname'",'
    echo '			"type":"Weapon",'
    echo '			"image":"'$imagename'"'
    echo '		},'
done < weapons.txt

