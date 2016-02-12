#!/bin/bash
## This is the script gz99 found at https://github.com/gmatht/joshell/blob/master/scripts/gz99
## created by gmatht (https://github.com/gmatht)
set -e

command -v advdef > /dev/null || echo advdef is not installed. Install for a better compression ratio
command -v 7z > /dev/null || echo 7z is not installed. Install for a better compression ratio

if [ -z "$1" ]
then
  echo "USAGE: $0 OUTPUT.gz"
  echo "Compresses stdin to OUTPUT.gz"
  exit 1
fi



OUT=$1

myhash(){
  sha256sum $@ | sed s/\ .*//
}

time gzip -9 > $OUT.new
mv $OUT.new $OUT
ORIG_HASH=`gunzip < $OUT|myhash`

accept_new(){
if [ ".`gunzip < $OUT.new|myhash`" = ".$ORIG_HASH" ]
then
  oldsize=$(stat -c%s "$OUT")
  newsize=$(stat -c%s "$OUT.new")
  if [ $oldsize -gt $newsize ]
  then
    echo "$oldsize->$newsize: Saved $((oldsize-newsize)) bytes."
    mv -f $OUT.new $OUT
  else 
    echo "$oldsize->$newsize: No savings, new file discarded."
  fi
else
  echo $OUT.new corrupt. Deleting.
  rm $OUT.new
fi
}
echo Finished GZIP compression, you now have a complete gz file
echo You can stop here, but we will try to make the file smaller for you.
rm $OUT.new || true
#time 7z a $OUT.new -mx=9 -tgzip $OUT 
gunzip < $OUT | time 7z a $OUT.new -mx=9 -tgzip -si
#time 7z a dummy -mx=9 -tgzip -so -si $OUT.new $OUT

accept_new
cp $OUT $OUT.new
echo Trying advdef compression 
time advdef -4 -z $OUT.new # instead link?
accept_new