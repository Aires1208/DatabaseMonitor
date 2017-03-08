#!/bin/bash

before(){
    count=1
    idle=0
    system=0
    user=0
    nice=0
    iowait=0
    processcnt=0
    userno=3
    sysno=5
    iowaitno=6
    idle=8

    temp=`sar 1 1 | tail -3 | head -1`
    for list in $temp
    do
      if [ $list = "%user" ]
      then
            userno=$count
      fi
      if [ $list = "%system" ]
      then
            sysno=$count
      fi
      if [ $list = "%iowait" ]
      then
            iowaitno=$count
      fi
      if [ $list = "%idle" ]
      then
            idleno=$count
      fi
      if [ $list = "AM" ] || [ $list = "PM" ]
      then
            continue
      fi
      count=`expr $count + 1`
    done
    temp=`sar 1 3 | tail -1`
    user=`echo $temp |awk '{printf("%s\n",$'$userno')}'`
    system=`echo $temp |awk '{printf("%s\n",$'$sysno')}'`
    iowait=`echo $temp |awk '{printf("%s\n",$'$iowaitno')}'`
    idle=`echo $temp |awk '{printf("%s\n",$'$idleno')}'`

    temp=`ps -ef | wc -l`
    processcnt=`echo $temp`

    #echo "TotalCPUUtilization          100"
    echo "CPUUtilization(user)          $user"
    echo "CPUUtilization(system)        $system"
    echo "CPUUtilization(iowait)        $iowait"
    echo "CPUUtilization(idle)          $idle"
    echo "CPUUtilization(processcnt)    $processcnt"
}

##################################################

#vmstat_cpu_info=$(vmstat 1 1)
#vmstat_cpu_info=${vmstat_cpu_info##*st}
#vmstat_user=$(echo $vmstat_cpu_info | awk '{print $13}')
#vmstat_system=$(echo $vmstat_cpu_info | awk '{print $14}')
#vmstat_iowait=$(echo $vmstat_cpu_info | awk '{print $16}')
##vmstat_idle=$(echo $vmstat_cpu_info | awk '{print $15}')

#sar_cpu_info=$(sar -u 1 3 | tail -1)
#sar_user=$(echo $sar_cpu_info | awk '{print $3}')
#sar_system=$(echo $sar_cpu_info | awk '{print $5}')
#sar_iowait=$(echo $sar_cpu_info | awk '{print $6}')
##sar_idle=$(echo $sar_cpu_info | awk '{print $8}')

iostat_cpu_info=$(iostat -c 1 1)
iostat_cpu_info=${iostat_cpu_info##*idle}
iostat_user=$(echo $iostat_cpu_info | awk '{print $1}')
iostat_system=$(echo $iostat_cpu_info | awk '{print $3}')
iostat_iowait=$(echo $iostat_cpu_info | awk '{print $4}')
#iostat_idle=$(echo $iostat_cpu_info | awk '{print $6}')

#user=$(echo "$sar_user $iostat_user" | awk '{printf ("%.2f", ($1 + $2) / 2)}')
#system=$(echo "$sar_system $iostat_system" | awk '{printf ("%.2f", ($1 + $2) / 2)}')
#iowait=$(echo "$sar_iowait $iostat_iowait" | awk '{printf ("%.2f", ($1 + $2) / 2)}')
#idle=$(echo "$user $system $iowait" | awk '{printf ("%.2f", 100 - $1 - $2 - $3)}')

echo "user        $iostat_user"
echo "system      $iostat_system"
echo "iowait      $iostat_iowait"