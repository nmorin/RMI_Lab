#!/bin/bash

echo "-------------------BUY------------------------"
for i in `seq 1 1`;
do
	java Client 54.172.100.161 iq b >> IntensiveBuyOutput.txt

done

echo "-------------------LOOK------------------------"
for i in `seq 1 1`;
do
	java Client 54.172.100.161 iq l >> IntensiveLookOutput.txt

done

echo "-------------------BUY------------------------"
for i in `seq 1 1`;
do
	java Client 54.172.100.161 cq b >> ConcurrentBuyOutput.txt

done

echo "-------------------LOOK------------------------"
for i in `seq 1 1`;
do
	java Client 54.172.100.161 cq l >> ConcurrentLookOutput.txt

done