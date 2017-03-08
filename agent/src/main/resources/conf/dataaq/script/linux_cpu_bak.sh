#!/bin/sh
#
# Copyright (C) 2015 ZTE, Inc. and others. All rights reserved. (ZTE)
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#         http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.


cpuinfo=$(sar -u 1 5 | tail -1)
user=$(echo $cpuinfo | awk '{print $3}')
system=$(echo $cpuinfo | awk '{print $5}')
iowait=$(echo $cpuinfo | awk '{print $6}')
#idle=$(echo $cpuinfo | awk '{print $8}')

cpuinfo2=$(iostat -c)
cpuinfo2=${cpuinfo2##*idle}
user2=$(echo $cpuinfo2 | awk '{print $1}')
system2=$(echo $cpuinfo2 | awk '{print $3}')
iowait2=$(echo $cpuinfo2 | awk '{print $4}')
#idle2=$(echo $cpuinfo2 | awk '{print $6}')

user=$(echo "$user $user2" | awk '{printf ("%.2f", ($1 + $2) / 2)}')
system=$(echo "$system $system2" | awk '{printf ("%.2f", ($1 + $2) / 2)}')
iowait=$(echo "$iowait $iowait" | awk '{printf ("%.2f", ($1 + $2) / 2)}')
idle=$(echo "$user $system $iowait" | awk '{printf ("%.2f", 100 - $1 - $2 - $3)}')

echo "user        $user"
echo "system        $system"
echo "iowait    $iowait"
echo "idle     idle"
