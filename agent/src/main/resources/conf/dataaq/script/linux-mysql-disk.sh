#!/bin/bash

before(){
    #pid=`pidof mysqld`
    #tmp=`iotop -p $pid -k -b -n 1 | tail -1 |awk '{print $4 " " $6}'`
    #echo "$tmp"

    line=`iostat |wc -l`
    num=`expr $line - 2`
    tmp=`iostat -x 3 2 | tail -$num`
    echo "$tmp"
}

##################################################

iostat_disk_info=$(iostat -d 1 1)
iostat_disk_info=${iostat_disk_info##*kB_wrtn}
iostat_disk_arr=(${iostat_disk_info})

dev_num=0
total_read=0
total_write=0

index=0
while [ $index -lt ${#iostat_disk_arr[@]} ];do
    dev_num=$(expr $dev_num + 1)

    total_read=$(echo "$total_read ${iostat_disk_arr[$(expr $index + 2)]}" | awk '{printf ("%.2f", $1 + $2)}')
    total_write=$(echo "$total_write ${iostat_disk_arr[$(expr $index + 3)]}" | awk '{printf ("%.2f", $1 + $2)}')

    #let index+=6
    index=$(expr $index + 6)
done

readpersec=0.00
writepersec=0.00
if [ ! -z $dev_num ];then
    readpersec=$(echo "$dev_num $total_read" | awk '{printf ("%.2f", $2 / $1)}')
    writepersec=$(echo "$dev_num $total_write" | awk '{printf ("%.2f", $2 / $1)}')
fi

echo "readpersec     $readpersec"
echo "writepersec    $writepersec"