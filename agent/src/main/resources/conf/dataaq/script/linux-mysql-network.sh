#!/bin/bash

before(){
    inputrowno=0
    outputrowno=0

    pid=`pidof mysqld`
    port=`netstat -anptu | grep $pid |awk '{print $4}'| awk -F':' '{print $2}'`

    iptables -A INPUT -p tcp --dport $port -j ACCEPT
    iptables -A OUTPUT -p tcp --sport $port -j ACCEPT

    inbts=`iptables -vnxL INPUT |awk '/tcp dpt:'$port'/{print $2}'|awk 'NR==1{print}'`
    outbts=`iptables -vnxL OUTPUT |awk '/tcp spt:'$port'/{print $2}'|awk 'NR==1{print}'`
    #echo "$inbts $outbts"

    inputline=`iptables -vnxL INPUT|wc |awk '{print $1}'`
    outputline=`iptables -vnxL OUTPUT|wc |awk '{print $1}'`

    inputnum=`expr $inputline - 3`
    outputnum=`expr $outputline - 3`
    #echo "$inputnum $outputnum"
    while [ $inputrowno -lt $inputnum ] ;
    do
        iptables -D INPUT 2
        inputrowno=`expr $inputrowno + 1`
    done

    while [ $outputrowno -lt $outputnum ] ;
    do
        iptables -D OUTPUT 2
        outputrowno=`expr $outputrowno + 1`
    done

    echo "$inbts $outbts"
}

##################################################

sar_network_info=$(sar -n DEV 1 3)
sar_network_info=${sar_network_info##*rxmcst/s}
sar_network_arr=(${sar_network_info})

net_num=0
total_receive=0
total_transmit=0

index=0
while [ $index -lt ${#sar_network_arr[@]} ];do
    net_num=$(expr $net_num + 1)

    total_receive=$(echo "$total_receive ${sar_network_arr[$(expr $index + 4)]}" | awk '{printf ("%.2f", $1 + $2)}')
    total_transmit=$(echo "$total_transmit ${sar_network_arr[$(expr $index + 5)]}" | awk '{printf ("%.2f", $1 + $2)}')

    #let index+=9
    index=$(expr $index + 9)
done

intransrate=0.00
outtransrate=0.00
if [ ! -z $net_num ];then
    intransrate=$(echo "$net_num $total_receive" | awk '{printf ("%.2f", $2 / $1)}')
    outtransrate=$(echo "$net_num $total_transmit" | awk '{printf ("%.2f", $2 / $1)}')
fi

echo "intransrate     $intransrate"
echo "outtransrate    $outtransrate"
