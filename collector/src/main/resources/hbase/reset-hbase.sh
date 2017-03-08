#!/usr/bin/env bash

hbase_bin='/root/version/pinpoint-master/quickstart/hbase/hbase-1.0.3'
echo $hbase_bin
"$hbase_bin"/bin/hbase shell scripts/hbase-drop.txt
