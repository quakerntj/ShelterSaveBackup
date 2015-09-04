grep "id\":" fos_weapon.json | sort |uniq | sed 's/.*:"//g' | sed 's/",//g' > weapons.txt
grep "id\":" fos_outfit.json | sort |uniq | sed 's/.*:"//g' | sed 's/",//g' > outfits.txt
