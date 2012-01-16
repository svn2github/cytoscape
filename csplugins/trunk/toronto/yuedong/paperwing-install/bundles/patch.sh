#!/bin/bash

for i in patches/*.patch
do
	patch -r patch.rej -p0 -N <$i
done
