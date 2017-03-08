#!/bin/bash

before(){
    sml_mem=0
    lg_mem=0
    ovsz_alloc=0
    sys_mem=0
    phys_mem=0
    sysmemratio=0
    total=0
    used=0

    temp=`vmstat 1 5 | tail -1`
    memqueue=`echo $temp | awk '{printf("%s\n",$2)}'`
    mempageinratio=`echo $temp | awk '{printf("%s\n",$7)}'`
    mempageoutratio=`echo $temp | awk '{printf("%s\n",$8)}'`

    temp=`free | head -2 | tail -1`
    phys_mem=`echo $temp | awk '{printf("%s\n",$2)}'`
    free_mem=`echo $temp | awk '{printf("%s\n",$4)}'`
    used_mem=`echo $temp | awk '{printf("%s\n",$3)}'`

    temp=`free | head -4 | tail -1`
    usedswap=`echo $temp | awk '{printf("%s\n",$3)}'`
    totalswap=`echo $temp | awk '{printf("%s\n",$2)}'`
    freeswap=`echo $temp | awk '{printf("%s\n",$4)}'`

    temp=`free | head -3 | tail -1`
    freebufferscache=`echo $temp | awk '{printf("%s\n",$4)}'`

    if [ "$totalswap" = "0" ]
    then
        usedswapratio=0.00
    else
        usedswapratio=$(echo "scale=4; $usedswap / $totalswap * 100" | bc)
        usedswapratio=`echo $usedswapratio  | awk '{printf("%f", $1)}'`
    fi

    total=`expr $phys_mem + $totalswap`
    used=`expr $total - $freebufferscache - $freeswap`

    if [ "$total" = "0" ]
    then
        usedmemratio=0.00
    else
        usedmemratio=$(echo "scale=4; $used / $total * 100" | bc)
        usedmemratio=`echo $usedmemratio | awk '{printf("%f", $1)}'`
    fi

      temp=`cat /proc/vmstat | grep pgpgin`
      swapin=`echo $temp | awk '{printf("%s\n",$2)}'`
      temp=`cat /proc/vmstat | grep pgpgout`
      swapout=`echo $temp | awk '{printf("%s\n",$2)}'`
      swaprequesttotal=`expr $swapin + $swapout`

    echo "usedmemratio    $usedmemratio"
    echo "used_mem        $used_mem"
    echo "phys_mem        $phys_mem"
    echo "sysmemratio     0"
    echo "usrmemratio     0"
    echo "mempagerequest  $swaprequesttotal"
    echo "memqueue        $memqueue"
    echo "mempageinratio  $mempageinratio"
    echo "mempageoutratio $mempageoutratio"
    echo "usedswap        $usedswap"
    echo "totalswap       $totalswap"
    echo "swapusedratio   $usedswapratio"
}

##################################################

#sar_mem_info=$(sar -r 1 3 | tail -1)
#sar_kbmemfree=$(echo $sar_mem_info | awk '{print $2}')
#sar_kbmemused=$(echo $sar_mem_info | awk '{print $3}')
#sar_memused=$(echo $sar_mem_info | awk '{print $4}')
#sar_commit=$(echo $sar_mem_info | awk '{print $8}')

#vmstat_mem_info=$(vmstat 1 1)
#vmstat_mem_info=${vmstat_mem_info##*st}
#vmstat_swpd=$(echo $vmstat_mem_info | awk '{print $3}')
#vmstat_free=$(echo $vmstat_mem_info | awk '{print $4}')

free_mem_info=$(free -m)
mem_info=${free_mem_info##*Mem:}
mem_info=${free_mem_info%%Swap:*}
total_mem=$(echo $mem_info | awk '{print $1}')
used_mem=$(echo $mem_info | awk '{print $2}')

swap_info=${free_mem_info##*Swap:}
total_swap=$(echo $swap_info | awk '{print $1}')
used_swap=$(echo $swap_info | awk '{print $2}')

usedmemratio=0.00
if [ ! -z $total_mem ] && [ ! -z $total_swap ];then
    usedmemratio=$(echo "$used_mem $used_swap $total_mem $total_swap" | awk '{printf ("%.2f", ($1 + $2) * 100 / ($3 + $4))}')
fi

echo "usedmemratio    $usedmemratio"

