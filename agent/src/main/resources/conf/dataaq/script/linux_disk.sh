#!/bin/sh

line=`iostat |wc -l`
num=`expr $line - 2`
tmp=`iostat -x 3 2 | tail -$num`
echo "$tmp"
