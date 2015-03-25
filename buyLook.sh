#!/bin/bash

echo "-------------------BUY------------------------"
for i in `seq 1 1`;
do
	java Client 54.172.100.161 b >> BuyOutput.txt

done

echo "-------------------LOOK------------------------"
for i in `seq 1 1`;
do
	java Client 54.172.100.161 l >> LookOutput.txt

done